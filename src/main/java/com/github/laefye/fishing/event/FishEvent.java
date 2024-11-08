package com.github.laefye.fishing.event;

import com.github.laefye.fishing.FishingPlugin;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishEvent implements Listener {
    private final FishingPlugin plugin;

    public FishEvent(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!(event.getCaught() instanceof Item itemEntity)) {
                return;
            }
            itemEntity.setItemStack(plugin.getLoot());
        }
    }
}
