package pw.spn.mptg.model.string;

public final class CapitalizedString {
    private final String original;

    public CapitalizedString(String original) {
        this.original = original;
    }

    @Override
    public String toString() {
        String inLowerCase = original.toLowerCase();
        return Character.toUpperCase(inLowerCase.charAt(0)) + inLowerCase.substring(1);
    }
}
