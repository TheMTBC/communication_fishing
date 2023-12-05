package com.github.laefye.fishing.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.MemorySection;

import java.util.Map;
import java.util.Optional;

public class Lang {
    public static class Size {
        private final Component small;
        private final Component medium;
        private final Component big;

        private Size(Component small, Component medium, Component big) {
            this.small = small;
            this.medium = medium;
            this.big = big;
        }

        public static Size deserialize(Map<String, Object> args) {
            return new Size(
                    Optional.ofNullable((String) args.get("small"))
                            .map(s -> MiniMessage.miniMessage().deserialize(s))
                            .orElse(Component.empty()),
                    Optional.ofNullable((String) args.get("medium"))
                            .map(s -> MiniMessage.miniMessage().deserialize(s))
                            .orElse(Component.empty()),
                    Optional.ofNullable((String) args.get("big"))
                            .map(s -> MiniMessage.miniMessage().deserialize(s))
                            .orElse(Component.empty())
                );
        }

        public Component getBig() {
            return big;
        }

        public Component getMedium() {
            return medium;
        }

        public Component getSmall() {
            return small;
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
