package pw.spn.mptg.model;

import pw.spn.mptg.model.string.CapitalizedString;

public enum ScmType {
    NONE(null, null, null),
    GITHUB("https://github.com/%s",
            "scm:git:git://github.com/%s.git",
            "scm:git:git@github.com:%s.git"),
    BITBUCKET("https://bitbucket.org/%s",
            "scm:git:ssh://git@bitbucket.org/%s.git",
            "scm:git:ssh://git@bitbucket.org/%s.git");

    private final String urlTemplate;
    private final String connectionTemplate;
    private final String developerConnectionTemplate;

    private ScmType(String urlTemplate, String connectionTemplate, String developerConnectionTemplate) {
        this.urlTemplate = urlTemplate;
        this.connectionTemplate = connectionTemplate;
        this.developerConnectionTemplate = developerConnectionTemplate;
    }

    @Override
    public String toString() {
        return new CapitalizedString(name()).toString();
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public String getConnectionTemplate() {
        return connectionTemplate;
    }

    public String getDeveloperConnectionTemplate() {
        return developerConnectionTemplate;
    }
}
