package org.confluence.mod.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviour$BlockStateBaseMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void shimmer(BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (pLevel.getBlockState(pPos).is(Blocks.BEDROCK)) return;
        if (pContext instanceof EntityCollisionContext context && context.getEntity() instanceof LivingEntity living && living.hasEffect(ModEffects.SHIMMER)) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void removeData(Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!level.isClientSide && getBlock() != newState.getBlock() && level instanceof ServerLevel serverLevel) {
            Map<ChunkPos, BrushData> dataMap = level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap();
            if (!dataMap.isEmpty()) {
                BrushingColorPacketS2C.remove(serverLevel, pos);
            }
        }
    }
}
