package org.confluence.mod.common.entity.minecart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.util.PlayerUtils;

public class DiggingMolecartEntity extends BaseMinecartEntity {
    public DiggingMolecartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DiggingMolecartEntity(Level level, double x, double y, double z, Abilities<? extends DiggingMolecartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

    @Override
    public void moveMinecartOnRail(BlockPos pos) {
        super.moveMinecartOnRail(pos);
        Vec3 motion = getDeltaMovement();
        if (motion.horizontalDistanceSqr() < 0.1) return;
        if (canDigging() && getFirstPassenger() instanceof Player player) {
            setYRot((float) -(Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG));
            Direction facing = Direction.fromYRot(getYRot());
            BlockPos facingPos = blockPosition().relative(facing);
            Tuple<ItemStack, Integer> tuple = PlayerUtils.getMaxDiggingPowerItem(player);
            ItemStack pickaxeItem = tuple.getA();
            int power = tuple.getB();
            if (power > 0 && !pickaxeItem.isEmpty()) {
                diggingBlocks(player, facingPos, facing, power, pickaxeItem);
            }
            placeRail(player, facingPos, facing);
        }
    }

    @Override
    protected double getUpgradedMaxSpeed() {
        return super.getUpgradedMaxSpeed() * 0.5;
    }

    protected boolean canDigging() {
        return getY() <= level().getSeaLevel();
    }

    protected void diggingBlocks(Player player, BlockPos facingPos, Direction facing, int power, ItemStack pickaxeItem) {
        BlockPos.betweenClosedStream(getDiggingRange(facingPos, facing)).forEach(blockPos -> {
            BlockState blockState = level().getBlockState(blockPos);
            if (!blockState.isAir() && !blockState.is(BlockTags.RAILS) && ModTiers.isCorrectToolForDrops(power, pickaxeItem, blockState)) {
                pickaxeItem.mineBlock(level(), blockState, blockPos, player);
                level().destroyBlock(blockPos, true, player);
            }
        });
    }

    protected AABB getDiggingRange(BlockPos facingPos, Direction facing) {
        Direction right = facing.getClockWise();
        Direction left = facing.getCounterClockWise();
        BlockPos rightBottom = facingPos.relative(right);
        BlockPos leftTop = facingPos.above(2).relative(left);
        return new AABB(rightBottom.getCenter(), leftTop.getCenter());
    }

    protected void placeRail(Player player, BlockPos facingPos, Direction facing) {
        if (level().getBlockState(facingPos).canBeReplaced()) {
            player.getInventory().items.stream().filter(itemStack -> itemStack.is(ItemTags.RAILS)).findAny().ifPresent(itemStack -> {
                if (itemStack.getItem() instanceof BlockItem blockItem) {
                    BlockHitResult hitResult = new BlockHitResult(facingPos.getBottomCenter(), facing, facingPos, true);
                    blockItem.place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, hitResult));
                }
            });
        }
    }
}
