package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.booleans.BooleanObjectMutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModLoader;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.api.event.RegisterCloakDataEvent;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.network.s2c.GlobalCloakSyncPacketS2C;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class GlobalCloakData implements IGlobalData {
    public static final GlobalCloakData INSTANCE = new GlobalCloakData();
    public static final Codec<Map<BlockState, BooleanObjectPair<BlockState>>> BLOCK_MAP_CODEC = LibCodecUtils.notStringKeyMap(
            "source", BlockState.CODEC,
            "pair", LibCodecUtils.booleanObjectPair("cloaked", "target", BlockState.CODEC)
    );
    public static final Codec<Map<Item, BooleanObjectPair<Item>>> ITEM_MAP_CODEC = Codec.lazyInitialized(() -> {
        Codec<Item> codec = BuiltInRegistries.ITEM.byNameCodec();
        return LibCodecUtils.notStringKeyMap(
                "source", codec,
                "pair", LibCodecUtils.booleanObjectPair("cloaked", "target", codec)
        );
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, Map<BlockState, BooleanObjectPair<BlockState>>> BLOCK_MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new, LibStreamCodecUtils.BLOCK_STATE, LibStreamCodecUtils.booleanObjectPair(LibStreamCodecUtils.BLOCK_STATE)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<Item, BooleanObjectPair<Item>>> ITEM_MAP_STREAM_CODEC = LibStreamCodecUtils.lazyInitialized(() -> {
        StreamCodec<RegistryFriendlyByteBuf, Item> streamCodec = ByteBufCodecs.registry(Registries.ITEM);
        return ByteBufCodecs.map(HashMap::new, streamCodec, LibStreamCodecUtils.booleanObjectPair(streamCodec));
    });

    private Map<BlockState, BooleanObjectPair<BlockState>> blockMap = new IdentityHashMap<>();
    private Map<BlockState, BlockBehaviour.Properties> backupProperties = new IdentityHashMap<>();
    private Map<Item, BooleanObjectPair<Item>> itemMap = new IdentityHashMap<>();

    private GlobalCloakData() {}

    public void initialize() {
        BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
        for (BlockState[] pair : StepRevealingBlock.PAIRS.get()) {
            fromBlock(pair[0], deepslate);
            fromBlock(pair[1], deepslate);
        }

        fromBlock(OreBlocks.CHLOROPHYTE_ORE.get().defaultBlockState(), Blocks.MUD.defaultBlockState());

        ModLoader.postEvent(new RegisterCloakDataEvent(this::fromBlock));
    }

    private void fromBlock(BlockState source, BlockState target) {
        blockMap.put(source, new BooleanObjectMutablePair<>(true, target));

        backupProperties.put(source, BlockBehaviour.Properties.ofFullCopy(source.getBlock()));
        replaceProperties(source, target);

        Item sourceItem = source.getBlock().asItem();
        Item targetItem = target.getBlock().asItem();
        if (sourceItem != Items.AIR && targetItem != Items.AIR) {
            itemMap.put(sourceItem, new BooleanObjectMutablePair<>(true, targetItem));
        }
    }

    public void reveal(BlockState... states) {
        for (BlockState state : states) {
            BooleanObjectPair<BlockState> pair = blockMap.get(state);
            if (pair == null) continue;
            pair.left(false);
            rollbackProperties(state, backupProperties.get(state));
            Item item = state.getBlock().asItem();
            if (item == Items.AIR) continue;
            BooleanObjectPair<Item> pair1 = itemMap.get(item);
            if (pair1 == null) continue;
            pair1.left(false);
        }
        GlobalCloakSyncPacketS2C.sendToAll();
    }

    public BlockState getTarget(BlockState source) {
        if (source.isAir()) return source;
        BooleanObjectPair<BlockState> pair = blockMap.get(source);
        if (pair == null || !pair.leftBoolean()) return source;
        return pair.right();
    }

    public Item getTarget(Item source) {
        BooleanObjectPair<Item> pair = itemMap.get(source);
        if (pair == null || !pair.leftBoolean()) return source;
        return pair.right();
    }

    public boolean isCloaked(BlockState source) {
        BooleanObjectPair<BlockState> pair = blockMap.get(source);
        return pair != null && pair.leftBoolean();
    }

    public boolean isRevealed(BlockState source) {
        return !isCloaked(source);
    }

    public boolean isCloaked(Item source) {
        BooleanObjectPair<Item> pair = itemMap.get(source);
        return pair != null && pair.leftBoolean();
    }

    public boolean isRevealed(Item source) {
        return !isCloaked(source);
    }

    @Override
    public <T> void decode(Dynamic<T> tag) {
        tag.get("BlockMap").orElseEmptyMap().read(BLOCK_MAP_CODEC).ifSuccess(blockMap::putAll);
        tag.get("ItemMap").orElseEmptyMap().read(ITEM_MAP_CODEC).ifSuccess(itemMap::putAll);

        rollbackAllProperties();
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("BlockMap", BLOCK_MAP_CODEC.encodeStart(NbtOps.INSTANCE, blockMap).result().orElseGet(CompoundTag::new));
        tag.put("ItemMap", ITEM_MAP_CODEC.encodeStart(NbtOps.INSTANCE, itemMap).result().orElseGet(CompoundTag::new));
    }

    @Override
    public void clear() {
        rollbackAllProperties();
        this.blockMap = new IdentityHashMap<>();
        this.itemMap = new IdentityHashMap<>();
        this.backupProperties = new IdentityHashMap<>();
        initialize();
    }

    @Override
    public String serializeKey() {
        return "confluence:global_cloak_data";
    }

    public void networkEncode(RegistryFriendlyByteBuf buffer) {
        BLOCK_MAP_STREAM_CODEC.encode(buffer, blockMap);
        ITEM_MAP_STREAM_CODEC.encode(buffer, itemMap);
    }

    public void networkDecode(RegistryFriendlyByteBuf buffer) {
        this.blockMap = BLOCK_MAP_STREAM_CODEC.decode(buffer);
        this.itemMap = ITEM_MAP_STREAM_CODEC.decode(buffer);
    }

    public void rollbackAllProperties() {
        for (Map.Entry<BlockState, BooleanObjectPair<BlockState>> entry : blockMap.entrySet()) {
            if (entry.getValue().leftBoolean()) continue;
            rollbackProperties(entry.getKey(), backupProperties.get(entry.getKey()));
        }
    }

    public static void replaceProperties(BlockState source, BlockState target) {
        Block sourceBlock = source.getBlock();
        Block targetBlock = target.getBlock();
        sourceBlock.hasCollision = targetBlock.hasCollision;
        sourceBlock.soundType = targetBlock.soundType;
        sourceBlock.friction = targetBlock.friction;
        sourceBlock.speedFactor = targetBlock.speedFactor;
        sourceBlock.jumpFactor = targetBlock.jumpFactor;
        sourceBlock.dynamicShape = targetBlock.dynamicShape;
        sourceBlock.requiredFeatures = targetBlock.requiredFeatures;
        sourceBlock.explosionResistance = targetBlock.explosionResistance;

        sourceBlock.properties = targetBlock.properties;
    }

    public static void rollbackProperties(BlockState source, BlockBehaviour.Properties properties) {
        Block sourceBlock = source.getBlock();
        sourceBlock.hasCollision = properties.hasCollision;
        sourceBlock.soundType = properties.soundType;
        sourceBlock.friction = properties.friction;
        sourceBlock.speedFactor = properties.speedFactor;
        sourceBlock.jumpFactor = properties.jumpFactor;
        sourceBlock.dynamicShape = properties.dynamicShape;
        sourceBlock.requiredFeatures = properties.requiredFeatures;
        sourceBlock.explosionResistance = properties.explosionResistance;

        sourceBlock.properties = properties;
    }
}
