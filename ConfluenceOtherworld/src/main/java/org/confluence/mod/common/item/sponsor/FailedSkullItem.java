package org.confluence.mod.common.item.sponsor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.init.block.ModBlocks;

import java.util.List;

public class FailedSkullItem extends StandingAndWallBlockItem implements Equipable {
    public FailedSkullItem() {
        super(ModBlocks.FAILED_SKULL.get(), ModBlocks.FAILED_SKULL_WALL.get(), new Properties().fireResistant(), Direction.DOWN);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && stack.is(this) && slotId == 39) {
            if (entity instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false));
                if (level.dimension() == Level.NETHER && !player.isCreative() && player.isAlive()) {
                    List<Piglin> piglinList = level.getEntitiesOfClass(Piglin.class, player.getBoundingBox().inflate(8));
                    for (Piglin piglin : piglinList) {
                        piglin.setTarget(player);
                    }
                }
            }
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(TooltipItem.getTooltipsFromString("failed_skull", 1, ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
