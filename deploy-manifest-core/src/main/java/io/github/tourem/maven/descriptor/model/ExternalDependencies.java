package io.github.tourem.maven.descriptor.model;

import lombok.Builder;

import java.util.List;

/**
 * External service dependencies required by the application.
 * 
 * @author tourem
 */
@Builder
public record ExternalDependencies(
    List<DatabaseDependency> databases,
    List<MessageQueueDependency> messageQueues,
    List<CacheDependency> caches,
    List<ExternalServiceDependency> services
) {
    
    /**
     * Database dependency information.
     */
    @Builder
    public record DatabaseDependency(
        String type,
        String minVersion,
        Boolean required,
        String connectionPool,
        String driver,
        String url
    ) {}
    
    /**
     * Message queue dependency information.
     */
    @Builder
    public record MessageQueueDependency(
        String type,
        String version,
        List<String> queues,
        List<String> topics,
        Boolean required
    ) {}
    
    /**
     * Cache dependency information.
     */
    @Builder
    public record CacheDependency(
        String type,
        String version,
        String usage,
        Boolean required,
        String mode
    ) {}
    
    /**
     * Generic external service dependency.
     */
    @Builder
    public record ExternalServiceDependency(
        String name,
        String type,
        String version,
        String endpoint,
        Boolean required
    ) {}
}
