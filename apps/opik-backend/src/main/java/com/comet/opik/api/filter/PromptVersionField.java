package com.comet.opik.api.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PromptVersionField implements Field {
    ID(ID_QUERY_PARAM, FieldType.STRING_STATE_DB),
    PROMPT_ID(PROMPT_IDS_QUERY_PARAM, FieldType.STRING_STATE_DB),
    COMMIT(COMMIT_QUERY_PARAM, FieldType.STRING_STATE_DB),
    TEMPLATE(TEMPLATE_QUERY_PARAM, FieldType.STRING_STATE_DB),
    CHANGE_DESCRIPTION(CHANGE_DESCRIPTION_QUERY_PARAM, FieldType.STRING_STATE_DB),
    TYPE(TYPE_QUERY_PARAM, FieldType.ENUM),
    TAGS(TAGS_QUERY_PARAM, FieldType.STRING_STATE_DB),
    CREATED_AT(CREATED_AT_QUERY_PARAM, FieldType.DATE_TIME_STATE_DB),
    CREATED_BY(CREATED_BY_QUERY_PARAM, FieldType.STRING_STATE_DB),
    ;

    private final String queryParamField;
    private final FieldType type;
}
