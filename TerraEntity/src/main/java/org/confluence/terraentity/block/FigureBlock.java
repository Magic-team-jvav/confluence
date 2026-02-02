package org.confluence.terraentity.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.block.TEFigureBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FigureBlock extends BaseEntityBlock {
    ResourceLocation entityType;
    float scale;

    public FigureBlock(ResourceLocation entityType, Properties properties) {
        this(entityType, 1, properties);
    }

    public FigureBlock(ResourceLocation entityType, float scale, Properties properties) {
        super(properties);
        this.entityType = entityType;
        this.scale = scale;
    }

    @Override
    protected MapCodec<? extends FigureBlock> codec() {
        return RecordCodecBuilder.mapCodec((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("entity_type").forGetter(e -> e.entityType),
                Codec.FLOAT.fieldOf("scale").forGetter(e -> e.scale),
                propertiesCodec()
        ).apply(instance, FigureBlock::new));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        FigureBlockEntity blockEntity = (FigureBlockEntity) level.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.turnOn = !blockEntity.turnOn;
        }
        player.swing(InteractionHand.MAIN_HAND);
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        FigureBlockEntity blockEntity = new FigureBlockEntity(blockPos, blockState);
        return blockEntity;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, TEFigureBlocks.FIGURE_BLOCK_ENTITY.get(), (level, pos, state, blockEntity) -> {
            if (blockEntity.entity != null) {
                if (blockEntity.turnOn)
                    ++blockEntity.ticks;
                blockEntity.entity.tickCount = blockEntity.ticks;
            } else {
                blockEntity.entity = BuiltInRegistries.ENTITY_TYPE.get(entityType).create(level);
            }
            if (!level.isClientSide && (blockEntity.ticks & 63) == 0) {

//                level.sendBlockUpdated(pos, state, state, 2);
                blockEntity.setChanged();
            }
        });
    }

    public static class FigureBlockEntity extends BlockEntity {
        public Entity entity;
        public int ticks;
        public boolean turnOn = false;
        public ResourceLocation entityType;
        public float scale = 1;

        public FigureBlockEntity(BlockPos pos, BlockState blockState) {
            super(TEFigureBlocks.FIGURE_BLOCK_ENTITY.get(), pos, blockState);
            FigureBlock block = (FigureBlock)blockState.getBlock();
            scale = block.scale;
            this.entityType = block.entityType;
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putInt("ticks", ticks);
            tag.putBoolean("turnOn", turnOn);
            tag.putString("entity_type", entityType.toString());
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            ticks = tag.getInt("ticks");
            turnOn = tag.getBoolean("turnOn");
            entityType = TerraEntity.parse(tag.getString("entity_type"));

//            if(entity == null){
//                if (this.level != null) {
//                    entity = BuiltInRegistries.ENTITY_TYPE.get(entityType).create(this.level);
//                }
//            }
//            if(entity != null){
//                entity.tickCount = ticks;
//            }
//            if (this.level != null) {
//                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
//            }
        }

        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
            CompoundTag tag = pkt.getTag();
            ticks = tag.getInt("ticks");
            turnOn = tag.getBoolean("turnOn");
            entityType = TerraEntity.parse(tag.getString("entity_type"));
//            if(entity == null){
//                if (this.level != null) {
//                    entity = BuiltInRegistries.ENTITY_TYPE.get(entityType).create(this.level);
//                }
//            }
//            if(entity != null){
//                entity.tickCount = ticks;
//            }
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = super.getUpdateTag(registries);
            tag.putInt("ticks", ticks);
            tag.putBoolean("turnOn", turnOn);
            tag.putString("entity_type", entityType.toString());
            return tag;
        }
    }
}
