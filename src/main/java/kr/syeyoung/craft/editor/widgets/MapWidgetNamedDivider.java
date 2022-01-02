package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import lombok.Getter;
import org.bukkit.ChatColor;

public class MapWidgetNamedDivider extends MapWidget {
    private MapWidgetColoredText coloredText = new MapWidgetColoredText();
    private String title;


    public MapWidgetNamedDivider(String title) {
        this.title = title;
        setFocusable(true);
        setSize(getWidth(), 40);
        setClipParent(true);
    }

    @Override
    public void onAttached() {
        coloredText.setBounds(5,5,getWidth()-10,30);
        coloredText.setText(title);
        coloredText.setFontSize(24.0f);
        coloredText.setAutoSize(false);
        coloredText.setDefaultColor(ChatColor.WHITE);
        addWidget(coloredText);
    }

    @Override
    public void onDetached() {
        clearWidgets();
    }

    public void onDraw() {
        if (this.isFocused()) {
            this.coloredText.setDefaultColor(ChatColor.WHITE);
            this.view.fillRectangle(0,0,getWidth(),40, MapColorPalette.getColor(75,75,75));
        } else {
            this.coloredText.setDefaultColor(ChatColor.BLACK);
            this.view.fillRectangle(0,0,getWidth(),40, MapColorPalette.getColor(150,150,150));
        }
        this.view.drawLine(0,0,getWidth(),0, MapColorPalette.COLOR_BLACK);
        this.view.drawLine(0,39,getWidth(),39, MapColorPalette.COLOR_BLACK);
    }

    @Override
    public void onActivate() {
        // CLICK!
    }
}
