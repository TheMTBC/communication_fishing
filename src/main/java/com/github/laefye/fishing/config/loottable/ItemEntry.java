package com.github.laefye.fishing.config.loottable;

import com.github.laefye.craft.Compound;
import com.github.laefye.craft.ItemTools;
import com.github.laefye.fishing.FishingPlugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ItemEntry {
    private final String material;
    private final Component title;
    private final JsonObject nbt;
    private final Integer cost;
    private final JsonObject costBySize;
    private static final String FISHING_COST = "fishingCost";
    private final FishingPlugin plugin;


    private ItemEntry(FishingPlugin plugin, String material, Component title, JsonObject nbt, Integer cost, JsonObject costBySize) {
        this.plugin = plugin;
        this.material = material;
        this.title = title;
        this.nbt = nbt;
        this.cost = cost;
        this.costBySize = costBySize;
    }

    private ItemMeta itemMeta(ItemMeta itemMeta, Component size) {
        if (title == null) {
            return itemMeta;
        }
        itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(title)
                        .build()
        );
        if (size != null) {
            itemMeta.lore(List.of(
                    Component.text()
                            .decoration(TextDecoration.ITALIC, false)
                            .append(size)
                            .build()
            ));
        }
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
        AtomicReference<String> size = new AtomicReference<>(null);
        Optional.ofNullable(cost)
                .ifPresent(integer -> compound.putInt(FISHING_COST, cost));
        Optional.ofNullable(costBySize)
                .ifPresent(jsonObject -> {
                    var random = new Random();
                    var sz = jsonObject.keySet().stream().toList().get(random.nextInt(jsonObject.size()));
                    size.set(sz);
                    compound.putInt(FISHING_COST, jsonObject.get(sz).getAsInt());
                });
        Optional.ofNullable(nbt)
                .ifPresent(jsonObject -> Compound.appendFromJsonObject(compound, jsonObject));
        itemStack = ItemTools.setItemTag(itemStack, compound);;
        Optional.ofNullable(itemStack.getItemMeta())
                .map(itemMeta -> itemMeta(itemMeta, Optional.ofNullable(size.get())
                        .map(sz -> plugin.getLang().getSize().getSize(sz))
                        .orElse(Component.text(size.get()))))
                .ifPresent(itemStack::setItemMeta);
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
                        .filter(JsonElement::isJsonPrimitive)
                        .map(JsonElement::getAsInt)
                        .orElse(null),
                Optional.ofNullable(jsonObject.get("cost"))
                        .filter(JsonElement::isJsonObject)
                        .map(JsonElement::getAsJsonObject)
                        .orElse(null));
    }

    public static int getCost(ItemStack itemStack) {
        return ItemTools.getItemTag(itemStack).map(compound -> compound.getInt(FISHING_COST) * itemStack.getAmount()).orElse(0);
    }
}
