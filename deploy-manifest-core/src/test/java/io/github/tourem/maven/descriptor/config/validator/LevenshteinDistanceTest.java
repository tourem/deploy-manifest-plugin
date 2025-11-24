package io.github.tourem.maven.descriptor.config.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for LevenshteinDistance.
 */
class LevenshteinDistanceTest {
    
    @Test
    void shouldCalculateDistanceForIdenticalStrings() {
        assertThat(LevenshteinDistance.calculate("hello", "hello")).isZero();
    }
    
    @Test
    void shouldCalculateDistanceForOneCharDifference() {
        assertThat(LevenshteinDistance.calculate("json", "jsn")).isEqualTo(1);
        assertThat(LevenshteinDistance.calculate("standard", "standart")).isEqualTo(1);
    }
    
    @Test
    void shouldCalculateDistanceForMultipleChanges() {
        assertThat(LevenshteinDistance.calculate("kitten", "sitting")).isEqualTo(3);
    }
    
    @Test
    void shouldFindClosestMatch() {
        String[] candidates = {"json", "yaml", "html", "xml"};
        
        assertThat(LevenshteinDistance.findClosestMatch("jsn", candidates)).isEqualTo("json");
        assertThat(LevenshteinDistance.findClosestMatch("yml", candidates)).isEqualTo("yaml");
        assertThat(LevenshteinDistance.findClosestMatch("htm", candidates)).isEqualTo("html");
    }
    
    @Test
    void shouldReturnNullWhenNoGoodMatch() {
        String[] candidates = {"json", "yaml", "html"};
        
        // "xyz" is too different from all candidates
        assertThat(LevenshteinDistance.findClosestMatch("xyz", candidates, 1)).isNull();
    }
    
    @Test
    void shouldBeCaseInsensitive() {
        String[] candidates = {"JSON", "YAML", "HTML"};
        
        assertThat(LevenshteinDistance.findClosestMatch("jsn", candidates)).isEqualTo("JSON");
    }
}
