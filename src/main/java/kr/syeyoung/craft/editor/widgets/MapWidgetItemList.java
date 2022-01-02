package kr.syeyoung.craft.editor.widgets;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import kr.syeyoung.craft.editor.info.RecipeItemChooser;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MapWidgetItemList<T> extends MapWidget {
    private List<T> itemList;

    private Callable<List<T>> listCallable;

    private BiConsumer<T, Integer> onClick;

    private BiFunction<T, Integer, ItemStack> itemConverter;

    private Runnable newButtonClick;

    public MapWidgetItemList(Runnable newButtonClick, BiConsumer<T, Integer> onClick, BiFunction<T, Integer, ItemStack> itemConverter, Callable<List<T>> itemList) {
        this.onClick = onClick;
        this.itemConverter = itemConverter;
        try {
            this.itemList = itemList.call();
        } catch (Exception e) {
        }
        this.newButtonClick = newButtonClick;
        this.listCallable = itemList;
        setClipParent(true);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        rePopulate();
    }

    public void rePopulate() {
        try {
            itemList = listCallable.call();
        } catch (Exception e) {
        }

        clearWidgets();
        int columns = Math.floorDiv(getWidth() , 69);
        int rows = (int) Math.ceil((itemList.size() + 1) / (double)columns);

        setSize(getWidth(), rows * 69 + 5);
        if (this.getParent() instanceof RecipeItemChooser)
            ((RecipeItemChooser) this.getParent()).onRecalcPositions();

        int i = 0;

        iterator:
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                if (i >= itemList.size()) {
                    MapWidgetButton newButton = new MapWidgetButton() {
                        @Override
                        public void onActivate() {
                            newButtonClick.run();
                        }
                    };
                    newButton.setIcon(this.display.loadTexture("kr/syeyoung/craft/res/smallnew.png"));
                    newButton.setBounds(x * 69 + 5, y * 69 + 5, 64, 64);
                    addWidget(newButton);
                    break iterator;
                }

                T element = itemList.get(i);

                int finalI = i;
                MapWidgetItemListElement elementWidget = new MapWidgetItemListElement(itemConverter.apply(element, finalI)) {
                    @Override
                    public void onActivate() {
                        onClick.accept(element, finalI);
                    }
                };
                elementWidget.setBounds(x * 69 + 5, y * 69 + 5, 64, 64);
                addWidget(elementWidget);
                i++;
            }
        }
    }
}
