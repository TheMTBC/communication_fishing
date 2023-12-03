package com.github.laefye.fishing.event;

import com.github.laefye.fishing.Fishing;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishEvent implements Listener {
    private final Fishing plugin;

    public FishEvent(Fishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!(event.getCaught() instanceof CraftItem itemEntity)) {
                return;
            }
            itemEntity.setItemStack(plugin.getLoot(itemEntity.getItemStack()));
        }
    }
}
