package pw.spn.mptg.model;

import java.util.Arrays;
import java.util.StringJoiner;

import pw.spn.mptg.model.string.CapitalizedString;

public enum ParentProject {
    NONE, SPRING_BOOT, WILDFLY_SWARM;

    @Override
    public String toString() {
        String[] parts = name().toLowerCase().split("_");
        StringJoiner joiner = new StringJoiner(" ");
        Arrays.stream(parts).map(CapitalizedString::new).map(CapitalizedString::toString).forEach(joiner::add);
        return joiner.toString();
    }
}
