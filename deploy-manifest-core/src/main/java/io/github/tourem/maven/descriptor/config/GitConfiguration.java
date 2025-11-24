package io.github.tourem.maven.descriptor.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration for Git information collection.
 */
public class GitConfiguration {
    
    @NotNull
    private GitFetchMode fetch = GitFetchMode.AUTO;
    
    @NotNull
    private Boolean includeUncommitted = false;
    
    @NotNull
    @Min(value = 1, message = "Git history depth must be at least 1")
    @Max(value = 1000, message = "Git history depth must be at most 1000")
    private Integer depth = 50;
    
    @NotNull
    private Boolean includeBranch = true;
    
    @NotNull
    private Boolean includeTags = true;
    
    @NotNull
    private Boolean includeRemote = true;
    
    public GitFetchMode getFetch() {
        return fetch;
    }
    
    public void setFetch(GitFetchMode fetch) {
        this.fetch = fetch;
    }
    
    public Boolean getIncludeUncommitted() {
        return includeUncommitted;
    }
    
    public void setIncludeUncommitted(Boolean includeUncommitted) {
        this.includeUncommitted = includeUncommitted;
    }
    
    public Integer getDepth() {
        return depth;
    }
    
    public void setDepth(Integer depth) {
        this.depth = depth;
    }
    
    public Boolean getIncludeBranch() {
        return includeBranch;
    }
    
    public void setIncludeBranch(Boolean includeBranch) {
        this.includeBranch = includeBranch;
    }
    
    public Boolean getIncludeTags() {
        return includeTags;
    }
    
    public void setIncludeTags(Boolean includeTags) {
        this.includeTags = includeTags;
    }
    
    public Boolean getIncludeRemote() {
        return includeRemote;
    }
    
    public void setIncludeRemote(Boolean includeRemote) {
        this.includeRemote = includeRemote;
    }
    
    @Override
    public String toString() {
        return "GitConfiguration{" +
                "fetch=" + fetch +
                ", includeUncommitted=" + includeUncommitted +
                ", depth=" + depth +
                ", includeBranch=" + includeBranch +
                ", includeTags=" + includeTags +
                ", includeRemote=" + includeRemote +
                '}';
    }
}
