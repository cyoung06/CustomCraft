package kr.syeyoung.craft;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.sun.javafx.stage.ScreenHelper;
import kr.syeyoung.craft.craftgui.GuiCraft;
import kr.syeyoung.craft.data.*;
import kr.syeyoung.craft.editor.EditorDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(name = "Trashfarm_Craft", version = "0.0.1")
@Description("A craft plugin specially made for Trashfarm")
@Author("syeyoung (cyoung06@naver.com)")
@Dependency("BKCommonLib")
@Dependency("Skript")
@Commands({
        @Command(name="조합", desc="조합 명령어"),
        @Command(name="조합관리", desc="조합관리 명령어")
})
public class Craft extends JavaPlugin {

    public static final Logger LOGGER = Logger.getLogger("Minecraft");


    private RecipeManager recipeManager;

    private SkriptAddon addon;


    public void onEnable() {
        ConfigurationSerialization.registerClass(RecipeItem.class, "RecipeItem");
        ConfigurationSerialization.registerClass(Recipe.class, "Recipe");
        ConfigurationSerialization.registerClass(RecipeCategory.class, "RecipeCategory");
        ConfigurationSerialization.registerClass(RecipePlayer.class, "RecipePlayer");
        ConfigurationSerialization.registerClass(RecipeQueue.class, "RecipeQueue");

        MapResourcePack.SERVER.load();

        loadData();


        addon = Skript.registerAddon(this);
        try {
            //This will register all our syntax for us. Explained below
            addon.loadClasses("kr.syeyoung.craft", "addon");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        saveData();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player && command.getLabel().equals("조합관리")) {
            if (!sender.isOp()) {
                sender.sendMessage("§b[조합] §c권한이 없습니다");
                return true;
            }
            ((Player) sender).getInventory().addItem(MapDisplay.createMapItem(this, EditorDisplay.class));
            sender.sendMessage("§b[조합] §f조합 관리 지도를 지급하였습니다");
        } else if (sender instanceof Player && command.getLabel().equals("조합")) {
            RecipeCategory category;
            if (args.length == 0) {
                category = null;
            } else {
                category = getRecipeManager().getRecipeCategory(args[0]);
            }

            GuiCraft craft = new GuiCraft((Player) sender, category);
        }
        return true;
    }

    public void loadData() {
        recipeManager = new RecipeManager();
        try {
            recipeManager.loadRecipes(getDataFolder());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load recipes");
            e.printStackTrace();
            return;
        }
        try {
            recipeManager.loadPlayerData(getDataFolder());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load player data");
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            recipeManager.saveAll(getDataFolder());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save all data");
            e.printStackTrace();
        }
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
