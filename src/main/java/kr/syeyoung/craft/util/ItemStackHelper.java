package kr.syeyoung.craft.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackHelper {
    public static ItemStack getItemStack(Material material, short data, String name, List<String > lore) {
        ItemStack itemStack = new ItemStack(material, 1, data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static String getName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            return meta.getDisplayName();
        } else if (meta.hasLocalizedName()) {
            return meta.getLocalizedName();
        } else {
            return WordUtils.capitalizeFully(item.getType().name().replace("_"," "));
        }
    }
}
