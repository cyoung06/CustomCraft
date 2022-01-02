package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipeItem;
import kr.syeyoung.craft.editor.widgets.MapWidgetItemList;
import kr.syeyoung.craft.editor.widgets.MapWidgetNamedDivider;
import kr.syeyoung.craft.editor.widgets.MapWidgetScrollBar;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RecipeItemChooser extends MapWidget {
    private Recipe recipe;

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);
        if (event.getName().equals("RESULT_ITEM_ADDED") || event.getName().equals("RESULT_ITEM_DELETED") || event.getName().equals("RESULT_ITEM_CHANGED")) {
            res_items.rePopulate();
            onRecalcPositions();
        }
        if (event.getName().equals("REQUIRED_ITEM_ADDED") || event.getName().equals("REQUIRED_ITEM_DELETED") || event.getName().equals("REQUIRED_ITEM_CHANGED")) {
            req_items.rePopulate();
            onRecalcPositions();
        }
    }

    public RecipeItemChooser(Recipe recipe) {
        setClipParent(true);
        this.recipe = recipe;

        this.req_items = new MapWidgetItemList<RecipeItem>(() -> {
            RecipeItem r = new RecipeItem(recipe);
            r.setRepresentation(new ItemStack(Material.STONE));
            recipe.getRequiredItems().add(r);
            req_items.rePopulate();
            onRecalcPositions();
        }, (recipeItem, index) -> {
            this.setView(new RecipeRequiredItemInfo(recipe, recipeItem));
        }, (recipeItem, index) -> {
            return recipeItem.getRepresentation();
        }, () -> {
            return new ArrayList<>(recipe.getRequiredItems());
        });

        this.res_items = new MapWidgetItemList<ItemStack>(() -> {
            ItemStack r = new ItemStack(Material.STONE);
            recipe.getResultItems().add(r);
            res_items.rePopulate();
            onRecalcPositions();
        }, (recipeItem, index) -> {
            this.setView(new RecipeResultItemInfo(recipe, recipeItem));
        }, (recipeItem, index) -> {
            return recipeItem;
        }, () -> {
            return recipe.getResultItems();
        });
    }

    private MapWidgetNamedDivider info = new MapWidgetNamedDivider("레시피 정보") {
        @Override
        public void onFocus() {
            super.onFocus();
            setView(new RecipeInfo(recipe));
        }
    };
    private MapWidgetNamedDivider requires = new MapWidgetNamedDivider("필요 아이템들") {
        @Override
        public void onFocus() {
            super.onFocus();
            setView(null);
        }
    };
    private MapWidgetNamedDivider results = new MapWidgetNamedDivider("결과 아이템들") {
        @Override
        public void onFocus() {
            super.onFocus();
            setView(null);
        }
    };

    private MapWidgetItemList req_items, res_items; // DEFINE CLASSES.

    private MapWidgetScrollBar scrollBar = new MapWidgetScrollBar() {
        @Override
        public void onUpdate() {
            onRecalcPositions();
        }
    };

    @Override
    public void onAttached() {
        clearWidgets();
        super.onAttached();

        addWidget(info);
        addWidget(requires);
        addWidget(req_items);
        addWidget(results);
        addWidget(res_items);
        addWidget(scrollBar);
        onRecalcPositions();
    }

    public void setView(MapWidget widget) {

    }

    @Override
    public void onBoundsChanged() {
        onRecalcPositions();
    }

    public void onRecalcPositions() {
        int current_y = 0;
        info.setBounds(0,current_y-  (int) scrollBar.getCurrentValue(),getWidth() - 30,info.getHeight()); current_y += info.getHeight();
        requires.setBounds(0,current_y-  (int) scrollBar.getCurrentValue(),getWidth()- 30,requires.getHeight()); current_y += requires.getHeight();
        req_items.setBounds(0,current_y-  (int) scrollBar.getCurrentValue(), getWidth()- 30, req_items.getHeight()); current_y += req_items.getHeight();
        results.setBounds(0,current_y-  (int) scrollBar.getCurrentValue(), getWidth()- 30, results.getHeight()); current_y += results.getHeight();
        res_items.setBounds(0,current_y-  (int) scrollBar.getCurrentValue(),getWidth()- 30,res_items.getHeight()); current_y += res_items.getHeight();

        scrollBar.setBounds(getWidth() -30,0,30,getHeight());
        scrollBar.setMin(0);
        scrollBar.setMax(Math.max(getHeight(), current_y));
        scrollBar.setThumbSize(getHeight());
        scrollBar.setDirection(MapWidgetScrollBar.ScrollBarDirection.VERTICAL);
    }
}
