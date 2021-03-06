package kr.syeyoung.craft.editor.popups;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.util.NanumFont;
import lombok.Getter;

import java.awt.*;

public class PopupConfirm extends Popup {

    @Getter
    private boolean confirm = false;

    MapWidgetButton yes = new MapWidgetFontSupportButton() {
        public void onActivate() {
            confirm = true;
            PopupConfirm.this.close();
        }
    };
    MapWidgetButton no = new MapWidgetFontSupportButton() {
        public void onActivate() {
            PopupConfirm.this.close();
        }
    };

    private String question;

    public PopupConfirm(String question) {
        super();
        setSize(400, 130);
        this.question = question;
    }

    public void onDraw() {
        super.onDraw();

        Dimension dimension = this.view.calcFontSize(NanumFont.BigMapNanumFont, question);
        int x = (getWidth()-dimension.width) / 2;
        this.view.draw(NanumFont.BigMapNanumFont, x,10, MapColorPalette.COLOR_BLACK, question);

    }

    @Override
    public void onAttached() {
        clearWidgets();
        super.onAttached();
        yes.setText("네");
        no.setText("아니요");
        yes.setBounds(getWidth() / 5, getHeight() - 50, getWidth() / 5, 40);
        no.setBounds(getWidth() * 3 / 5, getHeight() - 50, getWidth() / 5, 40);
        addWidget(no);
        addWidget(yes);
    }

    @Override
    public void onPopupClosed() {

    }
}
