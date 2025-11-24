package io.github.tourem.maven.descriptor.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for output generation.
 */
public class OutputConfiguration {
    
    @NotNull
    @NotEmpty(message = "Output directory cannot be empty")
    private String directory = "target";
    
    @NotNull
    @NotEmpty(message = "Output filename cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Filename must contain only alphanumeric characters, underscores, and hyphens")
    private String filename = "deployment-manifest-report";
    
    @NotNull
    @NotEmpty(message = "At least one output format must be specified")
    private List<String> formats = new ArrayList<>();
    
    @NotNull
    private Boolean archive = false;
    
    @NotNull
    @Pattern(regexp = "zip|tar\\.gz|tar\\.bz2|jar", message = "Archive format must be one of: zip, tar.gz, tar.bz2, jar")
    private String archiveFormat = "zip";
    
    @NotNull
    private Boolean attach = false;
    
    @NotNull
    @NotEmpty(message = "Classifier cannot be empty")
    private String classifier = "manifest";
    
    public OutputConfiguration() {
        formats.add("json");
    }
    
    public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public List<String> getFormats() {
        return formats;
    }
    
    public void setFormats(List<String> formats) {
        this.formats = formats;
    }
    
    public Boolean getArchive() {
        return archive;
    }
    
    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
    
    public String getArchiveFormat() {
        return archiveFormat;
    }
    
    public void setArchiveFormat(String archiveFormat) {
        this.archiveFormat = archiveFormat;
    }
    
    public Boolean getAttach() {
        return attach;
    }
    
    public void setAttach(Boolean attach) {
        this.attach = attach;
    }
    
    public String getClassifier() {
        return classifier;
    }
    
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
    
    @Override
    public String toString() {
        return "OutputConfiguration{" +
                "directory='" + directory + '\'' +
                ", filename='" + filename + '\'' +
                ", formats=" + formats +
                ", archive=" + archive +
                ", archiveFormat='" + archiveFormat + '\'' +
                ", attach=" + attach +
                ", classifier='" + classifier + '\'' +
                '}';
    }
}
