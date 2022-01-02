package kr.syeyoung.craft.data;

import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

@Getter
@Setter
@SerializableAs("Recipe")
public class Recipe implements ConfigurationSerializable {
    private Set<RecipeItem> requiredItems;
    private List<ItemStack> resultItems;

    private String identifier;

    private Duration requiredTime;

    private RecipeCategory category;

    private Recipe() {};

    public Recipe(RecipeCategory category, String identifier) {
        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
        if (rm.getRecipeCategory(category.getIdentifier()) == null) throw new IllegalArgumentException("Category is not registered");
        Objects.requireNonNull(category);
        Objects.requireNonNull(identifier);
        this.category = category;
        this.identifier = identifier;
        this.requiredItems = new HashSet<>();
        this.resultItems = new ArrayList<>(Arrays.asList(new ItemStack(Material.STONE)));
        this.requiredTime = Duration.ZERO;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("identifier", identifier);
        serialized.put("requiredItems", requiredItems);
        serialized.put("resultItems", resultItems);
        serialized.put("requiredTime", requiredTime.toMillis());
        return serialized;
    }

    public static Recipe deserialize(Map<String, Object> serialized) {
        Recipe r = new Recipe();
        r.setIdentifier((String) serialized.get("identifier"));
        r.setRequiredItems((Set<RecipeItem>) serialized.get("requiredItems"));
        r.setResultItems((List<ItemStack>) serialized.get("resultItems"));
        r.setRequiredTime(Duration.ofMillis((int) serialized.get("requiredTime")));
        return r;
    }
}
