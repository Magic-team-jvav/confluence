package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BaseHammerItem extends DiggerItem {
    private @Nullable TooltipComponent component;
    private boolean hasImage;

    public BaseHammerItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BaseHammerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_HAMMER, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public BaseHammerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<PortItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), tier, ModTags.Blocks.MINEABLE_WITH_HAMMER, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    public BaseHammerItem hasImage() {
        this.hasImage = true;
        return this;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && hasImage) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        hammerMineBlock(stack, level, state, pos, entity);
        return true;
    }

    public static void hammerMineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (level.isClientSide) return;
        int destroyCount = 1;
        if (entity instanceof Player player) {
            BlockHitResult picked = (BlockHitResult) player.pick(10, 1.0F, true);
            boolean xOff = true, yOff = true, zOff = true;
            switch (picked.getDirection()) {
                case NORTH, SOUTH -> zOff = false;
                case WEST, EAST -> xOff = false;
                default -> yOff = false;
            }
            destroyCount += iteForBlocks(level, player, pos, xOff, yOff, zOff, state.getDestroyProgress(player, level, pos) * 1.5F, stack);
            if (state.getDestroyProgress(player, level, pos) != 0.0F) {
                stack.hurtAndBreak(destroyCount, entity, EquipmentSlot.MAINHAND);
            }
        }
    }

    /**
     * Scan 3*1*3 blocks related to the given pos.
     */
    public static int iteForBlocks(Level level, Player player, BlockPos pos, boolean xOff, boolean yOff, boolean zOff, float speedOff, ItemStack stack) {
        Stream<BlockPos> posStream = BlockPos.betweenClosedStream(pos.offset(xOff ? 1 : 0, yOff ? 1 : 0, zOff ? 1 : 0), pos.offset(xOff ? -1 : 0, yOff ? -1 : 0, zOff ? -1 : 0));
        return (int) posStream.filter(pos1 -> !pos1.equals(pos))
                .filter(pos1 -> applyBlockDestroy(level, pos1, player, speedOff, stack))
                .count();
    }

    /// If the target block can hardly be break, skip it.
    ///
    /// @param pos      The current producing block's pos(Target pos).
    /// @param speedOff Related block's destroy speed * 1.5 (satisfied range).
    /// @param stack    The tool
    /// @return TRUE, if the block has been broke, otherwise return FALSE.
    public static boolean applyBlockDestroy(Level level, BlockPos pos, Player player, float speedOff, ItemStack stack) {
        BlockState targetState = level.getBlockState(pos);
        if (targetState.isAir() || targetState.liquid()) return false;
        float targetSpeed = targetState.getDestroySpeed(level, pos);
        boolean flag1 = targetState.canHarvestBlock(level, pos, player);
        boolean flag2 = speedOff > 0 ? targetSpeed >= 0 && speedOff >= targetSpeed : targetSpeed >= speedOff;
        boolean flag3 = player.hasInfiniteMaterials();
        if (flag1 && flag2 || flag3) {
            level.destroyBlock(pos, false, player);
            if (flag1) {
                targetState.getBlock().playerDestroy(level, player, pos, targetState, targetState.hasBlockEntity() ? level.getBlockEntity(pos) : null, stack);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
}
