package org.confluence.mod.common.item.sponsor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class KindMitaRingItem extends Item {
    public KindMitaRingItem() {
        super(new Properties());
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(getTooltipsFromString("kind_mita_ring", 2));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static List<Component> getTooltipsFromString(String id, int lineCount){
        List<Component> components = new ArrayList<>();
        for (int i = 1; i <= lineCount; i++){
            components.add(Component.translatable("item.confluence." + id + ".tooltip." + i).withStyle(ChatFormatting.DARK_GRAY));
        }
        return components;
    }
}
