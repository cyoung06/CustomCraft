package kr.syeyoung.craft.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@SerializableAs("RecipeItem")
public class RecipeItem implements ConfigurationSerializable {
    private ItemStack representation;
    private ItemStack result;
    private Set<ItemMatchStrategy> itemMatchStrategySet;
    private ItemResultStrategy resultStrategy;

    private int ammount;

    private Recipe recipe;

    private RecipeItem() {

    }

    public RecipeItem(Recipe recipe) {
        this.recipe = recipe;
        this.itemMatchStrategySet = new HashSet<>();
        this.resultStrategy = ItemResultStrategy.DESTROY;
    }

    public boolean match(ItemStack match) {
        if (match == null) return false;
        for (ItemMatchStrategy ims: itemMatchStrategySet) {
            if (!ims.match(representation, match)) return false;
        }
        return true;
    }

    public ItemStack afterCraft(ItemStack match) {
        switch (resultStrategy) {
            case DESTROY:
                return null;
            case KEEP:
                return match;
            case REPLACE:
                return result;
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("representation", representation);
        serialized.put("result", result);
        serialized.put("resultStrategy", resultStrategy.name());
        serialized.put("matchStrategies", itemMatchStrategySet.stream().map(ItemMatchStrategy::name).collect(Collectors.toSet()));
        serialized.put("ammount", ammount);
        return serialized;
    }

    public static RecipeItem deserialize(Map<String, Object> map) {
        RecipeItem item = new RecipeItem();
        item.setAmmount((Integer) map.get("ammount"));
        item.setResultStrategy(ItemResultStrategy.valueOf((String) map.get("resultStrategy")));
        item.setRepresentation((ItemStack) map.get("representation"));
        item.setResult((ItemStack) map.get("result"));
        Collection<String> strings = (Collection<String>) map.get("matchStrategies");
        Set<ItemMatchStrategy> matchStrategies = strings.stream().map(ItemMatchStrategy::valueOf).collect(Collectors.toSet());
        item.setItemMatchStrategySet(new HashSet<>(matchStrategies));

        return item;
    }
}
