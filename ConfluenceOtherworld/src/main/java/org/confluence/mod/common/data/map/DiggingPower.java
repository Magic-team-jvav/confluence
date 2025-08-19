package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.GetCustomDiggingPowerEvent;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModTiers;

/**
 * 一般用于镐子
 */
public record DiggingPower(int power) {
    public static final Codec<DiggingPower> CODEC = ExtraCodecs.POSITIVE_INT.xmap(DiggingPower::new, DiggingPower::power);

    public static int getPower(ItemStack itemStack) {
        int power = -1;
        DiggingPower diggingPower = itemStack.getItemHolder().getData(ModDataMaps.DIGGING_POWER);
        if (diggingPower == null) {
            if (itemStack.getItem() instanceof TieredItem tieredItem) {
                Tier tier = tieredItem.getTier();
                if (tier instanceof ModTiers.PoweredTier poweredTier) {
                    power = poweredTier.getPower();
                } else if (tier instanceof Tiers tiers) {
                    power = ModTiers.getPowerForVanillaTiers(tiers);
                }
            }
        } else {
            power = diggingPower.power;
        }
        return NeoForge.EVENT_BUS.post(new GetCustomDiggingPowerEvent(itemStack, power)).getPower();
    }
}
