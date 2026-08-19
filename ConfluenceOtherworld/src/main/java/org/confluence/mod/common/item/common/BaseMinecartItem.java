package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class BaseMinecartItem extends MinecartItem {
    private static final DispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior fallback = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            if (!(stack.getItem() instanceof BaseMinecartItem item))
                return fallback.dispense(source, stack);
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            ServerLevel level = source.getLevel();
            double x = source.x() + direction.getStepX() * 1.125;
            double y = Math.floor(source.y()) + direction.getStepY();
            double z = source.z() + direction.getStepZ() * 1.125;
            BlockPos pos = source.getPos().relative(direction);
            BlockState state = level.getBlockState(pos);
            RailShape shape = getRailShape(level, pos, state);
            double offset;
            if (state.is(BlockTags.RAILS)) {
                offset = shape.isAscending() ? 0.6 : 0.1;
            } else {
                BlockPos below = pos.below();
                BlockState belowState = level.getBlockState(below);
                if (!state.isAir() || !belowState.is(BlockTags.RAILS))
                    return fallback.dispense(source, stack);
                RailShape belowShape = getRailShape(level, below, belowState);
                offset = direction != Direction.DOWN && belowShape.isAscending() ? -0.4 : -0.9;
            }
            AbstractMinecart minecart = item.createMinecart(level, x, y + offset, z, AbstractMinecart.Type.RIDEABLE, stack, null);
            if (minecart == null) return fallback.dispense(source, stack);
            if (stack.hasCustomHoverName()) minecart.setCustomName(stack.getHoverName());
            level.addFreshEntity(minecart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    private final BaseMinecartEntity.Abilities abilities;
    private final MinecartFactory factory;

    public BaseMinecartItem(Properties properties, ModRarity rarity, BaseMinecartEntity.Abilities abilities, MinecartFactory factory) {
        super(AbstractMinecart.Type.RIDEABLE, properties.stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.abilities = abilities;
        this.factory = factory;
        DispenserBlock.registerBehavior(this, DISPENSE_BEHAVIOR);
    }

    /// 创建该物品对应的自定义矿车实体。
    public @Nullable AbstractMinecart createMinecart(ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, @Nullable Player player) {
        if (type == AbstractMinecart.Type.RIDEABLE && stack.is(this)) {
            return factory.createMinecart(level, x, y, z, abilities);
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if (!state.is(BlockTags.RAILS)) return InteractionResult.FAIL;
        ItemStack stack = context.getItemInHand();
        if (context.getLevel() instanceof ServerLevel level) {
            double offset = getRailShape(level, pos, state).isAscending() ? 0.5 : 0.0;
            AbstractMinecart minecart = createMinecart(level, pos.getX() + 0.5, pos.getY() + 0.0625 + offset, pos.getZ() + 0.5, AbstractMinecart.Type.RIDEABLE, stack, context.getPlayer());
            if (minecart == null) return InteractionResult.FAIL;
            if (stack.hasCustomHoverName()) minecart.setCustomName(stack.getHoverName());
            level.addFreshEntity(minecart);
            level.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(context.getPlayer(), level.getBlockState(pos.below())));
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    private static RailShape getRailShape(ServerLevel level, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof BaseRailBlock rail
                ? rail.getRailDirection(state, level, pos, null)
                : RailShape.NORTH_SOUTH;
    }

    @FunctionalInterface
    public interface MinecartFactory {
        AbstractMinecart createMinecart(ServerLevel level, double x, double y, double z, BaseMinecartEntity.Abilities abilities);
    }
}
