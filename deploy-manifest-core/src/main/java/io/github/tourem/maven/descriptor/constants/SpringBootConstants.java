package io.github.tourem.maven.descriptor.constants;

/**
 * Constants for Spring Boot-related values.
 *
 * @author tourem
 */
public final class SpringBootConstants {

    private SpringBootConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // Spring Boot dependencies
    public static final String ACTUATOR_ARTIFACT_ID = "spring-boot-starter-actuator";
    public static final String SPRING_BOOT_STARTER_PREFIX = "spring-boot-starter";

    // Actuator endpoints
    public static final String DEFAULT_ACTUATOR_BASE_PATH = "/actuator";
    public static final String HEALTH_ENDPOINT = "/health";
    public static final String INFO_ENDPOINT = "/info";
    public static final String METRICS_ENDPOINT = "/metrics";

    // Configuration files
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String APPLICATION_YML = "application.yml";
    public static final String APPLICATION_YAML = "application.yaml";

    // Property names
    public static final String SERVER_PORT_PROPERTY = "server.port";
    public static final String ACTUATOR_BASE_PATH_PROPERTY = "management.endpoints.web.base-path";
    public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    // Default values
    public static final int DEFAULT_SERVER_PORT = 8080;
}
