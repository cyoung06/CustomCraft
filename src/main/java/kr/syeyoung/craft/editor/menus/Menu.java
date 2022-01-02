package kr.syeyoung.craft.editor.menus;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.util.NanumFont;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.awt.*;

public class Menu extends MapWidget {

    protected String name;
    protected MapWidgetColoredText coloredText;

    private MapWidgetButton back = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("뒤로");
        }

        @Override
        public void onActivate() {
            ((EditorDisplay)this.display).goBack();
        }
    };

    public Menu() {
        setFocusable(true);
    }

    @Getter
    private boolean allowGoBack = false;

    public void setAllowGoBack(boolean allowGoBack) {
        if (allowGoBack != this.allowGoBack) {
            if (allowGoBack) {
                addWidget(back);
            } else{
                removeWidget(back);
            }
        }
        this.allowGoBack = allowGoBack;
        invalidate();
    }


    @Override
    public void onAttached() {
        super.onAttached();

        coloredText = new MapWidgetColoredText();
        coloredText.setDefaultColor(ChatColor.BLACK);
        coloredText.setAutoSize(false);
        coloredText.setText(name);
        coloredText.setFontSize(24.0f);
        coloredText.setBounds(5,5,getWidth() - 60,30);

        back.setBounds(getWidth() - 60,0, 60, 40);
        if (allowGoBack) {
            addWidget(back);
        }
        addWidget(coloredText);
        activate();
    }

    @Override
    public void onDetached() {
        clearWidgets();
    }

    public void setTitle(String title) {
        this.name = title;
        if (coloredText != null) coloredText.setText(title);
    }

    @Override
    public void onDraw() {
        this.view.drawLine(0,40, getWidth(), 40, MapColorPalette.COLOR_BLACK);
    }
}
