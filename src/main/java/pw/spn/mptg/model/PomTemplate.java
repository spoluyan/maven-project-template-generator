package pw.spn.mptg.model;

import java.util.ArrayList;
import java.util.List;

public class PomTemplate {
    // metadata
    private String groupId = "com.example";
    private String artifactId = "demo";
    private String packageName = "com.example.demo";
    private String version = "0.0.1-SNAPSHOT";
    private String name = "Demo";
    private String description = "Demo app";
    private Packaging packaging = Packaging.JAR;
    private JavaVersion javaVersion = JavaVersion.EIGHT;
    private ParentProject parent = ParentProject.NONE;
    private String url = "";
    private License license = License.NONE;

    // distribution management
    private DistributionManagement distributionManagement = new DistributionManagement("maven-repo-id",
            Repository.NONE);

    // scm
    private Scm scm = new Scm(ScmType.NONE, "");

    // developer
    private Developer developer = new Developer("", "", "", "");

    // integrations
    private boolean useJGitFlow = false;
    private boolean useTravisCI = false;
    private boolean useCoveralls = false;

    // dependencies
    private List<Dependency> preselectedDependencies = new ArrayList<>();

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Packaging getPackaging() {
        return packaging;
    }

    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }

    public DistributionManagement getDistributionManagement() {
        return distributionManagement;
    }

    public void setDistributionManagement(DistributionManagement distributionManagement) {
        this.distributionManagement = distributionManagement;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Scm getScm() {
        return scm;
    }

    public void setScm(Scm scm) {
        this.scm = scm;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    public ParentProject getParent() {
        return parent;
    }

    public void setParent(ParentProject parent) {
        this.parent = parent;
    }

    public boolean isUseJGitFlow() {
        return useJGitFlow;
    }

    public void setUseJGitFlow(boolean useJGitFlow) {
        this.useJGitFlow = useJGitFlow;
    }

    public boolean isUseTravisCI() {
        return useTravisCI;
    }

    public void setUseTravisCI(boolean useTravisCI) {
        this.useTravisCI = useTravisCI;
    }

    public boolean isUseCoveralls() {
        return useCoveralls;
    }

    public void setUseCoveralls(boolean useCoveralls) {
        this.useCoveralls = useCoveralls;
    }

    public List<Dependency> getPreselectedDependencies() {
        return preselectedDependencies;
    }

    public void setPreselectedDependencies(List<Dependency> preselectedDependencies) {
        this.preselectedDependencies = preselectedDependencies;
    }

    public String getPackageName() {
        StringBuilder filtered = new StringBuilder();
        char[] chars = packageName.toCharArray();
        for (char character : chars) {
            if (Character.isLetterOrDigit(character) || character == '.') {
                filtered.append(character);
            }
        }
        return filtered.toString();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
