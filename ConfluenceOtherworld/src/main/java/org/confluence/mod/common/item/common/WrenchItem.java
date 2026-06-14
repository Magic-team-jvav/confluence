package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.PathService;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class WrenchItem extends CustomRarityItem {
    public static final ResourceLocation BASE_ID = Confluence.asResource("wrench");

    public final int color;
    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public WrenchItem(int color) {
        super(new Properties().stacksTo(1), ModRarity.BLUE);
        this.color = color;
        this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(PortAttributesExtension.blockInteractionRange().value(), new AttributeModifier(
                        PortAttributeModifier.rl2uuid(BASE_ID),
                        BASE_ID.getPath(),
                        20,
                        AttributeModifier.Operation.ADDITION
                )).build();
    }

    @Diff
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return defaultModifiers;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return containsPos(pStack);
    }

    public static boolean containsPos(ItemStack pStack) {
        return LibUtils.getItemStackNbtNoCopy(pStack).contains("blockPos");
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
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(itemStack);
        if (tag.contains("blockPos", Tag.TAG_COMPOUND)) {
            return NbtUtils.readBlockPos(tag.getCompound("blockPos"));
        }
        return BlockPos.ZERO;
    }

    public static void removeBlockPos(ItemStack itemStack) {
        LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.remove("blockPos"));
    }
}
