package com.github.laefye.fishing.config.loottable;

import com.github.laefye.craft.ItemTools;
import com.github.laefye.fishing.Fishing;
import com.github.laefye.fishing.Randomizer;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FishLoot implements ILootType {
    private final Fishing plugin;
    private final int chance;
    private final String item;
    private final Component title;
    private final int cost;
    private static final String FISHING_COST = "fishingCost";

    private FishLoot(Fishing plugin, int chance, String item, Component title, int cost) {
        this.plugin = plugin;
        this.chance = chance;
        this.item = item;
        this.title = title;
        this.cost = cost;
    }

    private ItemMeta itemMeta(ItemMeta itemMeta, Component size) {
        itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(title)
                        .build()
        );
        itemMeta.lore(List.of(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(size)
                        .build()
        ));
        return itemMeta;
    }

    public static Optional<Integer> getCost(ItemStack itemStack) {
        return ItemTools.getItemTag(itemStack).map(value -> value.getInt(FISHING_COST) * itemStack.getAmount());
    }

    public int getChance() {
        return chance;
    }

    private int getSize() {
        var randomizer = new Randomizer();
        randomizer.add(3);
        randomizer.add(2);
        randomizer.add(1);
        return randomizer.random(new Random());
    }

    private Component getSizeComponent(int index) {
        return switch (index) {
            case 0 -> plugin.getLang().getSize().getSmall();
            case 1 -> plugin.getLang().getSize().getMedium();
            case 2 -> plugin.getLang().getSize().getBig();
            default -> null;
        };
    }

    @Override
    public ItemStack getItemStack() {
        var itemStack = Loot.getItem(item);
        var size = getSize();
        var compound = ItemTools.getOrCreateItemTag(itemStack);
        compound.putInt(FISHING_COST, cost * (size + 1));
        itemStack = ItemTools.setItemTag(itemStack, compound);
        Optional.ofNullable(itemStack.getItemMeta())
                .map(itemMeta -> itemMeta(itemMeta, getSizeComponent(size)))
                .ifPresent(itemStack::setItemMeta);
        return itemStack;
    }

    @Override
    public Category getCategory() {
        return Category.FISH;
    }

    public static FishLoot deserialize(Fishing plugin, JsonObject jsonObject) {
        return new FishLoot(
                plugin,
                jsonObject.get("chance").getAsInt(),
                jsonObject.get("item").getAsString(),
                MiniMessage.miniMessage().deserialize(jsonObject.get("title").getAsString()),
                jsonObject.get("cost").getAsInt()
        );
    }
}
