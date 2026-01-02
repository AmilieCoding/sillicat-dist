package sillicat.ui.designLanguage;

public final class Theme {
    private static int accent = 0xFFC226FF;

    public static int getAccent() {
        return accent;
    }

    public static void setAccent(int argb) {
        accent = argb;
    }

    /** Returns alpha component (0–255) */
    public static int getAccentAlpha() {
        return (accent >> 24) & 0xFF;
    }

    /** Returns accent color with overridden alpha */
    public static int getAccentWithAlpha(int alpha) {
        return (alpha & 0xFF) << 24 | (accent & 0x00FFFFFF);
    }
}