package io.github.tourem.maven.descriptor.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Main configuration class for the Maven Deploy Manifest Plugin.
 * <p>
 * This class represents the complete configuration that can be specified
 * in the .deploy-manifest.yml file, environment variables, or command line.
 * </p>
 */
public class ManifestConfiguration {
    
    @NotNull
    private ManifestProfile profile = ManifestProfile.BASIC;
    
    @NotNull
    @Valid
    private OutputConfiguration output;
    
    @NotNull
    @Valid
    private DependenciesConfiguration dependencies;
    
    @NotNull
    @Valid
    private MetadataConfiguration metadata;
    
    @NotNull
    @Valid
    private GitConfiguration git;
    
    @NotNull
    @Valid
    private DockerConfiguration docker;
    
    @NotNull
    @Valid
    private CiConfiguration ci;
    
    @NotNull
    @Valid
    private FrameworksConfiguration frameworks;
    
    @NotNull
    @Valid
    private ValidationConfiguration validation;
    
    @NotNull
    private Boolean verbose;
    
    @NotNull
    private Boolean dryRun;
    
    @NotNull
    private Boolean skip;
    
    /**
     * Default constructor with default values.
     */
    public ManifestConfiguration() {
        this.output = new OutputConfiguration();
        this.dependencies = new DependenciesConfiguration();
        this.metadata = new MetadataConfiguration();
        this.git = new GitConfiguration();
        this.docker = new DockerConfiguration();
        this.ci = new CiConfiguration();
        this.frameworks = new FrameworksConfiguration();
        this.validation = new ValidationConfiguration();
        this.verbose = false;
        this.dryRun = false;
        this.skip = false;
    }
    
    // Getters and Setters
    
    public ManifestProfile getProfile() {
        return profile;
    }
    
    public void setProfile(ManifestProfile profile) {
        this.profile = profile;
    }
    
    public OutputConfiguration getOutput() {
        return output;
    }
    
    public void setOutput(OutputConfiguration output) {
        this.output = output;
    }
    
    public DependenciesConfiguration getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(DependenciesConfiguration dependencies) {
        this.dependencies = dependencies;
    }
    
    public MetadataConfiguration getMetadata() {
        return metadata;
    }
    
    public void setMetadata(MetadataConfiguration metadata) {
        this.metadata = metadata;
    }
    
    public GitConfiguration getGit() {
        return git;
    }
    
    public void setGit(GitConfiguration git) {
        this.git = git;
    }
    
    public DockerConfiguration getDocker() {
        return docker;
    }
    
    public void setDocker(DockerConfiguration docker) {
        this.docker = docker;
    }
    
    public CiConfiguration getCi() {
        return ci;
    }
    
    public void setCi(CiConfiguration ci) {
        this.ci = ci;
    }
    
    public FrameworksConfiguration getFrameworks() {
        return frameworks;
    }
    
    public void setFrameworks(FrameworksConfiguration frameworks) {
        this.frameworks = frameworks;
    }
    
    public ValidationConfiguration getValidation() {
        return validation;
    }
    
    public void setValidation(ValidationConfiguration validation) {
        this.validation = validation;
    }
    
    public Boolean getVerbose() {
        return verbose;
    }
    
    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }
    
    public Boolean getDryRun() {
        return dryRun;
    }
    
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }
    
    public Boolean getSkip() {
        return skip;
    }
    
    public void setSkip(Boolean skip) {
        this.skip = skip;
    }
    
    /**
     * Creates a builder for ManifestConfiguration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for ManifestConfiguration.
     */
    public static class Builder {
        private final ManifestConfiguration config;
        
        public Builder() {
            this.config = new ManifestConfiguration();
        }
        
        public Builder profile(ManifestProfile profile) {
            config.setProfile(profile);
            return this;
        }
        
        public Builder output(OutputConfiguration output) {
            config.setOutput(output);
            return this;
        }
        
        public Builder dependencies(DependenciesConfiguration dependencies) {
            config.setDependencies(dependencies);
            return this;
        }
        
        public Builder metadata(MetadataConfiguration metadata) {
            config.setMetadata(metadata);
            return this;
        }
        
        public Builder git(GitConfiguration git) {
            config.setGit(git);
            return this;
        }
        
        public Builder docker(DockerConfiguration docker) {
            config.setDocker(docker);
            return this;
        }
        
        public Builder ci(CiConfiguration ci) {
            config.setCi(ci);
            return this;
        }
        
        public Builder frameworks(FrameworksConfiguration frameworks) {
            config.setFrameworks(frameworks);
            return this;
        }
        
        public Builder validation(ValidationConfiguration validation) {
            config.setValidation(validation);
            return this;
        }
        
        public Builder verbose(Boolean verbose) {
            config.setVerbose(verbose);
            return this;
        }
        
        public Builder dryRun(Boolean dryRun) {
            config.setDryRun(dryRun);
            return this;
        }
        
        public Builder skip(Boolean skip) {
            config.setSkip(skip);
            return this;
        }
        
        public ManifestConfiguration build() {
            return config;
        }
    }
    
    @Override
    public String toString() {
        return "ManifestConfiguration{" +
                "profile=" + profile +
                ", output=" + output +
                ", dependencies=" + dependencies +
                ", metadata=" + metadata +
                ", git=" + git +
                ", docker=" + docker +
                ", ci=" + ci +
                ", frameworks=" + frameworks +
                ", validation=" + validation +
                ", verbose=" + verbose +
                ", dryRun=" + dryRun +
                ", skip=" + skip +
                '}';
    }
}
