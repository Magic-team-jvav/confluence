package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.mod.common.init.armor.ArmorSetBonusData;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.jetbrains.annotations.Nullable;

public class GetArmorSetBonusDataEvent extends PlayerEvent {
    private final ArmorSetBonusKey key;
    private final @Nullable ArmorSetBonusData originalData;
    private @Nullable ArmorSetBonusData neoData;

    public GetArmorSetBonusDataEvent(Player player, ArmorSetBonusKey key, @Nullable ArmorSetBonusData originalData) {
        super(player);
        this.key = key;
        this.originalData = originalData;
        this.neoData = originalData;
    }

    public ArmorSetBonusKey getKey() {
        return key;
    }

    public @Nullable ArmorSetBonusData getOriginalData() {
        return originalData;
    }

    public void setNeoData(@Nullable ArmorSetBonusData neoData) {
        this.neoData = neoData;
    }

    public @Nullable ArmorSetBonusData getNeoData() {
        return neoData;
    }
}
