package org.confluence.mod.common.item.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class HammerItem extends DiggerItem {
    public HammerItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public HammerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMMER, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }

    public HammerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, ModTags.Blocks.MINEABLE_WITH_HAMMER, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4, consumer)));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        hammerMineBlock(stack, level, state, pos, entity);
        return true;
    }

    public static void hammerMineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide) {
            int destroyCount = 1;
            if (entity instanceof Player player) {
                BlockHitResult picked = (BlockHitResult) player.pick(10, 1.0F, true);
                boolean xOff = true, yOff = true, zOff = true;
                switch (picked.getDirection()) {
                    case NORTH, SOUTH -> zOff = false;
                    case WEST, EAST -> xOff = false;
                    default -> yOff = false;
                }
                destroyCount += iteForBlocks(level, player, pos, xOff, yOff, zOff, state.getDestroySpeed(level, pos) * 1.5F);
            }
            if (state.getDestroySpeed(level, pos) != 0.0F) {
                stack.hurtAndBreak(destroyCount, entity, EquipmentSlot.MAINHAND);
            }
        }
    }

    /**
     * Scan 3*1*3 blocks related to the given pos.
     */
    public static int iteForBlocks(Level level, Player player, BlockPos pos, boolean xOff, boolean yOff, boolean zOff, float speedOff) {
        Stream<BlockPos> posStream = BlockPos.betweenClosedStream(pos.offset(xOff ? 1 : 0, yOff ? 1 : 0, zOff ? 1 : 0), pos.offset(xOff ? -1 : 0, yOff ? -1 : 0, zOff ? -1 : 0));
        return (int) posStream.filter(pos1 -> !pos1.equals(pos))
                .map(pos1 -> applyBlockDestroy(level, pos1, player, speedOff))
                .filter(destroyed -> destroyed)
                .count();
    }

    /**
     * If the target block can hardly be break, skip it.
     *
     * @param pos      The current producing block's pos(Target pos).
     * @param speedOff Related block's destroy speed * 1.5 (satisfied range).
     * @return TRUE, if the block has been broke, otherwise return FALSE.
     */
    public static boolean applyBlockDestroy(Level level, BlockPos pos, Player player, float speedOff) {
        BlockState targetState = level.getBlockState(pos);
        float targetSpeed = targetState.getDestroySpeed(level, pos);
        boolean flag1 = targetState.canHarvestBlock(level, pos, player);
        boolean flag2 = speedOff > 0 ? targetSpeed >= 0 && speedOff >= targetSpeed : targetSpeed >= speedOff;
        boolean flag3 = player.hasInfiniteMaterials();
        if (flag1 && flag2 || flag3) {
            level.destroyBlock(pos, flag1, player);
            return true;
        }
        return false;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
}
