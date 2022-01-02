package kr.syeyoung.craft.data;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum ItemMatchStrategy {
    MATERIAL("아이템 타입 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);

            return given.getType().equals(match.getType());
        }
    }, DATA("데이터 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);

            return given.getData().equals(match.getData());
        }
    }, NAME("이름 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);

            String nameGiven = given.hasItemMeta() ? given.getItemMeta().getDisplayName() : null;
            String nameMatch = match.hasItemMeta() ? match.getItemMeta().getDisplayName() : null;

            return Objects.equals(nameGiven, nameMatch);
        }
    }, LORE("로어 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);

            List<String> loreGiven = given.hasItemMeta() ? given.getItemMeta().getLore() : null;
            List<String> loreMatch = match.hasItemMeta() ? match.getItemMeta().getLore() : null;

            return Objects.equals(loreGiven, loreMatch);
        }
    }, ENCHANTMENTS("인챈트 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);

            Map<Enchantment, Integer> enchantGiven = given.hasItemMeta() ? given.getItemMeta().getEnchants() : null;
            Map<Enchantment, Integer> enchantMatch = match.hasItemMeta() ? match.getItemMeta().getEnchants() : null;

            return Objects.equals(enchantGiven, enchantMatch);
        }
    }, DURABILITY("내구도 일치") {
        public boolean match(ItemStack given, ItemStack match) {
            Objects.requireNonNull(given);
            Objects.requireNonNull(match);


            return Objects.equals(given.getDurability(), match.getDurability());
        }
    };

    @Getter
    private String korean;

    private ItemMatchStrategy(String korean) {
        this.korean = korean;
    }

    public abstract boolean match(ItemStack given, ItemStack match);
}
