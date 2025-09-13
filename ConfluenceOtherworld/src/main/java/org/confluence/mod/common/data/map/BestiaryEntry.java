package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.util.Coins;

/// <a href="https://terraria.wiki.gg/zh/wiki/%E6%80%AA%E7%89%A9%E5%9B%BE%E9%89%B4">怪物图鉴</a>
public class BestiaryEntry {
    public static final Codec<BestiaryEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(entry -> entry.type),
            Codec.INT.fieldOf("killed_by_count").forGetter(entry -> entry.killedByCount),
            Codec.DOUBLE.fieldOf("max_health").forGetter(entry -> entry.maxHealth),
            Codec.DOUBLE.fieldOf("knockback_resistance").forGetter(entry -> entry.knockbackResistance),
            Codec.DOUBLE.fieldOf("attack_damage").forGetter(entry -> entry.attackDamage),
            Codec.DOUBLE.fieldOf("armor").forGetter(entry -> entry.armor),
            Coins.CODEC.fieldOf("coins").forGetter(entry -> entry.coins)
    ).apply(instance, (t1, t2, t3, t4, t5, t6, t7) -> {
        BestiaryEntry entry = new BestiaryEntry();
        entry.type = t1;
        entry.killedByCount = t2;
        entry.maxHealth = t3;
        entry.knockbackResistance = t4;
        entry.attackDamage = t5;
        entry.armor = t6;
        entry.coins = t7;
        return entry;
    }));

    public EntityType<?> type;
    public int killedByCount;
    public double maxHealth;
    public double knockbackResistance;
    public double attackDamage;
    public double armor;
    public Coins coins;
}
