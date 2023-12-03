package com.github.laefye.fishing.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LootType implements ConfigurationSerializable {
    private final Material item;
    private final Optional<Component> title;
    private enum LootCategory {
        FISH,
        ITEM,
    }
    private final LootCategory category;
    private final Optional<Integer> cost;

    private LootType(LootCategory category, Material item, Optional<Component> title, Optional<Integer> cost) {
        this.category = category;
        this.item = item;
        this.title = title;
        this.cost = cost;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("category", category == LootCategory.FISH ? "fish" : "item");
        data.put("item", item.name());
        title.ifPresent(component -> data.put("title", MiniMessage.miniMessage().serialize(component)));
        cost.ifPresent(integer -> data.put("cost", integer));
        return data;
    }

    public static LootType deserialize(Map<String, Object> args) {
        return new LootType(
                Optional.ofNullable((String) args.get("category"))
                        .filter(s -> s.equals("fish"))
                        .map(s -> LootCategory.FISH)
                        .orElse(LootCategory.ITEM),
                Optional.ofNullable((String) args.get("item"))
                        .map(Material::matchMaterial)
                        .orElse(Material.COD),
                Optional.ofNullable((String) args.get("title")).map(s -> MiniMessage.miniMessage().deserialize(s)),
                Optional.ofNullable((Integer) args.get("cost"))
        );
    }

    private ItemMeta itemMeta(ItemMeta itemMeta) {
        itemMeta.displayName(
                Component.text()
                        .decoration(TextDecoration.ITALIC, false)
                        .append(title.orElse(Component.empty()))
                        .build()
        );
        return itemMeta;
    }

    public ItemStack itemStack() {
        var itemStack = new ItemStack(item);
        if (category == LootCategory.FISH) {
            Optional.ofNullable(itemStack.getItemMeta()).map(this::itemMeta).ifPresent(itemStack::setItemMeta);
            var nms = CraftItemStack.unwrap(itemStack);
            cost.ifPresent(integer -> nms.w().a("fishingCost", integer));
            itemStack = CraftItemStack.asCraftMirror(nms);
        }
        return itemStack;
    }

    @Override
    public String toString() {
        return "LootType{" +
                "item=" + item +
                ", title=" + title +
                ", category=" + category +
                '}';
    }

    public static Optional<Integer> getCost(ItemStack itemStack) {
        var stack = CraftItemStack.unwrap(itemStack);
        var tagCompound = stack.v();
        if (tagCompound != null) {
            return Optional.of(tagCompound.h("fishingCost") * itemStack.getAmount());
        }
        return Optional.empty();
    }
}
