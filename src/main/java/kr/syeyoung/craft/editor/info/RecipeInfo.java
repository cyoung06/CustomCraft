package kr.syeyoung.craft.editor.info;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.editor.EditorDisplay;
import kr.syeyoung.craft.editor.popups.PopupConfirm;
import kr.syeyoung.craft.editor.popups.PopupWarning;
import kr.syeyoung.craft.editor.widgets.MapWidgetColoredText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.editor.widgets.MapWidgetTextField;
import kr.syeyoung.craft.util.NanumFont;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class RecipeInfo extends MapWidget {
    private Recipe recipe;
    public RecipeInfo(Recipe recipe) {
        this.setFocusable(true);
        this.recipe = recipe;
        setClipParent(true);
    }

    private MapWidgetColoredText category = new MapWidgetColoredText();
    private MapWidgetColoredText identifier = new MapWidgetColoredText();
    private MapWidgetFontSupportButton copyIdentifier = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("복사");
        }

        @Override
        public void onActivate() {
            Player p = getLastClicker();

            TextComponent clickToCopy = new TextComponent("클릭하여 ID 복사");
            clickToCopy.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(recipe.getIdentifier())}));
            clickToCopy.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, recipe.getIdentifier()));

            p.spigot().sendMessage(clickToCopy);
        }
    };

    private MapWidgetTextField takenTime = new MapWidgetTextField(false) {
        @Override
        public void onValueUpdated() {
            String value = getValue();
            String durationStr = "PT"+value.replace(" ","").toUpperCase();
            try {
                recipe.setRequiredTime(Duration.parse(durationStr));
            } catch (Exception e) {
                e.printStackTrace();
                PopupWarning warning = new PopupWarning("올바른 시간을 입력해주세요. 0h0m0s");
                ((EditorDisplay)this.display).addPopup(warning);
                Duration d = recipe.getRequiredTime();
                setValue(LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH'h'mm'm'ss's'")));
                return;
            }
            Duration d = recipe.getRequiredTime();
            takenTime.setValue(LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH'h'mm'm'ss's'")));
        }
    };
    private MapWidgetFontSupportButton delete = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("삭제");
        }

        @Override
        public void onActivate() {
            PopupConfirm pcc = new PopupConfirm("정말로 "+recipe.getIdentifier()+"를 삭제하시겠습니까?") {
                @Override
                public void onPopupClosed() {
                    if (isConfirm()) {
                        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
                        rm.unregisterRecipe(recipe);


                        PopupWarning warning = new PopupWarning("정상적으로 레시피를 삭제했다고 플러그인은 믿습니다");
                        ((EditorDisplay)RecipeInfo.this.display).addPopup(warning);
                        RecipeInfo.this.sendStatusChange("RECIPE_DELETED");
                        ((EditorDisplay)RecipeInfo.this.display).goBack();
                    }
                }
            };
            ((EditorDisplay)this.display).addPopup(pcc);
        }
    };


    @Override
    public void onAttached() {
        super.onAttached();
        clearWidgets();
        category.setText(recipe.getCategory().getName());
        identifier.setText(recipe.getIdentifier());
        Duration d = recipe.getRequiredTime();
        takenTime.setValue(LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH'h'mm'm'ss's'")));

        identifier.setDefaultColor(ChatColor.BLACK);

        category.setDefaultColor(ChatColor.BLACK);

        identifier.setBounds(90,5,getWidth()-155,20);
        category.setBounds(90,30,getWidth()-155, 20);
        takenTime.setBounds(90, 55, getWidth()-95, 20);
        copyIdentifier.setBounds(getWidth()-60,5,55,30);
        delete.setBounds(getWidth()-50,getHeight()-25,45,20);

        addWidget(identifier);
        addWidget(category);
        addWidget(takenTime);
        addWidget(copyIdentifier);
        addWidget(delete);
    }

    @Override
    public void onDraw() {
        this.view.draw(NanumFont.BigMapNanumFont, 5,5, MapColorPalette.COLOR_BLACK, "ID");
        this.view.draw(NanumFont.BigMapNanumFont, 5,30, MapColorPalette.COLOR_BLACK, "카테고리");
        this.view.draw(NanumFont.BigMapNanumFont, 5,55, MapColorPalette.COLOR_BLACK, "제작 소요 시간");
    }
}
