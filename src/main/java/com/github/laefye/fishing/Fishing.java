package com.github.laefye.fishing;

import com.github.laefye.fishing.config.LootType;
import com.github.laefye.fishing.event.FishEvent;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Fishing extends JavaPlugin {
    private final ArrayList<LootType> lootTypes = new ArrayList<>();

    @Override
    public void onEnable() {
        loadConfig();
        getCommand("fishing").setExecutor((commandSender, command, s, strings) -> {
            return true;
        });
        getServer().getPluginManager().registerEvents(new FishEvent(this), this);
    }

    private void loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        lootTypes.clear();
        for (Object d : Optional.ofNullable(
                (List) config.getList("loots")).orElse(List.of()
        )) {
            if (!(d instanceof Map map)) {
                continue;
            }
            lootTypes.add(LootType.deserialize(map));
            getLogger().info("Loot type -> " + lootTypes.get(lootTypes.size() - 1));
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ItemStack getLoot(ItemStack def) {
        var random = new Random();
        return lootTypes.get(random.nextInt(lootTypes.size())).itemStack(def);
    }
}
