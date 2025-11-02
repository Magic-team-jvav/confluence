package org.confluence.mod.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.common.BiomeChestBlock;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @ModifyVariable(method = "getDestroyProgress", at = @At("HEAD"), argsOnly = true)
    private BlockState replace(BlockState source) {
        return GlobalCloakData.INSTANCE.getTarget(source);
    }

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
                Map<ChunkPos, BrushData> dataMap = ChunkBrushData.of(level).getDataMap();
                if (!dataMap.isEmpty()) {
                    BrushingColorPacketS2C.remove((ServerLevel) level, pos);
                }
            }
        }

        @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
        private float modify(float original) {
            if (original == -1) return original;
            if ((getBlock() instanceof BaseChestBlock || getBlock() instanceof BiomeChestBlock) && !asState().getValue(StateProperties.UNLOCKED)) {
                return -1;
            }
            return original;
        }

        @WrapOperation(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDestroyProgress(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
        private float replaceBlockStateForDestroyProgress(Block instance, BlockState state, Player player, BlockGetter level, BlockPos pos, Operation<Float> original) {
            BlockState target = GlobalCloakData.INSTANCE.getTarget(state);
            if (target == state) {
                return original.call(instance, state, player, level, pos);
            }
            return original.call(target.getBlock(), target, player, level, pos);
        }

        @WrapOperation(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;onExplosionHit(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V"))
        private void replaceState(Block instance, BlockState blockState, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, Operation<Void> original) {
            BlockState target = GlobalCloakData.INSTANCE.getTarget(blockState);
            if (target == blockState) {
                original.call(instance, blockState, level, pos, explosion, biConsumer);
            }
            original.call(target.getBlock(), target, level, pos, explosion, biConsumer);
        }

        @WrapOperation(method = "getDrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"))
        private List<ItemStack> replaceState(Block instance, BlockState blockState, LootParams.Builder builder, Operation<List<ItemStack>> original) {
            BlockState target = GlobalCloakData.INSTANCE.getTarget(blockState);
            if (target == blockState) {
                return original.call(instance, blockState, builder);
            }
            return original.call(target.getBlock(), target, builder);
        }
    }
}
