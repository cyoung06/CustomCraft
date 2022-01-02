package kr.syeyoung.craft.editor.menus;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.editor.info.CategoryList;
import kr.syeyoung.craft.editor.info.RecipeItemChooser;

import java.awt.*;

public class MenuItemDetails extends Menu {
    private Recipe r;
    public MenuItemDetails(Recipe r) {
        super();
        this.r = r;
        this.setTitle("레시피 수정 §7- §r"+r.getCategory().getName()+"§7 - §r" + r.getIdentifier());
    }

    private MapWidget lastView;

    @Override
    public void onAttached() {
        clearWidgets();

        super.onAttached();


        RecipeItemChooser itemChooser = new RecipeItemChooser(r) {
            @Override
            public void setView(MapWidget widget) {
                if (widget == null) {
                    MenuItemDetails.this.removeWidget(lastView);
                    lastView = null;
                    return;
                }

                widget.setBounds(MenuItemDetails.this.getWidth() / 3 +2, 40,(MenuItemDetails.this.getWidth() * 2 / 3) -2, MenuItemDetails.this.getHeight() - 40);

                if (lastView == null) {
                    MenuItemDetails.this.addWidget(widget);
                } else {
                    MenuItemDetails.this.removeWidget(lastView);
                    MenuItemDetails.this.addWidget(widget);
                }
                lastView = widget;
            }
        };
        itemChooser.setBounds(0,40, getWidth() / 3, getHeight() - 40);

        addWidget(itemChooser);
    }
}
