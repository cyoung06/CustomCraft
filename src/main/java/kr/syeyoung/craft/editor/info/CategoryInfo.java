package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.popups.PopupConfirm;
import kr.syeyoung.craft.editor.popups.PopupCreateCategory;
import kr.syeyoung.craft.editor.popups.PopupEditCategory;
import kr.syeyoung.craft.editor.popups.PopupWarning;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.util.NanumFont;
import org.bukkit.ChatColor;

import java.io.IOException;

public class CategoryInfo extends MapWidget {
    private RecipeCategory category;
    public CategoryInfo(RecipeCategory category) {
        setFocusable(true);
        setSize(0, 74);
        this.category = category;
    }

    private MapWidgetButton edit = new MapWidgetButton() {
        @Override
        public void onActivate() {
            PopupEditCategory pcc = new PopupEditCategory(category);
            ((EditorDisplay)this.display).addPopup(pcc);
        }
    };
    private MapWidgetButton delete = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            PopupConfirm pcc = new PopupConfirm("정말로 "+category.getName()+"을 삭제하시겠습니까?") {
                @Override
                public void onPopupClosed() {
                    if (isConfirm()) {
                        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
                        rm.unregisterRecipeCategory(category);




                        PopupWarning warning = new PopupWarning("정상적으로 카테고리를 삭제했다고 플러그인은 믿습니다");
                        ((EditorDisplay)CategoryInfo.this.display).addPopup(warning);
                        CategoryInfo.this.sendStatusChange("CATEGORY_DELETED");
                        ((EditorDisplay)CategoryInfo.this.display).goBack();
                    }
                }
            };
            ((EditorDisplay)this.display).addPopup(pcc);
        }
    };

    private MapWidgetButton save = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("저장");
        }

        @Override
        public void onActivate() {
            try {
                category.save(Craft.getPlugin(Craft.class).getDataFolder());
            } catch (IOException e) {
                e.printStackTrace();
            }

            PopupWarning warning = new PopupWarning("정상적으로 해당 카테고리만 저장하였다고 플러그인은 믿습니다");
            ((EditorDisplay)CategoryInfo.this.display).addPopup(warning);
        }
    };

    private MapWidgetColoredText id = new MapWidgetColoredText();
    private MapWidgetColoredText name = new MapWidgetColoredText();

    @Override
    public void onAttached() {
        super.onAttached();
        delete.setBounds(getWidth()- 85, 5, 80,64);
        delete.setText("카테고리 삭제");
        edit.setBounds(getWidth() - 154, 5, 64,64);
        edit.setIcon(this.display.loadTexture("kr/syeyoung/craft/res/settings.png"));
        save.setBounds(getWidth()-239,5,80,64);


        id.setText(category.getIdentifier());
        name.setText(category.getName());
        id.setBounds(114,6, getWidth() - 188, 20);
        name.setBounds(114,38, getWidth() - 188, 20);
        id.setDefaultColor(ChatColor.BLACK);
        name.setDefaultColor(ChatColor.BLACK);
        addWidget(edit);
        addWidget(id);
        addWidget(name);
        addWidget(delete);
        addWidget(save);
    }

    @Override
    public void onDraw() {
        this.view.drawItem(MapResourcePack.SERVER, category.getIcon(), 5,5,64,64);
        this.view.draw(NanumFont.BigMapNanumFont, 74, 6, MapColorPalette.COLOR_BLACK, "ID: ");
        this.view.draw(NanumFont.BigMapNanumFont, 74, 38, MapColorPalette.COLOR_BLACK, "이름: ");
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);
        if (event.getName().equals("CATEGORY_EDITED")) {
            name.setText(category.getName());
            invalidate();
        }
    }
}
