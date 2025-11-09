package com.larbotech.maven.descriptor.service.spi;

import org.apache.maven.model.Model;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * SPI for framework detectors. Implementations should detect whether a given Maven module
 * uses a specific application framework (e.g., Spring Boot, Quarkus, Micronaut) and
 * optionally provide framework-specific details.
 */
public interface FrameworkDetector {

    /**
     * Framework unique name (e.g., "spring-boot", "quarkus").
     */
    String name();

    /**
     * Attempts to detect the framework in the provided module model/path.
     *
     * @param model Maven model of the module
     * @param modulePath filesystem path to the module
     * @return empty if not detected; otherwise a map of framework-specific details to be placed
     *         under descriptor.module.frameworkDetails[name]
     */
    Optional<Map<String, Object>> detect(Model model, Path modulePath);
}
