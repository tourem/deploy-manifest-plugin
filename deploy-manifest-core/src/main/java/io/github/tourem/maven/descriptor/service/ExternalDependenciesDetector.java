package io.github.tourem.maven.descriptor.service;

import io.github.tourem.maven.descriptor.model.ExternalDependencies;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Detects external service dependencies from Maven dependencies and configuration files.
 * 
 * @author tourem
 */
@Slf4j
public class ExternalDependenciesDetector {
    
    /**
     * Detect external dependencies from Maven model and application configuration.
     */
    public ExternalDependencies detect(Model model, Path modulePath) {
        if (model.getDependencies() == null || model.getDependencies().isEmpty()) {
            return null;
        }
        
        List<ExternalDependencies.DatabaseDependency> databases = detectDatabases(model, modulePath);
        List<ExternalDependencies.MessageQueueDependency> messageQueues = detectMessageQueues(model);
        List<ExternalDependencies.CacheDependency> caches = detectCaches(model);
        List<ExternalDependencies.ExternalServiceDependency> services = detectExternalServices(model);
        
        // Return null if no external dependencies found
        if ((databases == null || databases.isEmpty()) &&
            (messageQueues == null || messageQueues.isEmpty()) &&
            (caches == null || caches.isEmpty()) &&
            (services == null || services.isEmpty())) {
            return null;
        }
        
        return ExternalDependencies.builder()
            .databases(databases.isEmpty() ? null : databases)
            .messageQueues(messageQueues.isEmpty() ? null : messageQueues)
            .caches(caches.isEmpty() ? null : caches)
            .services(services.isEmpty() ? null : services)
            .build();
    }
    
    /**
     * Detect database dependencies.
     */
    private List<ExternalDependencies.DatabaseDependency> detectDatabases(Model model, Path modulePath) {
        List<ExternalDependencies.DatabaseDependency> databases = new ArrayList<>();
        
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            String groupId = dep.getGroupId();
            
            // PostgreSQL
            if (artifactId.contains("postgresql")) {
                String connectionPool = detectConnectionPool(model);
                databases.add(ExternalDependencies.DatabaseDependency.builder()
                    .type("PostgreSQL")
                    .minVersion(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .connectionPool(connectionPool)
                    .driver("org.postgresql.Driver")
                    .build());
            }
            // MySQL
            else if (artifactId.contains("mysql")) {
                String connectionPool = detectConnectionPool(model);
                databases.add(ExternalDependencies.DatabaseDependency.builder()
                    .type("MySQL")
                    .minVersion(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .connectionPool(connectionPool)
                    .driver("com.mysql.cj.jdbc.Driver")
                    .build());
            }
            // Oracle
            else if (artifactId.contains("ojdbc") || groupId.contains("oracle")) {
                String connectionPool = detectConnectionPool(model);
                databases.add(ExternalDependencies.DatabaseDependency.builder()
                    .type("Oracle")
                    .minVersion(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .connectionPool(connectionPool)
                    .driver("oracle.jdbc.OracleDriver")
                    .build());
            }
            // MongoDB
            else if (artifactId.contains("mongodb") || artifactId.contains("mongo-java-driver")) {
                databases.add(ExternalDependencies.DatabaseDependency.builder()
                    .type("MongoDB")
                    .minVersion(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .driver("mongodb")
                    .build());
            }
            // H2 (embedded)
            else if (artifactId.contains("h2")) {
                databases.add(ExternalDependencies.DatabaseDependency.builder()
                    .type("H2")
                    .minVersion(extractVersion(dep.getVersion()))
                    .required(false)
                    .driver("org.h2.Driver")
                    .build());
            }
        }
        
        return databases;
    }
    
    /**
     * Detect message queue dependencies.
     */
    private List<ExternalDependencies.MessageQueueDependency> detectMessageQueues(Model model) {
        List<ExternalDependencies.MessageQueueDependency> queues = new ArrayList<>();
        
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            
            // RabbitMQ
            if (artifactId.contains("amqp") || artifactId.contains("rabbitmq")) {
                queues.add(ExternalDependencies.MessageQueueDependency.builder()
                    .type("RabbitMQ")
                    .version(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .build());
            }
            // Kafka
            else if (artifactId.contains("kafka")) {
                queues.add(ExternalDependencies.MessageQueueDependency.builder()
                    .type("Apache Kafka")
                    .version(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .build());
            }
            // ActiveMQ
            else if (artifactId.contains("activemq")) {
                queues.add(ExternalDependencies.MessageQueueDependency.builder()
                    .type("ActiveMQ")
                    .version(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .build());
            }
        }
        
        return queues;
    }
    
    /**
     * Detect cache dependencies.
     */
    private List<ExternalDependencies.CacheDependency> detectCaches(Model model) {
        List<ExternalDependencies.CacheDependency> caches = new ArrayList<>();
        
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            
            // Redis
            if (artifactId.contains("redis") || artifactId.contains("jedis") || artifactId.contains("lettuce")) {
                caches.add(ExternalDependencies.CacheDependency.builder()
                    .type("Redis")
                    .version(extractVersion(dep.getVersion()))
                    .usage(detectRedisUsage(model))
                    .required(isRequired(dep))
                    .build());
            }
            // Memcached
            else if (artifactId.contains("memcached")) {
                caches.add(ExternalDependencies.CacheDependency.builder()
                    .type("Memcached")
                    .version(extractVersion(dep.getVersion()))
                    .usage("cache")
                    .required(isRequired(dep))
                    .build());
            }
            // Hazelcast
            else if (artifactId.contains("hazelcast")) {
                caches.add(ExternalDependencies.CacheDependency.builder()
                    .type("Hazelcast")
                    .version(extractVersion(dep.getVersion()))
                    .usage("distributed-cache")
                    .required(isRequired(dep))
                    .build());
            }
        }
        
        return caches;
    }
    
    /**
     * Detect other external services.
     */
    private List<ExternalDependencies.ExternalServiceDependency> detectExternalServices(Model model) {
        List<ExternalDependencies.ExternalServiceDependency> services = new ArrayList<>();
        
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            
            // Elasticsearch
            if (artifactId.contains("elasticsearch")) {
                services.add(ExternalDependencies.ExternalServiceDependency.builder()
                    .name("Elasticsearch")
                    .type("search-engine")
                    .version(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .build());
            }
            // AWS SDK
            else if (artifactId.contains("aws-java-sdk") || artifactId.contains("aws-sdk")) {
                services.add(ExternalDependencies.ExternalServiceDependency.builder()
                    .name("AWS Services")
                    .type("cloud-services")
                    .version(extractVersion(dep.getVersion()))
                    .required(isRequired(dep))
                    .build());
            }
        }
        
        return services;
    }
    
    /**
     * Detect connection pool from dependencies.
     */
    private String detectConnectionPool(Model model) {
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            if (artifactId.contains("hikari")) {
                return "HikariCP";
            } else if (artifactId.contains("tomcat-jdbc")) {
                return "Tomcat JDBC";
            } else if (artifactId.contains("c3p0")) {
                return "C3P0";
            } else if (artifactId.contains("dbcp")) {
                return "Apache DBCP";
            }
        }
        // Spring Boot default
        return "HikariCP";
    }
    
    /**
     * Detect Redis usage from Spring Boot configuration.
     */
    private String detectRedisUsage(Model model) {
        for (Dependency dep : model.getDependencies()) {
            String artifactId = dep.getArtifactId();
            if (artifactId.contains("spring-session-data-redis")) {
                return "session-store";
            } else if (artifactId.contains("spring-boot-starter-cache")) {
                return "cache";
            }
        }
        return "cache";
    }
    
    /**
     * Check if dependency is required (not optional or test scope).
     */
    private boolean isRequired(Dependency dep) {
        if (dep.isOptional()) {
            return false;
        }
        String scope = dep.getScope();
        return scope == null || scope.isEmpty() || "compile".equals(scope) || "runtime".equals(scope);
    }
    
    /**
     * Extract clean version number.
     */
    private String extractVersion(String version) {
        if (version == null || version.isEmpty()) {
            return null;
        }
        // Remove ${...} placeholders
        if (version.startsWith("${")) {
            return null;
        }
        // Extract major.minor version
        String[] parts = version.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1] + "+";
        }
        return version;
    }
}
