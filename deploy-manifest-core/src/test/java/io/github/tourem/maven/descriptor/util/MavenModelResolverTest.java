package io.github.tourem.maven.descriptor.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for MavenModelResolver.
 */
class MavenModelResolverTest {

    @Test
    void shouldResolveGroupIdFromModel() {
        Model model = new Model();
        model.setGroupId("com.example");
        model.setArtifactId("test-artifact");

        String groupId = MavenModelResolver.resolveGroupId(model);

        assertThat(groupId).isEqualTo("com.example");
    }

    @Test
    void shouldResolveGroupIdFromParent() {
        Parent parent = new Parent();
        parent.setGroupId("com.example.parent");

        Model model = new Model();
        model.setArtifactId("test-artifact");
        model.setParent(parent);

        String groupId = MavenModelResolver.resolveGroupId(model);

        assertThat(groupId).isEqualTo("com.example.parent");
    }

    @Test
    void shouldThrowExceptionWhenGroupIdCannotBeResolved() {
        Model model = new Model();
        model.setArtifactId("test-artifact");

        assertThatThrownBy(() -> MavenModelResolver.resolveGroupId(model))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot resolve groupId");
    }

    @Test
    void shouldResolveVersionFromModel() {
        Model model = new Model();
        model.setVersion("1.0.0");
        model.setArtifactId("test-artifact");

        String version = MavenModelResolver.resolveVersion(model);

        assertThat(version).isEqualTo("1.0.0");
    }

    @Test
    void shouldResolveVersionFromParent() {
        Parent parent = new Parent();
        parent.setVersion("2.0.0");

        Model model = new Model();
        model.setArtifactId("test-artifact");
        model.setParent(parent);

        String version = MavenModelResolver.resolveVersion(model);

        assertThat(version).isEqualTo("2.0.0");
    }

    @Test
    void shouldThrowExceptionWhenVersionCannotBeResolved() {
        Model model = new Model();
        model.setArtifactId("test-artifact");

        assertThatThrownBy(() -> MavenModelResolver.resolveVersion(model))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot resolve version");
    }

    @Test
    void shouldResolvePropertyFromModel() {
        Model model = new Model();
        Properties props = new Properties();
        props.setProperty("java.version", "17");
        model.setProperties(props);

        String value = MavenModelResolver.resolveProperty(model, null, "java.version");

        assertThat(value).isEqualTo("17");
    }

    @Test
    void shouldResolvePropertyFromParentModel() {
        Model parentModel = new Model();
        Properties parentProps = new Properties();
        parentProps.setProperty("java.version", "11");
        parentModel.setProperties(parentProps);

        Model model = new Model();

        String value = MavenModelResolver.resolveProperty(model, parentModel, "java.version");

        assertThat(value).isEqualTo("11");
    }

    @Test
    void shouldPreferModelPropertyOverParent() {
        Model parentModel = new Model();
        Properties parentProps = new Properties();
        parentProps.setProperty("java.version", "11");
        parentModel.setProperties(parentProps);

        Model model = new Model();
        Properties props = new Properties();
        props.setProperty("java.version", "17");
        model.setProperties(props);

        String value = MavenModelResolver.resolveProperty(model, parentModel, "java.version");

        assertThat(value).isEqualTo("17");
    }

    @Test
    void shouldReturnNullWhenPropertyNotFound() {
        Model model = new Model();

        String value = MavenModelResolver.resolveProperty(model, null, "nonexistent");

        assertThat(value).isNull();
    }

    @Test
    void shouldReturnEmptyStringWhenGroupIdCannotBeResolved() {
        Model model = new Model();
        model.setArtifactId("test");

        String groupId = MavenModelResolver.getGroupIdOrEmpty(model);

        assertThat(groupId).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenVersionCannotBeResolved() {
        Model model = new Model();
        model.setArtifactId("test");

        String version = MavenModelResolver.getVersionOrEmpty(model);

        assertThat(version).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenModelIsNull() {
        assertThatThrownBy(() -> MavenModelResolver.resolveGroupId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null");
    }
}
