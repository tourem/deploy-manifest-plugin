package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for Git information collection.
 */
public class GitConfiguration {
    
    private GitFetchMode fetch = GitFetchMode.AUTO;
    private Boolean includeUncommitted = false;
    private Integer depth = 50;
    private Boolean includeBranch = true;
    private Boolean includeTags = true;
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
