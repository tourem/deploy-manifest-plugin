package com.larbotech.maven.descriptor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * Build and VCS metadata captured at descriptor generation time.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuildInfo(
        String gitCommitSha,
        String gitBranch,
        String gitTag,
        String gitRepositoryUrl,
        boolean gitDirty,
        String ciProvider,
        String ciBuildId,
        String ciBuildUrl,
        String ciActor,
        OffsetDateTime buildTimestamp
) {}
