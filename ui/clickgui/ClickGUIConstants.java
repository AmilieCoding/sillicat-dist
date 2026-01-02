package sillicat.ui.clickgui;

import sillicat.Sillicat;
import sillicat.util.font.CustomFontRenderer;

public final class ClickGUIConstants {
    public static final int HEADER_H = 18;
    public static final int PAD = 4;

    // Module row sizing.
    public static final int ROW_H = 16;
    public static final int ROW_GAP = 2;
    public static final int BODY_PAD = 4;

    public static final CustomFontRenderer TITLE_FONT = Sillicat.INSTANCE.getFontManager().getInter().size(16);
    public static final CustomFontRenderer SMALL_FONT = Sillicat.INSTANCE.getFontManager().getInter().size(12);

    private ClickGUIConstants() {

    }
}
