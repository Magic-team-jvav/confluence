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
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.event.GameClientEvents;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.MusicBoxBlocks;
import org.confluence.mod.common.item.accessory.MusicBoxItem;
import org.confluence.mod.mixed.IMusicManager;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class MusicBoxBlock extends AbstractMechanicalBlock {
    public final @Nullable Music music;

    public MusicBoxBlock(@Nullable Music music) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX));
        this.music = music;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.DRIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.DRIVE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, state.cycle(StateProperties.DRIVE));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state, music);
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        pLevel.setBlockAndUpdate(pPos, pState.cycle(StateProperties.DRIVE));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? LibUtils.getTicker(blockEntityType, MusicBoxBlocks.MUSIC_BOX_ENTITY.get(), Entity::clientTick) : null;
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
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.contains("music")) {
                this.music = Music.CODEC.parse(NbtOps.INSTANCE, tag.get("music")).getOrThrow();
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            if (music != null) {
                tag.put("music", Music.CODEC.encodeStart(NbtOps.INSTANCE, music).getOrThrow());
            }
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = super.getUpdateTag(registries);
            if (music != null) {
                tag.put("music", Music.CODEC.encodeStart(NbtOps.INSTANCE, music).getOrThrow());
            }
            return tag;
        }

        private static void clientTick(Level level, BlockPos pos, BlockState state, Entity entity) {
            Music music = entity.music;
            if (music == null || !state.getValue(StateProperties.DRIVE)) return;
            MusicManager musicManager = Minecraft.getInstance().getMusicManager();
            IMusicManager manager = (IMusicManager) musicManager;
            if (manager.confluence$getMusicBoxOccupied().isAccessory()) return;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                musicManager.stopPlaying(music);
                manager.confluence$setMusicBoxOccupied(IMusicManager.State.NONE);
            } else {
                if (manager.confluence$getMusicBoxOccupied().isNone()) {
                    if (!musicManager.isPlayingMusic(music) && withinRange(entity.getBlockPos().getCenter(), player.position(), music)) {
                        musicManager.startPlaying(music);
                        /**
                         * @see GameClientEvents#clientTick$Post(ClientTickEvent.Post) 1st
                         * @see MusicBoxItem#curioTick(SlotContext, ItemStack) 2nd
                         */
                        manager.confluence$setMusicBoxOccupied(IMusicManager.State.BLOCK); // 3rd
                    }
                } else if (musicManager.isPlayingMusic(music)) {
                    if (!withinRange(entity.getBlockPos().getCenter(), player.position(), music)) {
                        musicManager.stopPlaying(music);
                        manager.confluence$setMusicBoxOccupied(IMusicManager.State.NONE);
                    }
                } else if (withinRange(entity.getBlockPos().getCenter(), player.position(), music)) {
                    musicManager.stopPlaying();
                    musicManager.startPlaying(music);
                }
            }
        }

        private static boolean withinRange(Vec3 block, Vec3 player, Music music) {
            double maxRangeSqr = Mth.square(music.getEvent().value().getRange(1.0F));
            double dx = block.x - player.x;
            double dy = block.y - player.y;
            double dz = block.z - player.z;
            return dx * dx + dy * dy + dz * dz <= maxRangeSqr;
        }
    }
}
