package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.menus.MenuItemDetails;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import org.bukkit.ChatColor;

import java.util.UUID;

public class RecipesView extends MapWidget {
    private RecipeCategory category;

    private MapWidgetFontSupportButton button = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("레시피 추가");
        }

        @Override
        public void onActivate() {
            Recipe r = new Recipe(category, UUID.randomUUID().toString());
            RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
            category.getRecipeSet().add(r);
            rm.registerRecipe(r);

            RecipesView.this.sendStatusChange("RECIPE_CREATED");

            MenuItemDetails itemDetails = new MenuItemDetails(r);
            ((EditorDisplay)this.display).addMenu(itemDetails);
        }
    };

    private MapWidgetColoredText count = new MapWidgetColoredText();

    private RecipeList recipeList;

    public RecipesView(RecipeCategory category) {
        setClipParent(true);
        this.category = category;
        setFocusable(true);

        count.setText("해당 카테고리에는 "+category.getRecipeSet().size()+"개의 레시피가 존재합니다");
        count.setFontSize(12.0f);
        count.setAutoSize(false);
        count.setDefaultColor(ChatColor.BLACK);

        recipeList = new RecipeList(category);
    }

    @Override
    public void onAttached() {
        clearWidgets();
        super.onAttached();
        count.setBounds(5,5,getWidth()-55,30);
        button.setBounds(getWidth() - 95, 5, 90, 30);
        recipeList.setBounds(0,40,getWidth(),getHeight() - 40);
        addWidget(button);
        addWidget(count);
        addWidget(recipeList);
    }

    @Override
    public void onBoundsChanged() {
        super.onBoundsChanged();
        count.setBounds(5,5,getWidth()-55,30);
        button.setBounds(getWidth() - 95, 5, 90, 30);
        recipeList.setBounds(0,40,getWidth(),getHeight() - 40);
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        if (event.getName().equals("RECIPE_CREATED")) {
            count.setText("해당 카테고리에는 "+category.getRecipeSet().size()+"개의 레시피가 존재합니다");
        }
    }
}
