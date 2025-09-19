package org.confluence.mod.common.data.saved;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.api.event.bestiary.ToBeBestiaryEntryEvent;
import org.confluence.mod.common.data.map.PresetBestiaryEntry;
import org.confluence.mod.network.s2c.BestiarySyncPacketS2C;
import org.confluence.mod.util.ModUtils;

import java.util.Map;

public class Bestiary implements IGlobalData {
    public static final Codec<Map<String, BestiaryEntry>> CODEC = Codec.unboundedMap(Codec.STRING, BestiaryEntry.CODEC);
    public static final Bestiary INSTANCE = new Bestiary();

    private final Map<String, BestiaryEntry> entries = Maps.newHashMap();

    private Bestiary() {}

    @Override
    public <T> void decode(Dynamic<T> tag) {
        entries.clear();
        tag.get("entries").orElseEmptyMap().read(CODEC).ifSuccess(entries::putAll);
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("entries", CODEC.encodeStart(NbtOps.INSTANCE, entries).result().orElseGet(CompoundTag::new));
    }

    @Override
    public String serializeKey() {
        return "confluence:bestiary";
    }

    public Map<String, BestiaryEntry> getEntries() {
        return entries;
    }

    @Override
    public void clear() {
        entries.clear();
    }

    public BestiaryEntry getOrCreateEntry(LivingEntity living) {
        return entries.computeIfAbsent(RegisterBestiaryKeyEvent.getKey(living), key -> {
            BestiaryEntry entry = PresetBestiaryEntry.getEntry(living);
            if (entry != null) return entry;

            entry = new BestiaryEntry();
            entry.type = living.getType();
            entry.key = key;
            AttributeMap map = living.getAttributes();
            entry.maxHealth = getAttributeBaseValue(map, Attributes.MAX_HEALTH);
            entry.knockbackResistance = getAttributeBaseValue(map, Attributes.KNOCKBACK_RESISTANCE);
            entry.attackDamage = getAttributeBaseValue(map, Attributes.ATTACK_DAMAGE);
            entry.armor = getAttributeBaseValue(map, Attributes.ARMOR);
            entry.drops = living instanceof Enemy ? (int) ModUtils.getLivingBaseMoneyDrops(living, living.level()) : 0;
            return entry;
        });
    }

    public void updateEntry(LivingEntity living, boolean killed) {
        if (NeoForge.EVENT_BUS.post(new ToBeBestiaryEntryEvent(living)).isCanceled()) return;

        BestiaryEntry entry = getOrCreateEntry(living);
        if (killed) entry.killedByCount++;

        if (entry.killedByCount > 1) {
            BestiarySyncPacketS2C.syncEntry(living);
        } else { // 表示需要初始化
            BestiarySyncPacketS2C.syncEntry(living, entry);
        }
    }

    public boolean containsKey(LivingEntity living) {
        return entries.containsKey(RegisterBestiaryKeyEvent.getKey(living));
    }

    private static float getAttributeBaseValue(AttributeMap map, Holder<Attribute> attribute) {
        AttributeInstance instance = map.getInstance(attribute);
        return instance == null ? 0.0F : (float) instance.getBaseValue();
    }
}
