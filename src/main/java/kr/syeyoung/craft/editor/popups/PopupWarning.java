package kr.syeyoung.craft.editor.popups;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import kr.syeyoung.craft.editor.widgets.MapWidgetFontSupportButton;
import kr.syeyoung.craft.util.NanumFont;

public class PopupWarning extends Popup {
    private String message;

    public PopupWarning(String message) {
        super();
        this.message = message;
        setDepthOffset(10);
        setSize(400,130);
    }

    private MapWidgetButton okButton = new MapWidgetFontSupportButton() {
        @Override
        public void onActivate() {
            close();
        }
    };

    @Override
    public void onAttached() {
        super.onAttached();

        MapWidgetText text = new MapWidgetText();
        text.setBounds(10,40, getWidth(), 20);
        text.setFont(NanumFont.BigMapNanumFont);
        text.setAlignment(MapFont.Alignment.MIDDLE);
        text.setColor(MapColorPalette.COLOR_BLACK);
        text.setText(message);

        okButton.setBounds(10, getHeight() - 50, getWidth() - 20, 40);
        okButton.setText("확인");
        addWidget(text);
        addWidget(okButton);
    }

    @Override
    public void onPopupClosed() {

    }
}
