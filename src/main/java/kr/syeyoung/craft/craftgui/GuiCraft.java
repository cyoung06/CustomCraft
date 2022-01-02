package kr.syeyoung.craft.craftgui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kr.syeyoung.craft.Craft;
import kr.syeyoung.craft.RecipeManager;
import kr.syeyoung.craft.data.*;
import kr.syeyoung.craft.util.ItemStackHelper;
import org.apache.commons.lang.SerializationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GuiCraft {
    public OutlinePane paneItemQueue, paneRecipes, paneItems;
    public StaticPane paneCurrentRecipe;
    public GuiItem buttonCraft;
    Player player;
    RecipePlayer recipePlayer;
    public Gui gui;

    int page = 0;
    List<Recipe> recipesAcquired;

    Recipe chosenRecipe;

    boolean requirementMet = false;

    public GuiCraft(Player p, RecipeCategory category) {
        Gui gui = Gui.load(Craft.getPlugin(Craft.class), this, Craft.getPlugin(Craft.class).getResource("kr/syeyoung/craft/res/craft.xml"));
        this.player = p;

        RecipeManager rm = Craft.getPlugin(Craft.class).getRecipeManager();
        recipePlayer = Craft.getPlugin(Craft.class).getRecipeManager().getRecipePlayer(p.getUniqueId());
//        if (category != null)
//            recipesAcquired = recipePlayer.getAcquiredRecipes().stream().filter(r -> r.getCategory().equals(category)).collect(Collectors.toList());
//        else
//            recipesAcquired = new ArrayList<>(recipePlayer.getAcquiredRecipes());

        if (category != null)
            recipesAcquired = rm.getRecipeCategory(category.getIdentifier()).getRecipeSet();
        else
            recipesAcquired = rm.getRecipes();

        gui.show(p);
        populatePage();
        updateRecipeQueue();
        gui.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void populatePage() {
        paneRecipes.clear();
        for (int i = 0; i < 9; i++) {
            int index = page * 9 + i;
            if (index >= recipesAcquired.size()) break;
            Recipe r = recipesAcquired.get(index);
            if (r != null) {
                paneRecipes.addItem(new GuiItem(r.getResultItems().size() == 0 ? new ItemStack(Material.BEDROCK) : r.getResultItems().get(0), click -> {
                    click.setCancelled(true);
                    selectRecipe(r);
                }));
            } else {
                paneRecipes.addItem(new GuiItem(new ItemStack(Material.BEDROCK), click -> {
                    click.setCancelled(true);
                }));
            }
        }
        gui.update();
    }

    public void selectRecipe(Recipe r) {
        requirementMet= false;
        chosenRecipe = r;
        paneCurrentRecipe.clear();
        paneCurrentRecipe.addItem(new GuiItem(r.getResultItems().size() == 0 ? new ItemStack(Material.BEDROCK) : r.getResultItems().get(0)),0,0);

        paneItems.clear();
        for (RecipeItem item : r.getRequiredItems()) {
            ItemStack rep = item.getRepresentation().clone();
            rep.setAmount(1);
            ItemMeta meta = rep.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§e필요 개수: §c"+item.getAmmount()+"개");
            meta.setLore(lore);
            rep.setItemMeta(meta);
            paneItems.addItem(new GuiItem(rep));
        }

        ItemStack stack = buttonCraft.getItem();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();
        checkRequirement(r, lore);
        lore.add("§7조합 소요시간: §4§l"+LocalTime.MIDNIGHT.plus(r.getRequiredTime()).format(DateTimeFormatter.ofPattern("HH'시간'mm'분'ss'초'")));
        meta.setLore(lore);
        stack.setItemMeta(meta);

        gui.update();
    }

    public ItemStack[] deepCopy(ItemStack[] prev) {
        ItemStack[] items = new ItemStack[prev.length];
        for (int i =0; i < items.length; i++) {
            if (prev[i] != null)
                items[i] = prev[i].clone();
        }
        return items;
    }

    public void checkRequirement(Recipe r, List<String> lore) {
        Set<RecipeItem> required = r.getRequiredItems();
        Iterator<RecipeItem> iterator = required.iterator();

        ItemStack[] items = deepCopy(player.getInventory().getContents());

        boolean misMatched = false;
        while(iterator.hasNext()) {
            RecipeItem recipeItem = iterator.next();

            int requiredAmmount = recipeItem.getAmmount();
            for (ItemStack item:items) {
                if (recipeItem.match(item)) {
                    int ammountToTake = Math.min(item.getAmount(), requiredAmmount);
                    requiredAmmount -= ammountToTake;
                    item.setAmount(item.getAmount() - ammountToTake);
                }
            }

            if (requiredAmmount > 0) {
                if (!misMatched) {
                    lore.add("§c조합에 필요한 재료가 부족합니다.");
                    lore.add("§c다음의 재료가 추가로 필요합니다.");
                    misMatched = true;
                }
                lore.add("§7└ " + ItemStackHelper.getName(recipeItem.getRepresentation()) +" §ex"+requiredAmmount);
            }
        }
        requirementMet = !misMatched;

        if (recipePlayer.getRecipeQueue().size() >= 9) {
            lore.add("§c조합큐가 꽉 찼습니다");
        }
    }

    public boolean takeItems() {
        Set<RecipeItem> required = chosenRecipe.getRequiredItems();
        Iterator<RecipeItem> iterator = required.iterator();

        ItemStack[] items = deepCopy(player.getInventory().getContents());

        boolean misMatched = false;
        while(iterator.hasNext()) {
            RecipeItem recipeItem = iterator.next();

            int requiredAmmount = recipeItem.getAmmount();
            for (ItemStack item:items) {
                if (recipeItem.match(item)) {
                    int ammountToTake = Math.min(item.getAmount(), requiredAmmount);
                    requiredAmmount -= ammountToTake;
                    if (recipeItem.getResultStrategy() != ItemResultStrategy.KEEP)
                        item.setAmount(item.getAmount() - ammountToTake);
                }
            }

            if (requiredAmmount > 0) {
                return false;
            }
        }

        player.getInventory().setContents(items);

        iterator = required.iterator();
        while (iterator.hasNext()) {
            RecipeItem item = iterator.next();
            if (item.getResultStrategy() == ItemResultStrategy.REPLACE) {
                giveItem(player, item.getResult());
            }
        }
        return true;
    }

    public void giveItem(Player p, ItemStack item) {
        PlayerInventory inv = p.getInventory();
        int i = inv.firstEmpty();
        if (i == -1) {
            p.getLocation().getWorld().dropItem(p.getLocation(), item);
        } else {
            inv.setItem(inv.firstEmpty(), item);
        }
    }


    public void craft() {
        if (requirementMet) {
            if (recipePlayer.getRecipeQueue().size() >= 9) {
                return;
            }
            if (!takeItems()) {
                selectRecipe(chosenRecipe);
                return;
            }


            RecipeQueue rq = new RecipeQueue(chosenRecipe, recipePlayer);
            recipePlayer.getRecipeQueue().add(rq);
            updateRecipeQueue();
            selectRecipe(chosenRecipe);
        }
    }

    public void updateRecipeQueue() {
        paneItemQueue.clear();
        for (RecipeQueue rq: recipePlayer.getRecipeQueue()) {
            if (rq == null) continue;
            ItemStack icon = rq.getToReturn().size() == 0 ? new ItemStack(Material.BEDROCK) : rq.getToReturn().get(0).clone();
            ItemMeta meta = icon.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            Duration d = Duration.ofMillis(rq.getFinish().getTime() - System.currentTimeMillis());
            if (rq.getFinish().after(new Date())) {
                lore.add("§7조합 완료까지 남은 시간: "+LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH'시간'mm'분'ss'초'")));
                lore.add("§7클릭하여 시간 업데이트");
            } else {
                lore.add("§7조합이 완료되었습니다. 클릭하여 아이템을 회수하세요");
            }
            meta.setLore(lore);
            icon.setItemMeta(meta);
            paneItemQueue.addItem(new GuiItem(icon, click -> {
                if (rq.getFinish().after(new Date())) {
                    updateRecipeQueue();
                } else if (recipePlayer.getRecipeQueue().contains(rq)) {
                    recipePlayer.getRecipeQueue().remove(rq);
                    rq.getToReturn().forEach(item -> giveItem(player, item));
                    updateRecipeQueue();

                    if (recipePlayer.getRecipeQueue().size() >= 8) {
                        if (chosenRecipe != null)
                            selectRecipe(chosenRecipe);
                    }
                }
            }));
        }
        gui.update();
    }

    public void left() {
        if (page > 0) {
            page--;
            populatePage();
        }
    }

    public void right() {
        if (page < (Math.ceil(recipesAcquired.size() / 9.0) - 1)) {
            page++;
            populatePage();
        }
    }




    public void populateBackground(StaticPane pane) {
        for (int i = 0; i < 9; i++)
            if (i != 1 && i!= 4 && i != 7)
                pane.addItem(new GuiItem(ItemStackHelper.getItemStack(Material.STAINED_GLASS_PANE, (short) 15, "§f", Arrays.asList())), i,0);
        for (int i = 1; i < 4; i++) {
            pane.addItem(new GuiItem(ItemStackHelper.getItemStack(Material.STAINED_GLASS_PANE, (short) 15, "§f", Arrays.asList())), 0,i);
            pane.addItem(new GuiItem(ItemStackHelper.getItemStack(Material.STAINED_GLASS_PANE, (short) 15, "§f", Arrays.asList())), 5,i);
        }
        for (int i = 0; i < 9; i++)
            if (i!=7)
                pane.addItem(new GuiItem(ItemStackHelper.getItemStack(Material.STAINED_GLASS_PANE, (short) 15, "§f", Arrays.asList())), i,4);
    }

    public void populateQueue(StaticPane pane) {
        for (int i =0; i<9; i++)
            pane.addItem(new GuiItem(ItemStackHelper.getItemStack(Material.BARRIER, (short) 0, "§7조합 큐 존재하지 않음", Arrays.asList("§7조합 큐가 대기중입니다"))), i,0);
    }
}
