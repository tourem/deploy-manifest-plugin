package io.github.tourem.maven.descriptor.model.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitInfo {
    private String commitId;
    private String authorName;
    private String authorEmail;
    private Instant authorWhen;
    private String commitMessage;
    private Long daysAgo;
}

