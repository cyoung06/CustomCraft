package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.widgets.MapWidgetScrollBar;

import java.util.List;

public class RecipeList extends MapWidget {

    private static final int GAP = 10;

    private RecipeCategory category;

    private MapWidgetScrollBar scrollBar = new MapWidgetScrollBar() {
        @Override
        public void onUpdate() {
            RecipeList.this.repopulate();
        }
    };

    public RecipeList(RecipeCategory category) {
        this.category = category;
        setFocusable(true);
    }

    private int columns, rows;

    @Override
    public void onAttached() {
        super.onAttached();

        reloadData();
        recalculateScrollbar();
        this.repopulate();
        setClipParent(true);
    }

    @Override
    public void onBoundsChanged() {
        super.onBoundsChanged();

        recalculateScrollbar();
    }

    public void recalculateScrollbar() {

        scrollBar.setMin(0);
        columns = (getWidth() + GAP) / (64 + GAP);
        rows = (int) Math.ceil(recipes.size() / (double)columns);

        scrollBar.setMax(Math.max(3, rows * 3));
        scrollBar.setDirection(MapWidgetScrollBar.ScrollBarDirection.VERTICAL);
        scrollBar.setButtons(true);
        scrollBar.setThumbSize(3);
        scrollBar.setBounds(getWidth()- 30,0,30,getHeight());
    }

    private List<Recipe> recipes;

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);

        if (event.getName().equals("RECIPE_CREATED") || event.getName().equals("RECIPE_DELETED")) {
            recalculateScrollbar();

            repopulate();
        }
    }

    public void reloadData() {
        recipes = category.getRecipeSet();
    }

    public void repopulate() {
        clearWidgets();
        addWidget(scrollBar);
        double currentValue = scrollBar.getCurrentValue();
        int startRow = Math.floorDiv((int) currentValue, 3);

        int yStart = (int) ((currentValue % 3) / 3.0 * (69.0));

        for (int y = startRow; y < (startRow + rows + (yStart == 0 ? 0 : 1)); y++) {
            for (int x = 0; x < columns; x++) {
                int index = y * columns + x;
                if (recipes.size() <= index) return;
                Recipe r = recipes.get(index);
                RecipeListElement element = new RecipeListElement(r);

                element.setPosition(x * 69 + 5, (y - startRow) * 69 + 5 - yStart);
                addWidget(element);
            }
        }
    }
}
