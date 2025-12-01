package com.comet.opik.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

/**
 * Request to update multiple prompt versions in a single operation.
 *
 * <p>This endpoint allows efficient bulk updates to organizational metadata across multiple prompt versions.
 * It supports updating 1 to 1000 versions in a single request.</p>
 *
 * <p><strong>Immutability Constraints:</strong></p>
 * <ul>
 *   <li>Prompt versions are immutable by design - their functional properties cannot be changed after creation</li>
 *   <li>Only organizational metadata (currently tags) can be updated</li>
 *   <li>Core properties (template, metadata, change_description) remain immutable for version integrity</li>
 * </ul>
 *
 * <p><strong>Usage Examples:</strong></p>
 * <pre>
 * // Replace tags for multiple versions
 * {
 *   "ids": ["uuid1", "uuid2", "uuid3"],
 *   "update": { "tags": ["production", "verified"] },
 *   "merge_tags": false
 * }
 *
 * // Add tags to multiple versions (merge mode)
 * {
 *   "ids": ["uuid1", "uuid2"],
 *   "update": { "tags": ["archived"] },
 *   "merge_tags": true
 * }
 * </pre>
 *
 * @see PromptVersionUpdate
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request to update one or more prompt versions. " +
        "Note: Prompt versions are immutable by design - only organizational metadata (tags) can be updated. " +
        "Supports both single and batch updates (1-1000 versions).")
public record PromptVersionBatchUpdate(
        @NotNull @NotEmpty @Size(min = 1, max = 1000) @Schema(description = "IDs of prompt versions to update. Minimum: 1, Maximum: 1000", example = "[\"019aca17-edb7-7e08-b62a-298dd41b1395\", \"019aca18-f123-7e08-b62a-298dd41b1396\"]", required = true) Set<UUID> ids,

        @NotNull @Valid @Schema(description = "Updates to apply to all specified prompt versions", required = true) PromptVersionUpdate update,

        @Schema(description = "Tag merge behavior: " +
                "- true: Add new tags to existing tags (union) " +
                "- false: Replace all existing tags with new tags (default) " +
                "- null: Defaults to false", example = "false", defaultValue = "false") Boolean mergeTags) {
}
