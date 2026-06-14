package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.PathService;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class WireCutterItem extends TooltipItem {
    public static final ResourceLocation BASE_ID = Confluence.asResource("wire_cutter");

    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public WireCutterItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, "tooltip.item.confluence.wire_cutter.0");
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
        return WrenchItem.containsPos(pStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack itemStack = pContext.getItemInHand();
        BlockPos pPos = pContext.getClickedPos();
        if (level.getBlockEntity(pPos) instanceof INetworkEntity entity) {
            BlockPos storedPos = WrenchItem.readBlockPos(itemStack);
            if (storedPos == BlockPos.ZERO) {
                WrenchItem.writeBlockPos(itemStack, pPos);
            } else if (level.getBlockEntity(storedPos) instanceof INetworkEntity entity1) {
                entity.getConnectedPoses().int2ObjectEntrySet().stream()
                        .filter(entry -> entry.getValue().contains(storedPos))
                        .forEach(entry -> entity.disconnectWith(entry.getIntKey(), storedPos, entity1));

                entity1.getConnectedPoses().int2ObjectEntrySet().stream()
                        .filter(entry -> entry.getValue().contains(pPos))
                        .forEach(entry -> entity1.disconnectWith(entry.getIntKey(), pPos, entity));

                PathService.INSTANCE.onBlockEntityUnload(entity);
                WrenchItem.removeBlockPos(itemStack);
            }
            return InteractionResult.CONSUME;
        }
        WrenchItem.removeBlockPos(itemStack);
        return InteractionResult.PASS;
    }
}
