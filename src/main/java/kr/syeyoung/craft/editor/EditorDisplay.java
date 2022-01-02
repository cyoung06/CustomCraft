package kr.syeyoung.craft.editor;

import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow;
import kr.syeyoung.craft.editor.menus.Menu;
import kr.syeyoung.craft.editor.menus.MenuCategoryList;
import kr.syeyoung.craft.editor.popups.Popup;
import kr.syeyoung.craft.util.NanumFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class EditorDisplay extends MapDisplay {
    private MapWidgetWindow window = new MapWidgetWindow();
    @Override
    public void onAttached() {
        clearWidgets();
        setSessionMode(MapSessionMode.FOREVER);
        setGlobal(true);

        menuStack.clear();
        window = new MapWidgetWindow();
        window.setBounds(0,0,getWidth(),getHeight());
        window.getTitle().setFont(NanumFont.getNanumFont(36.0f, Font.PLAIN));
        window.getTitle().setText("조합법 관리 GUI");
        window.getTitle().setAlignment(MapFont.Alignment.MIDDLE);
        window.getTitle().setColor(MapColorPalette.COLOR_BROWN);
        addWidget(window);

        addMenu(new MenuCategoryList());
    }


    private void invaliateEverything(MapWidget parent) {
        parent.invalidate();
        parent.getWidgets().forEach(this::invaliateEverything);
    }


    @Override
    public void onLeftClick(MapClickEvent event) {
        onClick(event);
    }

    @Override
    public void onRightClick(MapClickEvent event) {
        onClick(event);
    }

    private void onClick(MapClickEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == MapAction.RIGHT_CLICK)
            event.setCancelled(true);

        if ((lastTouch+75) > System.currentTimeMillis()) {
            return;
        }
        lastTouch = System.currentTimeMillis();
        if (prioritized != null && System.currentTimeMillis() < priortizedBefore) {

            if (prioritized instanceof MapClickListener) {
                ((MapClickListener) prioritized).onClick(event);
            }

            if (!prioritized.isFocused()) {
                prioritized.focus();
            } else {
                prioritized.activate();
            }

            return;
        }

        int x = event.getX();
        int y = event.getY();

        MapWidget theWidget = findSpecificWidgetWithin(this.getWidgets().get(this.getWidgets().size() - 1), x,y);


        if (theWidget == null) return;

        if (theWidget instanceof MapClickListener) {
            ((MapClickListener) theWidget).onClick(event);
        }

        if (!theWidget.isFocused()) {
            theWidget.focus();
        } else {
            theWidget.activate();
        }
    }

    private boolean withIn(MapWidget widget, int x, int y) {
        int childX = widget.getAbsoluteX();
        if (childX > x || x > (childX + widget.getWidth())) return false;
        int childY = widget.getAbsoluteY();
        if (childY > y || y > (childY + widget.getHeight())) return false;
        return true;
    }

    private MapWidget prioritized = null;
    private long priortizedBefore = System.currentTimeMillis();

    private long lastTouch = System.currentTimeMillis();

    @Override
    public void onStatusChanged(MapStatusEvent event) {
        super.onStatusChanged(event);

        if (event.getName().equals("PRIORITIZE")) {
            if (!(event.getArgument() instanceof Object[])) return;
            Object[] argument = (Object[]) event.getArgument();
            if (argument.length != 2) return;
            if (!(argument[0] instanceof MapWidget)) return;
            MapWidget widget = (MapWidget) argument[0];
            long time = (long) argument[1];

            this.prioritized = widget;
            this.priortizedBefore = time;
        }
    }

    private MapWidget findSpecificWidgetWithin(MapWidget widget, int x, int y) {
        List<MapWidget> widgetList = new ArrayList<>();
        widgetList.addAll(widget.getWidgets());
        Collections.reverse(widgetList);
        for (MapWidget children : widgetList) {
            if (withIn(children, x,y)) {
                MapWidget theWidget = findSpecificWidgetWithin(children, x,y);
                if (theWidget == null && children.isFocusable())
                    return children;
                else
                    return theWidget;
            }
        }
        return widget != null && widget.isFocusable() ? widget : null;
    }

    public void addPopup(Popup widget) {
        widget.setPosition((getWidth() - widget.getWidth()) /2, (getHeight() - widget.getHeight()) /2);
        addWidget(widget);
    }

    Stack<Menu> menuStack = new Stack<>();

    public void addMenu(Menu menu) {
        if (menuStack.size() == 0) {
            menuStack.push(menu);
            menu.setBounds(0, 50, getWidth(), getHeight() - 50);
            window.addWidget(menu);
            menu.setAllowGoBack(false);
        } else {
            Menu prev = menuStack.peek();
            menuStack.push(menu);
            window.swapWidget(prev, menu);
            menu.setBounds(0, 50, getWidth(), getHeight() - 50);
            menu.setAllowGoBack(true);
        }
    }

    public void goBack() {
        if (menuStack.size() >= 2) {
            Menu current = menuStack.pop();
            Menu toGo = menuStack.peek();
            window.swapWidget(current, toGo);
            toGo.setBounds(0, 50, getWidth(), getHeight() - 50);
            toGo.setAllowGoBack(menuStack.size() >= 2);
        }
    }
}
