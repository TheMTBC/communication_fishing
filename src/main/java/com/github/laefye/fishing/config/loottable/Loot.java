package com.github.laefye.fishing.config.loottable;

import com.github.laefye.fishing.FishingPlugin;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Loot {
    public static ItemStack getItem(String id) {
        return id.startsWith("#") ?
                FishingPlugin.getCustomItemService(Bukkit.getServer()).give(id.substring(1))
                        .orElse(ItemStack.empty()) :
                new ItemStack(Material.valueOf(id));
    }

    public static ILootType deserialize(FishingPlugin plugin, JsonObject jsonObject) {
        var category = jsonObject.get("category").getAsString();
        if (category.equals("fish")) {
            return FishLoot.deserialize(plugin, jsonObject);
        }
        if (category.equals("item")) {
            return ItemLoot.deserialize(jsonObject);
        }
        return null;
    }
}
