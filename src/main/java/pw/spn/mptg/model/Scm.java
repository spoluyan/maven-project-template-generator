package pw.spn.mptg.model;

public final class Scm {
    private ScmType type;
    private String relativePath;

    public Scm(ScmType type, String relativePath) {
        this.type = type;
        this.relativePath = relativePath;
    }

    public ScmType getType() {
        return type;
    }

    public void setType(ScmType type) {
        this.type = type;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getUrl() {
        return String.format(type.getUrlTemplate(), relativePath);
    }

    public String getConnection() {
        return String.format(type.getConnectionTemplate(), relativePath);
    }

    public String getDeveloperConnection() {
        return String.format(type.getDeveloperConnectionTemplate(), relativePath);
    }
}
