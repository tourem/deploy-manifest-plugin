package com.larbotech.maven.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.larbotech.maven.descriptor.model.ProjectDescriptor;
import com.larbotech.maven.descriptor.service.MavenProjectAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main application to generate Maven project descriptors.
 *
 * Usage:
 *   java -jar maven-project-descriptor.jar <project-root-path> [options]
 *
 * Arguments:
 *   project-root-path: Path to the root directory of the Maven project
 *
 * Options:
 *   -o, --output       Generate descriptor.json in current directory
 *   [output-file]      Custom path to output JSON file. If not specified, prints to stdout
 *
 * Examples:
 *   java -jar maven-project-descriptor.jar /path/to/maven/project
 *   java -jar maven-project-descriptor.jar /path/to/maven/project -o
 *   java -jar maven-project-descriptor.jar /path/to/maven/project descriptor.json
 *   java -jar maven-project-descriptor.jar /path/to/maven/project /custom/path/output.json
 */
@Slf4j
@SpringBootApplication
public class MavenProjectDescriptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MavenProjectDescriptorApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner run(MavenProjectAnalyzer analyzer) {
        return args -> {
            if (args.length == 0) {
                printUsage();
                return; // Don't exit during tests
            }

            String projectPath = args[0];
            String outputFile = null;

            // Parse arguments
            if (args.length > 1) {
                String secondArg = args[1];
                if ("-o".equals(secondArg) || "--output".equals(secondArg)) {
                    // Generate descriptor.json in current directory
                    outputFile = "descriptor.json";
                } else {
                    // Custom output file path
                    outputFile = secondArg;
                }
            }

            try {
                // Validate project path
                Path projectRootPath = Paths.get(projectPath).toAbsolutePath();
                if (!Files.exists(projectRootPath)) {
                    log.error("Project path does not exist: {}", projectRootPath);
                    throw new IllegalArgumentException("Project path does not exist: " + projectRootPath);
                }

                if (!Files.isDirectory(projectRootPath)) {
                    log.error("Project path is not a directory: {}", projectRootPath);
                    throw new IllegalArgumentException("Project path is not a directory: " + projectRootPath);
                }

                log.info("Analyzing Maven project at: {}", projectRootPath);

                // Analyze project
                ProjectDescriptor descriptor = analyzer.analyzeProject(projectRootPath);

                // Configure JSON mapper
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Output result
                if (outputFile != null) {
                    Path outputPath = Paths.get(outputFile);
                    mapper.writeValue(outputPath.toFile(), descriptor);
                    log.info("Descriptor written to: {}", outputPath.toAbsolutePath());
                    System.out.println("Descriptor successfully generated: " + outputPath.toAbsolutePath());
                } else {
                    String json = mapper.writeValueAsString(descriptor);
                    System.out.println(json);
                }

                log.info("Analysis complete. Found {} deployable modules out of {} total modules",
                        descriptor.deployableModulesCount(), descriptor.totalModules());

            } catch (Exception e) {
                log.error("Error generating descriptor: {}", e.getMessage(), e);
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException("Failed to generate descriptor", e);
            }
        };
    }
    
    private void printUsage() {
        System.out.println("""
                Maven Project Descriptor Generator

                Usage:
                  java -jar maven-project-descriptor.jar <project-root-path> [options]

                Arguments:
                  project-root-path  Path to the root directory of the Maven project

                Options:
                  -o, --output       Generate descriptor.json in current directory
                  [output-file]      Custom path to output JSON file
                                     If not specified, prints to stdout

                Examples:
                  java -jar maven-project-descriptor.jar /path/to/maven/project
                  java -jar maven-project-descriptor.jar /path/to/maven/project -o
                  java -jar maven-project-descriptor.jar /path/to/maven/project descriptor.json
                  java -jar maven-project-descriptor.jar /path/to/maven/project /custom/path/output.json
                """);
    }
}

