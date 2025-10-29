package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.jetbrains.annotations.Nullable;

public class GetArmorSetBonusEvent extends PlayerEvent {
    private final ArmorSetBonusKey key;
    private final @Nullable PrimitiveValueComponent originalBonus;
    private @Nullable PrimitiveValueComponent neoBonus;

    public GetArmorSetBonusEvent(Player player, ArmorSetBonusKey key, @Nullable PrimitiveValueComponent originalBonus) {
        super(player);
        this.key = key;
        this.originalBonus = originalBonus;
        this.neoBonus = originalBonus;
    }

    public ArmorSetBonusKey getKey() {
        return key;
    }

    public @Nullable PrimitiveValueComponent getOriginalBonus() {
        return originalBonus;
    }

    public void setNeoBonus(@Nullable PrimitiveValueComponent neoBonus) {
        this.neoBonus = neoBonus;
    }

    public @Nullable PrimitiveValueComponent getNeoBonus() {
        return neoBonus;
    }
}
