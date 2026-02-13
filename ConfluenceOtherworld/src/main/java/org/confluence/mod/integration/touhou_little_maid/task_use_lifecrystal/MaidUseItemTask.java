package org.confluence.mod.integration.touhou_little_maid.task_use_lifecrystal;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.jetbrains.annotations.NotNull;

public class MaidUseItemTask extends Behavior<EntityMaid> {

    int cooldown = 0;
    int _cooldown = 20;

    public MaidUseItemTask() {
        super(ImmutableMap.of(), 10);
    }

    protected boolean checkExtraStartConditions(@NotNull ServerLevel worldIn, @NotNull EntityMaid owner) {
        if (--cooldown > 0) return false;
        cooldown = _cooldown;

        ItemStack stack = owner.getItemBySlot(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof EverBeneficialItem beneficialItem) {
            EverBeneficial data = EverBeneficial.of(owner);
            return data.getLevel(beneficialItem.getStorageKey()) < beneficialItem.getMaxLevel();
        }
        return false;
    }

    protected void start(@NotNull ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        ItemStack stack = entityIn.getItemBySlot(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof EverBeneficialItem beneficialItem) {
            EverBeneficial data = EverBeneficial.of(entityIn);
            if (data.tryIncrease(beneficialItem.getStorageKey(), beneficialItem.getMaxLevel())) {
                beneficialItem.apply(entityIn, data);
                beneficialItem.getExtraEffect().accept(entityIn, false);
                worldIn.playSound(null, entityIn.getX(), entityIn.getY(), entityIn.getZ(), beneficialItem.getUseSound().get(), entityIn.getSoundSource(), 1.0F, 1.0F);
                stack.shrink(1);
                entityIn.setSwingingArms(true);
            }
        }
    }

    protected void stop(@NotNull ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        entityIn.setSwingingArms(false);
    }
}
