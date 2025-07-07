package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BewitchingTableBlock extends HorizontalDirectionalWithVerticalFourPartBlock implements EntityBlock {
    public static final MapCodec<BewitchingTableBlock> CODEC = simpleCodec(BewitchingTableBlock::new);

    public BewitchingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<BewitchingTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            StateProperties.VerticalFourPart part = state.getValue(PART);
            BlockPos basePos = part.isBase() ? pos : StateProperties.VerticalFourPart.getRelatives(part, state.getValue(FACING), pos).get(StateProperties.VerticalFourPart.BASE);
            if (level.getBlockEntity(basePos) instanceof Entity entity && level.getGameTime() - entity.lastClickTime > 110) {
                entity.lastClickTime = level.getGameTime();
                entity.markUpdated();
                player.addEffect(new MobEffectInstance(ModEffects.BEWITCHED, MobEffectInstance.INFINITE_DURATION));
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static class Entity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;
        private long lastClickTime;

        public Entity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.BEWITCHING_TABLE_ENTITY.get(), pos, blockState);
            this.isBase = blockState.getValue(PART).isBase();
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            this.lastClickTime = tag.getLong("LastClickTime");
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putLong("LastClickTime", lastClickTime);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("LastClickTime", lastClickTime);
            return tag;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
            this.lastClickTime = tag.getLong("LastClickTime");
        }

        public void markUpdated() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), UPDATE_CLIENTS);
            }
        }

        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            RawAnimation driving = RawAnimation.begin().thenPlay("driving");
            RawAnimation idling = RawAnimation.begin().thenLoop("idling");
            controllers.add(new AnimationController<>(this, state -> {
                if (level != null && level.getGameTime() - lastClickTime < 110) {
                    if (state.isCurrentAnimation(idling)) {
                        return state.setAndContinue(driving);
                    }
                    return PlayState.CONTINUE;
                }
                return state.setAndContinue(idling);
            }));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
