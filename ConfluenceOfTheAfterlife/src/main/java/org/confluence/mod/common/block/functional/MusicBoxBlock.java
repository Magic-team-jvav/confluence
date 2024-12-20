package org.confluence.mod.common.block.functional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.MusicBoxBlocks;
import org.confluence.mod.common.item.accessory.MusicBoxItem;
import org.confluence.mod.mixed.IMusicManager;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MusicBoxBlock extends AbstractMechanicalBlock {
    private final Supplier<MusicBoxItem> musicBoxItem;

    public MusicBoxBlock(Supplier<MusicBoxItem> musicBoxItem) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX));
        this.musicBoxItem = musicBoxItem;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.DRIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.DRIVE);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, state.cycle(StateProperties.DRIVE));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new Entity(pos, state, musicBoxItem.get().music);
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        pLevel.setBlockAndUpdate(pPos, pState.cycle(StateProperties.DRIVE));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? ModUtils.getTicker(blockEntityType, MusicBoxBlocks.MUSIC_BOX_ENTITY.get(), Entity::clientTick) : null;
    }

    public static class Entity extends AbstractMechanicalBlock.Entity {
        private @Nullable Music music;

        public Entity(BlockPos pos, BlockState blockState) {
            super(MusicBoxBlocks.MUSIC_BOX_ENTITY.get(), pos, blockState);
        }

        public Entity(BlockPos pos, BlockState blockState, @Nullable Music music) {
            this(pos, blockState);
            this.music = music;
        }

        @Override
        protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.contains("music")) {
                this.music = Music.CODEC.parse(NbtOps.INSTANCE, tag.get("music")).getOrThrow();
            }
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
            super.saveAdditional(tag, registries);
            if (music != null) {
                tag.put("music", Music.CODEC.encodeStart(NbtOps.INSTANCE, music).getOrThrow());
            }
        }

        @Override
        public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
            CompoundTag tag = super.getUpdateTag(registries);
            if (music != null) {
                tag.put("music", Music.CODEC.encodeStart(NbtOps.INSTANCE, music).getOrThrow());
            }
            return tag;
        }

        private static void clientTick(Level level, BlockPos pos, BlockState state, Entity entity) {
            Music music = entity.music;
            if (music == null || !state.getValue(StateProperties.DRIVE)) return;
            LocalPlayer player = Minecraft.getInstance().player;
            MusicManager musicManager = Minecraft.getInstance().getMusicManager();
            IMusicManager manager = (IMusicManager) musicManager;
            if (player == null) {
                musicManager.stopPlaying(music);
                manager.confluence$setMusicBoxOccupied(false);
            } else {
                Vec3 center = entity.getBlockPos().getCenter();
                double maxRangeSqr = Mth.square(music.getEvent().value().getRange(1.0F));
                double dx = center.x - player.getX();
                double dy = center.y - player.getY();
                double dz = center.z - player.getZ();
                boolean playingMusic = musicManager.isPlayingMusic(music);
                boolean withinRange = dx * dx + dy * dy + dz * dz <= maxRangeSqr;
                if (manager.confluence$isMusicBoxOccupied()) {
                    if (playingMusic && !withinRange) {
                        musicManager.stopPlaying(music);
                        manager.confluence$setMusicBoxOccupied(false);
                    }
                } else if (!playingMusic && withinRange) {
                    musicManager.startPlaying(music);
                    manager.confluence$setMusicBoxOccupied(true);
                }
            }
        }
    }
}
