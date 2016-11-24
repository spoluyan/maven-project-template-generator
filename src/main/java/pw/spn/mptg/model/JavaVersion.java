package pw.spn.mptg.model;

public enum JavaVersion {
    SEVEN("1.7"), EIGHT("1.8");

    private final String name;

    private JavaVersion(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
