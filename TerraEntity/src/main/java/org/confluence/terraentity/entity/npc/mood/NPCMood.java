package org.confluence.terraentity.entity.npc.mood;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 心情系统:数值越大，心情越好，100为正常
 */
public class NPCMood {
    public static final String KEY = "npc_mood";
    public static final Codec<NPCMood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("value").forGetter(i -> i.value),
            ResourceLocation.CODEC.listOf().fieldOf("mood_id_list").forGetter(i -> i.moodInfoList)
    ).apply(instance, (a, b) -> {
        NPCMood mood = new NPCMood();
        mood.value = a;
        mood.moodInfoList = b;
        return mood;
    }));
    public static final StreamCodec<ByteBuf, NPCMood> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    /**
     * 对应影响心情的NPC数量
     */
    protected int[] moodCounts = {0, 0, 0, 0, 0};

    /**
     * 当前心情值
     */
    private int value = 100;

    /**
     * 心情列表
     */
    List<ResourceLocation> moodInfoList = new ArrayList<>();

    /**
     * 对每种npc的心情评估
     */
    Map<EntityType<?>, MoodInfo> moodInfoMap = new HashMap<>();

    /**
     * 将心情值转换为数值
     */
    private EnumMap<Mood, Integer> getMoodValue = new EnumMap<>(Mood.class);

    public NPCMood() {
        getMoodValue.put(Mood.HATE, -20);
        getMoodValue.put(Mood.DISLIKE, -10);
        getMoodValue.put(Mood.NEUTRAL, 0);
        getMoodValue.put(Mood.LIKE, 10);
        getMoodValue.put(Mood.LOVER, 20);
    }

    public int getValue() {
        return Math.max(value, 50);
    }

    /**
     * 获取心情实例表
     */
    public List<ResourceLocation> getMoodInfoList() {
        return moodInfoList;
    }

    /**
     * 复制需要同步的信息
     */
    public void copyFrom(NPCMood mood) {
        this.value = mood.value;
        this.moodInfoList = new ArrayList<>(mood.moodInfoList);
    }

    public MoodInfo test(AbstractTerraNPC entity) {
        if (moodInfoMap.containsKey(entity.getType())) {
            return moodInfoMap.get(entity.getType());
        }
        return MoodInfo.EMPTY;
    }

    /**
     * 添加心情测试实例
     */
    public void addMoodInfo(MoodInfo moodInfo) {
        moodInfoMap.put(moodInfo.entityType(), moodInfo);
    }

    /**
     * 获取心情值转换值
     */
    public int getValue(Mood mood) {
        return getMoodValue.get(mood);
    }

    /**
     * 设置心情值转换表
     */
    public void setMoodValueTable(EnumMap<Mood, Integer> moodValue) {
        this.getMoodValue = moodValue;
    }

    /**
     * 重新计算心情值
     */
    public int evaluate(List<AbstractTerraNPC> entities) {
        int[] counts = {0, 0, 0, 0, 0};
        moodInfoList.clear();
        for (AbstractTerraNPC npc : entities) {
            MoodInfo moodInfo = test(npc);
            if (moodInfo != MoodInfo.EMPTY) {
                counts[moodInfo.mood().ordinal()]++;
                ResourceLocation location = Loader.getInstance().getId(moodInfo);
                if (!moodInfoList.contains(location)) {
                    moodInfoList.add(location);
                }
            }
        }
        this.moodCounts = counts;
        value = 100;
        for (int i = 0; i < moodCounts.length; i++) {
            value += getValue(Mood.values()[i]) * moodCounts[i];
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        // 由于mood始终是同一个对象，所以要重写
        if (obj instanceof NPCMood other) {
            if (value != other.value || moodInfoList.size() != other.moodInfoList.size()) {
                return false;
            }
            for (int i = 0; i < moodInfoList.size(); i++) {
                if (!moodInfoList.get(i).equals(other.moodInfoList.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * npc心情加载器
     */
    public static class Loader extends SingleJsonFileReloadListener {
        public static final Codec<Map<EntityType<?>, EntityMood>> CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), EntityMood.CODEC);
        private static Loader INSTANCE;
        private BiMap<ResourceLocation, MoodInfo> byId = ImmutableBiMap.of();
        private Map<EntityType<?>, EntityMood> byType = ImmutableMap.of();

        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            ConditionalOps<JsonElement> ops = makeConditionalOps();
            ImmutableBiMap.Builder<ResourceLocation, MoodInfo> byIdBuilder = ImmutableBiMap.builder();
            ImmutableMap.Builder<EntityType<?>, EntityMood> byTypeBuilder = ImmutableMap.builder();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(entityType -> EntityMood.CODEC.parse(ops, entry.getValue())
                        .resultOrPartial(errorMsg -> TerraEntity.LOGGER.warn("Could not decode npc moods with json id {} - error: {}", entry.getKey(), errorMsg))
                        .ifPresent(entityMood -> {
                            byTypeBuilder.put(entityType, entityMood);
                            for (MoodInfoData moodInfoData : entityMood.moodInfos()) {
                                MoodInfo moodInfo = moodInfoData.moodInfo();
                                byIdBuilder.put(moodInfoData.moodId(), moodInfo);
                            }
                        }));
            }
            this.byId = byIdBuilder.build();
            this.byType = byTypeBuilder.build();
        }

        @Override
        protected ResourceLocation resourcePath() {
            return TerraEntity.space("npc/moods.json");
        }

        @Override
        protected String identifier() {
            return "NPC Moods";
        }

        public @Nullable MoodInfo getMoodInfo(ResourceLocation id) {
            return byId.get(id);
        }

        public ResourceLocation getId(MoodInfo moodInfo) {
            return byId.inverse().get(moodInfo);
        }

        public @Nullable EntityMood getMood(EntityType<?> entityType) {
            return getByType().get(entityType);
        }

        public Map<EntityType<?>, EntityMood> getByType() {
            return byType;
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }

        public static void handle(Map<EntityType<?>, EntityMood> entityTypeEntityMoodMap) {
            getInstance().byType = entityTypeEntityMoodMap;
            ImmutableBiMap.Builder<ResourceLocation, MoodInfo> builder = ImmutableBiMap.builder();
            for (EntityMood entityMood : entityTypeEntityMoodMap.values()) {
                for (MoodInfoData moodInfo : entityMood.moodInfos) {
                    builder.put(moodInfo.moodId, moodInfo.moodInfo);
                }
            }
            getInstance().byId = builder.build();
        }

        /**
         * 心情的信息
         *
         * @param moodId   心情id，用来查询对应的信息
         * @param moodInfo 心情信息
         */
        public record MoodInfoData(ResourceLocation moodId, MoodInfo moodInfo) {
            public static final Codec<MoodInfoData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("moodId").forGetter(MoodInfoData::moodId),
                    MoodInfo.CODEC.fieldOf("moodInfo").forGetter(MoodInfoData::moodInfo)
            ).apply(instance, MoodInfoData::new));
        }

        /**
         * 心情的设置选项，可扩展
         *
         * @param toIntMap
         */
        public record MoodSetting(Map<Mood, Integer> toIntMap) {
            public static final Codec<MoodSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Mood.CODEC, Codec.INT).fieldOf("moodToValue").forGetter(MoodSetting::toIntMap)
            ).apply(instance, MoodSetting::new));

            MoodSetting(int a, int b, int c, int d, int e) {
                this(Map.of(Mood.HATE, a, Mood.DISLIKE, b, Mood.NEUTRAL, c, Mood.LIKE, d, Mood.LOVER, e));
            }

            public static MoodSetting of(int hate, int dislike, int neutral, int like, int lover) {
                return new MoodSetting(hate, dislike, neutral, like, lover);
            }

            public static MoodSetting DEFAULT = new MoodSetting(-20, -10, 0, 10, 20);

            public EnumMap<Mood, Integer> createEnumMap() {
                return new EnumMap<>(toIntMap);
            }
        }

    }

    /**
     * npc的心情设置和具体信息
     */
    public record EntityMood(Optional<Loader.MoodSetting> setting, List<Loader.MoodInfoData> moodInfos) {
        public final static Codec<EntityMood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Loader.MoodSetting.CODEC.optionalFieldOf("setting").forGetter(EntityMood::setting),
                Codec.list(Loader.MoodInfoData.CODEC).fieldOf("moodInfos").forGetter(EntityMood::moodInfos)
        ).apply(instance, EntityMood::new));

        /**
         * 安全获取setting，如果没有设置，则返回默认设置
         */
        public Loader.MoodSetting getSetting() {
            return setting.orElse(Loader.MoodSetting.DEFAULT);
        }

        public static class Builder {
            private Loader.MoodSetting setting;
            private final List<Loader.MoodInfoData> moodInfos;

            public Builder() {
                this.moodInfos = new ArrayList<>();
            }

            public Builder setSetting(Loader.MoodSetting setting) {
                this.setting = setting;
                return this;
            }

            public Builder addMoodInfo(ResourceLocation moodId, MoodInfo moodInfo) {
                moodInfos.add(new Loader.MoodInfoData(moodId, moodInfo));
                return this;
            }

            public EntityMood build() {
                return new EntityMood(Optional.ofNullable(setting), moodInfos);
            }
        }
    }
}
