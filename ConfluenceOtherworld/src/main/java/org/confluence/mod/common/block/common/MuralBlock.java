package org.confluence.mod.common.block.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MuralBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MuralBlock> CODEC = simpleCodec(MuralBlock::new);
    private static final Map<BlockPos, BlockEntity> BE_CACHE = new HashMap<>();

    public MuralBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.WEST));
    }

    @Override
    protected MapCodec<MuralBlock> codec() {return CODEC;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;
        if (!(level.getBlockEntity(pos) instanceof BEntity muralEntity)) return;

        int width = muralEntity.getMuralWidth();
        int height = muralEntity.getMuralHeight();
        if (width == -1 || height == -1) return;

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null && !lore.lines().isEmpty()) {
            muralEntity.setLore(new ArrayList<>(lore.lines()));
        }

        GroupItem.BelongsTo group = stack.get(ConfluenceMagicLib.BELONGS_TO_GROUP);
        if (group != null) {
            muralEntity.setBelongsToGroup(group);
        }

        Direction facing = state.getValue(FACING);
        int xOffset = -facing.getStepZ();
        int zOffset = facing.getStepX();

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                if (w == 0 && h == 0) continue;
                BlockPos targetPos = pos.offset(xOffset * w, h, zOffset * w);
                BlockState targetState = level.getBlockState(targetPos);
                if (!targetState.isAir() && !targetState.canBeReplaced()) continue;

                level.setBlockAndUpdate(targetPos, defaultBlockState().setValue(FACING, facing));
                if (level.getBlockEntity(targetPos) instanceof BEntity newMuralEntity) {
                    newMuralEntity.setMuralWidth(-1);
                    newMuralEntity.setMuralHeight(-1);
                    newMuralEntity.setHeadPos(new BlockPos(w, h, 0));
                    newMuralEntity.setChanged();
                }
            }
        }
        muralEntity.setHeadPos(BlockPos.ZERO);
        muralEntity.setChanged();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
        boolean hasSilkTouch = EnchantmentHelper.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.SILK_TOUCH), player) > 0;
        if (hasSilkTouch) {
            ItemStack dropStack = new ItemStack(this);
            BlockEntity headBeToSave = null;

            if (be instanceof BEntity muralEntity) {
                int w = muralEntity.getMuralWidth();
                int h = muralEntity.getMuralHeight();
                if (!(w == -1 || h == -1)) {
                    headBeToSave = be;
                } else {
                    Direction facing = state.getValue(FACING);
                    int xOffset = -facing.getStepZ();
                    int zOffset = facing.getStepX();
                    BlockPos relPos = muralEntity.getHeadPos();
                    BlockPos headWorldPos = pos.offset(-xOffset * relPos.getX(), -relPos.getY(), -zOffset * relPos.getX());
                    BlockEntity cachedHeadBe = BE_CACHE.get(headWorldPos);
                    if (cachedHeadBe instanceof BEntity) {
                        headBeToSave = cachedHeadBe;
                        BE_CACHE.remove(headWorldPos);
                    }
                }
            }

            if (headBeToSave != null) {
                headBeToSave.saveToItem(dropStack, level.registryAccess());
                if (headBeToSave instanceof BEntity b) {
                    b.restoreItemComponents(dropStack);
                }
            }
            popResource(level, pos, dropStack);
        } else {
            int dropCount = 1;
            if (be instanceof BEntity muralEntity) {
                int w = muralEntity.getMuralWidth();
                int h = muralEntity.getMuralHeight();
                if (!(w == -1 || h == -1)) {
                    dropCount = Math.max(1, w) * Math.max(1, h);
                } else {
                    Direction facing = state.getValue(FACING);
                    int xOffset = -facing.getStepZ();
                    int zOffset = facing.getStepX();
                    BlockPos relPos = muralEntity.getHeadPos();
                    BlockPos headWorldPos = pos.offset(-xOffset * relPos.getX(), -relPos.getY(), -zOffset * relPos.getX());
                    BlockEntity cachedHeadBe = BE_CACHE.get(headWorldPos);
                    if (cachedHeadBe instanceof BEntity headMuralEntity) {
                        dropCount = Math.max(1, headMuralEntity.getMuralWidth()) * Math.max(1, headMuralEntity.getMuralHeight());
                        BE_CACHE.remove(headWorldPos);
                    }
                }
            }
            if (dropCount > 0)
                popResource(level, pos, new ItemStack(Items.STONE_BRICKS, dropCount));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof BEntity muralEntity) {
            BlockEntity headBeToSave = null;
            int w = muralEntity.getMuralWidth();
            int h = muralEntity.getMuralHeight();
            if (!(w == -1 || h == -1)) {
                headBeToSave = muralEntity;
            } else {
                Direction facing = state.getValue(FACING);
                int xOffset = -facing.getStepZ();
                int zOffset = facing.getStepX();
                BlockPos relPos = muralEntity.getHeadPos();
                BlockPos headWorldPos = pos.offset(-xOffset * relPos.getX(), -relPos.getY(), -zOffset * relPos.getX());
                BlockEntity headBe = level.getBlockEntity(headWorldPos);
                if (headBe instanceof BEntity) headBeToSave = headBe;
            }

            if (headBeToSave != null) {
                headBeToSave.setChanged();
                headBeToSave.saveToItem(stack, level.registryAccess());
                if (headBeToSave instanceof BEntity b) {
                    b.restoreItemComponents(stack);
                }
            }
        }
        return stack;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof BEntity muralEntity)) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        int width = muralEntity.getMuralWidth();
        int height = muralEntity.getMuralHeight();
        if (width == -1 || height == -1) {
            BlockPos relPos = muralEntity.getHeadPos();
            int relX = relPos.getX();
            int relY = relPos.getY();
            Direction facing = state.getValue(FACING);
            int xOffset = -facing.getStepZ();
            int zOffset = facing.getStepX();
            BlockPos headPos = pos.offset(-xOffset * relX, -relY, -zOffset * relX);
            BlockEntity headBe = level.getBlockEntity(headPos);
            if (headBe instanceof BEntity) BE_CACHE.put(headPos, headBe);
            if (level.getBlockState(headPos).getBlock() == this) level.removeBlock(headPos, false);
        } else {
            int xOffset = -state.getValue(FACING).getStepZ();
            int zOffset = state.getValue(FACING).getStepX();
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    if (w == 0 && h == 0) continue;
                    BlockPos targetPos = pos.offset(xOffset * w, h, zOffset * w);
                    if (level.getBlockState(targetPos).getBlock() == this)
                        level.removeBlock(targetPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        int width = 1, height = 1;
        ItemStack stack = context.getItemInHand();
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("muralWidth")) width = tag.getInt("muralWidth");
            if (tag.contains("muralHeight")) height = tag.getInt("muralHeight");
        }
        if (width == -1 || height == -1) return defaultBlockState().setValue(FACING, facing);
        int xOffset = -facing.getStepZ();
        int zOffset = facing.getStepX();
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                if (w == 0 && h == 0) continue;
                BlockPos targetPos = pos.offset(xOffset * w, h, zOffset * w);
                if (!level.getBlockState(targetPos).canBeReplaced(context)) return null;
            }
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    public static class BEntity extends BlockEntity {
        private Optional<List<MuralData>> back = Optional.empty();
        private Optional<List<MuralData>> left = Optional.empty();
        private Optional<List<MuralData>> right = Optional.empty();
        private Optional<List<MuralData>> front = Optional.empty();
        private int muralWidth = 1;
        private int muralHeight = 1;
        private BlockPos headPos = BlockPos.ZERO;
        private List<Component> lore = null;
        private GroupItem.BelongsTo belongsToGroup = null;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(DecorativeBlocks.MURAL_ENTITY_BLOCK.get(), pos, blockState);
        }

        public Optional<List<MuralData>> getBack() {return back;}

        public Optional<List<MuralData>> getLeft() {return left;}

        public Optional<List<MuralData>> getRight() {return right;}

        public Optional<List<MuralData>> getFront() {return front;}

        public int getMuralWidth() {return muralWidth;}

        public int getMuralHeight() {return muralHeight;}

        public BlockPos getHeadPos() {return headPos;}

        public void setMuralWidth(int w) {muralWidth = w;}

        public void setMuralHeight(int h) {muralHeight = h;}

        public void setHeadPos(BlockPos p) {headPos = p;}

        public List<Component> getLore() {return lore;}

        public void setLore(List<Component> l) {this.lore = l;}

        public GroupItem.BelongsTo getBelongsToGroup() {return belongsToGroup;}

        public void setBelongsToGroup(GroupItem.BelongsTo group) {this.belongsToGroup = group;}

        public void restoreItemComponents(ItemStack stack) {
            if (lore != null && !lore.isEmpty()) {
                stack.set(DataComponents.LORE, new ItemLore(lore));
            }
            if (belongsToGroup != null) {
                stack.set(ConfluenceMagicLib.BELONGS_TO_GROUP, belongsToGroup);
            }
        }

        private void encodeForNetwork(CompoundTag tag, HolderLookup.Provider registries) {
            tag.put("back", MuralData.encode(back, registries));
            tag.put("left", MuralData.encode(left, registries));
            tag.put("right", MuralData.encode(right, registries));
            tag.put("front", MuralData.encode(front, registries));
            tag.putInt("muralWidth", muralWidth);
            tag.putInt("muralHeight", muralHeight);
            tag.putLong("headPos", headPos.asLong());

            if (lore != null && !lore.isEmpty()) {
                ListTag loreTag = new ListTag();
                for (Component comp : lore) {
                    ComponentSerialization.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), comp)
                            .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode lore: " + err))
                            .ifPresent(loreTag::add);
                }
                tag.put("lore", loreTag);
            }
            if (belongsToGroup != null) {
                GroupItem.BelongsTo.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), belongsToGroup)
                        .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode belongsToGroup: " + err))
                        .ifPresent(groupTag -> tag.put("belongs_to_group", groupTag));
            }
        }

        private void encodeForPersistence(CompoundTag tag, HolderLookup.Provider registries) {
            tag.put("back", MuralData.encode(back, registries));
            tag.put("left", MuralData.encode(left, registries));
            tag.put("right", MuralData.encode(right, registries));
            tag.put("front", MuralData.encode(front, registries));
            tag.putInt("muralWidth", muralWidth);
            tag.putInt("muralHeight", muralHeight);
            tag.putLong("headPos", headPos.asLong());
        }

        private void decode(CompoundTag tag, HolderLookup.Provider registries) {
            back = MuralData.decode(tag.getList("back", Tag.TAG_COMPOUND), registries);
            left = MuralData.decode(tag.getList("left", Tag.TAG_COMPOUND), registries);
            right = MuralData.decode(tag.getList("right", Tag.TAG_COMPOUND), registries);
            front = MuralData.decode(tag.getList("front", Tag.TAG_COMPOUND), registries);
            muralWidth = tag.contains("muralWidth") ? tag.getInt("muralWidth") : 1;
            muralHeight = tag.contains("muralHeight") ? tag.getInt("muralHeight") : 1;
            headPos = tag.contains("headPos") ? BlockPos.of(tag.getLong("headPos")) : BlockPos.ZERO;

            if (tag.contains("lore")) {
                ListTag loreTag = tag.getList("lore", Tag.TAG_COMPOUND);
                List<Component> loreList = new ArrayList<>();
                for (Tag value : loreTag) {
                    ComponentSerialization.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), value)
                            .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode lore: " + err))
                            .ifPresent(loreList::add);
                }
                this.lore = loreList.isEmpty() ? null : loreList;
            } else {
                this.lore = null;
            }

            if (tag.contains("belongs_to_group")) {
                GroupItem.BelongsTo.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("belongs_to_group"))
                        .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode belongsToGroup: " + err))
                        .ifPresent(group -> this.belongsToGroup = group);
            } else {
                this.belongsToGroup = null;
            }
        }


        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            encodeForNetwork(tag, registries);
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
            encodeForPersistence(tag, registries);
        }

        @Override
        public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }
    }

    public record MuralData(float x, float y, float z, float roll, float scale, Optional<Icon> icon,
                            Optional<Text> text) {
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

    public record Icon(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset,
                       int uWidth, int vHeight, int textureWidth, int textureHeight) {
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

    public record Text(Component component, float x, float y, int color, int backgroundColor,
                       boolean dropShadow) {
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
