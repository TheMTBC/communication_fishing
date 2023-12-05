package com.github.laefye.fishing.config.loottable;

import com.github.laefye.craft.Compound;
import com.github.laefye.craft.ItemTools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class ItemLoot implements ILootType {
    private final int chance;
    private final String item;
    private final Component title;
    private final JsonObject nbt;

    private ItemLoot(int chance, String item, Component title, JsonObject nbt) {
        this.chance = chance;
        this.item = item;
        this.title = title;
        this.nbt = nbt;
    }

    private ItemMeta itemMeta(ItemMeta itemMeta) {
        if (title == null) {
            return itemMeta;
        }
        itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(title)
                        .build()
        );
        return itemMeta;
    }

    public int getChance() {
        return chance;
    }

    @Override
    public ItemStack getItemStack() {
        var originalItemStack = Loot.getItem(item);
        var itemStack = Optional.ofNullable(nbt)
                .map(Compound::fromJsonObject)
                .map(compound -> ItemTools.setItemTag(originalItemStack, compound))
                .orElse(originalItemStack);
        Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
        return itemStack;
    }

    @Override
    public Category getCategory() {
        return Category.ITEM;
    }

    public static ItemLoot deserialize(JsonObject jsonObject) {
        return new ItemLoot(
                jsonObject.get("chance").getAsInt(),
                jsonObject.get("item").getAsString(),
                Optional.ofNullable(jsonObject.get("title"))
                        .map(JsonElement::getAsString)
                        .map(s -> MiniMessage.miniMessage().deserialize(s))
                        .orElse(null),
                Optional.ofNullable(jsonObject.get("nbt"))
                        .map(JsonElement::getAsJsonObject)
                        .orElse(null)
        );
    }
}
