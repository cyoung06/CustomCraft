package kr.syeyoung.craft.addon;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.Recipe;
import kr.syeyoung.craft.data.RecipePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EffAddRecipe extends Effect {

    static {
        Skript.registerEffect(EffAddRecipe.class, "add_trash_recipe %string% to %players%");
    }

    private Expression<String> recipeIds;
    private Expression<Player> players;

    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getAll(event);
        String[] ids = this.recipeIds.getAll(event);

        RecipeManager manager = Craft.getPlugin(Craft.class).getRecipeManager();

        List<RecipePlayer> recipePlayers = new ArrayList<>();
        for (Player player : players) {
            recipePlayers.add(manager.getRecipePlayer(player.getUniqueId()));
        }

        for (String id : ids) {

            Recipe r = manager.getRecipe(id);
            recipePlayers.forEach(p -> p.getAcquiredRecipes().add(r));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "add recipe effect with expression player: " + players.toString(event, b) + " and string epxression: "+recipeIds.toString(event,b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.players = (Expression<Player>) expressions[1];
        this.recipeIds = (Expression<String>) expressions[0];
        return true;
    }
}
