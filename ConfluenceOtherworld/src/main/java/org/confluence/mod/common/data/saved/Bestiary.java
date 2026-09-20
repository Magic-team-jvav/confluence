package org.confluence.mod.common.data.saved;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.api.event.bestiary.ToBeBestiaryEntryEvent;
import org.confluence.mod.common.data.map.PresetBestiaryEntry;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.network.s2c.BestiarySyncPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Map;
import java.util.function.Predicate;

public enum Bestiary implements IGlobalData {
    INSTANCE;
    public static final Codec<Map<String, Entry>> CODEC = Codec.unboundedMap(Codec.STRING, Entry.CODEC);
    private static final Object2BooleanMap<EntityType<?>> AVAILABLE = new Object2BooleanOpenCustomHashMap<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(EntityType<?> o) {
            return System.identityHashCode(o);
        }

        @Override
        public boolean equals(EntityType<?> a, EntityType<?> b) {
            return a == b;
        }
    });

    private Map<String, Entry> entries = new Object2ObjectOpenHashMap<>();

    @Override
    public void decode(CompoundTag tag) {
        PortDataResultExtension.ifSuccess(CODEC.parse(NbtOps.INSTANCE, tag.get("entries")),
                result -> this.entries = new Object2ObjectOpenHashMap<>(result));
    }

    @Override
    public void encode(CompoundTag tag) {
        PortDataResultExtension.ifSuccess(CODEC.encodeStart(NbtOps.INSTANCE, entries),
                nbt -> tag.put("entries", nbt));
    }

    @Override
    public String serializeKey() {
        return "confluence:bestiary";
    }

    public Map<String, Entry> getEntries() {
        return entries;
    }

    public int getUnlockedCount() {
        return entries.size();
    }

    @Override
    public void clear() {
        this.entries = new Object2ObjectOpenHashMap<>();
    }

    public Entry getOrCreateEntry(LivingEntity living) {
        return entries.computeIfAbsent(RegisterBestiaryKeyEvent.getKey(living), key -> {
            Entry entry = PresetBestiaryEntry.getEntry(living, key);
            if (entry != null) return entry;

            entry = new Entry();
            entry.type = living.getType();
            entry.key = key;
            AttributeMap map = living.getAttributes();
            entry.maxHealth = getAttributeBaseValue(map, Attributes.MAX_HEALTH);
            entry.knockbackResistance = getAttributeBaseValue(map, Attributes.KNOCKBACK_RESISTANCE);
            entry.attackDamage = getAttributeBaseValue(map, LibAttributes.getAttackDamage().value());
            entry.armor = getAttributeBaseValue(map, Attributes.ARMOR);
            entry.drops = living instanceof Enemy ? (int) ModUtils.getLivingBaseMoneyDrops(living, living.level()) : 0;
            return entry;
        });
    }

    public void updateEntry(LivingEntity living, boolean killed) {
        if (living.level().isClientSide) return;
        if (!canBeSeenAsBestiaryEntry(living)) return;

        Entry entry = getOrCreateEntry(living);
        entry.unlock();
        if (killed) {
            entry.killedByCount++;
        }
        if (!entry.isCompleted()) {
            entry.updateUnlockedProgress(living);
        }

        if (entry.killedByCount > 1) {
            BestiarySyncPacketS2C.syncEntry(living);
        } else { // 表示需要初始化
            BestiarySyncPacketS2C.syncEntry(living, entry);
        }

        if (getUnlockedCount() >= 540) {
            MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    AchievementUtils.awardAchievement(player, "book_worm");
                }
            }
        }
    }

    public boolean containsKey(LivingEntity living) {
        return entries.containsKey(RegisterBestiaryKeyEvent.getKey(living));
    }

    private static float getAttributeBaseValue(AttributeMap map, Attribute attribute) {
        AttributeInstance instance = map.getInstance(attribute);
        return instance == null ? 0.0F : (float) instance.getBaseValue();
    }

    public static boolean isAvailableType(EntityType<?> type, Level level) {
        return AVAILABLE.computeIfAbsent(type, (Predicate<EntityType<?>>) t -> t.create(level) != null);
    }

    public static boolean canBeSeenAsBestiaryEntry(LivingEntity living) {
        return isAvailableType(living.getType(), living.level()) &&
                (living.getType().is(ModTags.EntityTypes.BESTIARY_WHITELIST) ||
                        !PortEventHandler.postEventWithReturn(new ToBeBestiaryEntryEvent(living)).isCanceled());
    }

    /// [怪物图鉴](https://terraria.wiki.gg/zh/wiki/%E6%80%AA%E7%89%A9%E5%9B%BE%E9%89%B4)
    public static class Entry {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(entry -> entry.type),
                Codec.INT.fieldOf("killed_by_count").forGetter(entry -> entry.killedByCount),
                Codec.FLOAT.fieldOf("max_health").forGetter(entry -> entry.maxHealth),
                Codec.FLOAT.fieldOf("knockback_resistance").forGetter(entry -> entry.knockbackResistance),
                Codec.FLOAT.fieldOf("attack_damage").forGetter(entry -> entry.attackDamage),
                Codec.FLOAT.fieldOf("armor").forGetter(entry -> entry.armor),
                Codec.INT.fieldOf("drops").forGetter(entry -> entry.drops),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "unlocked_progress", 1F).forGetter(entry -> entry.unlockedProgress)
        ).apply(instance, Entry::new));
        public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Entry> STREAM_CODEC = LibStreamCodecUtils.composite(
                PortByteBufCodecs.registry(Registries.ENTITY_TYPE), entry -> entry.type,
                PortByteBufCodecs.VAR_INT, entry -> entry.killedByCount,
                PortByteBufCodecs.FLOAT, entry -> entry.maxHealth,
                PortByteBufCodecs.FLOAT, entry -> entry.knockbackResistance,
                PortByteBufCodecs.FLOAT, entry -> entry.attackDamage,
                PortByteBufCodecs.FLOAT, entry -> entry.armor,
                PortByteBufCodecs.VAR_INT, entry -> entry.drops,
                Entry::new
        );

        public EntityType<?> type;
        public int killedByCount;
        public float maxHealth;
        public float knockbackResistance;
        public float attackDamage;
        public float armor;
        public int drops;
        public float unlockedProgress = -1; // 小于零代表未解锁

        public transient String key;
        private transient Coins coins;

        public Entry() {}

        private Entry(
                EntityType<?> type,
                int killedByCount,
                float maxHealth,
                float knockbackResistance,
                float attackDamage,
                float armor,
                int drops
        ) {
            this(type, killedByCount, maxHealth, knockbackResistance, attackDamage, armor, drops, -1);
        }

        private Entry(
                EntityType<?> type,
                int killedByCount,
                float maxHealth,
                float knockbackResistance,
                float attackDamage,
                float armor,
                int drops,
                float unlockedProgress
        ) {
            this.type = type;
            this.killedByCount = killedByCount;
            this.maxHealth = maxHealth;
            this.knockbackResistance = knockbackResistance;
            this.attackDamage = attackDamage;
            this.armor = armor;
            this.drops = drops;
            this.unlockedProgress = unlockedProgress;
        }

        public float getUnlockedProgress() {
            return Mth.clamp(unlockedProgress, 0.0F, 1.0F);
        }

        public boolean isLocked() {
            return unlockedProgress < -Mth.EPSILON;
        }

        public boolean unlock() {
            if (isLocked()) {
                this.unlockedProgress = 0.0F;
                return true;
            }
            return false;
        }

        public boolean isCompleted() {
            return unlockedProgress >= 1.0F - Mth.EPSILON;
        }

        protected void updateUnlockedProgress(LivingEntity living) {
            Integer required = ModDataMaps.getEntityData(ModDataMaps.BANNER_UNLOCK_REQUIRED, type);
            if (required != null) {
                float v = required.floatValue();
                if (v <= 0) {
                    this.unlockedProgress = 1;
                } else {
                    this.unlockedProgress = Mth.clamp(killedByCount / v, 0, 1);
                }
            } else if (living instanceof Npc || LibEntityUtils.isAnimal(living) || type.is(Tags.EntityTypes.BOSSES)) {
                this.unlockedProgress = 1;
            } else {
                this.unlockedProgress = Mth.clamp(killedByCount / 50.0F, 0, 1);
            }
        }

        public Coins getCoins() {
            if (coins == null) {
                this.coins = PlayerUtils.decodeCoin(drops);
            }
            return coins;
        }

        public Entry copy() {
            Entry entry = new Entry(
                    type,
                    killedByCount,
                    maxHealth,
                    knockbackResistance,
                    attackDamage,
                    armor,
                    drops
            );
            entry.key = key;
            return entry;
        }

        public static Builder builder(EntityType<?> type, String key) {
            return new Builder(type, key);
        }

        public static class Builder {
            private final EntityType<?> type;
            private final String key;
            private float maxHealth;
            private float knockbackResistance;
            private float attackDamage;
            private float armor;
            private int drops;

            private Builder(EntityType<?> type, String key) {
                this.type = type;
                this.key = key;
            }

            public Builder maxHealth(float maxHealth) {
                this.maxHealth = maxHealth;
                return this;
            }

            public Builder knockbackResistance(float knockbackResistance) {
                this.knockbackResistance = knockbackResistance;
                return this;
            }

            public Builder attackDamage(float attackDamage) {
                this.attackDamage = attackDamage;
                return this;
            }

            public Builder armor(float armor) {
                this.armor = armor;
                return this;
            }

            public Builder drops(int drops) {
                this.drops = drops;
                return this;
            }

            public Entry build() {
                Entry entry = new Entry(type, 0, maxHealth, knockbackResistance, attackDamage, armor, drops);
                entry.key = key;
                return entry;
            }
        }
    }
}
