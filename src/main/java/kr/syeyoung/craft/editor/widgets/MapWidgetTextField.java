package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetAnvil;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MapWidgetTextField extends MapWidget {

    private boolean allowColor = false;
    private MapFont font;


    public MapWidgetTextField(boolean color) {
        setFocusable(true);
        this.allowColor = color;
        this.font = MapFont.MINECRAFT;
        setSize(100,16);
        setClipParent(true);
    }

    public void setFont(MapFont font) {
        this.font = font;
    }

    @Getter
    private String value = "";

    public void setValue(String value) {
        this.value = value;
        this.coloredText.setText(value);
        invalidate();;
    }

    MapWidgetSubmitText anvilWidget = new MapWidgetSubmitText() {
        @Override
        public void onAccept(String str) {
            value = allowColor ? ChatColor.translateAlternateColorCodes('&', str) : str;
            coloredText.setText(value);
            MapWidgetTextField.this.setFocusable(true);
            MapWidgetTextField.this.invalidate();
            MapWidgetTextField.this.onValueUpdated();

        }
        public void onCancel() {
            MapWidgetTextField.this.setFocusable(true);
            MapWidgetTextField.this.invalidate();
            MapWidgetTextField.this.onValueUpdated();
        }
    };
    MapWidgetColoredText coloredText = new MapWidgetColoredText();

    @Override
    public void onAttached() {
        super.onAttached();
        clearWidgets();
        anvilWidget.setDescription("enter name");
        coloredText.setBounds(2,2,getWidth() -4, getHeight() -4);
        coloredText.setDefaultColor(ChatColor.BLACK);
        addWidget(anvilWidget);
        addWidget(coloredText);
    }

    @Override
    public void onBoundsChanged() {
        super.onBoundsChanged();
        coloredText.setBounds(2,2,getWidth() -4, getHeight() -4);
    }

    public void onValueUpdated() {

    }
    @Override
    public void onDraw() {
        this.view.fillRectangle(0,0,getWidth(),getHeight(), MapColorPalette.COLOR_WHITE);
        byte color = 0;
        if (isFocused()) {
            color = MapColorPalette.getColor(75,75,75);
        } else {
            color = MapColorPalette.getColor(150,150,150);
        }

        this.view.drawRectangle(0,0,getWidth(),getHeight(), color);
    }

    @Override
    public void onActivate() {
        setFocusable(false);
        anvilWidget.activate();
    }
}
