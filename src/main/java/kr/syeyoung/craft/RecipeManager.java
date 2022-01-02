package kr.syeyoung.craft;

import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipeCategory;
import kr.syeyoung.craft.data.RecipePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class RecipeManager {
    private Map<String, Recipe> recipeMap = new HashMap<>();
    private Map<String, RecipeCategory> recipeCategoryMap = new HashMap<>();
    private Map<UUID, RecipePlayer> recipePlayerMap = new HashMap<>();

    public RecipeManager() {}

    public void registerRecipe(Recipe r) {
        Objects.requireNonNull(r);
        if (recipeMap.containsKey(r.getIdentifier())) {
            Craft.LOGGER.log(Level.WARNING, "Conflicting Recipe being registered :: "+r.getIdentifier() +" / ignoring it");
            return;
        }
        recipeMap.put(r.getIdentifier(), r);
    }

    public List<RecipeCategory> getRecipeCategories() {
        return new ArrayList<>(recipeCategoryMap.values());
    }
    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipeMap.values());
    }


    public Recipe getRecipe(String identifier) {
        return recipeMap.get(identifier);
    }

    public void registerRecipeCategory(RecipeCategory category) {
        Objects.requireNonNull(category);
        if (recipeCategoryMap.containsKey(category.getIdentifier())) throw new IllegalStateException("The category with same identifier exists");
        recipeCategoryMap.put(category.getIdentifier(), category);
        category.getRecipeSet().forEach(this::registerRecipe);
    }

    public void unregisterRecipeCategory(RecipeCategory category) {
        recipeCategoryMap.remove(category.getIdentifier());
        category.getFile().delete();
    }

    public void unregisterRecipe(Recipe recipe) {
        recipe.getCategory().getRecipeSet().remove(recipe);
        recipeMap.remove(recipe.getIdentifier());
    }


    public RecipeCategory getRecipeCategory(String identifier) {
        return recipeCategoryMap.get(identifier);
    }

    public RecipePlayer getRecipePlayer(UUID player) {
        if (!recipePlayerMap.containsKey(player))
            recipePlayerMap.put(player, new RecipePlayer(player));
        return recipePlayerMap.get(player);
    }

    public void loadPlayerData(File baseDir) throws IOException {
        File playerData = new File(baseDir, "players.yml");
        if (!playerData.exists()) {
            playerData.createNewFile();
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerData);
        List<RecipePlayer> players = ((List<RecipePlayer>)config.getList("players"));
        players.forEach(p -> recipePlayerMap.put(p.getPlayerUUID(), p));
    }

    public void loadRecipes(File baseDir) throws IOException {
        File categoriesFolder = new File(baseDir, "recipes");
        if (!categoriesFolder.exists())
            categoriesFolder.mkdirs();
        for (File f:categoriesFolder.listFiles()) {
            if (!f.getName().endsWith(".yml")) continue;

            try {
                registerRecipeCategory(RecipeCategory.load(f));
            } catch (Exception e) {
                Craft.LOGGER.log(Level.WARNING, "Failed to load category from " + f.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    public void saveAll(File baseDir) throws IOException {
        if (!baseDir.exists())
            baseDir.mkdirs();

        YamlConfiguration config = new YamlConfiguration();
        config.set("players", new ArrayList<>(recipePlayerMap.values()));
        config.save(new File(baseDir, "players.yml"));

        File categoriesFolder = new File(baseDir, "recipes");
        for (RecipeCategory value : recipeCategoryMap.values()) {
            try {
                value.save(categoriesFolder);
            } catch (Exception e) {
                Craft.LOGGER.log(Level.WARNING, "Failed to save category :: "+value.getIdentifier() +" / " +value.getName());
                e.printStackTrace();
            }
        }
    }
}
