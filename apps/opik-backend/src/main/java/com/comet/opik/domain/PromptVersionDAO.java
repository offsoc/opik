package com.comet.opik.domain;

import com.comet.opik.api.PromptVersion;
import com.comet.opik.infrastructure.db.SetFlatArgumentFactory;
import com.comet.opik.infrastructure.db.UUIDArgumentFactory;
import jakarta.annotation.Nullable;
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.AllowUnusedBindings;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.BindMap;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.stringtemplate4.UseStringTemplateEngine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RegisterArgumentFactory(UUIDArgumentFactory.class)
@RegisterArgumentFactory(SetFlatArgumentFactory.class)
@RegisterColumnMapper(SetFlatArgumentFactory.class)
@RegisterConstructorMapper(PromptVersion.class)
@RegisterConstructorMapper(PromptVersionInfo.class)
interface PromptVersionDAO {

    @SqlUpdate("""
            INSERT INTO prompt_versions (
                id,
                prompt_id,
                commit,
                template,
                metadata,
                change_description,
                type,
                tags,
                created_by,
                workspace_id
            )
            VALUES (
                :bean.id,
                :bean.promptId,
                :bean.commit,
                :bean.template,
                :bean.metadata,
                :bean.changeDescription,
                :bean.type,
                :bean.tags,
                :bean.createdBy,
                :workspace_id
            )
            """)
    void save(@Bind("workspace_id") String workspaceId, @BindMethods("bean") PromptVersion prompt);

    @SqlQuery("""
            SELECT * FROM prompt_versions
            WHERE workspace_id = :workspace_id
            <if(ids)> AND id IN (<ids>) <endif>
            <if(prompt_id)> AND prompt_id = :prompt_id <endif>
            <if(commit)> AND commit like concat('%', :commit, '%') <endif>
            <if(filters)> AND <filters> <endif>
            ORDER BY <if(sort_fields)><sort_fields>, <endif>id DESC
            <if(limit)> LIMIT :limit OFFSET :offset <endif>
            """)
    @UseStringTemplateEngine
    @AllowUnusedBindings
    List<PromptVersion> find(
            @Bind("workspace_id") String workspaceId,
            @Nullable @Define("ids") @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE, value = "ids") Collection<UUID> ids,
            @Nullable @Bind("prompt_id") UUID promptId,
            @Nullable @Define("commit") @Bind("commit") String commit,
            @Nullable @Bind("offset") Integer offset,
            @Nullable @Bind("limit") Integer limit,
            @Define("sort_fields") String sortingFields,
            @Define("filters") String filters,
            @BindMap Map<String, Object> filterMapping);

    default List<PromptVersion> find(
            String workspaceId,
            UUID promptId,
            Integer offset,
            Integer limit,
            String sortingFields,
            String filters,
            Map<String, Object> filterMapping) {
        return find(workspaceId, null, promptId, null, offset, limit, sortingFields, filters, filterMapping);
    }

    default List<PromptVersion> findByIds(Collection<UUID> ids, String workspaceId) {
        return find(workspaceId, ids, null, null, null, null, null, null, Map.of());
    }

    @SqlQuery("""
            SELECT count(id) FROM prompt_versions
            WHERE workspace_id = :workspace_id
            <if(ids)> AND id IN (<ids>) <endif>
            <if(prompt_id)> AND prompt_id = :prompt_id <endif>
            <if(commit)> AND commit like concat('%', :commit, '%') <endif>
            <if(filters)> AND <filters> <endif>
            """)
    @UseStringTemplateEngine
    @AllowUnusedBindings
    long findCount(
            @Bind("workspace_id") String workspaceId,
            @Nullable @Define("ids") @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE, value = "ids") Collection<UUID> ids,
            @Nullable @Bind("prompt_id") UUID promptId,
            @Nullable @Define("commit") @Bind("commit") String commit,
            @Define("filters") String filters,
            @BindMap Map<String, Object> filterMapping);

    default long findCount(
            String workspaceId,
            UUID promptId,
            String filters,
            Map<String, Object> filterMapping) {
        return findCount(workspaceId, null, promptId, null, filters, filterMapping);
    }

    @SqlQuery("SELECT * FROM prompt_versions WHERE prompt_id = :prompt_id AND commit = :commit AND workspace_id = :workspace_id")
    PromptVersion findByCommit(@Bind("prompt_id") UUID promptId, @Bind("commit") String commit,
            @Bind("workspace_id") String workspaceId);

    @SqlUpdate("UPDATE prompt_versions SET tags = :tags WHERE id = :id AND workspace_id = :workspace_id")
    int updateTags(@Bind("id") UUID id, @Bind("tags") Set<String> tags, @Bind("workspace_id") String workspaceId);

    @SqlUpdate("DELETE FROM prompt_versions WHERE prompt_id = :prompt_id AND workspace_id = :workspace_id")
    int deleteByPromptId(@Bind("prompt_id") UUID promptId, @Bind("workspace_id") String workspaceId);

    @SqlQuery("""
            SELECT pv.id, pv.commit, p.name AS prompt_name
            FROM prompt_versions pv
            INNER JOIN prompts p ON pv.prompt_id = p.id
            WHERE pv.id IN (<ids>) AND pv.workspace_id = :workspace_id
            """)
    List<PromptVersionInfo> findPromptVersionInfoByVersionsIds(@BindList("ids") Set<UUID> ids,
            @Bind("workspace_id") String workspaceId);
}
