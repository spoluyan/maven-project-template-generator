package pw.spn.mptg.model;

enum LicenseDistribution {
    REPO, MANUAL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
