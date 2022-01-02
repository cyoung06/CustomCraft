package kr.syeyoung.craft.editor.popups;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.editor.widgets.MapWidgetTextField;
import kr.syeyoung.craft.util.NanumFont;

import java.awt.*;
import java.util.regex.Pattern;

public class PopupCreateCategory extends Popup {
    public PopupCreateCategory() {
        super();
        setSize(300, 175);
        setDepthOffset(4);
    }

    private MapWidgetTextField identifierField = new MapWidgetTextField(false);
    private MapWidgetTextField nameField=  new MapWidgetTextField(true);
    private MapWidgetButton button = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            checkValidity();
        }
    };

    private Pattern idPattern = Pattern.compile("\\w{5,}");

    private void checkValidity() {
        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
        if (!idPattern.matcher(identifierField.getValue()).matches()) {
            PopupWarning warning = new PopupWarning("ID는 영어-숫자만 포함한 5자리 이상이여야 합니다");
            ((EditorDisplay)this.display).addPopup(warning);
            return;
        }
        RecipeCategory rc = rm.getRecipeCategory(identifierField.getValue());
        if (rc != null) {
            PopupWarning warning = new PopupWarning("이미 그 ID를 가진 카테고리가 존재합니다");
            ((EditorDisplay)this.display).addPopup(warning);
            return;
        }

        RecipeCategory category = new RecipeCategory(identifierField.getValue());
        category.setName(nameField.getValue());
        rm.registerRecipeCategory(category);

        PopupWarning warning = new PopupWarning("카테고리를 등록했습니다!") {
            @Override
            public void onPopupClosed() {
                PopupCreateCategory.this.close();
            }
        };
        ((EditorDisplay)this.display).addPopup(warning);
        PopupCreateCategory.this.sendStatusChange("CATEGORY_CREATED");
        return;
    }

    @Override
    public void onPopupClosed() { }

    @Override
    public void onActivate() {
        super.onActivate();

        identifierField.setBounds(70, 40, 220, 20);
        nameField.setBounds(70, 65, 220,20);
        button.setBounds(10,125,getWidth() - 20,40);
        button.setText("카테고리 생성");
        addWidget(identifierField);
        addWidget(nameField);
        addWidget(button);
    }

    @Override
    public void onDraw() {
        super.onDraw();

        this.view.draw(NanumFont.getNanumFont(24.0f, Font.PLAIN), 2, 2, MapColorPalette.COLOR_BLACK,  "새 카테고리");
        this.view.draw(NanumFont.BigMapNanumFont, 10, 40, MapColorPalette.COLOR_BLACK,  "ID");
        this.view.draw(NanumFont.BigMapNanumFont, 10, 65, MapColorPalette.COLOR_BLACK, "이름");
        this.view.draw(NanumFont.BigMapNanumFont, 10,95, MapColorPalette.COLOR_BLACK, "ID는 영어-숫자만 포함한 5자리 이상이여야 합니다");
    }
}
