package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import lombok.Getter;

public class MapWidgetCheckbox extends MapWidget {


    @Getter
    private boolean seleceted;

    public void setSeleceted(boolean seleceted) {
        this.seleceted = seleceted;
        invalidate();;
    }

    @Getter
    private String text;

    private MapWidgetColoredText color_text = new MapWidgetColoredText();

    public void setText(String text) {
        this.text= text;
        color_text.setText(text);
        invalidate();
    }

    public MapWidgetCheckbox(String text, boolean selected) {
        setClipParent(true);
        this.text= text;
        color_text.setText(text);
        this.seleceted = selected;
        setFocusable(true);
    }

    @Override
    public void onAttached() {
        clearWidgets();
        color_text.setBounds(getHeight(), 0,getWidth()-getHeight(), getHeight());
        addWidget(color_text);
    }

    @Override
    public void onDraw() {
        if (seleceted) {
            this.view.fillRectangle(2,2,getHeight()-4, getHeight()-4, MapColorPalette.COLOR_BLUE);
        } else {
            this.view.fillRectangle(2,2,getHeight()-4, getHeight()-4, MapColorPalette.COLOR_WHITE);
        }
        this.view.drawRectangle(2,2,getHeight()-4, getHeight()-4, isFocused() ? MapColorPalette.COLOR_BLACK : MapColorPalette.getColor(100,100,100));
    }

    @Override
    public void onActivate() {
        setSeleceted(!seleceted);
        onValueToggle();
    }

    public void onValueToggle() {

    }
}
