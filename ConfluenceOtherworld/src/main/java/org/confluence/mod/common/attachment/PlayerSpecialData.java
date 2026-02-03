package org.confluence.mod.common.attachment;

import com.google.common.collect.Iterables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.armor.ArmorSetBonusData;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.network.s2c.SyncEnemyBannerEntriesPacketS2C;
import org.confluence.terra_curio.common.attachment.PrimitiveValueHolder;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerSpecialData extends PrimitiveValueHolder {
    private @NotNull ArmorSetBonusKey armorSetBonusKey = ArmorSetBonusKey.NONE;

    private ItemStack currentQuestedFish;
    private ITradeLock currentQuestedFishCondition;

    private boolean couldHurtCritters;
    private boolean couldDamageEnvironment;
    //    private boolean fallenSoulCoreActive = false;
    private final transient Map<String, Set<BlockPos>> enemyBannerEntries = new HashMap<>();
    private transient boolean dirty;

    @Override
    public void setToDefaultValue() {
        super.setToDefaultValue();
        this.armorSetBonusKey = ArmorSetBonusKey.NONE;

        this.currentQuestedFish = ItemStack.EMPTY;
        this.currentQuestedFishCondition = ITradeLock.alwaysTrue();

        this.couldHurtCritters = true;
        this.couldDamageEnvironment = true;
//        this.fallenSoulCoreActive = false;
    }

    public ArmorSetBonusKey getArmorSetBonusKey() {
        return armorSetBonusKey;
    }

    public void setCurrentQuestedFish(ItemStack cost, ITradeLock lock) {
        this.currentQuestedFish = cost;
        this.currentQuestedFishCondition = lock;
    }

    public void removeCurrentQuestedFish() {
        setCurrentQuestedFish(ItemStack.EMPTY, ITradeLock.alwaysTrue());
    }

    public ItemStack getCurrentQuestedFish(Player player) {
        if (currentQuestedFishCondition.canTrade(player, ITradeHolder.dummy(player), 0)) {
            return currentQuestedFish;
        }
        return ItemStack.EMPTY;
    }

    public void setCouldHurtCritters(boolean couldHurtCritters) {
        this.couldHurtCritters = couldHurtCritters;
    }

    public boolean isCouldHurtCritters() {
        return couldHurtCritters;
    }

    public void setCouldDamageEnvironment(boolean couldDamageEnvironment) {
        this.couldDamageEnvironment = couldDamageEnvironment;
    }

    public boolean isCouldDamageEnvironment() {
        return couldDamageEnvironment;
    }

//    public boolean isFallenSoulCoreActive() {
//        return fallenSoulCoreActive;
//    }
//
//    public void setFallenSoulCoreActive(boolean active) {
//        this.fallenSoulCoreActive = active;
//    }

    public void updateEnemyBannerEntries(String key, BlockPos pos, boolean add) {
        Set<BlockPos> blockPos = enemyBannerEntries.computeIfAbsent(key, s -> new HashSet<>());
        if (add) {
            if (blockPos.add(pos)) {
                dirty = true;
            }
        } else if (blockPos.remove(pos)) {
            dirty = true;
        }
    }

    public void setEnemyBannerEntries(List<String> entries) {
        clearEnemyBannerEntries();
        for (String entry : entries) {
            enemyBannerEntries.put(entry, Set.of());
        }
    }

    public Set<String> getEnemyBannerEntries() {
        return enemyBannerEntries.keySet();
    }

    public void clearEnemyBannerEntries() {
        enemyBannerEntries.clear();
    }

    private void syncEnemyBannerEntries(ServerPlayer player) {
        List<String> entries = null;
        ServerChunkCache chunkSource = null;
        Iterator<Map.Entry<String, Set<BlockPos>>> entryIterator = enemyBannerEntries.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Set<BlockPos>> entry = entryIterator.next();
            Iterator<BlockPos> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                BlockPos pos = iterator.next();
                if (chunkSource == null) chunkSource = player.serverLevel().getChunkSource();
                ChunkAccess access = LibUtils.getChunkIfLoaded(chunkSource, pos);
                if (access == null) continue;
                BlockEntity blockEntity = access.getBlockEntity(pos);
                if (blockEntity != null && blockEntity.getType() == ModBlocks.ENEMY_BANNER_ENTITY.get()) {
                    if (entries == null) entries = new ArrayList<>();
                    entries.add(entry.getKey());
                } else {
                    iterator.remove();
                }
            }
            if (entry.getValue().isEmpty()) {
                entryIterator.remove();
            }
        }
        if (enemyBannerEntries.isEmpty()) {
            player.removeEffect(ModEffects.ENEMY_BANNER);
        }
        if (entries != null) {
            PacketDistributor.sendToPlayer(player, new SyncEnemyBannerEntriesPacketS2C(entries));
        }
    }

    public void sync(ServerPlayer player) {
        if (!dirty) return;
        this.dirty = false;
        syncEnemyBannerEntries(player);
    }

    /// [net.minecraft.world.entity.LivingEntity#collectEquipmentChanges]
    public void flushArmorSetBonus(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());
        ItemStack chest = inventory.getArmor(EquipmentSlot.CHEST.getIndex());
        ItemStack legs = inventory.getArmor(EquipmentSlot.LEGS.getIndex());
        ItemStack feet = inventory.getArmor(EquipmentSlot.FEET.getIndex());
        ArmorSetBonusKey key = ArmorSetBonusKey.of(head, chest, legs, feet);
        if (armorSetBonusKey.equals(key)) {
            ArmorSetBonusKey lastKey = ArmorSetBonusKey.of(
                    player.getLastArmorItem(EquipmentSlot.HEAD),
                    player.getLastArmorItem(EquipmentSlot.CHEST),
                    player.getLastArmorItem(EquipmentSlot.LEGS),
                    player.getLastArmorItem(EquipmentSlot.FEET)
            );
            if (lastKey.equals(key)) {
                return;
            }
        }
        AttributeMap attributes = player.getAttributes();

        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : getValue(TCItems.ATTRIBUTES).asMap().entrySet()) {
            AttributeInstance attributeinstance = attributes.getInstance(entry.getKey());
            if (attributeinstance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                attributeinstance.removeModifier(modifier);
            }
        }

        setToDefaultValue();
        ArmorSetBonusData data = ModArmorBonus.getArmorSetBonusData(player, key);
        if (data == null) {
            this.armorSetBonusKey = ArmorSetBonusKey.NONE;
        } else {
            this.armorSetBonusKey = key;
            compute(data.bonus());
        }
        flushArmor(head);
        flushArmor(chest);
        flushArmor(legs);
        flushArmor(feet);

        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : getValue(TCItems.ATTRIBUTES).asMap().entrySet()) {
            AttributeInstance attributeinstance = attributes.getInstance(entry.getKey());
            if (attributeinstance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                attributeinstance.removeModifier(modifier);
                attributeinstance.addTransientModifier(modifier);
            }
        }

        NeoForge.EVENT_BUS.post(new AfterFlushArmorSetBonusEvent(player, this));
    }

    private void flushArmor(ItemStack stack) {
        if (stack.isEmpty()) return;
        PrimitiveValueComponent component = ModArmorBonus.getArmorStackBonus(stack);
        if (component != null) compute(component);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag tag = super.serializeNBT(provider);

        ArmorSetBonusKey.CODEC.encodeStart(ops, armorSetBonusKey).ifSuccess(nbt -> tag.put("ArmorBonusKey", nbt));
        tag.put("CurrentQuestedFish", ItemStack.OPTIONAL_CODEC.encodeStart(ops, currentQuestedFish).result().orElseGet(CompoundTag::new));
        tag.put("CurrentQuestedFishCondition", ITradeLock.TYPED_CODEC.encodeStart(ops, currentQuestedFishCondition).result().orElseGet(CompoundTag::new));
        tag.putBoolean("CouldHurtCritters", couldHurtCritters);
//        tag.putBoolean("FallenSoulCoreActive", fallenSoulCoreActive);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        super.deserializeNBT(provider, nbt);

        if (nbt.contains("ArmorBonusKey")) {
            this.armorSetBonusKey = ArmorSetBonusKey.CODEC.parse(ops, nbt.get("ArmorBonusKey")).result().orElse(ArmorSetBonusKey.NONE);
        }
        this.currentQuestedFish = ItemStack.OPTIONAL_CODEC.parse(ops, nbt.get("CurrentQuestedFish")).result().orElse(ItemStack.EMPTY);
        this.currentQuestedFishCondition = ITradeLock.TYPED_CODEC.parse(ops, nbt.get("CurrentQuestedFishCondition")).result().orElse(ITradeLock.alwaysTrue());
        this.couldHurtCritters = nbt.getBoolean("CouldHurtCritters");
//        this.fallenSoulCoreActive = nbt.getBoolean("FallenSoulCoreActive");
    }

    public static PlayerSpecialData of(Player player) {
        return player.getData(ModAttachmentTypes.SPECIAL_DATA);
    }

    public static void resetSomeData(Player player) {
        PlayerSpecialData data = of(player);
        if (!data.isCouldHurtCritters() || !data.isCouldDamageEnvironment()) {
            data.setCouldHurtCritters(true);
            data.setCouldDamageEnvironment(true);
            for (ItemStack stack : Iterables.concat(player.getInventory().offhand, player.getInventory().items)) {
                if (stack == null) continue;
                boolean a = data.isCouldHurtCritters();
                boolean b = data.isCouldDamageEnvironment();
                if (!a && !b) break;
                Item item = stack.getItem();
                boolean c = item == ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get();
                if (a && (c || item == ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get())) {
                    data.setCouldHurtCritters(false);
                }
                if (b && (c || item == ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get())) {
                    data.setCouldDamageEnvironment(false);
                }
            }
        }
        if (player.getActiveEffectsMap().get(ModEffects.ENEMY_BANNER) == null) {
            data.clearEnemyBannerEntries();
        } else if (player instanceof ServerPlayer serverPlayer) {
            data.syncEnemyBannerEntries(serverPlayer);
        }
    }
}
