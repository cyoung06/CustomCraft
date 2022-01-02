package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.menus.MenuCategoryDetails;
import kr.syeyoung.craft.editor.menus.MenuItemDetails;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import org.bukkit.inventory.ItemStack;

public class RecipeListElement extends MapWidget {
    private Recipe recipe;
    public RecipeListElement(Recipe recipe) {
        setFocusable(true);
        setClipParent(true);

        this.recipe = recipe;
        this.setSize(64,64);
    }

    private MapWidgetColoredText coloredText = new MapWidgetColoredText();

    @Override
    public void onFocus() {
        super.onFocus();
        coloredText.setAlignment(MapFont.Alignment.MIDDLE);
        coloredText.setAutoSize(false);
        coloredText.setFontSize(8.0f);
        coloredText.setBounds(0, 52, getWidth(), 12);
        coloredText.setText(recipe.getIdentifier());
        addWidget(coloredText);
    }

    @Override
    public void onBlur() {
        super.onBlur();
        removeWidget(coloredText);
    }

    @Override
    public void onDraw() {
        ItemStack item = this.recipe.getResultItems().iterator().next();
        this.view.drawItem(MapResourcePack.SERVER, item, 8,2,48,48);
        if (isFocused()) {
            this.view.fillRectangle(0,52,getWidth(), 12, MapColorPalette.COLOR_BLACK);
            this.view.drawRectangle(0,0, getWidth(),getHeight(), MapColorPalette.COLOR_BLACK);
        }
    }

    @Override
    public void onActivate() {
        ((EditorDisplay)this.display).addMenu(new MenuItemDetails(recipe));
    }
}
