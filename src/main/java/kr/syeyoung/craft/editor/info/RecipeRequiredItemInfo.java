package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.data.ItemMatchStrategy;
import kr.syeyoung.craft.data.ItemResultStrategy;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipeItem;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.popups.PopupClickItem;
import kr.syeyoung.craft.editor.popups.PopupConfirm;
import kr.syeyoung.craft.editor.popups.PopupWarning;
import kr.syeyoung.craft.editor.widgets.*;
import kr.syeyoung.craft.util.NanumFont;
import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class RecipeRequiredItemInfo extends MapWidget {
    private Recipe recipe;
    private RecipeItem item;

    private ItemMatchStrategy[] strategies = ItemMatchStrategy.values();

    public RecipeRequiredItemInfo(Recipe r, RecipeItem item) {
        setClipParent(true);
        this.recipe = r;
        this.item = item;

        for (int i = 0; i < checkboxes.length; i++) {
            int finalI = i;
            checkboxes[i] = new MapWidgetCheckbox(strategies[finalI].getKorean(), item.getItemMatchStrategySet().contains(strategies[finalI])) {
                @Override
                public void onValueToggle() {
                    if (isSeleceted())
                        item.getItemMatchStrategySet().add(strategies[finalI]);
                    else
                        item.getItemMatchStrategySet().remove(strategies[finalI]);
                }
            };
        }

        result = new MapWidgetFontSupportButton() {
            private int selected_index = item.getResultStrategy().ordinal();
            private ItemResultStrategy[] strategies = ItemResultStrategy.values();

            @Override
            public void onAttached() {
                super.onAttached();
                selected_index = item.getResultStrategy().ordinal();
                setText(strategies[selected_index].name());
            }

            @Override
            public void onActivate() {
                selected_index ++;
                if (selected_index == strategies.length)
                    selected_index = 0;

                setText(strategies[selected_index].name());
                item.setResultStrategy(strategies[selected_index]);
            }
        };
    }

    private MapWidgetColoredText id_data = new MapWidgetColoredText(); // AND DATA
    private MapWidgetColoredText type = new MapWidgetColoredText();
    private MapWidgetColoredText durability = new MapWidgetColoredText();
    private MapWidgetColoredText amount = new MapWidgetColoredText();
    private MapWidgetColoredText displayName = new MapWidgetColoredText();
    private MapWidgetColoredText lore = new MapWidgetColoredText();
    private MapWidgetColoredText enchantments = new MapWidgetColoredText();
    private MapWidgetColoredText item_flags = new MapWidgetColoredText();

    private MapWidgetCheckbox[] checkboxes = new MapWidgetCheckbox[strategies.length];

    private MapWidgetButton change = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("아이템 변경");
        }

        @Override
        public void onActivate() {
            PopupClickItem warning = new PopupClickItem(item.getRepresentation()) {
                @Override
                public void onPopupClosed() {
                    item.setRepresentation(this.getSelected());
                    RecipeRequiredItemInfo.this.invalidate();
                    RecipeRequiredItemInfo.this.sendStatusChange("REQUIRED_ITEM_CHANGED");
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
                        recipe.getRequiredItems().remove(item);

                        RecipeRequiredItemInfo.this.sendStatusChange("REQUIRED_ITEM_DELETED");
                        RecipeRequiredItemInfo.this.removeWidget();
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
            getLastClicker().getInventory().addItem(item.getRepresentation());
        }
    }, result, change_result = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("변경 아이템 바꾸기");
        }

        @Override
        public void onActivate() {
            PopupClickItem warning = new PopupClickItem(item.getResult()) {
                @Override
                public void onPopupClosed() {
                    item.setResult(this.getSelected());
                    RecipeRequiredItemInfo.this.invalidate();
                    setTexts();
                    reSize();
                }
            };
            ((EditorDisplay)this.display).addPopup(warning);
        }
    }, get_result = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            setText("변경 아이템 얻기");
        }

        @Override
        public void onActivate() {
            getLastClicker().getInventory().addItem(item.getResult());
        }
    };

    private MapWidgetTextField required_ammount = new MapWidgetTextField(false) {
        @Override
        public void onValueUpdated() {
            try {
                item.setAmmount(Integer.parseInt(getValue()));
            } catch (Exception e) {
                PopupWarning warning = new PopupWarning("올바른 숫자를 입력해주세요");
                ((EditorDisplay)RecipeRequiredItemInfo.this.display).addPopup(warning);
            }
        }
    };

    public void setTexts() {
        ItemStack item = this.item.getRepresentation();
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
        required_ammount.setValue(this.item.getAmmount() +"");

        for (int i = 0; i < checkboxes.length; i++)
            checkboxes[i].setSeleceted(this.item.getItemMatchStrategySet().contains(strategies[i]));
    }

    @Override
    public void onDraw() {
        this.view.clear();
        this.view.drawItem(MapResourcePack.SERVER, item.getRepresentation(), 5,5, 64,64);
        this.view.draw(NanumFont.BigMapNanumFont, 74, id_data.getY(), MapColorPalette.COLOR_BLACK, "아이템 ID");
        this.view.draw(NanumFont.BigMapNanumFont, 74, type.getY(), MapColorPalette.COLOR_BLACK, "타입");
        this.view.draw(NanumFont.BigMapNanumFont, 74, durability.getY(), MapColorPalette.COLOR_BLACK, "내구도");
        this.view.draw(NanumFont.BigMapNanumFont, 5, amount.getY(), MapColorPalette.COLOR_BLACK, "갯수");
        this.view.draw(NanumFont.BigMapNanumFont, 5, displayName.getY(), MapColorPalette.COLOR_BLACK, "이름");
        this.view.draw(NanumFont.BigMapNanumFont, 5, lore.getY(), MapColorPalette.COLOR_BLACK, "로어");
        this.view.draw(NanumFont.BigMapNanumFont, 5, enchantments.getY(), MapColorPalette.COLOR_BLACK, "인챈트");
        this.view.draw(NanumFont.BigMapNanumFont, 5, item_flags.getY(), MapColorPalette.COLOR_BLACK, "아이템 속성");
        this.view.draw(NanumFont.BigMapNanumFont, 5, checkboxes[0].getY(), MapColorPalette.COLOR_BLACK, "아이템 매칭 조건");
        this.view.draw(NanumFont.BigMapNanumFont, 5, required_ammount.getY(), MapColorPalette.COLOR_BLACK, "아이템 매칭 갯수");
        if (item.getResult() != null)
            this.view.drawItem(MapResourcePack.SERVER, item.getResult(), 5,get_result.getY(), 64,64);
        else
            this.view.draw(this.display.loadTexture("kr/syeyoung/craft/res/not_selected.png"), 5, get_result.getY());
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
        addWidget(result);
        addWidget(change_result);
        addWidget(get_result);
        addWidget(required_ammount);
        for (int i = 0; i < checkboxes.length; i++)
            addWidget(checkboxes[i]);
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

        int x_temp = x + 20;
        for (int i =0 ; i < checkboxes.length; i++) {
            checkboxes[i].setBounds(x_temp, y-(int)scrollBar.getCurrentValue(), 150, 20); x_temp += 150;
            if (x_temp+150 >= getWidth()) {
                x_temp = x + 20;
                y += 25;
            }
        }

        required_ammount.setBounds(x + 20, y -  (int)scrollBar.getCurrentValue(), getWidth() - x - 50, 20); y += 25;

        result.setBounds(5, y-(int)scrollBar.getCurrentValue(), getWidth() - 40, 40); y += result.getHeight() + 5;

        change_result.setBounds(74, y-(int)scrollBar.getCurrentValue(), 150, 40);
        get_result.setBounds(229, y-(int)scrollBar.getCurrentValue(), 150, 40); y+= 79;

        delete.setBounds(getWidth() - 110, y - (int)scrollBar.getCurrentValue(), 75, 30); y+= delete.getHeight();

        scrollBar.setBounds(getWidth() -30,0,30,getHeight());
        scrollBar.setMin(0);
        scrollBar.setMax(Math.max(getHeight(), y));
        scrollBar.setThumbSize(getHeight());
        scrollBar.setDirection(MapWidgetScrollBar.ScrollBarDirection.VERTICAL);
    }
}
