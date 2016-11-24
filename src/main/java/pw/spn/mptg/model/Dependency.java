package pw.spn.mptg.model;

public enum Dependency {
    OK_HTTP("com.squareup.okhttp3", "okhttp", "3.4.1", false),
    JACKSON_DATABIND("com.fasterxml.jackson.core", "jackson-databind", "2.8.3", false),
    SLF4J_API("org.slf4j", "slf4j-api", "1.7.21", false),
    MOCKITO("org.mockito", "mockito-all", "1.10.19", true),
    WIREMOCK("com.github.tomakehurst", "wiremock", "2.3.1", true);

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final boolean isTestScope;

    private Dependency(String groupId, String artifactId, String version, boolean isTestScope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.isTestScope = isTestScope;
    }

    @Override
    public String toString() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public boolean isTestScope() {
        return isTestScope;
    }
}
