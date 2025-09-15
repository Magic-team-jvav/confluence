package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

/// <a href="https://terraria.wiki.gg/zh/wiki/%E6%80%AA%E7%89%A9%E5%9B%BE%E9%89%B4">怪物图鉴</a>
public class BestiaryEntry {
    public static final Codec<BestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(BestiaryEntry::getType),
            Codec.INT.fieldOf("killed_by_count").forGetter(BestiaryEntry::getKilledByCount),
            Codec.FLOAT.fieldOf("max_health").forGetter(BestiaryEntry::getMaxHealth),
            Codec.FLOAT.fieldOf("knockback_resistance").forGetter(BestiaryEntry::getKnockbackResistance),
            Codec.FLOAT.fieldOf("attack_damage").forGetter(BestiaryEntry::getAttackDamage),
            Codec.FLOAT.fieldOf("armor").forGetter(BestiaryEntry::getArmor),
            Codec.INT.fieldOf("drops").forGetter(BestiaryEntry::getDrops)
    ).apply(instance, BestiaryEntry::new));
    public static final StreamCodec<ByteBuf, BestiaryEntry> STREAM_CODEC = LibStreamCodecUtils.composite(
            LibStreamCodecUtils.registry(BuiltInRegistries.ENTITY_TYPE), BestiaryEntry::getType,
            ByteBufCodecs.VAR_INT, BestiaryEntry::getKilledByCount,
            ByteBufCodecs.FLOAT, BestiaryEntry::getMaxHealth,
            ByteBufCodecs.FLOAT, BestiaryEntry::getKnockbackResistance,
            ByteBufCodecs.FLOAT, BestiaryEntry::getAttackDamage,
            ByteBufCodecs.FLOAT, BestiaryEntry::getArmor,
            ByteBufCodecs.VAR_INT, BestiaryEntry::getDrops,
            BestiaryEntry::new
    );

    public EntityType<?> type;
    public int killedByCount;
    public float maxHealth;
    public float knockbackResistance;
    public float attackDamage;
    public float armor;
    public int drops;

    public transient String key;
    private transient Coins coins;

    public BestiaryEntry() {}

    private BestiaryEntry(EntityType<?> type, int killedByCount, float maxHealth, float knockbackResistance, float attackDamage, float armor, int drops) {
        this.type = type;
        this.killedByCount = killedByCount;
        this.maxHealth = maxHealth;
        this.knockbackResistance = knockbackResistance;
        this.attackDamage = attackDamage;
        this.armor = armor;
        this.drops = drops;
    }

    public EntityType<?> getType() {
        return type;
    }

    public int getKilledByCount() {
        return killedByCount;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getArmor() {
        return armor;
    }

    public int getDrops() {
        return drops;
    }

    public Coins getCoins() {
        if (coins == null) {
            this.coins = PlayerUtils.decodeCoin(drops);
        }
        return coins;
    }

    public static BestiaryEntry getPresetEntry(EntityType<?> entityType) {
        return ModDataMaps.getEntityData(ModDataMaps.BESTIARY_ENTRY, entityType);
    }
}
