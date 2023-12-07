package com.github.laefye.fishing.config.loottable;

import com.github.laefye.craft.Compound;
import com.github.laefye.craft.ItemTools;
import com.github.laefye.fishing.FishingPlugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class ItemEntry {
    private final String material;
    private final Component title;
    private final JsonObject nbt;
    private final Integer cost;
    private static final String FISHING_COST = "fishingCost";
    private final FishingPlugin plugin;


    private ItemEntry(FishingPlugin plugin, String material, Component title, JsonObject nbt, Integer cost) {
        this.plugin = plugin;
        this.material = material;
        this.title = title;
        this.nbt = nbt;
        this.cost = cost;
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

    private ItemStack getItem(String id) {
        return id.startsWith("#") ?
                plugin.getMagicPlugin().getItemManager().give(id.substring(1))
                        .orElse(ItemStack.empty()) :
                new ItemStack(Material.valueOf(id));
    }

    public ItemStack getItemStack() {
        var itemStack = getItem(material);
        var compound = new Compound();
        Optional.ofNullable(cost)
                .ifPresent(integer -> compound.putInt(FISHING_COST, cost));
        Optional.ofNullable(nbt)
                .ifPresent(jsonObject -> Compound.appendFromJsonObject(compound, jsonObject));
        itemStack = ItemTools.setItemTag(itemStack, compound);;
        Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
        return itemStack;
    }

    public static ItemEntry deserialize(FishingPlugin plugin, JsonObject jsonObject) {
        return new ItemEntry(
                plugin,
                jsonObject.get("material").getAsString(),
                Optional.ofNullable(jsonObject.get("title"))
                        .map(JsonElement::getAsString)
                        .map(s -> MiniMessage.miniMessage().deserialize(s))
                        .orElse(null),
                Optional.ofNullable(jsonObject.get("nbt"))
                        .map(JsonElement::getAsJsonObject)
                        .orElse(null),
                Optional.ofNullable(jsonObject.get("cost"))
                        .map(JsonElement::getAsInt)
                        .orElse(null)
        );
    }

    public static int getCost(ItemStack itemStack) {
        return ItemTools.getItemTag(itemStack).map(compound -> compound.getInt(FISHING_COST) * itemStack.getAmount()).orElse(0);
    }
}
