package org.confluence.mod.common.block.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
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
    public static final String TAG_DISPLAY_STATE = "displayState";
    public static final String TAG_MURAL_WIDTH = "muralWidth";
    public static final String TAG_MURAL_HEIGHT = "muralHeight";
    private static final String TAG_HEAD_POS = "headPos";
    private static final String TAG_LORE = "lore";
    private static final String TAG_BELONGS_TO_GROUP = "belongs_to_group";
    private static final BlockState DEFAULT_DISPLAY_STATE = Blocks.STONE_BRICKS.defaultBlockState();
    private static final Map<GlobalPos, BEntity> BE_CACHE = new HashMap<>();

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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
        GroupItem.BelongsTo belongsToGroup = stack.get(ConfluenceMagicLib.BELONGS_TO_GROUP);
        if (belongsToGroup != null) {
            muralEntity.setBelongsToGroup(belongsToGroup);
        }

        Direction facing = state.getValue(FACING);
        int xOffset = -facing.getStepZ();
        int zOffset = facing.getStepX();
        BlockState displayState = muralEntity.getDisplayState();

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
                    newMuralEntity.setDisplayState(displayState);
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
        BEntity headMuralEntity = findHeadEntity(level, pos, state, be, true);
        if (hasSilkTouch) {
            ItemStack dropStack = new ItemStack(this);
            if (headMuralEntity != null) {
                headMuralEntity.saveToItem(dropStack, level.registryAccess());
                headMuralEntity.restoreItemComponents(dropStack);
            }
            popResource(level, pos, dropStack);
        } else {
            BEntity dropSource = headMuralEntity == null ? null : headMuralEntity;
            BlockState displayState = dropSource == null ? DEFAULT_DISPLAY_STATE : dropSource.getDisplayState();
            int dropCount = dropSource == null ? 1 : dropSource.getBlockCount();
            ItemStack dropStack = new ItemStack(displayState.getBlock().asItem(), dropCount);
            if (!dropStack.isEmpty()) popResource(level, pos, dropStack);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        BEntity headMuralEntity = findHeadEntity(level, pos, state, level.getBlockEntity(pos), false);
        if (headMuralEntity != null) {
            headMuralEntity.setChanged();
            headMuralEntity.saveToItem(stack, level.registryAccess());
            headMuralEntity.restoreItemComponents(stack);
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
            if (headBe instanceof BEntity headMuralEntity)
                BE_CACHE.put(GlobalPos.of(level.dimension(), headPos), headMuralEntity);
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
        ItemStack stack = context.getItemInHand();
        int width = getMuralWidth(stack);
        int height = getMuralHeight(stack);
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

    public static int getMuralWidth(ItemStack stack) {
        return getMuralSize(stack, TAG_MURAL_WIDTH);
    }

    public static int getMuralHeight(ItemStack stack) {
        return getMuralSize(stack, TAG_MURAL_HEIGHT);
    }

    private static int getMuralSize(ItemStack stack, String key) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return 1;
        CompoundTag tag = customData.copyTag();
        return tag.contains(key) ? Math.max(1, tag.getInt(key)) : 1;
    }

    private static @Nullable BEntity findHeadEntity(LevelReader level, BlockPos pos, BlockState state, @Nullable BlockEntity be, boolean useCache) {
        if (!(be instanceof BEntity muralEntity)) return null;
        if (!muralEntity.isChild()) return muralEntity;

        BlockPos headWorldPos = getHeadWorldPos(pos, state, muralEntity.getHeadPos());
        if (useCache && level instanceof Level realLevel) {
            BEntity cached = BE_CACHE.remove(GlobalPos.of(realLevel.dimension(), headWorldPos));
            if (cached != null) return cached;
        }
        BlockEntity headBe = level.getBlockEntity(headWorldPos);
        return headBe instanceof BEntity headMuralEntity ? headMuralEntity : null;
    }

    private static BlockPos getHeadWorldPos(BlockPos pos, BlockState state, BlockPos relPos) {
        Direction facing = state.getValue(FACING);
        int xOffset = -facing.getStepZ();
        int zOffset = facing.getStepX();
        return pos.offset(-xOffset * relPos.getX(), -relPos.getY(), -zOffset * relPos.getX());
    }

    private static BlockState sanitizeDisplayState(@Nullable BlockState state) {
        if (state == null || state.isAir() || state.getBlock() instanceof MuralBlock || state.getRenderShape() != RenderShape.MODEL) {
            return DEFAULT_DISPLAY_STATE;
        }
        return state;
    }

    private static void putDisplayState(CompoundTag tag, HolderLookup.Provider registries, BlockState state) {
        BlockState.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), sanitizeDisplayState(state))
                .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode displayState: " + err))
                .ifPresent(stateTag -> tag.put(TAG_DISPLAY_STATE, stateTag));
    }

    private static BlockState readDisplayState(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains(TAG_DISPLAY_STATE)) return DEFAULT_DISPLAY_STATE;
        return BlockState.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(TAG_DISPLAY_STATE))
                .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode displayState: " + err))
                .map(MuralBlock::sanitizeDisplayState)
                .orElse(DEFAULT_DISPLAY_STATE);
    }

    private static void putLore(CompoundTag tag, HolderLookup.Provider registries, @Nullable List<Component> lore) {
        if (lore == null || lore.isEmpty()) return;
        ListTag loreTag = new ListTag();
        for (Component component : lore) {
            ComponentSerialization.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), component)
                    .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode lore: " + err))
                    .ifPresent(loreTag::add);
        }
        if (!loreTag.isEmpty()) tag.put(TAG_LORE, loreTag);
    }

    private static @Nullable List<Component> readLore(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains(TAG_LORE)) return null;
        ListTag loreTag = tag.getList(TAG_LORE, Tag.TAG_COMPOUND);
        if (loreTag.isEmpty()) return null;
        List<Component> lore = new ArrayList<>();
        for (Tag value : loreTag) {
            ComponentSerialization.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), value)
                    .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode lore: " + err))
                    .ifPresent(lore::add);
        }
        return lore.isEmpty() ? null : lore;
    }

    private static void putBelongsToGroup(CompoundTag tag, HolderLookup.Provider registries, @Nullable GroupItem.BelongsTo belongsToGroup) {
        if (belongsToGroup == null) return;
        GroupItem.BelongsTo.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), belongsToGroup)
                .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to encode belongsToGroup: " + err))
                .ifPresent(groupTag -> tag.put(TAG_BELONGS_TO_GROUP, groupTag));
    }

    private static @Nullable GroupItem.BelongsTo readBelongsToGroup(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains(TAG_BELONGS_TO_GROUP)) return null;
        return GroupItem.BelongsTo.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(TAG_BELONGS_TO_GROUP))
                .resultOrPartial(err -> System.err.println("[MuralBlock] Failed to decode belongsToGroup: " + err))
                .orElse(null);
    }

    public static class BEntity extends BlockEntity {
        private Optional<List<MuralData>> back = Optional.empty();
        private Optional<List<MuralData>> left = Optional.empty();
        private Optional<List<MuralData>> right = Optional.empty();
        private Optional<List<MuralData>> front = Optional.empty();
        private int muralWidth = 1;
        private int muralHeight = 1;
        private BlockPos headPos = BlockPos.ZERO;
        private BlockState displayState = DEFAULT_DISPLAY_STATE;
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

        public BlockState getDisplayState() {return displayState;}

        public List<Component> getLore() {return lore;}

        public GroupItem.BelongsTo getBelongsToGroup() {return belongsToGroup;}

        public boolean isChild() {return muralWidth == -1 || muralHeight == -1;}

        public int getBlockCount() {return Math.max(1, muralWidth) * Math.max(1, muralHeight);}

        public void setMuralWidth(int w) {muralWidth = w;}

        public void setMuralHeight(int h) {muralHeight = h;}

        public void setHeadPos(BlockPos p) {headPos = p;}

        public void setDisplayState(BlockState state) {
            displayState = sanitizeDisplayState(state);
            requestModelDataUpdate();
            if (level != null && level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }

        public void setLore(List<Component> lore) {this.lore = lore;}

        public void setBelongsToGroup(GroupItem.BelongsTo belongsToGroup) {this.belongsToGroup = belongsToGroup;}

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
            tag.putInt(TAG_MURAL_WIDTH, muralWidth);
            tag.putInt(TAG_MURAL_HEIGHT, muralHeight);
            tag.putLong(TAG_HEAD_POS, headPos.asLong());
            putDisplayState(tag, registries, displayState);
            putLore(tag, registries, lore);
            putBelongsToGroup(tag, registries, belongsToGroup);
        }

        private void encodeForPersistence(CompoundTag tag, HolderLookup.Provider registries) {
            tag.put("back", MuralData.encode(back, registries));
            tag.put("left", MuralData.encode(left, registries));
            tag.put("right", MuralData.encode(right, registries));
            tag.put("front", MuralData.encode(front, registries));
            tag.putInt(TAG_MURAL_WIDTH, muralWidth);
            tag.putInt(TAG_MURAL_HEIGHT, muralHeight);
            tag.putLong(TAG_HEAD_POS, headPos.asLong());
            putDisplayState(tag, registries, displayState);
            putLore(tag, registries, lore);
            putBelongsToGroup(tag, registries, belongsToGroup);
        }

        private void decode(CompoundTag tag, HolderLookup.Provider registries) {
            back = MuralData.decode(tag.getList("back", Tag.TAG_COMPOUND), registries);
            left = MuralData.decode(tag.getList("left", Tag.TAG_COMPOUND), registries);
            right = MuralData.decode(tag.getList("right", Tag.TAG_COMPOUND), registries);
            front = MuralData.decode(tag.getList("front", Tag.TAG_COMPOUND), registries);
            muralWidth = tag.contains(TAG_MURAL_WIDTH) ? tag.getInt(TAG_MURAL_WIDTH) : 1;
            muralHeight = tag.contains(TAG_MURAL_HEIGHT) ? tag.getInt(TAG_MURAL_HEIGHT) : 1;
            headPos = tag.contains(TAG_HEAD_POS) ? BlockPos.of(tag.getLong(TAG_HEAD_POS)) : BlockPos.ZERO;
            displayState = readDisplayState(tag, registries);
            lore = readLore(tag, registries);
            belongsToGroup = readBelongsToGroup(tag, registries);
            requestModelDataUpdate();
            if (level != null && level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
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
