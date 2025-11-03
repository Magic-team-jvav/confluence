package org.confluence.mod.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RegisterArmorSetBonusEvent extends Event implements IModBusEvent {
    private final Registration registration;

    public RegisterArmorSetBonusEvent(Registration registration) {
        this.registration = registration;
    }

    public void register(
            ResourceLocation id,
            int tooltipCount,
            @Nullable ItemLike head,
            @Nullable ItemLike chest,
            @Nullable ItemLike legs,
            @Nullable ItemLike feet,
            Consumer<ArmorSetBonusKey> consumer
    ) {
        registration.register(id, tooltipCount, head, chest, legs, feet, consumer);
    }

    @FunctionalInterface
    public interface Registration {
        /**
         * @see ModArmorBonus#register(ResourceLocation, int, ItemLike, ItemLike, ItemLike, ItemLike, Consumer)
         */
        void register(
                ResourceLocation id,
                int tooltipCount,
                @Nullable ItemLike head,
                @Nullable ItemLike chest,
                @Nullable ItemLike legs,
                @Nullable ItemLike feet,
                Consumer<ArmorSetBonusKey> consumer
        );
    }
}
