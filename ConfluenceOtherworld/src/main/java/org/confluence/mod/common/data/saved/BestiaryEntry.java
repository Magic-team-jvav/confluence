package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Npc;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

/// <a href="https://terraria.wiki.gg/zh/wiki/%E6%80%AA%E7%89%A9%E5%9B%BE%E9%89%B4">怪物图鉴</a>
public class BestiaryEntry {
    public static final Codec<BestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(entry -> entry.type),
            Codec.INT.fieldOf("killed_by_count").forGetter(entry -> entry.killedByCount),
            Codec.FLOAT.fieldOf("max_health").forGetter(entry -> entry.maxHealth),
            Codec.FLOAT.fieldOf("knockback_resistance").forGetter(entry -> entry.knockbackResistance),
            Codec.FLOAT.fieldOf("attack_damage").forGetter(entry -> entry.attackDamage),
            Codec.FLOAT.fieldOf("armor").forGetter(entry -> entry.armor),
            Codec.INT.fieldOf("drops").forGetter(entry -> entry.drops),
            Codec.FLOAT.lenientOptionalFieldOf("unlocked_progress", 1F).forGetter(entry -> entry.unlockedProgress)
    ).apply(instance, BestiaryEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BestiaryEntry> STREAM_CODEC = LibStreamCodecUtils.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), entry -> entry.type,
            ByteBufCodecs.VAR_INT, entry -> entry.killedByCount,
            ByteBufCodecs.FLOAT, entry -> entry.maxHealth,
            ByteBufCodecs.FLOAT, entry -> entry.knockbackResistance,
            ByteBufCodecs.FLOAT, entry -> entry.attackDamage,
            ByteBufCodecs.FLOAT, entry -> entry.armor,
            ByteBufCodecs.VAR_INT, entry -> entry.drops,
            BestiaryEntry::new
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

    public BestiaryEntry() {}

    private BestiaryEntry(
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

    private BestiaryEntry(
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
        } else if (living instanceof Npc || LibUtils.isAnimal(living) || type.is(Tags.EntityTypes.BOSSES)) {
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

    public BestiaryEntry copy() {
        BestiaryEntry entry = new BestiaryEntry(
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

        public BestiaryEntry build() {
            BestiaryEntry entry = new BestiaryEntry(type, 0, maxHealth, knockbackResistance, attackDamage, armor, drops);
            entry.key = key;
            return entry;
        }
    }
}
