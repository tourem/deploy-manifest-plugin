# Release Guide - Maven Central Publication

This document describes how to publish the Maven Descriptor Plugin to Maven Central via Sonatype.

## Prerequisites

### 1. Sonatype Account
- You already have an account on `central.sonatype.com` linked to your GitHub account
- Namespace verified: `io.github.tourem`

### 2. GPG Key
You need a GPG key pair for signing artifacts.

#### Generate GPG Key (if not already done)
```bash
# Generate key
gpg --gen-key

# List keys
gpg --list-keys

# Export public key to keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export private key (for GitHub Secrets)
gpg --armor --export-secret-keys YOUR_KEY_ID
```

### 3. GitHub Secrets
Configure the following secrets in your GitHub repository (`Settings > Secrets and variables > Actions`):

| Secret Name | Description | How to get |
|-------------|-------------|------------|
| `SONATYPE_USERNAME` | Your Sonatype username | From central.sonatype.com account |
| `SONATYPE_TOKEN` | Your Sonatype token | Generate at central.sonatype.com |
| `GPG_PRIVATE_KEY` | Your GPG private key | `gpg --armor --export-secret-keys YOUR_KEY_ID` |
| `GPG_PASSPHRASE` | Your GPG key passphrase | The passphrase you set when creating the key |

#### How to get Sonatype Token
1. Go to https://central.sonatype.com/
2. Login with your account
3. Click on your profile (top right)
4. Go to "Account" > "Generate User Token"
5. Copy the username and token
6. Add them as GitHub secrets:
   - `SONATYPE_USERNAME`: the username from the token
   - `SONATYPE_TOKEN`: the password/token from the token

## Release Process

### Using GitHub Actions (Recommended)

1. **Go to GitHub Actions**
   - Navigate to your repository on GitHub
   - Click on "Actions" tab
   - Select "Release to Maven Central" workflow

2. **Trigger the workflow**
   - Click "Run workflow"
   - Enter the release version (e.g., `1.0.0`)
   - Click "Run workflow"

3. **Wait for completion**
   - The workflow will:
     - Validate the version format
     - Build and test the project
     - Sign artifacts with GPG
     - Deploy to Maven Central
     - Create a Git tag
     - Create a GitHub Release
     - Bump to next SNAPSHOT version

4. **Verify publication**
   - Check Maven Central: https://central.sonatype.com/artifact/io.github.tourem/descriptor-plugin
   - Check GitHub Release: https://github.com/tourem/descriptor-plugin/releases

## Verification

### 1. Check Maven Central
After release (can take 15-30 minutes to appear):
- Search: https://central.sonatype.com/search?q=io.github.tourem
- Direct link: https://central.sonatype.com/artifact/io.github.tourem/descriptor-plugin

### 2. Test the published artifact
```bash
# Create a test project
mkdir test-plugin
cd test-plugin

# Create a simple pom.xml
cat > pom.xml << 'EOF'
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>test</groupId>
  <artifactId>test</artifactId>
  <version>1.0</version>
</project>
EOF

# Run the plugin
mvn io.github.tourem:descriptor-plugin:1.0.0:generate
```

## Troubleshooting

### GPG Signing Issues
```bash
# If GPG signing fails, check:
gpg --list-keys
gpg --list-secret-keys

# Test signing
echo "test" | gpg --clearsign
```

### Sonatype Authentication Issues
- Verify your token is still valid at central.sonatype.com
- Regenerate token if needed
- Update GitHub secrets

### Deployment Failures
- Check GitHub Actions logs for detailed error messages
- Verify all required metadata is present in POM (licenses, developers, SCM, etc.)
- Ensure GPG key is published to a keyserver

## Version Numbering

We follow Semantic Versioning (SemVer):
- **MAJOR.MINOR.PATCH** (e.g., 1.0.0)
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

## Release Checklist

Before releasing:
- [ ] All tests pass (`mvn clean verify`)
- [ ] Documentation is up to date
- [ ] Version number follows SemVer
- [ ] GitHub secrets are configured
- [ ] GPG key is valid and published

After releasing:
- [ ] Verify artifact on Maven Central
- [ ] Test the published artifact
- [ ] Update README.md
- [ ] Announce the release

## References

- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [GPG Documentation](https://www.gnupg.org/documentation/)
- [Semantic Versioning](https://semver.org/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

