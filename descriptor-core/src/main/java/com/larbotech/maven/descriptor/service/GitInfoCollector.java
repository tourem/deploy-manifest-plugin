package com.larbotech.maven.descriptor.service;

import com.larbotech.maven.descriptor.model.BuildInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Collects Git and CI metadata without requiring external dependencies.
 * Best-effort: reads common CI environment variables and minimal data from .git.
 */
public class GitInfoCollector {

    public BuildInfo collect(Path projectRoot) {
        String sha = readEnvFirst(
                "GITHUB_SHA", "CI_COMMIT_SHA", "GIT_COMMIT", "BUILD_VCS_NUMBER")
                .orElseGet(() -> readGitHeadSha(projectRoot).orElse(null));
        String branch = readEnvFirst(
                "GITHUB_REF_NAME", "CI_COMMIT_REF_NAME", "GIT_BRANCH")
                .orElseGet(() -> readHeadRef(projectRoot).orElse(null));
        String tag = readEnvFirst("GITHUB_REF_NAME", "CI_COMMIT_TAG").orElse(null);
        String repoUrl = readEnvFirst(
                "GITHUB_SERVER_URL", "CI_REPOSITORY_URL", "GIT_URL").orElse(null);
        boolean dirty = isDirty(projectRoot);

        String ciProvider = detectCiProvider();
        String buildId = readEnvFirst(
                "GITHUB_RUN_ID", "CI_JOB_ID", "BUILD_ID").orElse(null);
        String buildUrl = readEnvFirst(
                "GITHUB_SERVER_URL", "CI_JOB_URL", "BUILD_URL").orElse(null);
        String actor = readEnvFirst(
                "GITHUB_ACTOR", "GITLAB_USER_NAME", "BUILD_USER").orElse(null);

        return BuildInfo.builder()
                .gitCommitSha(sha)
                .gitBranch(branch)
                .gitTag(tag)
                .gitRepositoryUrl(repoUrl)
                .gitDirty(dirty)
                .ciProvider(ciProvider)
                .ciBuildId(buildId)
                .ciBuildUrl(buildUrl)
                .ciActor(actor)
                .buildTimestamp(OffsetDateTime.now())
                .build();
    }

    private Optional<String> readEnvFirst(String... keys) {
        for (String k : keys) {
            String v = System.getenv(k);
            if (v != null && !v.isBlank()) return Optional.of(v);
        }
        return Optional.empty();
    }

    private Optional<String> readGitHeadSha(Path root) {
        try {
            Path git = root.resolve(".git");
            Path head = git.resolve("HEAD");
            if (!Files.exists(head)) return Optional.empty();
            String ref = Files.readString(head, StandardCharsets.UTF_8).trim();
            if (ref.startsWith("ref: ")) {
                String refPath = ref.substring(5).trim();
                Path refFile = git.resolve(refPath);
                if (Files.exists(refFile)) {
                    String sha = Files.readString(refFile, StandardCharsets.UTF_8).trim();
                    return Optional.ofNullable(sha.isEmpty() ? null : sha);
                }
            } else if (ref.length() >= 7) {
                return Optional.of(ref);
            }
        } catch (Exception ignored) { }
        return Optional.empty();
    }

    private Optional<String> readHeadRef(Path root) {
        try {
            Path git = root.resolve(".git");
            Path head = git.resolve("HEAD");
            if (!Files.exists(head)) return Optional.empty();
            String ref = Files.readString(head, StandardCharsets.UTF_8).trim();
            if (ref.startsWith("ref: ")) {
                String refPath = ref.substring(5).trim();
                int idx = refPath.lastIndexOf('/');
                if (idx >= 0 && idx + 1 < refPath.length()) {
                    return Optional.of(refPath.substring(idx + 1));
                }
            }
        } catch (Exception ignored) { }
        return Optional.empty();
    }

    private boolean isDirty(Path root) {
        try {
            Path git = root.resolve(".git");
            Path indexLock = git.resolve("index.lock");
            return Files.exists(indexLock);
        } catch (Exception e) {
            return false;
        }
    }

    private String detectCiProvider() {
        if (System.getenv("GITHUB_ACTIONS") != null) return "github";
        if (System.getenv("GITLAB_CI") != null) return "gitlab";
        if (System.getenv("JENKINS_HOME") != null) return "jenkins";
        if (System.getenv("TEAMCITY_VERSION") != null) return "teamcity";
        return null;
    }
}
