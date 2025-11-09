# GitHub Secrets Configuration for Maven Central Release

This document describes the GitHub Secrets you need to configure for publishing to Maven Central.

## Required Secrets

Go to your GitHub repository: **Settings > Secrets and variables > Actions > New repository secret**

### 1. SONATYPE_USERNAME

**Description:** Your Sonatype Central username (from the generated token)

**How to get:**
1. Go to https://central.sonatype.com/
2. Login with your GitHub account
3. Click on your profile (top right corner)
4. Select "View Account"
5. Click "Generate User Token"
6. Copy the **username** part of the token
7. Add it as `SONATYPE_USERNAME` in GitHub Secrets

**Example value:** `AbCdEfGh` (8 characters)

---

### 2. SONATYPE_TOKEN

**Description:** Your Sonatype Central token/password (from the generated token)

**How to get:**
1. Same steps as above for generating the token
2. Copy the **password** part of the token
3. Add it as `SONATYPE_TOKEN` in GitHub Secrets

**Example value:** `1234567890abcdefghijklmnopqrstuvwxyz` (long alphanumeric string)

**Important:** Keep this token safe! You can only see it once when generated.

---

### 3. GPG_PRIVATE_KEY

**Description:** Your GPG private key for signing artifacts

**How to get:**

#### If you don't have a GPG key yet:

```bash
# Generate a new GPG key
gpg --gen-key

# Follow the prompts:
# - Select: (1) RSA and RSA (default)
# - Key size: 4096
# - Expiration: 0 (does not expire) or set an expiration date
# - Real name: Your name (e.g., "Mamadou Touré")
# - Email: Your email (e.g., "touremamadou1990@gmail.com")
# - Comment: Optional (e.g., "Maven Central Signing Key")
# - Passphrase: Choose a strong passphrase (you'll need this for GPG_PASSPHRASE)
```

#### Export your GPG private key:

```bash
# List your keys to get the KEY_ID
gpg --list-secret-keys --keyid-format=long

# Output example:
# sec   rsa4096/ABCD1234EFGH5678 2024-11-09 [SC]
#       1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234
# uid                 [ultimate] Mamadou Touré <touremamadou1990@gmail.com>
# ssb   rsa4096/IJKL9012MNOP3456 2024-11-09 [E]

# The KEY_ID is: ABCD1234EFGH5678 (or the full fingerprint)

# Export the private key in ASCII armor format
gpg --armor --export-secret-keys ABCD1234EFGH5678

# Copy the ENTIRE output including:
# -----BEGIN PGP PRIVATE KEY BLOCK-----
# ... (many lines of base64 encoded data)
# -----END PGP PRIVATE KEY BLOCK-----
```

**Add to GitHub Secrets:**
- Copy the entire output (including BEGIN and END lines)
- Paste it as `GPG_PRIVATE_KEY` in GitHub Secrets

---

### 4. GPG_PASSPHRASE

**Description:** The passphrase you set when creating your GPG key

**How to get:**
- This is the passphrase you entered when running `gpg --gen-key`
- If you forgot it, you'll need to create a new GPG key

**Add to GitHub Secrets:**
- Enter your GPG passphrase as `GPG_PASSPHRASE`

---

## Publish Your GPG Public Key

After creating your GPG key, you MUST publish the public key to a keyserver:

```bash
# Get your KEY_ID (from gpg --list-keys)
gpg --list-keys --keyid-format=long

# Publish to multiple keyservers (recommended)
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID
```

**Why?** Maven Central verifies that artifacts are signed with a public key that's available on a keyserver.

---

## Verification Checklist

Before running the release workflow, verify:

- [ ] `SONATYPE_USERNAME` is set in GitHub Secrets
- [ ] `SONATYPE_TOKEN` is set in GitHub Secrets
- [ ] `GPG_PRIVATE_KEY` is set in GitHub Secrets (includes BEGIN/END lines)
- [ ] `GPG_PASSPHRASE` is set in GitHub Secrets
- [ ] GPG public key is published to keyservers
- [ ] You can login to https://central.sonatype.com/
- [ ] Namespace `io.github.tourem` is verified in your Sonatype account

---

## Testing Your GPG Key Locally

Before using it in GitHub Actions, test your GPG key locally:

```bash
# Test signing
echo "test" | gpg --clearsign

# Test with passphrase from environment
export GPG_TTY=$(tty)
echo "test" | gpg --batch --pinentry-mode loopback --passphrase "YOUR_PASSPHRASE" --clearsign

# If this works, your key is ready for GitHub Actions
```

---

## Troubleshooting

### "gpg: signing failed: No secret key"
- Your GPG_PRIVATE_KEY might be incorrect
- Re-export and update the secret

### "gpg: signing failed: Inappropriate ioctl for device"
- This is normal in CI/CD environments
- The workflow uses `--pinentry-mode loopback` to handle this

### "401 Unauthorized" when deploying
- Check your SONATYPE_USERNAME and SONATYPE_TOKEN
- Regenerate the token at central.sonatype.com if needed

### "Public key not found on keyserver"
- Publish your public key: `gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID`
- Wait a few minutes for propagation

---

## Security Best Practices

1. **Never commit secrets to Git**
   - Always use GitHub Secrets
   - Never put credentials in code or configuration files

2. **Rotate tokens regularly**
   - Regenerate Sonatype tokens periodically
   - Update GitHub Secrets when you rotate

3. **Use strong GPG passphrase**
   - At least 20 characters
   - Mix of letters, numbers, and symbols

4. **Backup your GPG key**
   - Export and store securely: `gpg --armor --export-secret-keys YOUR_KEY_ID > gpg-private-key-backup.asc`
   - Store in a secure location (password manager, encrypted drive)

---

## Quick Setup Script

```bash
#!/bin/bash

echo "=== GPG Key Setup for Maven Central ==="
echo ""

# Generate key
echo "1. Generating GPG key..."
gpg --gen-key

# Get key ID
echo ""
echo "2. Your GPG keys:"
gpg --list-secret-keys --keyid-format=long

echo ""
read -p "Enter your KEY_ID (e.g., ABCD1234EFGH5678): " KEY_ID

# Publish public key
echo ""
echo "3. Publishing public key to keyservers..."
gpg --keyserver keyserver.ubuntu.com --send-keys $KEY_ID
gpg --keyserver keys.openpgp.org --send-keys $KEY_ID
gpg --keyserver pgp.mit.edu --send-keys $KEY_ID

# Export private key
echo ""
echo "4. Exporting private key..."
echo "Copy the following output and add it as GPG_PRIVATE_KEY in GitHub Secrets:"
echo "---"
gpg --armor --export-secret-keys $KEY_ID

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "1. Copy the private key above to GitHub Secrets as GPG_PRIVATE_KEY"
echo "2. Add your GPG passphrase to GitHub Secrets as GPG_PASSPHRASE"
echo "3. Generate Sonatype token at https://central.sonatype.com/"
echo "4. Add Sonatype credentials to GitHub Secrets"
```

Save this as `setup-gpg.sh`, make it executable (`chmod +x setup-gpg.sh`), and run it.

---

## Support

If you encounter issues:
1. Check the [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
2. Review GitHub Actions logs for detailed error messages
3. Contact Sonatype support if needed

