package kr.syeyoung.craft.data;

import kr.syeyoung.craft.Craft;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
@Setter
@SerializableAs("RecipeCategory")
public class RecipeCategory implements ConfigurationSerializable {
    private String identifier;
    private String name;
    private ItemStack icon;
    private File file;

    private List<Recipe> recipeSet;

    private RecipeCategory() {
    }

    public RecipeCategory(String identifier) {
        Objects.requireNonNull(identifier);
        recipeSet = new ArrayList<>();
        this.identifier = identifier;
        name = identifier;
        icon = new ItemStack(Material.STONE);
    }

    public void save(File baseDir) throws IOException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("data", this);
        yamlConfiguration.save(getFile());
    }

    public File getFile() {
        if (file == null)
            file = new File(Craft.getPlugin(Craft.class).getDataFolder(), "recipes/"+identifier+".yml");
        return file;
    }

    public static RecipeCategory load(File f) throws IOException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        RecipeCategory category = (RecipeCategory) config.get("data");
        category.setFile(f);
        category.getRecipeSet().forEach(r -> r.setCategory(category));
        return category;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String,Object> serialized = new HashMap<>();
        serialized.put("identifier", identifier);
        serialized.put("name", name.replace("§", "&"));
        serialized.put("recipes", recipeSet);
        serialized.put("icon", icon);
        return serialized;
    }

    public static RecipeCategory deserialize(Map<String, Object> map) {
        RecipeCategory category = new RecipeCategory();
        category.setIdentifier((String) map.get("identifier"));
        category.setName(ChatColor.translateAlternateColorCodes('&', (String) map.get("name")));
        category.setRecipeSet(((List<Recipe>) map.get("recipes")));
        category.setIcon((ItemStack) map.get("icon"));
        return category;
    }
}