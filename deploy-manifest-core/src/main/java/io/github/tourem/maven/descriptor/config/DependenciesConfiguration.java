package io.github.tourem.maven.descriptor.config;

/**
 * Configuration for dependencies analysis and tree.
 */
public class DependenciesConfiguration {
    
    private DependencyTreeConfiguration tree;
    private DependencyAnalysisConfiguration analysis;
    
    public DependenciesConfiguration() {
        this.tree = new DependencyTreeConfiguration();
        this.analysis = new DependencyAnalysisConfiguration();
    }
    
    public DependencyTreeConfiguration getTree() {
        return tree;
    }
    
    public void setTree(DependencyTreeConfiguration tree) {
        this.tree = tree;
    }
    
    public DependencyAnalysisConfiguration getAnalysis() {
        return analysis;
    }
    
    public void setAnalysis(DependencyAnalysisConfiguration analysis) {
        this.analysis = analysis;
    }
    
    @Override
    public String toString() {
        return "DependenciesConfiguration{" +
                "tree=" + tree +
                ", analysis=" + analysis +
                '}';
    }
}
