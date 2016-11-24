package pw.spn.mptg.model;

import pw.spn.mptg.model.string.CapitalizedString;

public enum Repository {
    NONE(null, null, null),
    SONATYPE("https://oss.sonatype.org/content/repositories/snapshots",
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/", 
            "https://oss.sonatype.org/");

    private final String snapshotRepository;
    private final String releaseRepository;
    private final String nexusUrl;

    private Repository(String snapshotRepository, String releaseRepository, String nexusUrl) {
        this.snapshotRepository = snapshotRepository;
        this.releaseRepository = releaseRepository;
        this.nexusUrl = nexusUrl;
    }

    @Override
    public String toString() {
        return new CapitalizedString(name()).toString();
    }

    public String getSnapshotRepository() {
        return snapshotRepository;
    }

    public String getReleaseRepository() {
        return releaseRepository;
    }

    public String getNexusUrl() {
        return nexusUrl;
    }
}
