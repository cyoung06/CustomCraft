package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.popups.PopupClickItem;
import kr.syeyoung.craft.editor.popups.PopupConfirm;
import kr.syeyoung.craft.editor.popups.PopupEditCategory;
import kr.syeyoung.craft.editor.popups.PopupWarning;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.editor.widgets.MapWidgetScrollBar;
import kr.syeyoung.craft.util.NanumFont;
import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class RecipeResultItemInfo extends MapWidget {
    private Recipe recipe;
    private ItemStack item;
    public RecipeResultItemInfo(Recipe r, ItemStack item) {
        setClipParent(true);
        this.recipe = r;
        this.item = item;
    }

    private MapWidgetColoredText id_data = new MapWidgetColoredText(); // AND DATA
    private MapWidgetColoredText type = new MapWidgetColoredText();
    private MapWidgetColoredText durability = new MapWidgetColoredText();
    private MapWidgetColoredText amount = new MapWidgetColoredText();
    private MapWidgetColoredText displayName = new MapWidgetColoredText();
    private MapWidgetColoredText lore = new MapWidgetColoredText();
    private MapWidgetColoredText enchantments = new MapWidgetColoredText();
    private MapWidgetColoredText item_flags = new MapWidgetColoredText();

    private MapWidgetButton change = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("아이템 변경");
        }

        @Override
        public void onActivate() {
            PopupClickItem warning = new PopupClickItem(item) {
                @Override
                public void onPopupClosed() {
                    recipe.getResultItems().set(recipe.getResultItems().indexOf(item), this.getSelected());
                    item = this.getSelected();
                    RecipeResultItemInfo.this.invalidate();
                    RecipeResultItemInfo.this.sendStatusChange("RESULT_ITEM_CHANGED");
                    setTexts();
                    reSize();
                }
            };
            ((EditorDisplay)this.display).addPopup(warning);
        }
    }, delete = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("삭제");
        }

        @Override
        public void onActivate() {
            PopupConfirm pcc = new PopupConfirm("정말로 해당 결과 아이템을 삭제하시겠습니까?") {
                @Override
                public void onPopupClosed() {
                    if (isConfirm()) {
                        recipe.getResultItems().remove(item);

                        RecipeResultItemInfo.this.sendStatusChange("RESULT_ITEM_DELETED");
                        RecipeResultItemInfo.this.removeWidget();
                    }
                }
            };
            ((EditorDisplay)this.display).addPopup(pcc);
        }
    }, get = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("아이템 얻기");
        }

        @Override
        public void onActivate() {
            getLastClicker().getInventory().addItem(item);
        }
    };

    public void setTexts() {
        id_data.setText(item.getData().getItemTypeId() + " - " + item.getData().getData());
        type.setText(item.getType().name());
        durability.setText(String.valueOf(item.getDurability()));
        amount.setText(String.valueOf(item.getAmount()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            displayName.setText(meta.hasDisplayName() ? meta.getDisplayName() : meta.hasLocalizedName() ? meta.getLocalizedName() : "null");
            if (meta.hasLore())
                lore.setText(String.join("§r\n§r",meta.getLore().<String>toArray(new String[0])));
            else
                lore.setText("");
            enchantments.setText(WordUtils.wrap(meta.getEnchants().entrySet().stream().map(e -> e.getKey().getName() + "-" + e.getValue()).collect(Collectors.joining(" ")),(getWidth() - 80) / 15));
            item_flags.setText(meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.joining(" ")));
        } else {
            displayName.setText("meta does not exist");
            lore.setText("meta does not exist");
            enchantments.setText("meta does not exist");
            item_flags.setText("meta does not exist");
        }
    }

    @Override
    public void onDraw() {
        this.view.clear();
        this.view.drawItem(MapResourcePack.SERVER, item, 5,5, 64,64);
        this.view.draw(NanumFont.BigMapNanumFont, 74, id_data.getY(), MapColorPalette.COLOR_BLACK, "아이템 ID");
        this.view.draw(NanumFont.BigMapNanumFont, 74, type.getY(), MapColorPalette.COLOR_BLACK, "타입");
        this.view.draw(NanumFont.BigMapNanumFont, 74, durability.getY(), MapColorPalette.COLOR_BLACK, "내구도");
        this.view.draw(NanumFont.BigMapNanumFont, 5, amount.getY(), MapColorPalette.COLOR_BLACK, "갯수");
        this.view.draw(NanumFont.BigMapNanumFont, 5, displayName.getY(), MapColorPalette.COLOR_BLACK, "이름");
        this.view.draw(NanumFont.BigMapNanumFont, 5, lore.getY(), MapColorPalette.COLOR_BLACK, "로어");
        this.view.draw(NanumFont.BigMapNanumFont, 5, enchantments.getY(), MapColorPalette.COLOR_BLACK, "인챈트");
        this.view.draw(NanumFont.BigMapNanumFont, 5, item_flags.getY(), MapColorPalette.COLOR_BLACK, "아이템 속성");
    }

    private MapWidgetScrollBar scrollBar = new MapWidgetScrollBar() {
        @Override
        public void onUpdate() {
            reSize();
        }
    };

    @Override
    public void onAttached() {
        clearWidgets();
        addWidget(id_data);
        addWidget(type);
        addWidget(durability);
        addWidget(amount);
        addWidget(displayName);
        addWidget(lore);
        addWidget(enchantments);
        addWidget(item_flags);
        addWidget(change);
        addWidget(delete);
        addWidget(scrollBar);
        addWidget(get);
        setTexts();
        reSize();
    }

    @Override
    public void onDetached() {
        clearWidgets();
    }

    public void reSize() {
        int y = 5;
        int x = 80;

        change.setBounds(getWidth() - 110, 0, 75, 30);
        get.setBounds(getWidth() - 190, 0, 75, 30);

        id_data.setBounds(x + 74,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 264,id_data.getHeight()); y += id_data.getHeight();
        type.setBounds(x+ 74,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 104,type.getHeight()); y += type.getHeight();
        durability.setBounds(x+ 74,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 104,durability.getHeight()); y += durability.getHeight();
        amount.setBounds(x,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 30,amount.getHeight()); y += amount.getHeight();
        displayName.setBounds(x,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 30,displayName.getHeight()); y += displayName.getHeight();
        lore.setBounds(x,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 30,lore.getText().split("\n").length * 20); y += lore.getText().split("\n").length * 20;
        enchantments.setBounds(x,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 30,enchantments.getText().split("\n").length * 20); y += enchantments.getText().split("\n").length * 20;
        item_flags.setBounds(x,y - (int)scrollBar.getCurrentValue(),getWidth()-x - 30,item_flags.getHeight()); y += item_flags.getHeight();
        delete.setBounds(getWidth() - 110, y - (int)scrollBar.getCurrentValue(), 75, 30); y+= delete.getHeight();

        scrollBar.setBounds(getWidth() -30,0,30,getHeight());
        scrollBar.setMin(0);
        scrollBar.setMax(Math.max(getHeight(), y));
        scrollBar.setThumbSize(getHeight());
        scrollBar.setDirection(MapWidgetScrollBar.ScrollBarDirection.VERTICAL);
    }
}
