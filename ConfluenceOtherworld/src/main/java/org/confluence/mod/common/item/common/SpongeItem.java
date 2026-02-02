package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;

import java.util.Collections;
import java.util.function.Predicate;

public class SpongeItem extends TooltipItem {
    private final Predicate<BlockState> fluidPredicate;

    public SpongeItem(ModRarity rarity, String name, int blockInteractionRange, Predicate<BlockState> fluidPredicate) {
        super(new Properties().stacksTo(1).attributes(new ItemAttributeModifiers(Collections.singletonList(
                new ItemAttributeModifiers.Entry(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(
                        Confluence.asResource(name), blockInteractionRange, AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)), true)), rarity, "tooltip.item.confluence." + name + ".0");
        this.fluidPredicate = fluidPredicate;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }
        BlockPos blockPos = hitResult.getBlockPos();
        if (!level.mayInteract(player, blockPos)) return InteractionResultHolder.fail(itemStack);
        if (tryAbsorbLiquid(level, blockPos)) return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
        return InteractionResultHolder.fail(itemStack);
    }

    public boolean tryAbsorbLiquid(Level level, BlockPos blockPos) {
        boolean absorbed = false;
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-2, -2, -2), blockPos.offset(2, 2, 2))) {
            if (fluidPredicate.test(level.getBlockState(pos))) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                absorbed = true;
            }
        }
        return absorbed;
    }
}
