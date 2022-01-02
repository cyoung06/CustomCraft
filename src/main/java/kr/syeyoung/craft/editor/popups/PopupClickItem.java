package kr.syeyoung.craft.editor.popups;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.editor.MapClickListener;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.util.NanumFont;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PopupClickItem extends Popup implements MapClickListener {
    @Getter
    private ItemStack selected;

    @Getter
    private ItemStack current;

    private MapWidgetButton button = new MapWidgetFontSupportButton() {
        @Override
        public void onAttached() {
            super.onAttached();
            this.setText("변경");
        }

        @Override
        public void onActivate() {
            selected = current;
            PopupClickItem.this.close();
        }
    };

    public void setCurrent(ItemStack current) {
        this.current = current;
        this.selected = current;
        this.invalidate();
    }



    public PopupClickItem(ItemStack chosenItem) {
        super();
        setSize(200,270);
        setDepthOffset(5);
        setFocusable(true);
        this.selected = chosenItem;
        this.current = chosenItem;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        button.setBounds(5,225,getWidth()- 10, 40);
        addWidget(button);
    }

    @Override
    public void onDraw() {
        super.onDraw();

        if (current != null)
            this.view.drawItem(MapResourcePack.SERVER, current, 5,5,190,190);
        this.view.draw(NanumFont.BigMapNanumFont, 5, 200, MapColorPalette.COLOR_BLACK, "아이템을 들고 화면을 클릭해주세요");
    }

    @Override
    public void onPopupClosed() {
    }

    @Override
    public boolean onClick(MapClickEvent event) {

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) return true;
        this.current = item;
        invalidate();
        return true;
    }
}
