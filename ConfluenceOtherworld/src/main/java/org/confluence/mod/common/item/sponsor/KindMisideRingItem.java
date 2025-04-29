package org.confluence.mod.common.item.sponsor;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;

import java.util.List;

public class KindMisideRingItem extends TooltipItem {

    public KindMisideRingItem() {
        super(new Properties(), ModRarity.COMMON, TooltipItem.getTooltipsFromString("kind_miside_ring", 1, ChatFormatting.GRAY));
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
