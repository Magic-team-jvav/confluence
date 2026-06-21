package org.confluence.mod.common.block.common;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.chat.PortComponentSerialization;

import java.util.*;

public class MuralBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final Map<BlockPos, BlockEntity> BE_CACHE = new HashMap<>();

    public MuralBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.WEST));
    }

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

        // 从 NBT 获取 lore
        CompoundTag displayTag = stack.getTagElement("display");
        if (displayTag != null && displayTag.contains("Lore", Tag.TAG_LIST)) {
            ListTag loreTag = displayTag.getList("Lore", Tag.TAG_STRING);
            if (!loreTag.isEmpty()) {
                List<Component> loreList = new ArrayList<>();
                for (int i = 0; i < loreTag.size(); i++) {
                    try {
                        Component comp = Component.Serializer.fromJson(loreTag.getString(i));
                        if (comp != null) loreList.add(comp);
                    } catch (Exception ignored) {}
                }
                if (!loreList.isEmpty()) {
                    muralEntity.setLore(loreList);
                }
            }
        }

        GroupItem.BelongsTo group = stack.getData(ConfluenceMagicLib.BELONGS_TO_GROUP);
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
        if (EnchantmentHelper.hasSilkTouch(tool)) {
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
                headBeToSave.saveToItem(dropStack);
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
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
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
                headBeToSave.saveToItem(stack);
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
        CompoundTag beTag = BlockItem.getBlockEntityData(stack);
        if (beTag != null) {
            if (beTag.contains("muralWidth")) width = beTag.getInt("muralWidth");
            if (beTag.contains("muralHeight")) height = beTag.getInt("muralHeight");
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
                ListTag loreTag = new ListTag();
                for (Component comp : lore) {
                    loreTag.add(StringTag.valueOf(Component.Serializer.toJson(comp)));
                }
                CompoundTag displayTag = stack.getOrCreateTagElement("display");
                displayTag.put("Lore", loreTag);
            }
            if (belongsToGroup != null) {
                stack.setData(ConfluenceMagicLib.BELONGS_TO_GROUP, belongsToGroup);
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
                    PortComponentSerialization.CODEC.encodeStart(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), comp)
                            .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode lore: " + err))
                            .ifPresent(loreTag::add);
                }
                tag.put("lore", loreTag);
            }
            if (belongsToGroup != null) {
                GroupItem.BelongsTo.CODEC.encodeStart(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), belongsToGroup)
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
                    PortComponentSerialization.CODEC.parse(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), value)
                            .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode lore: " + err))
                            .ifPresent(loreList::add);
                }
                this.lore = loreList.isEmpty() ? null : loreList;
            } else {
                this.lore = null;
            }

            if (tag.contains("belongs_to_group")) {
                GroupItem.BelongsTo.CODEC.parse(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), tag.get("belongs_to_group"))
                        .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode belongsToGroup: " + err))
                        .ifPresent(group -> this.belongsToGroup = group);
            } else {
                this.belongsToGroup = null;
            }
        }

        @Nullable
        private HolderLookup.Provider getRegistries() {
            if (level != null) {
                return level.registryAccess();
            }
            return null;
        }

        @Override
        public CompoundTag getUpdateTag() {
            CompoundTag tag = new CompoundTag();
            encodeForNetwork(tag, Objects.requireNonNull(getRegistries()));
            return tag;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag) {
            decode(tag, Objects.requireNonNull(getRegistries()));
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            decode(tag, Objects.requireNonNull(getRegistries()));
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            encodeForPersistence(tag, Objects.requireNonNull(getRegistries()));
        }

        @Override
        public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }
    }

    public record MuralData(float x, float y, float z, float roll, float scale, Optional<Icon> icon,
                            Optional<Text> text) {
        public static final Codec<MuralData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT,"x", 0.0F).forGetter(MuralData::x),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT,"y", 0.0F).forGetter(MuralData::y),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT,"z", 0.0F).forGetter(MuralData::z),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT,"roll", 0.0F).forGetter(MuralData::roll),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT,"scale", 1.0F).forGetter(MuralData::scale),
                PortCodecExtension.lenientOptionalFieldOf(Icon.CODEC, "icon").forGetter(MuralData::icon),
                PortCodecExtension.lenientOptionalFieldOf(Text.CODEC, "text").forGetter(MuralData::text)
        ).apply(instance, MuralData::new));
        public static final Codec<List<MuralData>> LIST_CODEC = CODEC.listOf();

        public static Tag encode(Optional<List<MuralData>> datas, HolderLookup.Provider registries) {
            return datas.flatMap(data -> LIST_CODEC.encodeStart(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), data).result()).orElseGet(ListTag::new);
        }

        public static Optional<List<MuralData>> decode(ListTag tag, HolderLookup.Provider registries) {
            return LIST_CODEC.parse(PortHolderLookupExtension.Provider.createSerializationContext(registries, NbtOps.INSTANCE), tag).result();
        }
    }

    public record Icon(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset,
                       int uWidth, int vHeight, int textureWidth, int textureHeight) {
        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("atlasLocation").forGetter(Icon::atlasLocation),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "x", 0).forGetter(Icon::x),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "y", 0).forGetter(Icon::y),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "uOffset", 0.0F).forGetter(Icon::uOffset),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "vOffset", 0.0F).forGetter(Icon::vOffset),
                Codec.INT.fieldOf("uWidth").forGetter(Icon::uWidth),
                Codec.INT.fieldOf("vHeight").forGetter(Icon::vHeight),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "textureWidth", 256).forGetter(Icon::textureWidth),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "textureHeight", 256).forGetter(Icon::textureHeight)
        ).apply(instance, Icon::new));
    }

    public record Text(Component component, float x, float y, int color, int backgroundColor,
                       boolean dropShadow) {
        public static final Codec<Text> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortComponentSerialization.CODEC.fieldOf("component").forGetter(Text::component),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "x", 0.0F).forGetter(Text::x),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "y", 0.0F).forGetter(Text::y),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "color", -1).forGetter(Text::color),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "backgroundColor", 0).forGetter(Text::backgroundColor),
                PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "dropShadow", false).forGetter(Text::dropShadow)
        ).apply(instance, Text::new));
    }
}
