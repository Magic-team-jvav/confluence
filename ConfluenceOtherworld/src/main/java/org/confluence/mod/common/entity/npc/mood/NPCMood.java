package org.confluence.mod.common.entity.npc.mood;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// NPC 心情系统。100 为基准值；购买价格按 100 / 心情值计算，售回价格按心情值 / 100 计算。
public final class NPCMood {
    private static final int BASE_VALUE = 100;
    private static final float MIN_BUY_MULTIPLIER = 0.75F;
    private static final float MAX_BUY_MULTIPLIER = 1.5F;

    private final EntityType<?> ownerType;
    private int value = BASE_VALUE;
    private List<Component> reasons = List.of();

    public NPCMood(EntityType<?> ownerType) {
        this.ownerType = ownerType;
    }

    /// 获取心情值。
    public int getValue() {
        return Math.max(value, 50);
    }

    public List<Component> getReasons() {
        return reasons;
    }

    /// 获取 NPC 向玩家出售商品时的价格系数。
    public float getBuyPriceMultiplier() {
        return Mth.clamp((float) BASE_VALUE / getValue(), MIN_BUY_MULTIPLIER, MAX_BUY_MULTIPLIER);
    }

    /// 获取 NPC 回收玩家物品时的价格系数。
    public float getSellPriceMultiplier() {
        return 1.0F / getBuyPriceMultiplier();
    }

    /// 根据附近 NPC 重新计算心情值。
    public void evaluate(Iterable<? extends LivingEntity> nearbyEntities) {
        List<BaseNPC> neighbors = new ArrayList<>();
        for (LivingEntity entity : nearbyEntities) {
            if (entity instanceof BaseNPC npc) neighbors.add(npc);
        }
        List<Component> factors = new ArrayList<>();
        value = calculateNeighbors(neighbors, factors);
        reasons = List.copyOf(factors);
    }

    /// 将邻居、群系与聚居密度分别计算，并保留玩家可见的原因。
    public void evaluate(MoodEnvironment environment) {
        List<Component> factors = new ArrayList<>();
        int total = calculateNeighbors(environment.neighbors(), factors);
        Mood positiveBiome = Mood.NEUTRAL;
        Mood negativeBiome = Mood.NEUTRAL;
        for (MoodData.BiomeEntry preference : MoodData.getBiomeMoodsFor(ownerType)) {
            if (!environment.biome().is(preference.biome())) continue;
            int points = points(preference.mood());
            if (points > points(positiveBiome)) positiveBiome = preference.mood();
            if (points < points(negativeBiome)) negativeBiome = preference.mood();
        }
        Mood bestBiome = positiveBiome != Mood.NEUTRAL ? positiveBiome : negativeBiome;
        if (bestBiome != Mood.NEUTRAL) {
            int biomePoints = Integer.signum(points(bestBiome)) * (Math.abs(points(bestBiome)) == 20 ? 12 : 6);
            total += biomePoints;
            String biomeId = environment.biome().unwrapKey().map(key -> key.location().toLanguageKey("biome")).orElse("");
            factors.add(Component.translatable("gui.confluence.mood.biome." + bestBiome.getSerializedName(), Component.translatable(biomeId)));
        }
        int nearby = environment.neighbors().size();
        if (nearby <= 2 && environment.distantCount() <= 3) {
            total += 5;
            factors.add(Component.translatable("gui.confluence.mood.solitude"));
        }
        if (nearby > 3) {
            total -= (nearby - 3) * 5;
            factors.add(Component.translatable("gui.confluence.mood.crowded", nearby - 3));
        }
        if (environment.housing() == MoodEnvironment.HousingStatus.HOMELESS) {
            factors.add(Component.translatable("gui.confluence.mood.homeless"));
        } else if (environment.housing() == MoodEnvironment.HousingStatus.FAR_FROM_HOME) {
            factors.add(Component.translatable("gui.confluence.mood.far_from_home"));
        }
        if (environment.evilBiome())
            factors.add(Component.translatable("gui.confluence.mood.evil_biome"));
        value = environment.housing() != MoodEnvironment.HousingStatus.HOUSED || environment.evilBiome() ? 50 : total;
        reasons = List.copyOf(factors);
    }

    private int calculateNeighbors(Iterable<BaseNPC> neighbors, List<Component> factors) {
        Map<EntityType<?>, Mood> moods = MoodData.getMoodsFor(ownerType);
        int total = BASE_VALUE;
        for (BaseNPC npc : neighbors) {
            Mood mood = moods.getOrDefault(npc.getType(), Mood.NEUTRAL);
            total += points(mood);
            if (mood != Mood.NEUTRAL) {
                factors.add(Component.translatable("gui.confluence.mood.neighbor." + mood.getSerializedName(), npc.getName()));
            }
        }
        return total;
    }

    private static int points(Mood mood) {
        return switch (mood) {
            case LOVER -> 20;
            case LIKE -> 10;
            case NEUTRAL -> 0;
            case DISLIKE -> -10;
            case HATE -> -20;
        };
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Value", value);
        ListTag descriptions = new ListTag();
        for (Component reason : reasons)
            descriptions.add(StringTag.valueOf(Component.Serializer.toJson(reason)));
        tag.put("Reasons", descriptions);
        return tag;
    }

    public void loadTag(CompoundTag tag) {
        value = tag.getInt("Value");
        List<Component> descriptions = new ArrayList<>();
        for (Tag entry : tag.getList("Reasons", Tag.TAG_STRING)) {
            Component description = Component.Serializer.fromJson(entry.getAsString());
            if (description != null) descriptions.add(description);
        }
        reasons = List.copyOf(descriptions);
    }
}
