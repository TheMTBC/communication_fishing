package com.github.laefye.fishing.config.loottable;

import com.github.laefye.fishing.FishingPlugin;
import com.google.gson.JsonObject;

public class LootEntry {
    private final ItemEntry itemEntry;
    private final double rare;

    public LootEntry(ItemEntry itemEntry, double rare) {
        this.itemEntry = itemEntry;
        this.rare = rare;
    }

    public static LootEntry deserialize(FishingPlugin plugin, JsonObject jsonObject) {
        return new LootEntry(
                ItemEntry.deserialize(plugin, jsonObject.get("item").getAsJsonObject()),
                jsonObject.get("rare").getAsInt()
        );

    }

    public ItemEntry getItemEntry() {
        return itemEntry;
    }

    public double getRare() {
        return rare;
    }
}
