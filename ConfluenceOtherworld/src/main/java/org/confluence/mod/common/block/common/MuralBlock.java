package org.confluence.mod.common.block.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MuralBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MuralBlock> CODEC = simpleCodec(MuralBlock::new);

    public MuralBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.WEST));
    }

    @Override
    protected MapCodec<MuralBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity {
        private Optional<List<MuralData>> back = Optional.empty();
        private Optional<List<MuralData>> left = Optional.empty();
        private Optional<List<MuralData>> right = Optional.empty();
        private Optional<List<MuralData>> front = Optional.empty();

        public Entity(BlockPos pos, BlockState blockState) {
            super(DecorativeBlocks.MURAL_ENTITY_BLOCK.get(), pos, blockState);
        }

        public Optional<List<MuralData>> getBack() {
            return back;
        }

        public Optional<List<MuralData>> getLeft() {
            return left;
        }

        public Optional<List<MuralData>> getRight() {
            return right;
        }

        public Optional<List<MuralData>> getFront() {
            return front;
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            encode(tag, registries);
            return tag;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
            decode(tag, registries);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            decode(tag, registries);
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            encode(tag, registries);
        }

        private void encode(CompoundTag tag, HolderLookup.Provider registries) {
            tag.put("back", MuralData.encode(back, registries));
            tag.put("left", MuralData.encode(left, registries));
            tag.put("right", MuralData.encode(right, registries));
            tag.put("front", MuralData.encode(front, registries));
        }

        private void decode(CompoundTag tag, HolderLookup.Provider registries) {
            this.back = MuralData.decode(tag.getList("back", Tag.TAG_COMPOUND), registries);
            this.left = MuralData.decode(tag.getList("left", Tag.TAG_COMPOUND), registries);
            this.right = MuralData.decode(tag.getList("right", Tag.TAG_COMPOUND), registries);
            this.front = MuralData.decode(tag.getList("front", Tag.TAG_COMPOUND), registries);
        }

        @Override
        public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }
    }

    public record MuralData(float x, float y, float z, float roll, float scale, Optional<Icon> icon, Optional<Text> text) {
        public static final Codec<MuralData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.lenientOptionalFieldOf("x", 0.0F).forGetter(MuralData::x),
                Codec.FLOAT.lenientOptionalFieldOf("y", 0.0F).forGetter(MuralData::y),
                Codec.FLOAT.lenientOptionalFieldOf("z", 0.0F).forGetter(MuralData::z),
                Codec.FLOAT.lenientOptionalFieldOf("roll", 0.0F).forGetter(MuralData::roll),
                Codec.FLOAT.lenientOptionalFieldOf("scale", 1.0F).forGetter(MuralData::scale),
                Icon.CODEC.lenientOptionalFieldOf("icon").forGetter(MuralData::icon),
                Text.CODEC.lenientOptionalFieldOf("text").forGetter(MuralData::text)
        ).apply(instance, MuralData::new));
        public static final Codec<List<MuralData>> LIST_CODEC = CODEC.listOf();

        public static Tag encode(Optional<List<MuralData>> datas, HolderLookup.Provider registries) {
            return datas.flatMap(data -> LIST_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), data).result()).orElseGet(ListTag::new);
        }

        public static Optional<List<MuralData>> decode(ListTag tag, HolderLookup.Provider registries) {
            return LIST_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).result();
        }
    }

    public record Icon(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("atlasLocation").forGetter(Icon::atlasLocation),
                Codec.INT.lenientOptionalFieldOf("x", 0).forGetter(Icon::x),
                Codec.INT.lenientOptionalFieldOf("y", 0).forGetter(Icon::y),
                Codec.FLOAT.lenientOptionalFieldOf("uOffset", 0.0F).forGetter(Icon::uOffset),
                Codec.FLOAT.lenientOptionalFieldOf("vOffset", 0.0F).forGetter(Icon::vOffset),
                Codec.INT.fieldOf("uWidth").forGetter(Icon::uWidth),
                Codec.INT.fieldOf("vHeight").forGetter(Icon::vHeight),
                Codec.INT.lenientOptionalFieldOf("textureWidth", 256).forGetter(Icon::textureWidth),
                Codec.INT.lenientOptionalFieldOf("textureHeight", 256).forGetter(Icon::textureHeight)
        ).apply(instance, Icon::new));
    }

    public record Text(Component component, float x, float y, int color, int backgroundColor, boolean dropShadow) {
        public static final Codec<Text> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC.fieldOf("component").forGetter(Text::component),
                Codec.FLOAT.lenientOptionalFieldOf("x", 0.0F).forGetter(Text::x),
                Codec.FLOAT.lenientOptionalFieldOf("y", 0.0F).forGetter(Text::y),
                Codec.INT.lenientOptionalFieldOf("color", -1).forGetter(Text::color),
                Codec.INT.lenientOptionalFieldOf("backgroundColor", 0).forGetter(Text::backgroundColor),
                Codec.BOOL.lenientOptionalFieldOf("dropShadow", false).forGetter(Text::dropShadow)
        ).apply(instance, Text::new));
    }
}
