package com.xiaohunao.enemybanner;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@EventBusSubscriber(modid = com.xiaohunao.enemybanner.EnemyBanner.MODID)
public class BannerConfig {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final int DEFAULT_BASIC_KILLS = 50;

    private static final ModConfigSpec.IntValue BASIC_KILLS = BUILDER
            .comment("每兑换一个旗帜所需要的击杀数")
            .defineInRange("basicKills", DEFAULT_BASIC_KILLS, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> MONSTER_IDS_ADDED = BUILDER
            .defineListAllowEmpty("monsterIds.added", new ArrayList<>(), String::new, Objects::nonNull);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> MONSTER_IDS_EXCLUDED = BUILDER
            .defineListAllowEmpty("monsterIds.excluded", new ArrayList<>(), String::new, Objects::nonNull);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> MONSTER_IDS_ONLY = BUILDER
            .defineListAllowEmpty("monsterIds.only", new ArrayList<>(), String::new, Objects::nonNull);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> SPECIAL_BASIC_KILLS = BUILDER
            .comment("特定怪物兑换一个旗帜所需的击杀数")
            .comment("例如：\"minecraft:zombie = 5\"")
            .defineList("special.basicKills", new ArrayList<>(), String::new, Objects::nonNull);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int basicKills;
    public static List<? extends String> addedList;
    public static List<? extends String> excludedList;
    public static List<? extends String> onlyList;
    public static Map<String, Integer> specialBasicKills;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event){
        basicKills = BASIC_KILLS.get();
        addedList = MONSTER_IDS_ADDED.get();
        excludedList = MONSTER_IDS_EXCLUDED.get();
        onlyList = MONSTER_IDS_ONLY.get();
        specialBasicKills = new HashMap<>();
        for (String special : SPECIAL_BASIC_KILLS.get()) {
            String[] split = special.split("=");
            specialBasicKills.put(split[0].strip(), Integer.parseInt(split[1].strip()));
        }
    }

    public static boolean contains(String key){
        boolean reuslt = false;
        if (key == null || key.isEmpty())
            return reuslt;
        reuslt = EntityType.byString(key).map(type -> type.getCategory().equals(MobCategory.MONSTER)).orElse(false);
        if (addedList.contains(key))
            reuslt = true;
        if (excludedList.contains(key))
            reuslt = false;
        if (!onlyList.isEmpty())
            reuslt = onlyList.contains(key);
        return reuslt;
    }

    public static Banner getBanner(String key){
        if (contains(key)){
            return getBanner(EntityType.byString(key).get());
        }
        return null;
    }

    public static Banner getBanner(EntityType<?> entityType){
        String key = EntityType.getKey(entityType).toString();
        if (contains(key)){
            return new Banner(key, specialBasicKills.containsKey(key) ? specialBasicKills.get(key) : basicKills);
        }
        return null;
    }

    public static int getBasicKills(String key){
        if (specialBasicKills.containsKey(key)) {
            return specialBasicKills.get(key);
        }
        return basicKills;
    }

    public static class Banner implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        public String monsterId;
        public int basicKills;

        public Banner(String monsterId, int basicKills){
            this.monsterId = monsterId;
            this.basicKills = basicKills;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Banner banner = (Banner) o;
            return basicKills == banner.basicKills && Objects.equals(monsterId, banner.monsterId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(monsterId, basicKills);
        }

        @Override
        public String toString() {
            return "Banner{" +
                    "monsterId='" + monsterId + '\'' +
                    ", basicKills=" + basicKills +
                    '}';
        }
    }
}
