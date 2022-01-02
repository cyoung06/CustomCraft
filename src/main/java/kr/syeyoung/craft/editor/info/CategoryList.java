package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.popups.PopupCreateCategory;

import java.util.List;

public class CategoryList extends MapWidget {

    private static final int GAP = 10;

    public CategoryList() {
        setClipParent(true);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.repopulate();
    }

    private MapWidgetButton newCategory = new MapWidgetButton() {
        @Override
        public void onActivate() {
            PopupCreateCategory pcc = new PopupCreateCategory();
            ((EditorDisplay)this.display).addPopup(pcc);
        }
    };
    private List<RecipeCategory> categories;

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);

        if (event.getName().equals("CATEGORY_CREATED")) {
            repopulate();
        } else if (event.getName().equals("CATEGORY_DELETED")) {
            repopulate();
        } else if (event.getName().equals("CATEGORY_EDITED")) {
            repopulate();
        }
    }

    public void repopulate() {
        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
        categories = rm.getRecipeCategories();

        int width = getWidth();
        int height = getHeight();
        int currentX = GAP;
        int currentY = GAP;

        clearWidgets();

        for (RecipeCategory category : categories) {
            CategoryListElement element = new CategoryListElement(category);
            element.setPosition(currentX,currentY);
            currentX += element.getWidth() + GAP;
            if ((currentX + element.getWidth()) > width) {
                currentY += element.getHeight() + GAP;
                currentX = GAP;
            }

            addWidget(element);
        }

        newCategory.setIcon(this.display.loadTexture("kr/syeyoung/craft/res/new.png"));
        newCategory.setBounds(currentX, currentY, 160,160);
        addWidget(newCategory);
    }
}
