package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.awt.*;

public class MapWidgetAcordian extends MapWidget {
    private MapWidget content;
    private MapWidgetColoredText coloredText = new MapWidgetColoredText();
    private String title;

    private int maxHeight;
    @Getter
    private boolean open = false;

    public MapWidgetAcordian(MapWidget content, String title, int maxHeight) {
        this.content = content;
        this.title = title;
        this.maxHeight = maxHeight;
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

    @Override
    public void onActivate() {
        toggleOpen();
    }

    public void onStateToggle() {

    }

    public void onDraw() {
        if (this.open || this.isFocused()) {
            this.coloredText.setDefaultColor(ChatColor.WHITE);
            this.view.fillRectangle(0,0,getWidth(),40, MapColorPalette.getColor(75,75,75));
        } else {
            this.coloredText.setDefaultColor(ChatColor.BLACK);
            this.view.fillRectangle(0,0,getWidth(),40, MapColorPalette.getColor(150,150,150));
        }
        this.view.drawLine(0,0,getWidth(),0, MapColorPalette.COLOR_BLACK);
        this.view.drawLine(0,39,getWidth(),39, MapColorPalette.COLOR_BLACK);
    }

    public void setOpen(boolean open) {
        if (open != this.open) {
            this.open = open;
            if (!open) {
                this.setSize(getWidth(),40);
                try {
                    removeWidget(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                this.setSize(getWidth(),maxHeight);
                try {
                    content.setBounds(0,40,getWidth(), maxHeight - 40);
                    addWidget(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            onStateToggle();
        }

        invalidate();
    }

    public void toggleOpen() {
        this.setOpen(!open);
    }
}
