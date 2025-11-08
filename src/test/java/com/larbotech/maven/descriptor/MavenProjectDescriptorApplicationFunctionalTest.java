package com.larbotech.maven.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.larbotech.maven.descriptor.model.DeployableModule;
import com.larbotech.maven.descriptor.model.ProjectDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional end-to-end tests for the Maven Project Descriptor application.
 */
@SpringBootTest
class MavenProjectDescriptorApplicationFunctionalTest {
    
    @Autowired
    private ApplicationContext context;
    
    @TempDir
    Path tempDir;
    
    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }
    
    @Test
    void shouldGenerateCompleteDescriptorForRealWorldProject() throws IOException {
        // Create a realistic multi-module Spring Boot project
        Path projectDir = createRealWorldProject();
        
        // Create output file
        Path outputFile = tempDir.resolve("descriptor.json");
        
        // Simulate running the application
        var analyzer = context.getBean(com.larbotech.maven.descriptor.service.MavenProjectAnalyzer.class);
        ProjectDescriptor descriptor = analyzer.analyzeProject(projectDir);
        
        // Write to file
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile.toFile(), descriptor);
        
        // Verify file was created
        assertThat(outputFile).exists();
        
        // Read and verify content
        String jsonContent = Files.readString(outputFile);
        assertThat(jsonContent).isNotEmpty();
        
        // Parse back and verify
        ProjectDescriptor parsedDescriptor = mapper.readValue(jsonContent, ProjectDescriptor.class);
        
        assertThat(parsedDescriptor.projectGroupId()).isEqualTo("com.larbotech.platform");
        assertThat(parsedDescriptor.projectArtifactId()).isEqualTo("microservices-platform");
        assertThat(parsedDescriptor.projectVersion()).isEqualTo("2.5.0-SNAPSHOT");
        // Parent (pom) + common + api-gateway + services (pom) + auth-service + user-service + admin-web = 7 total
        assertThat(parsedDescriptor.totalModules()).isEqualTo(7);
        // common + api-gateway + auth-service + user-service + admin-web = 5 deployable (services is pom)
        assertThat(parsedDescriptor.deployableModulesCount()).isEqualTo(5);
        assertThat(parsedDescriptor.deployableModules()).hasSize(5);
        
        // Verify specific modules
        DeployableModule apiGateway = parsedDescriptor.deployableModules().stream()
            .filter(m -> m.getArtifactId().equals("api-gateway"))
            .findFirst()
            .orElseThrow();
        
        assertThat(apiGateway.isSpringBootExecutable()).isTrue();
        assertThat(apiGateway.getRepositoryPath())
            .isEqualTo("com/larbotech/platform/api-gateway/2.5.0-SNAPSHOT/api-gateway-2.5.0-SNAPSHOT.jar");
        
        DeployableModule authService = parsedDescriptor.deployableModules().stream()
            .filter(m -> m.getArtifactId().equals("auth-service"))
            .findFirst()
            .orElseThrow();
        
        assertThat(authService.isSpringBootExecutable()).isTrue();
        assertThat(authService.getFinalName()).isEqualTo("authentication-service");
        // Maven repositories always use standard naming artifactId-version.extension
        assertThat(authService.getRepositoryPath())
            .isEqualTo("com/larbotech/platform/auth-service/2.5.0-SNAPSHOT/auth-service-2.5.0-SNAPSHOT.jar");
        
        DeployableModule adminWeb = parsedDescriptor.deployableModules().stream()
            .filter(m -> m.getArtifactId().equals("admin-web"))
            .findFirst()
            .orElseThrow();
        
        assertThat(adminWeb.getPackaging()).isEqualTo("war");
        assertThat(adminWeb.isSpringBootExecutable()).isFalse();
    }
    
    private Path createRealWorldProject() throws IOException {
        Path projectDir = tempDir.resolve("microservices-platform");
        Files.createDirectories(projectDir);
        
        // Parent POM
        String parentPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <groupId>com.larbotech.platform</groupId>
                <artifactId>microservices-platform</artifactId>
                <version>2.5.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                
                <name>Microservices Platform</name>
                <description>Enterprise microservices platform</description>
                
                <modules>
                    <module>common</module>
                    <module>api-gateway</module>
                    <module>services</module>
                    <module>admin-web</module>
                </modules>
            </project>
            """;
        Files.writeString(projectDir.resolve("pom.xml"), parentPom);
        
        // Common module (library, JAR)
        Path commonModule = projectDir.resolve("common");
        Files.createDirectories(commonModule);
        String commonPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>microservices-platform</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>common</artifactId>
                <packaging>jar</packaging>
            </project>
            """;
        Files.writeString(commonModule.resolve("pom.xml"), commonPom);
        
        // API Gateway (Spring Boot)
        Path apiGatewayModule = projectDir.resolve("api-gateway");
        Files.createDirectories(apiGatewayModule);
        String apiGatewayPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>microservices-platform</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>api-gateway</artifactId>
                <packaging>jar</packaging>
                
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """;
        Files.writeString(apiGatewayModule.resolve("pom.xml"), apiGatewayPom);
        
        // Services parent (POM)
        Path servicesModule = projectDir.resolve("services");
        Files.createDirectories(servicesModule);
        String servicesPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>microservices-platform</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>services</artifactId>
                <packaging>pom</packaging>
                
                <modules>
                    <module>auth-service</module>
                    <module>user-service</module>
                </modules>
            </project>
            """;
        Files.writeString(servicesModule.resolve("pom.xml"), servicesPom);
        
        // Auth Service (Spring Boot with custom final name)
        Path authServiceModule = servicesModule.resolve("auth-service");
        Files.createDirectories(authServiceModule);
        String authServicePom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>services</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>auth-service</artifactId>
                <packaging>jar</packaging>
                
                <build>
                    <finalName>authentication-service</finalName>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """;
        Files.writeString(authServiceModule.resolve("pom.xml"), authServicePom);
        
        // User Service (Spring Boot)
        Path userServiceModule = servicesModule.resolve("user-service");
        Files.createDirectories(userServiceModule);
        String userServicePom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>services</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>user-service</artifactId>
                <packaging>jar</packaging>
                
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """;
        Files.writeString(userServiceModule.resolve("pom.xml"), userServicePom);
        
        // Admin Web (WAR)
        Path adminWebModule = projectDir.resolve("admin-web");
        Files.createDirectories(adminWebModule);
        String adminWebPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                     http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <parent>
                    <groupId>com.larbotech.platform</groupId>
                    <artifactId>microservices-platform</artifactId>
                    <version>2.5.0-SNAPSHOT</version>
                </parent>
                
                <artifactId>admin-web</artifactId>
                <packaging>war</packaging>
            </project>
            """;
        Files.writeString(adminWebModule.resolve("pom.xml"), adminWebPom);
        
        return projectDir;
    }
}

