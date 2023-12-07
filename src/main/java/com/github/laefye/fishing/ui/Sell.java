package com.github.laefye.fishing.ui;

import com.github.laefye.fishing.FishingPlugin;
import com.github.laefye.fishing.config.loottable.ItemEntry;
import com.github.laefye.services.ui.Ui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

public class Sell extends Ui {
    private final FishingPlugin plugin;
    private final BukkitTask task;
    private boolean sold = false;

    public Sell(FishingPlugin plugin) {
        super(Bukkit.createInventory(null, 54));
        this.plugin = plugin;
        for (int i = 0; i < 54; i++) {
            if (checkSlotInBorder(i)) {
                inventory.setItem(i, new ItemStack(Material.PINK_STAINED_GLASS_PANE));
            }
        }
        recalculate();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::recalculate, 0L, 10L);
    }

    private int calculate() {
        int amount = 0;
        for (int i = 0; i < 54; i++) {
            amount += ItemEntry.getCost(inventory.getItem(i));
        }
        return amount;
    }

    private void recalculate() {
        var button = new ItemStack(Material.FISHING_ROD);
        Optional.ofNullable(button.getItemMeta()).ifPresent(itemMeta -> {
            itemMeta.displayName(Component.text("Sell for " + calculate() + " coins"));
            button.setItemMeta(itemMeta);
        });
        inventory.setItem(49, button);
    }

    @Override
    public void click(InventoryClickEvent event) {
        if (event.getClickedInventory() != inventory) {
            return;
        }
        if (event.getSlot() == 49 && calculate() > 0) {
            event.setCancelled(true);
            int amount = calculate();
            event.getWhoClicked().sendMessage(plugin.getLang().getSold(amount));
            plugin.deposit((Player) event.getWhoClicked(), amount);
            sold = true;
            inventory.close();
            return;
        }
        if (checkSlotInBorder(event.getSlot())) {
            event.setCancelled(true);
            return;
        }
        if (ItemEntry.getCost(event.getCursor()) <= 0 && !event.getCursor().isEmpty()) {
            event.setCancelled(true);
        }
    }

    private boolean checkSlotInBorder(int slot) {
        int y = slot / 9;
        int x = slot % 9;
        return y == 0 || y == 5 || x == 0 || x == 8;
    }

    @Override
    public void drag(InventoryDragEvent event) {
        var items = event.getNewItems();
        long count = items.keySet().stream().filter(integer -> checkSlotInBorder(integer) || ItemEntry.getCost(items.get(integer)) <= 0).count();
        if (count > 0) {
            event.setCancelled(true);
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        task.cancel();
        if (!sold) {
            for (int i = 0; i < 54; i++) {
                var item = inventory.getItem(i);
                if (!checkSlotInBorder(i) && item != null) {
                    event.getPlayer().getInventory().addItem(item);
                }
            }
        }
    }
}
