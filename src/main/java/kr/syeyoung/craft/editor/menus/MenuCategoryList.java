package kr.syeyoung.craft.editor.menus;

import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.info.CategoryList;
import kr.syeyoung.craft.editor.info.RecipeInfo;
import kr.syeyoung.craft.editor.popups.PopupWarning;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;

public class MenuCategoryList extends Menu {
    private CategoryList categoryList;

    public MenuCategoryList() {
        super();
        this.setTitle("카테고리 목록");
    }

    private MapWidgetFontSupportButton button = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("전체 저장");
        }

        @Override
        public void onActivate() {
            super.onActivate();
            Craft.getPlugin(Craft.class).saveData();

            PopupWarning warning = new PopupWarning("정상적으로 모든 데이터를 저장했다고 플러그인은 믿습니다");
            ((EditorDisplay) MenuCategoryList.this.display).addPopup(warning);
        }
    };

    @Override
    public void onAttached() {
        clearWidgets();

        super.onAttached();


        categoryList = new CategoryList();
        categoryList.setBounds(0,40, getWidth(), getHeight() - 40);

        button.setBounds(getWidth() - 65, 5, 60, 30);

        addWidget(categoryList);
        addWidget(button);
    }
}
