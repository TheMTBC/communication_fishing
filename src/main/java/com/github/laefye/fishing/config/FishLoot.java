package com.github.laefye.fishing.config;

import com.github.laefye.craft.ItemTools;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FishLoot implements ILootType {
    private final int chance;
    private final String item;
    private final Component title;
    private final int cost;
    private static final String FISHING_COST = "fishingCost";

    private FishLoot(int chance, String item, Component title, int cost) {
        this.chance = chance;
        this.item = item;
        this.title = title;
        this.cost = cost;
    }

    private ItemMeta itemMeta(ItemMeta itemMeta) {
        itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(title)
                        .build()
        );
        return itemMeta;
    }

    public static Optional<Integer> getCost(ItemStack itemStack) {
        return ItemTools.getItemTag(itemStack).map(value -> value.getInt(FISHING_COST) * itemStack.getAmount());
    }

    public int getChance() {
        return chance;
    }

    @Override
    public ItemStack getItemStack() {
        var itemStack = Loot.getItem(item);
        var compound = ItemTools.getOrCreateItemTag(itemStack);
        compound.putInt(FISHING_COST, cost);
        itemStack = ItemTools.setItemTag(itemStack, compound);
        Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
        return itemStack;
    }

    @Override
    public Category getCategory() {
        return Category.FISH;
    }

    @Override
    public String toString() {
        return "FishLoot{" +
                "chance=" + chance +
                ", item='" + item + '\'' +
                ", title=" + title +
                ", cost=" + cost +
                '}';
    }

    public static FishLoot deserialize(JsonObject jsonObject) {
        return new FishLoot(
                jsonObject.get("chance").getAsInt(),
                jsonObject.get("item").getAsString(),
                MiniMessage.miniMessage().deserialize(jsonObject.get("title").getAsString()),
                jsonObject.get("cost").getAsInt()
        );
    }
}
