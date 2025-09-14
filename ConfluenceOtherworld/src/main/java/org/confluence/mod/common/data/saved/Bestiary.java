package org.confluence.mod.common.data.saved;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.common.data.map.BestiaryEntry;
import org.confluence.mod.network.s2c.BestiarySyncPacketS2C;
import org.confluence.mod.util.ModUtils;

import java.util.Map;

public class Bestiary implements IGlobalData {
    public static final Bestiary INSTANCE = new Bestiary();

    private final Map<EntityType<?>, BestiaryEntry> entries = Maps.newHashMap();

    private Bestiary() {}

    @Override
    public <T> void decode(Dynamic<T> tag) {
        entries.clear();
        tag.get("entries").orElseEmptyList().asStream().forEach(dynamic -> {
            BestiaryEntry entry = dynamic.decode(BestiaryEntry.CODEC).map(Pair::getFirst).getOrThrow();
            entries.put(entry.type, entry);
        });
    }

    @Override
    public void encode(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<EntityType<?>, BestiaryEntry> entry : entries.entrySet()) {
            list.add(BestiaryEntry.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).getOrThrow());
        }
        tag.put("entries", list);
    }

    @Override
    public String serializeKey() {
        return "confluence:bestiary";
    }

    public Map<EntityType<?>, BestiaryEntry> getEntries() {
        return entries;
    }

    public BestiaryEntry getOrCreateEntry(LivingEntity living) {
        return entries.computeIfAbsent(living.getType(), type -> {
            BestiaryEntry entry = BestiaryEntry.getPresetEntry(type);
            if (entry != null) return entry;

            entry = new BestiaryEntry();
            entry.type = type;
            AttributeMap map = living.getAttributes();
            entry.maxHealth = getAttributeBaseValue(map, Attributes.MAX_HEALTH);
            entry.knockbackResistance = getAttributeBaseValue(map, Attributes.KNOCKBACK_RESISTANCE);
            entry.attackDamage = getAttributeBaseValue(map, Attributes.ATTACK_DAMAGE);
            entry.armor = getAttributeBaseValue(map, Attributes.ARMOR);
            entry.drops = (int) ModUtils.getLivingBaseMoneyDrops(living, living.level());
            return entry;
        });
    }

    public void updateEntry(LivingEntity living, boolean killed) {
        BestiaryEntry entry = getOrCreateEntry(living);
        if (killed) {
            entry.killedByCount++;
            BestiarySyncPacketS2C.syncEntry(living.getType());
        } else {
            BestiarySyncPacketS2C.syncEntry(living.getType(), entry);
        }
    }

    private static float getAttributeBaseValue(AttributeMap map, Holder<Attribute> attribute) {
        AttributeInstance instance = map.getInstance(attribute);
        return instance == null ? 0.0F : (float) instance.getBaseValue();
    }
}
