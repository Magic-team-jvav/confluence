package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.common.BaseRopeBlock;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;

public class RopeCoils extends ThrowableItemProjectile {
    public static final int SIZE = 4;

    public RopeCoils(EntityType<? extends RopeCoils> type, Level level) {
        super(type, level);
    }

    public RopeCoils(double x, double y, double z, Level level) {
        super(ModEntities.ROPE_COILS.get(), x, y, z, level);
    }

    public RopeCoils(LivingEntity shooter, Level level) {
        super(ModEntities.ROPE_COILS.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ROPE_COILS.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Level level = this.level();
        if (level.isClientSide) {
            return;
        }
        Direction offset = result.getDirection();
        BlockPos pos = result.getBlockPos().relative(offset);
        boolean willUp;
        boolean isPlaced = false;
        if (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced()) {
            int up = 0; int down = 0;
            for (int i = 0; i < SIZE; i++){
                if (level.isEmptyBlock(pos.above(i))) up++;
                else break;
            }
            for (int i = 0; i < SIZE; i++){
                if (level.isEmptyBlock(pos.below(i))) down++;
                else break;
            }
            willUp = up > down;
            BlockState state = ModBlocks.ROPE.get().defaultBlockState();
            isPlaced = place(willUp, state, level, pos);
        }
        if (!isPlaced) {
            ModUtils.createItemEntity(ModItems.ROPE_COILS.get().getDefaultInstance(), pos.getCenter(), level, 0);
        }
        this.discard();
        super.onHitBlock(result);
    }

    public static boolean place(boolean willUp, BlockState state, Level level, BlockPos pos){
        return place(willUp, state, level, pos, 0);
    }

    public static void createRope(int offset, int i, BlockPos pos, Level level){
        ModUtils.createItemEntity(ModBlocks.ROPE.get().asItem(), SIZE + offset - i, Vec3.atLowerCornerOf(pos), level, 0);
    }

    public static boolean place(boolean willUp, BlockState state, Level level, BlockPos pos, int offset){
        boolean isPlaced = false;
        if (willUp){
            for (int i = offset; i < SIZE + offset; i++){
                if (state.canSurvive(level, pos.above(i)) && level.isEmptyBlock(pos.above(i))){
                    if (level.setBlockAndUpdate(pos.above(i), state)){
                        isPlaced = true;
                    } else {
                        if (i == offset) break;
                        createRope(offset, i, pos, level);
                        break;
                    }
                } else {
                    isPlaced = true;
                    createRope(offset, i, pos, level);
                    break;
                }
            }
        } else {
            for (int i = offset; i < SIZE + offset; i++){
                if (state.canSurvive(level, pos.below(i)) && level.isEmptyBlock(pos.below(i))){
                    if (level.setBlockAndUpdate(pos.below(i), state)){
                        isPlaced = true;
                    } else {
                        if (i == offset) break;
                        createRope(offset, i, pos, level);
                        break;
                    }
                } else {
                    isPlaced = true;
                    createRope(offset, i, pos, level);
                    break;
                }
            }
        }
        return isPlaced;
    }

    BlockPos pos;

    @Override
    public void tick() {
        super.tick();
        BlockPos tempPos = BlockPos.containing(this.position());
        if (!tempPos.equals(pos)){
            pos = tempPos;
            Level level = level();
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof BaseRopeBlock){
                boolean willUp;
                boolean isPlaced;
                int up = 0; int down = 0;
                for (int i = 1; i < SIZE + 1; i++){
                    if (level.getBlockState(pos.above(i)).isAir()) up++;
                    else break;
                }
                for (int i = 1; i < SIZE + 1; i++){
                    if (level.getBlockState(pos.below(i)).isAir()) down++;
                    else break;
                }
                willUp = up > down;
                BlockState state = ModBlocks.ROPE.get().defaultBlockState();
                isPlaced = place(willUp, state, level, pos, 1);
                if (!isPlaced) {
                    ModUtils.createItemEntity(ModItems.ROPE_COILS.get(), 1, Vec3.atLowerCornerOf(pos), level, 0);
                }
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Level level = this.level();
        if (level.isClientSide) {
            return;
        }
        BlockPos pos = result.getEntity().getOnPos();
        ModUtils.createItemEntity(ModItems.ROPE_COILS.get(), 1, Vec3.atLowerCornerOf(pos), level, 0);
        super.onHitEntity(result);
    }
}
