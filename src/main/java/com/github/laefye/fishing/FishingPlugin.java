package com.github.laefye.fishing;

import com.github.laefye.MagicPlugin;
import com.github.laefye.fishing.config.Lang;
import com.github.laefye.fishing.config.loottable.LootEntry;
import com.github.laefye.fishing.event.FishEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class FishingPlugin extends JavaPlugin {
    private final ArrayList<LootEntry> loots = new ArrayList<>();
    private Economy economy;
    private Lang lang;
    private MagicPlugin magicPlugin;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(new FishEvent(this), this);
        loadDependencies();
        Optional.ofNullable(getCommand("sell"))
                .ifPresent(pluginCommand -> pluginCommand.setExecutor(new SellCommand(this)));
    }

    private void saveDefaultLootTable() {
        var lootTable = new File(getDataFolder(), "loots.json");
        if (!lootTable.exists()) {
            lootTable.getParentFile().mkdirs();
            saveResource("loots.json", false);
        }
    }

    private JsonArray readLootTable() {
        var lootTable = new File(getDataFolder(), "loots.json");
        try {
            var reader = new FileReader(lootTable);
            var jsonArray = JsonParser.parseReader(reader);
            reader.close();
            if (jsonArray.isJsonArray()) {
                return jsonArray.getAsJsonArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    private void loadConfig() {
        saveDefaultConfig();
        saveDefaultLootTable();
        FileConfiguration config = getConfig();
        loots.clear();
        for (var loot : readLootTable()) {
            Optional.ofNullable(LootEntry.deserialize(this, loot.getAsJsonObject()))
                    .ifPresent(loots::add);
        }
        lang = Lang.deserialize(((MemorySection) config.get("lang")).getValues(false));
    }

    private void loadDependencies() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            var rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }
        magicPlugin = MagicPlugin.getInstance();
    }

    public Lang getLang() {
        return lang;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ItemStack getLoot() {
        if (this.loots.isEmpty()) {
            return ItemStack.empty();
        }
        var random = new Random();
        var rare = random.nextInt(1, 10);
        var loots = new ArrayList<>(this.loots.stream().filter(lootEntry -> lootEntry.getRare() < rare).toList());
        var loot = loots.get(random.nextInt(loots.size()));
        return loot.getItemEntry().getItemStack();
    }

    public void deposit(Player player, int amount) {
        if (economy == null) {
            getLogger().warning("There isn't Vault plugin");
            return;
        }
        economy.depositPlayer(player, amount);
    }

    public MagicPlugin getMagicPlugin() {
        return magicPlugin;
    }
}
