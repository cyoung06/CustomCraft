package kr.syeyoung.craft.editor.popups;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.editor.widgets.MapWidgetTextField;
import kr.syeyoung.craft.util.NanumFont;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.regex.Pattern;

public class PopupEditCategory extends Popup {
    private RecipeCategory category;
    public PopupEditCategory(RecipeCategory category) {
        super();
        setSize(300, 214);
        setDepthOffset(4);
        this.category = category;
    }

    @Override
    public void onAttached() {
        super.onAttached();

        nameField.setBounds(70, 65, 220,20);
        button.setBounds(10,159,getWidth() - 20,40);
        changeItem.setBounds(139, 90, 100, 30);
        getItem.setBounds(139,125, 100, 30);

        nameField.setValue(category.getName());
        chosenItem = category.getIcon();
        button.setText("변경");
        getItem.setText("아이템 얻기");
        changeItem.setText("아이템 변경");
        addWidget(nameField);
        addWidget(button);
        addWidget(changeItem);
        addWidget(getItem);
    }

    private MapWidgetTextField nameField = new MapWidgetTextField(true);
    private ItemStack chosenItem;
    private MapWidgetButton button = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            checkValidity();
        }
    };
    private MapWidgetButton changeItem = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            itemChange();
        }
    };
    private MapWidgetButton getItem = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            this.getLastClicker().getInventory().addItem(chosenItem);
        }
    };

    private void itemChange() {
        PopupClickItem warning = new PopupClickItem(chosenItem) {
            @Override
            public void onPopupClosed() {
                chosenItem = this.getSelected();
                PopupEditCategory.this.invalidate();
            }
        };
        ((EditorDisplay)this.display).addPopup(warning);
    }

    private void checkValidity() {
        category.setName(nameField.getValue());
        category.setIcon(chosenItem);

        PopupWarning warning = new PopupWarning("카테고리를 변경했습니다!") {
            @Override
            public void onPopupClosed() {
                PopupEditCategory.this.close();
            }
        };
        ((EditorDisplay)this.display).addPopup(warning);
        PopupEditCategory.this.sendStatusChange("CATEGORY_EDITED");
        return;
    }

    @Override
    public void onPopupClosed() { }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @Override
    public void onDraw() {
        super.onDraw();

        this.view.draw(NanumFont.BigMapNanumFont, 70, 40, MapColorPalette.COLOR_BLACK, category.getIdentifier());
        this.view.draw(NanumFont.getNanumFont(24.0f, Font.PLAIN), 2, 2, MapColorPalette.COLOR_BLACK,  "새 카테고리");
        this.view.draw(NanumFont.BigMapNanumFont, 10, 40, MapColorPalette.COLOR_BLACK,  "ID");
        this.view.draw(NanumFont.BigMapNanumFont, 10, 65, MapColorPalette.COLOR_BLACK, "이름");
        this.view.draw(NanumFont.BigMapNanumFont, 10, 90, MapColorPalette.COLOR_BLACK, "아이템");
        this.view.drawItem(MapResourcePack.SERVER, chosenItem, 70, 90, 64,64);
    }
}
