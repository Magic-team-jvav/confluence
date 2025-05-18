package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.PathService;

import java.util.Collections;

public class WrenchItem extends CustomRarityItem {
    public final int color;

    public WrenchItem(int color) {
        super(new Properties().stacksTo(1).attributes(new ItemAttributeModifiers(Collections.singletonList(new ItemAttributeModifiers.Entry(
                Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(Confluence.asResource("wrench"), 20, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
        )), true)), ModRarity.BLUE);
        this.color = color;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return containsPos(pStack);
    }

    public static boolean containsPos(ItemStack pStack) {
        return LibUtils.getItemStackNbt(pStack).contains("blockPos");
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level pLevel = pContext.getLevel();
        if (pLevel.isClientSide) return InteractionResult.SUCCESS;

        ItemStack itemStack = pContext.getItemInHand();
        BlockPos pPos = pContext.getClickedPos();
        if (pLevel.getBlockEntity(pPos) instanceof INetworkEntity entity) {
            BlockPos storedPos = readBlockPos(itemStack);
            if (storedPos == BlockPos.ZERO) {
                writeBlockPos(itemStack, pPos);
            } else if (pLevel.getBlockEntity(storedPos) instanceof INetworkEntity entity1) {
                if (entity1.getConnectedPoses().int2ObjectEntrySet().stream()
                        .noneMatch(entry -> entry.getIntKey() == color && entry.getValue().contains(pPos))
                ) {
                    entity.connectTo(color, storedPos, entity1);
                    PathService.INSTANCE.addToQueue(entity);
                }
                removeBlockPos(itemStack);
            }
            return InteractionResult.CONSUME;
        }
        removeBlockPos(itemStack);
        return InteractionResult.PASS;
    }

    public static void writeBlockPos(ItemStack itemStack, BlockPos pos) {
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.put("blockPos", NbtUtils.writeBlockPos(pos)));
    }

    public static BlockPos readBlockPos(ItemStack itemStack) {
        return NbtUtils.readBlockPos(LibUtils.getItemStackNbt(itemStack), "blockPos").orElse(BlockPos.ZERO);
    }

    public static void removeBlockPos(ItemStack itemStack) {
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.remove("blockPos"));
    }
}
