package io.github.tourem.maven.descriptor.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for output generation.
 */
public class OutputConfiguration {
    
    private String directory = "target";
    private String filename = "deployment-manifest-report";
    private List<String> formats = new ArrayList<>();
    private Boolean archive = false;
    private String archiveFormat = "zip";
    private Boolean attach = false;
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
