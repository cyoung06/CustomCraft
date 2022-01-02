package kr.syeyoung.craft.data;

import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
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
@SerializableAs("RecipePlayer")
public class RecipePlayer implements ConfigurationSerializable {
    private UUID playerUUID;

    private Set<Recipe> acquiredRecipes;
    private List<RecipeQueue> recipeQueue;


    private RecipePlayer() {}

    public RecipePlayer(UUID uuid) {
        Objects.requireNonNull(uuid);
        this.playerUUID = uuid;
        this.acquiredRecipes = new HashSet<>();
        this.recipeQueue = new ArrayList<>();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized=  new HashMap<>();
        serialized.put("playerUUID", playerUUID.toString());
        serialized.put("acquiredRecipes", acquiredRecipes.stream().map(Recipe::getIdentifier).collect(Collectors.toList()));
        serialized.put("recipeQueue", recipeQueue);
        return serialized;
    }

    public static RecipePlayer deserialize(Map<String,Object> serialized) {
        RecipePlayer player = new RecipePlayer();
        player.setPlayerUUID(UUID.fromString((String) serialized.get("playerUUID")));
        List<String> recipes = (List<String>) serialized.get("acquiredRecipes");
        RecipeManager recipeManager = Craft.getPlugin(Craft.class).getRecipeManager();
        Set<Recipe> recipesInstance = recipes.stream().map(recipeManager::getRecipe).filter(Objects::nonNull).collect(Collectors.toSet());
        player.setAcquiredRecipes(new HashSet<>(recipesInstance));
        player.setRecipeQueue((List<RecipeQueue>) serialized.get("recipeQueue"));
        player.getRecipeQueue().forEach(rq -> rq.setPlayer(player));
        return player;
    }
}
