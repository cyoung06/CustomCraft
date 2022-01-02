package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.Recipe;
import org.bukkit.inventory.ItemStack;

public class MapWidgetItemListElement extends MapWidget {
    private ItemStack item;
    public MapWidgetItemListElement(ItemStack item) {
        setFocusable(true);
        this.item = item;
        this.setSize(64,64);
        setClipParent(true);
    }

    @Override
    public void onDraw() {
        ItemStack item = this.item;
        this.view.drawItem(MapResourcePack.SERVER, item, 5,5,54,54);
        if (isFocused()) {
            this.view.drawRectangle(0,0, getWidth(),getHeight(), MapColorPalette.COLOR_BLACK);
        }
    }

    @Override
    public void onActivate() {

    }
}
