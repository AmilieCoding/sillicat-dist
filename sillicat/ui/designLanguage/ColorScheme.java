package sillicat.ui.designLanguage;

public enum ColorScheme {
    PANEL_BG(0xFF222222),
    PANEL_BG_ALT(0xDD222222),
    ROW_BG(0xDD555555),
    STANDARD_BG(0x90000000),

    TEXT_PRIMARY(0xFFFFFFFF),
    TEXT_SECONDARY(0xFFDDDDDD);

    // LEGACY ACCENT.
    // ACCENT(0xFFC226FF);

    private final int argb;

    ColorScheme(int argb) {
        this.argb = argb;
    }

    public int get() {
        return argb;
    }

    public int alpha(int a) {
        return (argb & 0x00FFFFFF) | (a << 24);
    }
}

