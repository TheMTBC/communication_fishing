package com.github.laefye.fishing.config;

import org.bukkit.inventory.ItemStack;

public interface ILootType {
    enum Category {
        FISH,
        ITEM,
    }

    int getChance();

    ItemStack getItemStack();

    Category getCategory();
}
