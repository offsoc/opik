package com.comet.opik.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

/**
 * Represents updates to apply to one or more prompt versions.
 *
 * <p><strong>Important:</strong> Prompt versions are immutable by design. Once created, their core properties
 * (template, metadata, change description) cannot be modified. This immutability ensures version history integrity
 * and reproducibility.</p>
 *
 * <p><strong>Current Mutable Fields:</strong></p>
 * <ul>
 *   <li><strong>tags</strong> - Organizational metadata that can be modified without affecting version semantics</li>
 * </ul>
 *
 * <p>Future extensions may allow updating additional organizational metadata while preserving the immutability
 * of functional properties (template, metadata, etc.).</p>
 *
 * @see PromptVersionBatchUpdate
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Update to apply to prompt version(s). Note: Prompt versions are immutable by design. " +
        "Only organizational metadata (currently tags) can be updated. Core properties like template and " +
        "metadata cannot be modified after creation.")
public record PromptVersionUpdate(
        @Schema(description = "Tags to set or merge with existing tags. " +
                "If merge_tags is true, these tags will be added to existing tags. " +
                "If merge_tags is false, these tags will replace all existing tags.", example = "[\"production\", \"verified\", \"v2\"]") Set<String> tags) {
}
