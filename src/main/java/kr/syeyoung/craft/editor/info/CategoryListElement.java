package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.menus.MenuCategoryDetails;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import org.bukkit.inventory.ItemStack;

public class CategoryListElement extends MapWidget {
    private RecipeCategory category;
    public CategoryListElement(RecipeCategory category) {
        setFocusable(true);
        this.category = category;
        this.setSize(160,160);
        setClipParent(true);
    }

    private MapWidgetColoredText coloredText = new MapWidgetColoredText();

    @Override
    public void onFocus() {
        super.onFocus();
        coloredText.setAlignment(MapFont.Alignment.MIDDLE);
        coloredText.setAutoSize(false);
        coloredText.setBounds(0, 140, getWidth(), 20);
        coloredText.setText(category.getName());
        addWidget(coloredText);
    }

    @Override
    public void onBlur() {
        super.onBlur();
        removeWidget(coloredText);
    }

    @Override
    public void onDraw() {
        ItemStack item = this.category.getIcon();
        this.view.drawItem(MapResourcePack.SERVER, item, 12,2,136,136);
        if (isFocused()) {
            this.view.fillRectangle(0,140,getWidth(), 20, MapColorPalette.COLOR_BLACK);
            this.view.drawRectangle(0,0,getWidth(),getHeight(), MapColorPalette.COLOR_BLACK);
        }
    }

    @Override
    public void onActivate() {
        super.onActivate();

        MenuCategoryDetails details = new MenuCategoryDetails(category);
        ((EditorDisplay)this.display).addMenu(details);
    }
}
