package io.github.tourem.maven.descriptor.config.validator;

/**
 * Calculates Levenshtein distance between two strings.
 * Used for "Did you mean?" suggestions.
 */
public class LevenshteinDistance {
    
    /**
     * Calculates the Levenshtein distance between two strings.
     * The distance is the minimum number of single-character edits (insertions, deletions, or substitutions)
     * required to change one string into the other.
     *
     * @param s1 first string
     * @param s2 second string
     * @return the Levenshtein distance
     */
    public static int calculate(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return Integer.MAX_VALUE;
        }
        
        if (s1.equals(s2)) {
            return 0;
        }
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        if (len1 == 0) return len2;
        if (len2 == 0) return len1;
        
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                
                dp[i][j] = Math.min(
                    Math.min(
                        dp[i - 1][j] + 1,      // deletion
                        dp[i][j - 1] + 1),     // insertion
                    dp[i - 1][j - 1] + cost    // substitution
                );
            }
        }
        
        return dp[len1][len2];
    }
    
    /**
     * Finds the closest match from a list of candidates.
     *
     * @param input the input string
     * @param candidates the list of valid candidates
     * @return the closest match, or null if no good match found
     */
    public static String findClosestMatch(String input, String[] candidates) {
        return findClosestMatch(input, candidates, 3);
    }
    
    /**
     * Finds the closest match from a list of candidates with a maximum distance threshold.
     *
     * @param input the input string
     * @param candidates the list of valid candidates
     * @param maxDistance maximum distance to consider (default: 3)
     * @return the closest match, or null if no match within threshold
     */
    public static String findClosestMatch(String input, String[] candidates, int maxDistance) {
        if (input == null || candidates == null || candidates.length == 0) {
            return null;
        }
        
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (String candidate : candidates) {
            int distance = calculate(input.toLowerCase(), candidate.toLowerCase());
            
            if (distance < minDistance && distance <= maxDistance) {
                minDistance = distance;
                closestMatch = candidate;
            }
        }
        
        return closestMatch;
    }
}
