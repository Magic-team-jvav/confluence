package org.confluence.mod.common.item.common;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;

import java.util.function.Function;

public class EntityDisplayItem extends CustomRarityItem {
    public EntityDisplayItem() {
        super(new Properties(), ModRarity.MASTER);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            ItemStack itemStack = context.getItemInHand();
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemStack);
            if (tag != null) {
                Entity entity = EntityType.loadEntityRecursive(tag, level, Function.identity());
                if (entity != null) {
                    entity.setPos(context.getClickLocation());
                    Component customName = itemStack.get(DataComponents.CUSTOM_NAME);
                    if (customName != null) {
                        entity.setCustomName(customName);
                    }
                    level.addFreshEntity(entity);
                }
            }
            itemStack.shrink(1);
            if (itemStack.isEmpty() && context.getPlayer() != null) {
                context.getPlayer().setItemInHand(context.getHand(), ItemStack.EMPTY);
            }
            return InteractionResult.CONSUME_PARTIAL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag == null) return super.getName(stack);
        return EntityType.by(tag).orElse(EntityType.PIG).getDescription();
    }
}
