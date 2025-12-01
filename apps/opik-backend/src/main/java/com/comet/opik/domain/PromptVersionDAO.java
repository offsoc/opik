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
            <if(filters)> AND <filters> <endif>
            ORDER BY <if(sort_fields)><sort_fields>, <endif>id DESC
            <if(limit)> LIMIT :limit OFFSET :offset <endif>
            """)
    @UseStringTemplateEngine
    @AllowUnusedBindings
    List<PromptVersion> find(
            @Bind("workspace_id") String workspaceId,
            @Nullable @Define("ids") @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE, value = "ids") Collection<UUID> ids,
            @Nullable @Define("prompt_id") @Bind("prompt_id") UUID promptId,
            @Nullable @Define("offset") @Bind("offset") Integer offset,
            @Nullable @Define("limit") @Bind("limit") Integer limit,
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
        return find(workspaceId, null, promptId, offset, limit, sortingFields, filters, filterMapping);
    }

    default List<PromptVersion> findByIds(Collection<UUID> ids, String workspaceId) {
        return find(workspaceId, ids, null, null, null, null, null, Map.of());
    }

    @SqlQuery("""
            SELECT count(id) FROM prompt_versions
            WHERE workspace_id = :workspace_id
            <if(ids)> AND id IN (<ids>) <endif>
            <if(prompt_id)> AND prompt_id = :prompt_id <endif>
            <if(filters)> AND <filters> <endif>
            """)
    @UseStringTemplateEngine
    @AllowUnusedBindings
    long findCount(
            @Bind("workspace_id") String workspaceId,
            @Nullable @Define("ids") @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE, value = "ids") Collection<UUID> ids,
            @Nullable @Define("prompt_id") @Bind("prompt_id") UUID promptId,
            @Define("filters") String filters,
            @BindMap Map<String, Object> filterMapping);

    default long findCount(
            String workspaceId,
            UUID promptId,
            String filters,
            Map<String, Object> filterMapping) {
        return findCount(workspaceId, null, promptId, filters, filterMapping);
    }

    @SqlQuery("SELECT * FROM prompt_versions WHERE prompt_id = :prompt_id AND commit = :commit AND workspace_id = :workspace_id")
    PromptVersion findByCommit(@Bind("prompt_id") UUID promptId, @Bind("commit") String commit,
            @Bind("workspace_id") String workspaceId);

    @SqlUpdate("UPDATE prompt_versions SET tags = :tags WHERE id = :id AND workspace_id = :workspace_id")
    int updateTags(@Bind("id") UUID id, @Bind("tags") Set<String> tags, @Bind("workspace_id") String workspaceId);

    /**
     * Batch update tags for multiple prompt versions in a single database operation.
     *
     * <p>This method performs tag updates directly at the database level for optimal performance,
     * avoiding the need to fetch existing versions when in replace mode.</p>
     *
     * <p><strong>Replace Mode (mergeTags = false):</strong></p>
     * <ul>
     *   <li>Replaces all existing tags with the provided tags</li>
     *   <li>Most common and performant path</li>
     *   <li>Single UPDATE statement, no fetch required</li>
     * </ul>
     *
     * <p><strong>Merge Mode (mergeTags = true):</strong></p>
     * <ul>
     *   <li>Combines existing tags with new tags using MySQL JSON_MERGE_PRESERVE</li>
     *   <li>Handles NULL tags gracefully - uses COALESCE to default to empty JSON array</li>
     *   <li>Duplicates are acceptable - deduplicated automatically when tags are read into Set via JSON deserialization</li>
     *   <li>Note: Tags are stored as JSON arrays, e.g., ["tag1", "tag2"]</li>
     * </ul>
     *
     * @param ids Set of prompt version IDs to update
     * @param tags New tags to set or merge
     * @param mergeTags If true, merge with existing tags; if false, replace all tags
     * @param workspaceId Workspace ID for security filtering
     * @return Number of rows updated
     */
    @SqlUpdate("""
            UPDATE prompt_versions
            SET tags = <if(mergeTags)>
                    JSON_MERGE_PRESERVE(
                        COALESCE(tags, '[]'),
                        COALESCE(:tags, '[]')
                    )
                <else>
                    :tags
                <endif>
            WHERE id IN (<ids>)
            AND workspace_id = :workspace_id
            """)
    @UseStringTemplateEngine
    @AllowUnusedBindings
    int batchUpdateTags(
            @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE, value = "ids") Set<UUID> ids,
            @Bind("tags") Set<String> tags,
            @Define("mergeTags") boolean mergeTags,
            @Bind("workspace_id") String workspaceId);

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
