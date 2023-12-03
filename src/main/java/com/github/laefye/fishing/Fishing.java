package com.github.laefye.fishing;

import com.github.laefye.fishing.config.Lang;
import com.github.laefye.fishing.config.LootType;
import com.github.laefye.fishing.event.FishEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Fishing extends JavaPlugin {
    private final ArrayList<LootType> lootTypes = new ArrayList<>();
    private Economy economy;
    private Lang lang;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(new FishEvent(this), this);
        loadDependencies();
        Optional.ofNullable(getCommand("sell"))
                .ifPresent(pluginCommand -> pluginCommand.setExecutor(new SellCommand(this)));
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
        lang = Lang.deserialize(((MemorySection) config.get("lang")).getValues(false));
    }

    private void loadDependencies() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }
    }

    public Lang getLang() {
        return lang;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ItemStack getLoot() {
        var random = new Random();
        return lootTypes.get(random.nextInt(lootTypes.size())).itemStack();
    }

    public void deposit(Player player, int amount) {
        if (economy == null) {
            getLogger().warning("There isn't Vault plugin");
            return;
        }
        economy.depositPlayer(player, amount);
    }
}
