package com.github.laefye.fishing.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Lang implements ConfigurationSerializable {
    private final Component sold;
    private static final String SOLD = "sold";
    private static final String PLACEHOLDER_AMOUNT = "%amount%";

    private Lang(Component sold) {
        this.sold = sold;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        var data = new HashMap<String, Object>();
        data.put(SOLD, MiniMessage.miniMessage().serialize(sold));
        return data;
    }

    public static Lang deserialize(Map<String, Object> args) {
        return new Lang(
                Optional.ofNullable((String) args.get(SOLD))
                        .map(s -> MiniMessage.miniMessage().deserialize(s))
                        .orElse(Component.empty())
        );
    }

    public Component getSold(int amount) {
        return sold.replaceText(builder -> builder.match(PLACEHOLDER_AMOUNT).replacement(Integer.toString(amount)));
    }
}
