package org.confluence.mod.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.common.BiomeChestBlock;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"))
    private float deny(float original, @Local(argsOnly = true) BlockState state, @Local int i) {
        if (i > 30 && state.is(ModTags.Blocks.UNBREAKABLE_IF_CANNOT_HARVEST)) {
            return 0.0F;
        }
        return original;
    }

    @Mixin(BlockBehaviour.BlockStateBase.class)
    public static abstract class BlockStateBaseMixin {
        @Shadow
        public abstract Block getBlock();

        @Shadow
        protected abstract BlockState asState();

        @Shadow
        @Final
        private boolean isAir;

        @Shadow
        @Nullable
        protected BlockBehaviour.BlockStateBase.Cache cache;

        @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
        private void shimmer(BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
            if (cache == null || asState().getDestroySpeed(pLevel, pPos) == -1) return;
            if (pContext instanceof EntityCollisionContext context && context.getEntity() instanceof LivingEntity living && living.hasEffect(ModEffects.SHIMMER)) {
                cir.setReturnValue(Shapes.empty());
            }
        }

        @Inject(method = "onRemove", at = @At("HEAD"))
        private void removeData(Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
            if (!isAir && !level.isClientSide && getBlock() != newState.getBlock()) {
                Map<ChunkPos, BrushData> dataMap = level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap();
                if (!dataMap.isEmpty()) {
                    BrushingColorPacketS2C.remove((ServerLevel) level, pos);
                }
            }
        }

        @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
        private void modify(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
            if (cir.getReturnValue() == -1) return;
            if ((getBlock() instanceof BaseChestBlock || getBlock() instanceof BiomeChestBlock) && !asState().getValue(StateProperties.UNLOCKED)) {
                cir.setReturnValue(-1.0F);
            }
        }
    }
}
