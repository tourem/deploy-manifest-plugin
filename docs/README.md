# Developer Documentation

This directory contains technical documentation for developers working on or integrating the YAML configuration system.

## 📚 Available Documents

### Integration Guide
**[INTEGRATION_STEP_BY_STEP.md](./INTEGRATION_STEP_BY_STEP.md)**

Step-by-step guide to integrate the YAML configuration system into `GenerateDescriptorMojo`.

**Contents**:
- Adding ConfigurationResolver
- Building POM configuration
- Modifying execute() method
- Configuration logging
- Refactoring generation logic
- Testing checklist

**Time**: 1-2 hours  
**Difficulty**: Medium

---

### Implementation Details
**[YAML_CONFIG_SUMMARY.md](./YAML_CONFIG_SUMMARY.md)**

Complete technical documentation of the YAML configuration system implementation.

**Contents**:
- Project status and completion
- All implemented features
- Files created
- Usage examples
- Testing summary
- Integration status
- Architecture details

**Use for**: Understanding the complete implementation, architecture decisions, and technical details.

---

### Integration Test Report
**[INTEGRATION_TEST_REPORT.md](./INTEGRATION_TEST_REPORT.md)**

Comprehensive test report validating the YAML configuration system with real projects.

**Contents**:
- Test results from 3 real projects
- Feature validation
- Performance metrics
- Known limitations
- Production readiness assessment

**Use for**: Understanding test coverage, validation status, and production readiness.

---

## 🎯 Quick Links

**For Users**:
- [Main README](../README.md) - Quick start and overview
- [English Documentation](../doc-en.md) - Complete user guide
- [French Documentation](../doc.md) - Guide complet utilisateur

**For Developers**:
- [Integration Guide](./INTEGRATION_STEP_BY_STEP.md) - How to integrate
- [Implementation Details](./YAML_CONFIG_SUMMARY.md) - Technical documentation
- [Integration Test Report](./INTEGRATION_TEST_REPORT.md) - Test results and validation

**Examples**:
- [examples/](../examples/) - 5 complete YAML configuration examples

---

## 🔧 Getting Started

1. **Read** [YAML_CONFIG_SUMMARY.md](./YAML_CONFIG_SUMMARY.md) to understand the system
2. **Review** [INTEGRATION_TEST_REPORT.md](./INTEGRATION_TEST_REPORT.md) to see validation results
3. **Follow** [INTEGRATION_STEP_BY_STEP.md](./INTEGRATION_STEP_BY_STEP.md) to integrate
4. **Test** using the examples in [examples/](../examples/)

---

## 📁 Project Documentation Structure

### Root Directory
**User Documentation**:
- `README.md` - Main documentation with quick start
- `doc.md` - Complete French guide
- `doc-en.md` - Complete English guide
- `CHANGELOG.md` - Version history

**Project Documentation**:
- `CODE_OF_CONDUCT.md` - Community guidelines
- `CONTRIBUTING.md` - Contribution guide

### docs/ Directory (You are here)
**Developer Documentation**:
- `INTEGRATION_STEP_BY_STEP.md` - Integration guide
- `YAML_CONFIG_SUMMARY.md` - Technical documentation
- `INTEGRATION_TEST_REPORT.md` - Test results

### examples/ Directory
**Configuration Examples**:
- `.deploy-manifest-minimal.yml`
- `.deploy-manifest-standard-profile.yml`
- `.deploy-manifest-full-profile.yml`
- `.deploy-manifest-ci-profile.yml`
- `.deploy-manifest-complete.yml`
- `README.md`

---

**Questions?** Open an issue on GitHub.
