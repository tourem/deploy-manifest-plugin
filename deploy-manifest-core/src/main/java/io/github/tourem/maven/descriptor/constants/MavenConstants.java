package io.github.tourem.maven.descriptor.constants;

/**
 * Constants for Maven-related values.
 *
 * @author tourem
 */
public final class MavenConstants {

    private MavenConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // Default values
    public static final String DEFAULT_PACKAGING = "jar";
    public static final String DEFAULT_SCOPE = "compile";
    public static final String DEFAULT_TYPE = "jar";

    // Maven property names
    public static final String PROPERTY_COMPILER_RELEASE = "maven.compiler.release";
    public static final String PROPERTY_COMPILER_SOURCE = "maven.compiler.source";
    public static final String PROPERTY_COMPILER_TARGET = "maven.compiler.target";

    // Maven plugin identifiers
    public static final String COMPILER_PLUGIN_ARTIFACT_ID = "maven-compiler-plugin";
    public static final String SPRING_BOOT_PLUGIN_GROUP_ID = "org.springframework.boot";
    public static final String SPRING_BOOT_PLUGIN_ARTIFACT_ID = "spring-boot-maven-plugin";
    public static final String ASSEMBLY_PLUGIN_ARTIFACT_ID = "maven-assembly-plugin";

    // Configuration node names
    public static final String CONFIG_RELEASE = "release";
    public static final String CONFIG_SOURCE = "source";
    public static final String CONFIG_TARGET = "target";
    public static final String CONFIG_CLASSIFIER = "classifier";
    public static final String CONFIG_FINAL_NAME = "finalName";
    public static final String CONFIG_MAIN_CLASS = "mainClass";

    // Dependency scopes
    public static final String SCOPE_COMPILE = "compile";
    public static final String SCOPE_PROVIDED = "provided";
    public static final String SCOPE_RUNTIME = "runtime";
    public static final String SCOPE_TEST = "test";
    public static final String SCOPE_SYSTEM = "system";
    public static final String SCOPE_IMPORT = "import";
}
