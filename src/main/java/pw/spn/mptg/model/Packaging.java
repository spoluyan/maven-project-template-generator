package pw.spn.mptg.model;

public enum Packaging {
    JAR, WAR;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
