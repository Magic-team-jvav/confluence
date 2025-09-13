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
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

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

    public void increaseKilledByCount(LivingEntity living) {
        getOrCreateEntry(living).killedByCount++;
    }

    public @Nullable BestiaryEntry getEntry(EntityType<?> entityType) {
        return entries.get(entityType);
    }

    public BestiaryEntry getOrCreateEntry(LivingEntity living) {
        return entries.computeIfAbsent(living.getType(), type -> {
            BestiaryEntry entry = new BestiaryEntry();
            entry.type = type;
            AttributeMap map = living.getAttributes();
            entry.maxHealth = getAttributeBaseValue(map, Attributes.MAX_HEALTH);
            entry.knockbackResistance = getAttributeBaseValue(map, Attributes.KNOCKBACK_RESISTANCE);
            entry.attackDamage = getAttributeBaseValue(map, Attributes.ATTACK_DAMAGE);
            entry.armor = getAttributeBaseValue(map, Attributes.ARMOR);
            entry.coins = PlayerUtils.decodeCoin((int) ModUtils.getLivingBaseMoneyDrops(living, living.level()));
            return entry;
        });
    }

    private static double getAttributeBaseValue(AttributeMap map, Holder<Attribute> attribute) {
        AttributeInstance instance = map.getInstance(attribute);
        return instance == null ? 0.0 : instance.getBaseValue();
    }
}
