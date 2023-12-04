package com.github.laefye.fishing.config;

import com.github.laefye.fishing.Fishing;
import com.github.laefye.tools.ItemTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LootType implements ConfigurationSerializable {
    private final LootCategory category;
    private final int chance;
    private final String item;
    private final Optional<Component> title;
    private enum LootCategory {
        FISH,
        ITEM,
        }
    private final Optional<Integer> cost;
    private static final String FISHING_COST = "fishingCost";
    private static final String CONFIG_TITLE = "title";
    private static final String CONFIG_CHANCE = "chance";
    private static final String CONFIG_COST = "cost";
    private static final String CONFIG_ITEM = "item";
    private static final String CONFIG_CATEGORY = "category";
    private static final String CONFIG_CATEGORY_FISH = "fish";
    private static final String CONFIG_CATEGORY_ITEM = "item";

    private LootType(LootCategory category, int chance, String item, Optional<Component> title, Optional<Integer> cost) {
        this.category = category;
        this.chance = chance;
        this.item = item;
        this.title = title;
        this.cost = cost;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put(CONFIG_CATEGORY, category == LootCategory.FISH ? CONFIG_CATEGORY_FISH : CONFIG_CATEGORY_ITEM);
        data.put(CONFIG_CHANCE, chance);
        data.put(CONFIG_ITEM, item);
        title.ifPresent(component -> data.put(CONFIG_TITLE, MiniMessage.miniMessage().serialize(component)));
        cost.ifPresent(integer -> data.put(CONFIG_COST, integer));
        return data;
    }

    public static LootType deserialize(Map<String, Object> args) {
        return new LootType(
                Optional.ofNullable((String) args.get(CONFIG_CATEGORY))
                        .filter(s -> s.equals(CONFIG_CATEGORY_FISH))
                        .map(s -> LootCategory.FISH)
                        .orElse(LootCategory.ITEM),
                Optional.ofNullable((Integer) args.get(CONFIG_CHANCE))
                        .orElse(0),
                Optional.ofNullable((String) args.get(CONFIG_ITEM))
                        .orElse("COD"),
                Optional.ofNullable((String) args.get(CONFIG_TITLE)).map(s -> MiniMessage.miniMessage().deserialize(s)),
                Optional.ofNullable((Integer) args.get(CONFIG_COST))
        );
    }

    private ItemMeta itemMeta(ItemMeta itemMeta) {
        title.ifPresent(component -> itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(component)
                        .build()
        ));
        return itemMeta;
    }

    public ItemStack itemStack() {
        var itemStack = item.startsWith("#") ?
                Fishing.getCustomItemService(Bukkit.getServer()).give(item.substring(1))
                        .orElse(ItemStack.empty()) :
                new ItemStack(Material.valueOf(item));
        var compound = ItemTools.getOrCreateItemTag(itemStack);
        cost.ifPresent(cost -> compound.setInt(FISHING_COST, cost));
        itemStack = ItemTools.setItemTag(itemStack, compound);
        Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
        return itemStack;
    }

    @Override
    public String toString() {
        return "LootType{" +
                "item=" + item +
                ", title=" + title +
                ", category=" + category +
                ", chance=" + chance +
                ", cost=" + cost +
                '}';
    }

    public static Optional<Integer> getCost(ItemStack itemStack) {
        return ItemTools.getItemTag(itemStack).map(value -> value.getInt(FISHING_COST) * itemStack.getAmount());
    }

    public int getChance() {
        return chance;
    }
}
