package pw.spn.mptg.model;

public enum License {
    NONE(null, null, null),
    MIT("MIT License",
            "http://www.opensource.org/licenses/mit-license.php",
            LicenseDistribution.REPO);

    private final String name;
    private final String url;
    private final LicenseDistribution distribution;

    private License(String name, String url, LicenseDistribution distribution) {
        this.name = name;
        this.url = url;
        this.distribution = distribution;
    }

    @Override
    public String toString() {
        return name == null ? "None" : name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public LicenseDistribution getDistribution() {
        return distribution;
    }
}
