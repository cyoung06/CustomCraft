package kr.syeyoung.craft.data;

import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Getter
@Setter
@SerializableAs("RecipeQueue")
public class RecipeQueue implements ConfigurationSerializable {
    private RecipePlayer player;
    private Date start;
    private Date finish;
    private List<ItemStack> toReturn;
    private Recipe recipe;

    private RecipeQueue() {}

    public RecipeQueue(Recipe r, RecipePlayer player) {
        this.player = player;
        this.recipe = r;
        this.toReturn = r.getResultItems();
        this.start = new Date();
        this.finish = Date.from( LocalDateTime.now().plus(r.getRequiredTime()).atZone( ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialize = new HashMap<>();
        serialize.put("start", start.getTime());
        serialize.put("finish", finish.getTime());
        serialize.put("toReturn", toReturn);
        serialize.put("recipeId", recipe.getIdentifier());
        return serialize;
    }

    public static RecipeQueue deserialize(Map<String, Object> serialized) {
        RecipeQueue rq=  new RecipeQueue();
        rq.setStart(new Date((long)serialized.get("start")));
        rq.setFinish(new Date((long)serialized.get("finish")));
        rq.setToReturn(new ArrayList<ItemStack>((List<ItemStack>) serialized.get("toReturn")));
        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
        rq.setRecipe(rm.getRecipe((String) serialized.get("recipeId")));
        return rq;
    }
}
