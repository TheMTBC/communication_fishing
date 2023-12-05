package com.github.laefye.fishing.config;

import com.github.laefye.fishing.Fishing;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Loot {
    public static ItemStack getItem(String id) {
        return id.startsWith("#") ?
                Fishing.getCustomItemService(Bukkit.getServer()).give(id.substring(1))
                        .orElse(ItemStack.empty()) :
                new ItemStack(Material.valueOf(id));
    }

    public static ILootType deserialize(JsonObject jsonObject) {
        var category = jsonObject.get("category").getAsString();
        if (category.equals("fish")) {
            return FishLoot.deserialize(jsonObject);
        }
        if (category.equals("item")) {
            return ItemLoot.deserialize(jsonObject);
        }
        return null;
    }
}
