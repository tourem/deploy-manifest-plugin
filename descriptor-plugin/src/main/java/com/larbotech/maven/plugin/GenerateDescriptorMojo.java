package com.larbotech.maven.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.larbotech.maven.descriptor.model.ProjectDescriptor;
import com.larbotech.maven.descriptor.service.MavenProjectAnalyzer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * Maven plugin goal that generates a deployment descriptor for the project.
 * 
 * This plugin analyzes the Maven project structure and generates a comprehensive
 * JSON descriptor containing deployment information including:
 * - Deployable modules (JAR, WAR, EAR)
 * - Spring Boot executables
 * - Environment-specific configurations
 * - Actuator endpoints
 * - Assembly artifacts
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class GenerateDescriptorMojo extends AbstractMojo {

    /**
     * The Maven project being analyzed.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Maven project helper for attaching artifacts.
     */
    @Component
    private MavenProjectHelper projectHelper;

    /**
     * Output file name for the generated descriptor.
     * Default: descriptor.json
     */
    @Parameter(property = "descriptor.outputFile", defaultValue = "descriptor.json")
    private String outputFile;

    /**
     * Output directory for the generated descriptor.
     * If not specified, defaults to ${project.build.directory} (target/).
     * Can be an absolute path or relative to the project root.
     */
    @Parameter(property = "descriptor.outputDirectory", defaultValue = "${project.build.directory}")
    private String outputDirectory;

    /**
     * Skip the plugin execution.
     * Default: false
     */
    @Parameter(property = "descriptor.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Pretty print the JSON output.
     * Default: true
     */
    @Parameter(property = "descriptor.prettyPrint", defaultValue = "true")
    private boolean prettyPrint;

    /**
     * Archive format for the descriptor file.
     * Supported formats: zip, tar.gz, tar.bz2, jar
     * If not specified, only the JSON file is generated without archiving.
     *
     * Examples:
     * - "zip" : Creates a ZIP archive containing the JSON file
     * - "tar.gz" : Creates a gzipped TAR archive
     * - "tar.bz2" : Creates a bzip2 compressed TAR archive
     * - "jar" : Creates a JAR archive (same as ZIP)
     */
    @Parameter(property = "descriptor.format")
    private String format;

    /**
     * Classifier to use for the attached artifact.
     * Default: "descriptor"
     *
     * The classifier is appended to the artifact name:
     * - artifactId-version-classifier.format
     *
     * Example: myapp-1.0.0-descriptor.zip
     */
    @Parameter(property = "descriptor.classifier", defaultValue = "descriptor")
    private String classifier;

    /**
     * Whether to attach the generated descriptor (or archive) to the project.
     * When true, the artifact will be installed and deployed along with the main artifact.
     * Default: false
     *
     * Set to true to deploy the descriptor to Maven repository (Nexus, JFrog, etc.)
     */
    @Parameter(property = "descriptor.attach", defaultValue = "false")
    private boolean attach;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Descriptor plugin execution skipped");
            return;
        }

        try {
            getLog().info("Analyzing Maven project: " + project.getName());

            // Get the project base directory
            File projectDir = project.getBasedir();
            getLog().debug("Project directory: " + projectDir.getAbsolutePath());

            // Analyze the project
            MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer();
            ProjectDescriptor descriptor = analyzer.analyzeProject(projectDir.toPath());

            // Determine output path for JSON file
            Path jsonOutputPath = resolveOutputPath();
            getLog().info("Generating descriptor: " + jsonOutputPath.toAbsolutePath());

            // Create output directory if needed
            Files.createDirectories(jsonOutputPath.getParent());

            // Write JSON descriptor
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Register Java 8 date/time module
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Write dates as ISO-8601 strings
            if (prettyPrint) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
            }
            mapper.writeValue(jsonOutputPath.toFile(), descriptor);

            getLog().info("✓ Descriptor JSON generated successfully");
            getLog().info("  - Total modules: " + descriptor.totalModules());
            getLog().info("  - Deployable modules: " + descriptor.deployableModulesCount());
            getLog().info("  - Output: " + jsonOutputPath.toAbsolutePath());

            // Handle archiving and attachment if format is specified
            File finalArtifact = jsonOutputPath.toFile();

            if (format != null && !format.trim().isEmpty()) {
                finalArtifact = createArchive(jsonOutputPath);
                getLog().info("✓ Archive created: " + finalArtifact.getAbsolutePath());
            }

            // Attach artifact to project if requested
            if (attach) {
                attachArtifact(finalArtifact);
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate descriptor", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Error analyzing project", e);
        }
    }

    /**
     * Resolves the output path based on configuration.
     *
     * @return the resolved output path
     */
    private Path resolveOutputPath() {
        Path basePath;

        if (outputDirectory != null && !outputDirectory.trim().isEmpty()) {
            // Use configured output directory
            Path configuredPath = Paths.get(outputDirectory);

            if (configuredPath.isAbsolute()) {
                basePath = configuredPath;
            } else {
                // Relative to project root
                basePath = project.getBasedir().toPath().resolve(configuredPath);
            }
        } else {
            // Default: target directory
            basePath = Paths.get(project.getBuild().getDirectory());
        }

        return basePath.resolve(outputFile);
    }

    /**
     * Creates an archive containing the JSON descriptor file.
     *
     * @param jsonFile the JSON file to archive
     * @return the created archive file
     * @throws IOException if archive creation fails
     */
    private File createArchive(Path jsonFile) throws IOException {
        String normalizedFormat = format.trim().toLowerCase();

        // Determine archive file name
        String archiveBaseName = project.getArtifactId() + "-" + project.getVersion();
        if (classifier != null && !classifier.trim().isEmpty()) {
            archiveBaseName += "-" + classifier;
        }

        File archiveFile;

        switch (normalizedFormat) {
            case "zip":
            case "jar":
                archiveFile = new File(jsonFile.getParent().toFile(), archiveBaseName + ".zip");
                createZipArchive(jsonFile, archiveFile);
                break;

            case "tar.gz":
            case "tgz":
                archiveFile = new File(jsonFile.getParent().toFile(), archiveBaseName + ".tar.gz");
                createTarGzArchive(jsonFile, archiveFile);
                break;

            case "tar.bz2":
            case "tbz2":
                archiveFile = new File(jsonFile.getParent().toFile(), archiveBaseName + ".tar.bz2");
                createTarBz2Archive(jsonFile, archiveFile);
                break;

            default:
                throw new IOException("Unsupported archive format: " + format +
                    ". Supported formats: zip, jar, tar.gz, tgz, tar.bz2, tbz2");
        }

        getLog().info("  - Archive format: " + normalizedFormat);
        getLog().info("  - Archive size: " + formatFileSize(archiveFile.length()));

        return archiveFile;
    }

    /**
     * Creates a ZIP archive containing the JSON file.
     */
    private void createZipArchive(Path jsonFile, File archiveFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(archiveFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            ZipEntry entry = new ZipEntry(jsonFile.getFileName().toString());
            zos.putNextEntry(entry);

            Files.copy(jsonFile, zos);
            zos.closeEntry();
        }
    }

    /**
     * Creates a TAR.GZ archive containing the JSON file.
     */
    private void createTarGzArchive(Path jsonFile, File archiveFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(archiveFile);
             GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(fos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(gzos)) {

            TarArchiveEntry entry = new TarArchiveEntry(jsonFile.toFile(), jsonFile.getFileName().toString());
            taos.putArchiveEntry(entry);

            Files.copy(jsonFile, taos);
            taos.closeArchiveEntry();
            taos.finish();
        }
    }

    /**
     * Creates a TAR.BZ2 archive containing the JSON file.
     */
    private void createTarBz2Archive(Path jsonFile, File archiveFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(archiveFile);
             BZip2CompressorOutputStream bz2os = new BZip2CompressorOutputStream(fos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(bz2os)) {

            TarArchiveEntry entry = new TarArchiveEntry(jsonFile.toFile(), jsonFile.getFileName().toString());
            taos.putArchiveEntry(entry);

            Files.copy(jsonFile, taos);
            taos.closeArchiveEntry();
            taos.finish();
        }
    }

    /**
     * Attaches the artifact to the Maven project for installation and deployment.
     */
    private void attachArtifact(File artifact) {
        String type = determineArtifactType(artifact);

        getLog().info("✓ Attaching artifact to project");
        getLog().info("  - Type: " + type);
        getLog().info("  - Classifier: " + (classifier != null ? classifier : "none"));
        getLog().info("  - File: " + artifact.getName());

        projectHelper.attachArtifact(project, type, classifier, artifact);

        getLog().info("  → Artifact will be deployed to Maven repository during 'mvn deploy'");
    }

    /**
     * Determines the artifact type based on file extension.
     */
    private String determineArtifactType(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".zip")) {
            return "zip";
        } else if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
            return "tar.gz";
        } else if (name.endsWith(".tar.bz2") || name.endsWith(".tbz2")) {
            return "tar.bz2";
        } else if (name.endsWith(".json")) {
            return "json";
        } else {
            return "file";
        }
    }

    /**
     * Formats file size in human-readable format.
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}

