package io.github.tourem.maven.descriptor.util;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for XmlConfigurationExtractor.
 */
class XmlConfigurationExtractorTest {

    @Test
    void shouldExtractChildValue() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom child = new Xpp3Dom("classifier");
        child.setValue("exec");
        config.addChild(child);

        String value = XmlConfigurationExtractor.extractChildValue(config, "classifier");

        assertThat(value).isEqualTo("exec");
    }

    @Test
    void shouldReturnNullWhenChildNotFound() {
        Xpp3Dom config = new Xpp3Dom("configuration");

        String value = XmlConfigurationExtractor.extractChildValue(config, "nonexistent");

        assertThat(value).isNull();
    }

    @Test
    void shouldReturnNullWhenConfigurationIsNull() {
        String value = XmlConfigurationExtractor.extractChildValue(null, "classifier");

        assertThat(value).isNull();
    }

    @Test
    void shouldReturnNullWhenConfigurationIsNotXpp3Dom() {
        String value = XmlConfigurationExtractor.extractChildValue("not xml", "classifier");

        assertThat(value).isNull();
    }

    @Test
    void shouldExtractNestedValue() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom execution = new Xpp3Dom("execution");
        Xpp3Dom mainClass = new Xpp3Dom("mainClass");
        mainClass.setValue("com.example.Main");
        execution.addChild(mainClass);
        config.addChild(execution);

        String value = XmlConfigurationExtractor.extractNestedValue(config, "execution", "mainClass");

        assertThat(value).isEqualTo("com.example.Main");
    }

    @Test
    void shouldReturnNullWhenNestedPathNotFound() {
        Xpp3Dom config = new Xpp3Dom("configuration");

        String value = XmlConfigurationExtractor.extractNestedValue(config, "execution", "mainClass");

        assertThat(value).isNull();
    }

    @Test
    void shouldCheckIfChildExists() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom child = new Xpp3Dom("classifier");
        config.addChild(child);

        boolean exists = XmlConfigurationExtractor.hasChild(config, "classifier");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenChildDoesNotExist() {
        Xpp3Dom config = new Xpp3Dom("configuration");

        boolean exists = XmlConfigurationExtractor.hasChild(config, "nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldExtractBooleanValue() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom child = new Xpp3Dom("skip");
        child.setValue("true");
        config.addChild(child);

        boolean value = XmlConfigurationExtractor.extractBooleanValue(config, "skip", false);

        assertThat(value).isTrue();
    }

    @Test
    void shouldReturnDefaultWhenBooleanNotFound() {
        Xpp3Dom config = new Xpp3Dom("configuration");

        boolean value = XmlConfigurationExtractor.extractBooleanValue(config, "skip", true);

        assertThat(value).isTrue();
    }

    @Test
    void shouldReturnDefaultWhenBooleanValueIsInvalid() {
        Xpp3Dom config = new Xpp3Dom("configuration");
        Xpp3Dom child = new Xpp3Dom("skip");
        child.setValue("not-a-boolean");
        config.addChild(child);

        boolean value = XmlConfigurationExtractor.extractBooleanValue(config, "skip", false);

        assertThat(value).isFalse();
    }
}
