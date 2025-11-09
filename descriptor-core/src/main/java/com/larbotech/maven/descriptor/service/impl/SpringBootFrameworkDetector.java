package com.larbotech.maven.descriptor.service.impl;

import com.larbotech.maven.descriptor.service.SpringBootDetector;
import com.larbotech.maven.descriptor.service.spi.FrameworkDetector;
import org.apache.maven.model.Model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Framework detector for Spring Boot based on presence of spring-boot-maven-plugin.
 */
public class SpringBootFrameworkDetector implements FrameworkDetector {

    private final SpringBootDetector delegate = new SpringBootDetector();

    @Override
    public String name() {
        return "spring-boot";
    }

    @Override
    public Optional<Map<String, Object>> detect(Model model, Path modulePath) {
        boolean isBoot = delegate.isSpringBootExecutable(model);
        if (!isBoot) return Optional.empty();
        Map<String, Object> details = new HashMap<>();
        details.put("executable", true);
        String classifier = delegate.getSpringBootClassifier(model);
        if (classifier != null) details.put("classifier", classifier);
        String finalName = delegate.getSpringBootFinalName(model);
        if (finalName != null) details.put("finalName", finalName);
        return Optional.of(details);
    }
}
