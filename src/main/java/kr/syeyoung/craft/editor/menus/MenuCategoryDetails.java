package kr.syeyoung.craft.editor.menus;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.info.CategoryInfo;
import kr.syeyoung.craft.editor.info.RecipeList;
import kr.syeyoung.craft.editor.info.RecipesView;
import kr.syeyoung.craft.editor.widgets.MapWidgetAcordian;

public class MenuCategoryDetails extends Menu {
    private RecipesView recipesView;
    private CategoryInfo categoryInfo;

    private RecipeCategory category;

    private MapWidgetAcordian infoAcordian, categoryAcordian;


    public MenuCategoryDetails(RecipeCategory category) {
        super();
        this.category = category;
        this.setTitle("카테고리 - "+category.getName());
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);
        if (event.getName().equals("CATEGORY_EDITED")) {
            this.setTitle("카테고리 - "+category.getName());
        }
    }

    @Override
    public void onAttached() {
        clearWidgets();

        super.onAttached();


        categoryInfo = new CategoryInfo(category);
        infoAcordian = new MapWidgetAcordian(categoryInfo, "카테고리 정보", categoryInfo.getHeight() + 40) {
            @Override
            public void onStateToggle() {
                super.onStateToggle();
                categoryAcordian.setPosition(0,40 + getHeight());
                if (this.isOpen())
                    categoryAcordian.setOpen(false);
            }
        };
        infoAcordian.setPosition(0,40);
        infoAcordian.setSize(getWidth(), infoAcordian.getHeight());

        recipesView = new RecipesView(category);
        categoryAcordian = new MapWidgetAcordian(recipesView, "레시피 목록", getHeight() - 80) {
            @Override
            public void onStateToggle() {
                super.onStateToggle();
                if (this.isOpen())
                    infoAcordian.setOpen(false);
            }
        };
        categoryAcordian.setPosition(0,40 + infoAcordian.getHeight());
        categoryAcordian.setSize(getWidth(), categoryAcordian.getHeight());

        addWidget(infoAcordian);
        addWidget(categoryAcordian);
    }
}
