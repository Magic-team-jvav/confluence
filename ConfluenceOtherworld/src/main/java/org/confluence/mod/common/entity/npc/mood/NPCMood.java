package org.confluence.mod.common.entity.npc.mood;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.entity.npc.BaseNPC;


/// NPC 心情系统。100 为基准值；购买价格按 100 / 心情值计算，售回价格按心情值 / 100 计算。
public final class NPCMood {
    private static final int BASE_VALUE = 100;
    private static final float MIN_BUY_MULTIPLIER = 0.75F;
    private static final float MAX_BUY_MULTIPLIER = 1.5F;

    /// 心情原因（{@link Component}）的序列化编解码器。
    ///
    /// 1.20 在这里用 `Component.Serializer.toJson(Component)` / `fromJson(String)`（1.20 `NPCMood.java:131/:140`），
    /// 1.21 那两个方法都要 `HolderLookup.Provider`（`Component.java` 的 `Serializer#toJson(Component, HolderLookup.Provider)`），
    /// 而 NPCMood 是纯自序列化、静态上下文里拿不到 level/provider（调用方 `BaseNPC#refreshMood` 与
    /// `BaseNPC#onSyncedDataUpdated` 都不传注册表）。这里改用同一套编解码器的「JSON 字符串」形态
    /// `ComponentSerialization.FLAT_CODEC`：输入输出都是 String、不需要注册表上下文，语义与 1.20 的
    /// `toJson`/`fromJson(String)` 完全一致（1.20 也是「Component ↔ JSON 字符串」往返）。
    private static final Codec<Component> REASON_CODEC = ComponentSerialization.FLAT_CODEC;

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
            REASON_CODEC.encodeStart(NbtOps.INSTANCE, reason).result().ifPresent(descriptions::add);
        tag.put("Reasons", descriptions);
        return tag;
    }

    public void loadTag(CompoundTag tag) {
        value = tag.getInt("Value");
        List<Component> descriptions = new ArrayList<>();
        for (Tag entry : tag.getList("Reasons", Tag.TAG_STRING)) {
            Component description = REASON_CODEC.parse(NbtOps.INSTANCE, entry).result().orElse(null);
            if (description != null) descriptions.add(description);
        }
        reasons = List.copyOf(descriptions);
    }
}
