package com.comet.opik.api.sorting;

import java.util.List;

public class SortingFactoryPromptVersions extends SortingFactory {
    @Override
    public List<String> getSortableFields() {
        return List.of(
                SortableFields.ID,
                SortableFields.PROMPT_ID,
                SortableFields.COMMIT,
                SortableFields.TEMPLATE,
                SortableFields.CHANGE_DESCRIPTION,
                SortableFields.TYPE,
                SortableFields.TAGS,
                SortableFields.CREATED_AT,
                SortableFields.CREATED_BY);
    }
}
