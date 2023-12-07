package com.github.laefye.fishing;

import com.github.laefye.fishing.config.loottable.ItemEntry;
import com.github.laefye.fishing.ui.Sell;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SellCommand implements CommandExecutor {
    private final FishingPlugin plugin;

    public SellCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    private int sell(Player player) {
        int amount = 0;
        var stacks = new ArrayList<ItemStack>();
        for (var item : player.getInventory()) {
            var cost = ItemEntry.getCost(item);
            if (cost <= 0) {
                continue;
            }
            amount += cost;
            stacks.add(item);
        }
        stacks.forEach(itemStack -> player.getInventory().remove(itemStack));
        plugin.deposit(player, amount);
        return amount;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }
//        int amount = sell(player);
//        if (amount > 0) {
//            player.sendMessage(plugin.getLang().getSold(amount));
//        }
        plugin.getMagicPlugin().getUiManager().open(player, new Sell(plugin));
        return true;
    }
}
