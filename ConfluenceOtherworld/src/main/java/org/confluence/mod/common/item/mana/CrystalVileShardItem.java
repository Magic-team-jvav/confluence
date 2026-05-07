package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

public class CrystalVileShardItem extends ManaStaffItem<CrystalVileShardProjectile> {
    public static final float ARMOR_PENETRATION = 10;

    public CrystalVileShardItem() {
        super(ModRarity.PINK, CrystalVileShardProjectile::new, 17.5F, 13, 7.0F, 22, 0.04);
        withTooltip(Component.translatable("tooltip.confluence.armor_penetration", ARMOR_PENETRATION).withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected SoundEvent getShootSound() {
        return ModSoundEvents.CRYSTAL_VILE_SHARD_SHOOT.get();
    }
}
