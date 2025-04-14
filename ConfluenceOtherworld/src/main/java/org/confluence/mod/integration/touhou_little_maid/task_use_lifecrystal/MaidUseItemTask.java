package org.confluence.mod.integration.touhou_little_maid.task_use_lifecrystal;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.item.common.EverBeneficialItem;

public class MaidUseItemTask extends Behavior<EntityMaid> {

    int cooldown = 0;
    int _cooldown = 20;

    public MaidUseItemTask() {
        super(ImmutableMap.of(), 10);

    }

    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        if (--cooldown > 0) {
            return false;
        }
        cooldown = _cooldown;
        var data = owner.getData(ModAttachmentTypes.EVER_BENEFICIAL.get());
        if (data.isLifeCrystalsMaximum()) return false;
        ItemStack stack = owner.getItemBySlot(EquipmentSlot.MAINHAND);
        return stack.getItem() == ConsumableItems.LIFE_CRYSTAL.get();
    }

    protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        return true;
    }

    protected void start(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        ItemStack stack = entityIn.getItemBySlot(EquipmentSlot.MAINHAND);
        if (stack.getItem() == ConsumableItems.LIFE_CRYSTAL.get()) {
            var data = entityIn.getData(ModAttachmentTypes.EVER_BENEFICIAL.get());
            if (data.increaseCrystals()) {
                var att = entityIn.getAttribute(Attributes.MAX_HEALTH);
                if (att != null) {
                    att.addOrReplacePermanentModifier(new AttributeModifier(EverBeneficialItem.LIFE_CRYSTAL.id(), getStepIncrement(), AttributeModifier.Operation.ADD_VALUE));
                    entityIn.playSound(ModSoundEvents.LIFE_CRYSTAL_USE.get());
                    entityIn.heal(getStepIncrement());
                    stack.shrink(1);
                    entityIn.setSwingingArms(true);
                }
            }
        }
    }

    protected void stop(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        entityIn.setSwingingArms(false);
    }

    public int getStepIncrement() {
        return 2;
    }
}