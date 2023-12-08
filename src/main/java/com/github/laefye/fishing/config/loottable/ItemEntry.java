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
import java.util.stream.Stream;

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

    private ItemStack getItem(String id) {
        return id.startsWith("#") ?
                plugin.getMagicPlugin().getItemManager().give(id.substring(1))
                        .orElse(ItemStack.empty()) :
                new ItemStack(Material.valueOf(id));
    }

    private static class Transaction {

        public ItemStack itemStack;
        public Component title;
        public List<Component> lore;
        public Compound compound;

        private Transaction(ItemStack itemStack, Component title, List<Component> lore, Compound compound) {
            this.itemStack = itemStack;
            this.title = title;
            this.lore = lore;
            this.compound = compound;
        }

        private ItemMeta itemMeta(ItemMeta itemMeta) {
            Optional.ofNullable(title).map(component -> Component.text().decoration(TextDecoration.ITALIC, false)
                    .append(component).build())
                    .ifPresent(itemMeta::displayName);
            Optional.ofNullable(lore)
                    .map(components -> lore.stream().map(component -> Component.text().decoration(TextDecoration.ITALIC, false).append(component).build()))
                    .map(Stream::toList)
                    .ifPresent(itemMeta::lore);
            return itemMeta;
        }

        public ItemStack transact() {
            var itemStack = Optional.ofNullable(compound)
                    .map(compound -> ItemTools.setItemTag(this.itemStack, compound))
                    .orElse(this.itemStack);
            Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
            return itemStack;
        }
    }

    public ItemStack getItemStack() {
        var transaction = new Transaction(getItem(material), title, null, new Compound());
        Optional.ofNullable(nbt).ifPresent(jsonObject -> Compound.appendFromJsonObject(transaction.compound, jsonObject));
        if (costBySize != null) {
            var random = new Random();
            var size = costBySize.keySet().stream().toList().get(random.nextInt(costBySize.size()));
            transaction.compound.putInt(FISHING_COST, costBySize.get(size).getAsInt());
            transaction.lore = List.of(plugin.getLang().getSize().getSize(size));
        } else if (cost != null) {
            transaction.compound.putInt(FISHING_COST, cost);
        }
        return transaction.transact();
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
