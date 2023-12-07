package com.github.laefye.fishing.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.MemorySection;

import java.util.Map;
import java.util.Optional;

public class Lang {
    public static class Size {
        private final Map<String, Object> sizes;

        private Size(Map<String, Object> sizes) {
            this.sizes = sizes;
        }

        public static Size deserialize(Map<String, Object> args) {
            return new Size(args);
        }

        public Component getSize(String name) {
            return MiniMessage.miniMessage().deserialize(Optional.ofNullable((String) sizes.get(name)).orElse(name));
        }
    }

    private final Component sold;
    private final Size size;

    private Lang(Component sold, Size size) {
        this.sold = sold;
        this.size = size;
    }

    public static Lang deserialize(Map<String, Object> args) {
        return new Lang(
                Optional.ofNullable((String) args.get("sold"))
                        .map(s -> MiniMessage.miniMessage().deserialize(s))
                        .orElse(Component.empty()),
                Optional.ofNullable((MemorySection) args.get("size"))
                        .map(memorySection -> memorySection.getValues(false))
                        .map(Size::deserialize)
                        .get()
        );
    }

    public Component getSold(int amount) {
        return sold.replaceText(builder -> builder.match("%amount%").replacement(Integer.toString(amount)));
    }

    public Size getSize() {
        return size;
    }
}
