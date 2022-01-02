package kr.syeyoung.craft.util;

import com.bergerkiller.bukkit.common.map.MapFont;
import kr.syeyoung.craft.Craft;

import java.awt.*;

public class NanumFont {
    public static final Font NanumFont;
    public static final MapFont<Character> BigMapNanumFont;
    public static final MapFont<Character> NormalMapNanumFont;

    static {
        Font f = null;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, Craft.getPlugin(Craft.class).getResource("kr/syeyoung/craft/res/NanumGothic.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        NanumFont = f;

        BigMapNanumFont = MapFont.fromJavaFont(NanumFont.deriveFont(12.0f));
        NormalMapNanumFont = MapFont.fromJavaFont(NanumFont.deriveFont(10.0f));
    }

    public static MapFont<Character> getNanumFont(float size, int style) {
        return MapFont.fromJavaFont(NanumFont.deriveFont(style, size));
    }
}
