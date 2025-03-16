package org.confluence.mod.common.item.sponsor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.common.TooltipItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.List;

public class KindMitaRingItem extends TooltipItem {

    public KindMitaRingItem() {
        super(new Properties(), ModRarity.COMMON, TooltipItem.getTooltipsFromString("kind_mita_ring", 2));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if (entity instanceof Player player && !player.isCreative() && player.isAlive()) {
                List<Monster> monsters = level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(8));
                for (Monster ignored : monsters) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false));
                }
            }
        }
    }
}
