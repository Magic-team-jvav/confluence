package org.confluence.terraentity.data.gen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.terraentity.entity.npc.house.HouseDetectInfo;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.entity.*;
import org.confluence.terraentity.init.item.*;
import org.confluence.terraentity.integration.curios.CuriosHelper;

import static org.confluence.terraentity.TerraEntity.MODID;


public class TEChineseProvider extends LanguageProvider {
    public TEChineseProvider(PackOutput output) {
        super(output, MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.terraentity.title", "泰拉生物");

        add("title.terra_entity.npc_trade", "泰拉商店");
        add("title.terra_entity.npc_trade.task.daily", "每日任务");
        add("title.terra_entity.npc_trade.task.fixed_level", "固定等级任务");
        add("title.terra_entity.npc_trade.task.random", "随机任务");
        add("title.terra_entity.npc_trade.task.dynamic_reward", "动态奖励任务");
        add("title.terra_entity.npc_trade.task.progress", "进度任务");

        add("container.terra_entity.chester", "切斯特");

        add("key.terra_entity.ride", "使用坐骑(需要CuriosAPI)");


        add(TEMonsterEntities.ICE_SLIME.get(), "冰雪史莱姆");
        add(TEMonsterEntities.BLUE_SLIME.get(), "蓝色史莱姆");
        add(TEMonsterEntities.RED_SLIME.get(), "红色史莱姆");
        add(TEMonsterEntities.PURPLE_SLIME.get(), "紫色史莱姆");
        add(TEMonsterEntities.JUNGLE_SLIME.get(), "丛林史莱姆");
        add(TEMonsterEntities.PINK_SLIME.get(), "粉色史莱姆");
        add(TEMonsterEntities.YELLOW_SLIME.get(), "黄色史莱姆");
        add(TEMonsterEntities.HONEY_SLIME.get(), "蜂蜜史莱姆");
        add(TEMonsterEntities.CRIMSLIME.get(), "猩红史莱姆");
        add(TEMonsterEntities.CORRUPT_SLIME.get(), "腐化史莱姆");
        add(TEMonsterEntities.DESERT_SLIME.get(), "沙漠史莱姆");
        add(TEMonsterEntities.TROPIC_SLIME.get(), "热带史莱姆");
        add(TEMonsterEntities.LUMINOUS_SLIME.get(), "夜明史莱姆");
        add(TEMonsterEntities.GREEN_SLIME.get(), "绿色史莱姆");
        add(TEMonsterEntities.BLACK_SLIME.get(), "黑色史莱姆");
        add("entity.terra_entity.mother_slime", "史莱姆之母");
        add("entity.terra_entity.baby_slime", "史莱姆宝宝");
        add(TEMonsterEntities.LAVA_SLIME.get(), "熔岩史莱姆");
        add(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), "青团史莱姆");
        add(TEMonsterEntities.SWAMP_SLIME.get(), "沼泽史莱姆");
        add(TEMonsterEntities.DUNGEON_SLIME.get(), "地牢史莱姆");
        add(TEMonsterEntities.GOLDEN_SLIME.get(), "金史莱姆");
        add(TEMonsterEntities.SPIKED_SLIME.get(), "尖刺史莱姆");
        add(TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(), "尖刺丛林史莱姆");
        add(TEMonsterEntities.SPIKED_ICE_SLIME.get(), "尖刺冰雪史莱姆");
        add(TEMonsterEntities.DEMON_EYE.get(), "恶魔眼");
        add(TEMonsterEntities.FLYING_FISH.get(), "飞鱼");
        add(TEMonsterEntities.GIANT_SHELLY.get(), "巨型卷壳怪");
        add(TEMonsterEntities.CRAWDAD.get(), "龙虾");
        add(TEMonsterEntities.NYMPH.get(), "宁芙");
        add(TEMonsterEntities.DRIPPLER.get(), "滴滴怪");
        add(TEMonsterEntities.BLOOD_ZOMBIE.get(), "血腥僵尸");
        add(TEMonsterEntities.SNOW_FLINX.get(), "小雪怪");

        add(TEMonsterEntities.PIRANHA.get(), "食人鱼");
        add(TEMonsterEntities.SHARK.get(), "鲨鱼");
        add(TEMonsterEntities.ARAPAIMA.get(), "巨骨舌鱼");
        add(TEMonsterEntities.BLUE_JELLYFISH.get(), "蓝水母");
        add(TEMonsterEntities.PINK_JELLYFISH.get(), "粉水母");
        add(TEMonsterEntities.GREEN_JELLYFISH.get(), "绿水母");

        add(TEMonsterEntities.WANDERING_EYE_FISH.get(), "游荡眼球怪鱼");
        add(TEMonsterEntities.BLOOD_CRAWLER.get(), "血爬虫");
        add(TEMonsterEntities.BLOODY_SPORE.get(), "血腥芽孢");
        add(TEMonsterEntities.FACE_MONSTER.get(), "脸怪");
        add(TEMonsterEntities.CRIMERA.get(), "猩红喀迈拉");
        add(TEMonsterEntities.EATER_OF_SOULS.get(), "噬魂怪");
        add(TEMonsterEntities.DECAYEDER.get(), "腐骴");
        add(TEMonsterEntities.DEVOURER.get(), "吞噬怪");
        add(TEMonsterEntities.GIANT_WORM.get(), "巨型蠕虫");
        add(TEMonsterEntities.TOMB_CRAWLER.get(), "墓穴爬虫");
        add(TEMonsterEntities.CAVE_BAT.get(), "洞穴蝙蝠");
        add(TEMonsterEntities.JUNGLE_BAT.get(), "丛林蝙蝠");
        add(TEMonsterEntities.SNATCHER.get(), "抓人草");
        add(TEMonsterEntities.MAN_EATER.get(), "食人怪");
        add(TEMonsterEntities.HORNET.get(), "黄蜂");
        add(TEMonsterEntities.HELL_BAT.get(), "地狱蝙蝠");
        add(TEMonsterEntities.ICE_BAT.get(), "冰雪蝙蝠");
        add(TEMonsterEntities.SPORE_BAT.get(), "孢子蝙蝠");
        add(TEMonsterEntities.SPORE_SKELETON.get(), "孢子骷髅");
        add(TEMonsterEntities.SPORE_ZOMBIE.get(), "孢子僵尸");
        add(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), "帽子蘑菇僵尸");
        add(TEMonsterEntities.METEOR_HEAD.get(), "流星头");
        add(TEMonsterEntities.HARPY.get(), "鸟妖");
        add(TEMonsterEntities.DEMON.get(), "恶魔");
        add(TEMonsterEntities.VOODOO_DEMON.get(), "巫毒恶魔");
        add(TEMonsterEntities.BONE_SERPENT.get(), "骨蛇");
        add(TEMonsterEntities.WITHER_BONE_SERPENT.get(), "凋零骨蛇");
        add(TEMonsterEntities.FIRE_IMP.get(), "火焰小鬼");
        add(TEMonsterEntities.ANGER_BONES.get(), "愤怒骷髅");
        add(TEMonsterEntities.SHORT_BONES.get(), "矮骷髅");
        add(TEMonsterEntities.BIG_BONES.get(), "大骷髅");
        add(TEMonsterEntities.BIG_ANGER_BONES.get(), "大愤怒骷髅");
        add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), "大块头愤怒骷髅");
        add(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), "大头盔愤怒骷髅");
        add(TEMonsterEntities.CURSED_SKULL.get(), "诅咒骷髅头");
        add(TEMonsterEntities.DARK_CASTER.get(), "暗黑法师");
        add(TEMonsterEntities.UNDEAD_VIKING.get(), "亡灵维京海盗");
        add(TEMonsterEntities.ANTLION_SWARMER.get(), "蚁狮蜂");
        add(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), "巨型蚁狮蜂");
        add(TEMonsterEntities.GHOST.get(), "鬼魂");
        add(TEMonsterEntities.GRANITE_ELEMENTAL.get(), "花岗精");
        add(TEMonsterEntities.PIXIE.get(), "妖精");
        add(TEMonsterEntities.WYVERN.get(), "飞龙");
        add(TEMonsterEntities.POSSESS_ARMOR.get(), "装甲幻影魔");
        add(TEMonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(), "装甲幻影魔-虚空载体");
        add(TEMonsterEntities.WRAITH.get(), "幻灵");
        add(TEMonsterEntities.WOODEN_MIMIC.get(), "木宝箱怪");
        add(TEMonsterEntities.GOLDEN_MIMIC.get(), "金宝箱怪");
        add(TEMonsterEntities.SHADOW_MIMIC.get(), "暗影宝箱怪");
        add(TEMonsterEntities.ICE_MIMIC.get(), "冰雪宝箱怪");
        add(TEMonsterEntities.CRIMSON_MIMIC.get(), "猩红宝箱怪");
        add(TEMonsterEntities.CORRUPT_MIMIC.get(), "腐化宝箱怪");
        add(TEMonsterEntities.HALLOWED_MIMIC.get(), "神圣宝箱怪");
        add(TEMonsterEntities.JUNGLE_MIMIC.get(), "丛林宝箱怪");
        add(TEMonsterEntities.MUMMY.get(), "木乃伊");
        add(TEMonsterEntities.DARK_MUMMY.get(), "暗黑木乃伊");
        add(TEMonsterEntities.BLOOD_MUMMY.get(), "血木乃伊");
        add(TEMonsterEntities.LIGHT_MUMMY.get(), "光明木乃伊");
        add(TEMonsterEntities.DARK_LAMIA.get(), "黑暗拉弥亚");
        add(TEMonsterEntities.LIGHT_LAMIA.get(), "光明拉弥亚");
        add(TEMonsterEntities.GHOUL.get(), "食尸鬼");
        add(TEMonsterEntities.TAINTED_GHOUL.get(), "红染食尸鬼");
        add(TEMonsterEntities.VILE_GHOUL.get(), "腐恶食尸鬼");
        add(TEMonsterEntities.DREAMER_GHOUL .get(), "神梦食尸鬼");
        add(TEMonsterEntities.SAND_POACHER .get(), "沙贼");
        add(TEMonsterEntities.DERPLING.get(), "跳跳兽");
        add(TEMonsterEntities.HERPLING.get(), "蹦蹦兽");

        // boss
        add(TEBossEntities.KING_SLIME.get(), "史莱姆王");
        add(TEBossEntities.EYE_OF_CTHULHU.get(), "克苏鲁之眼");
        add(TEMonsterEntities.SERVANT_OF_CTHULHU.get(), "克苏鲁之仆");
        add(TEBossEntities.EATER_OF_WORLDS.get(), "世界吞噬怪");
        add(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), "世界吞噬怪体节");
        add(TEBossEntities.BRAIN_OF_CTHULHU.get(), "克苏鲁之脑");
        add(TEBossEntities.BRAIN_FAKE.get(), "克苏鲁之脑幻象");
        add(TEMonsterEntities.VISUAL_NEURON.get(), "视神经元");
        add(TEBossEntities.QUEEN_BEE.get(), "蜂王");
        add(TEMonsterEntities.LITTLE_HORNET.get(), "小黄蜂");
        add(TEBossEntities.SKELETRON.get(), "骷髅王");
        add(TEBossEntities.DUNGEON_GUARDIAN.get(), "地牢守卫");
        add(TEBossEntities.DEERCLOPS.get(), "独眼巨鹿");
        add(TEBossEntities.HILL_OF_FLESH.get(), "血肉山");
        add(TEBossEntities.WALL_OF_FLESH.get(), "血肉墙");
        add(TEMonsterEntities.LEECH.get(), "血蛭");
        add(TEMonsterEntities.THE_HUNGRY.get(), "饿鬼");
        add(TEMonsterEntities.HILL_HUNGRY.get(), "饿鬼");
        add(TEMonsterEntities.FLESH_SLIME.get(), "血肉史莱姆");
        add(TEBossEntities.RETINAZER.get(), "激光眼");
        add(TEBossEntities.SPAZMATISM.get(), "魔焰眼");
        add(TEBossEntities.THE_TWINS.get(), "双子魔眼");
        add(TEBossEntities.SKELETRON_PRIME.get(), "机械骷髅王");
        add(TEBossEntities.SKELETRON_PRIME_PART.get(), "机械骷髅王（机械臂）");
        add(TEBossEntities.THE_DESTROYER.get(), "毁灭者");
        add(TEBossEntities.THE_DESTROYER_PART.get(), "毁灭者体节");
        add(TEBossEntities.THE_DESTROYER_PROBE.get(), "毁灭者探测器");
        add(TEBossEntities.PLANTERA.get(), "世纪之花");
        add(TEBossEntities.PLANTERA_HOOK.get(), "世纪之花钩");
        add(TEBossEntities.PLANTERA_TENTACLE.get(), "世纪之花触手");


        // 召唤物
        add(TESummonEntities.CHESTER.get(), "切斯特");

        add(TESummonEntities.SUMMON_FINCH.get(), "雀宝宝");
        add(TESummonEntities.SUMMON_SLIME.get(), "史莱姆宝宝");
        add(TESummonEntities.SUMMON_IRON_GOLEM.get(), "i-32型铁傀儡");
        add(TESummonEntities.SUMMON_HORNET.get(), "仆役黄蜂");
        add(TESummonEntities.SCULK_WISP.get(), "幽匿游灵");
        add(TESummonEntities.IMP.get(), "召唤的小鬼");
        add(TESummonEntities.SUMMON_SNOW_FLINX.get(), "小雪怪");


        add(TESummonEntities.SUMMON_WOODEN_SWORD.get(), "青冥");
        add(TESummonEntities.SUMMON_STONE_SWORD.get(), "破山");
        add(TESummonEntities.SUMMON_IRON_SWORD.get(), "白虹");
        add(TESummonEntities.SUMMON_GOLDEN_SWORD.get(), "流星");
        add(TESummonEntities.SUMMON_DIAMOND_SWORD.get(), "龙渊");
        add(TESummonEntities.SUMMON_NETHERITE_SWORD.get(), "赤霄");
        add(TESummonEntities.TERRAPRISMA.get(), "泰拉棱镜");

        // 坐骑
        add(TERideableEntities.RIDEABLE_SLIME.get(), "史莱姆坐骑");
        add(TERideableEntities.RIDEABLE_BEE.get(), "蜜蜂坐骑");


        // 刷怪蛋
        add(TESpawnEggItems.BLUE_SLIME_SPAWN_EGG.get(), "蓝色史莱姆刷怪蛋");
        add(TESpawnEggItems.RED_SLIME_SPAWN_EGG.get(), "红色史莱姆刷怪蛋");
        add(TESpawnEggItems.YELLOW_SLIME_SPAWN_EGG.get(), "黄色史莱姆刷怪蛋");
        add(TESpawnEggItems.HONEY_SLIME_SPAWN_EGG.get(), "蜂蜜史莱姆刷怪蛋");
        add(TESpawnEggItems.PURPLE_SLIME_SPAWN_EGG.get(), "紫色史莱姆刷怪蛋");
        add(TESpawnEggItems.DESERT_SLIME_SPAWN_EGG.get(), "沙漠史莱姆刷怪蛋");
        add(TESpawnEggItems.GREEN_DUMPLING_SLIME_SPAWN_EGG.get(), "青团史莱姆刷怪蛋");
        add(TESpawnEggItems.SWAMP_SLIME_SPAWN_EGG.get(), "沼泽史莱姆刷怪蛋");
        add(TESpawnEggItems.JUNGLE_SLIME_SPAWN_EGG.get(), "丛林史莱姆刷怪蛋");
        add(TESpawnEggItems.PINK_SLIME_SPAWN_EGG.get(), "粉色史莱姆刷怪蛋");
        add(TESpawnEggItems.ICE_SLIME_SPAWN_EGG.get(), "冰冻史莱姆刷怪蛋");
        add(TESpawnEggItems.GREEN_SLIME_SPAWN_EGG.get(), "绿色史莱姆刷怪蛋");
        add(TESpawnEggItems.BLACK_SLIME_SPAWN_EGG.get(), "黑色史莱姆刷怪蛋");
        add(TESpawnEggItems.DUNGEON_SLIME_SPAWN_EGG.get(), "地牢史莱姆刷怪蛋");
        add(TESpawnEggItems.CRIMSLIME_SPAWN_EGG.get(), "猩红史莱姆刷怪蛋");
        add(TESpawnEggItems.CORRUPT_SLIME_SPAWN_EGG.get(), "腐化史莱姆刷怪蛋");
        add(TESpawnEggItems.TROPIC_SLIME_SPAWN_EGG.get(), "热带史莱姆刷怪蛋");
        add(TESpawnEggItems.LUMINOUS_SLIME_SPAWN_EGG.get(), "夜明史莱姆刷怪蛋");
        add(TESpawnEggItems.LAVA_SLIME_SPAWN_EGG.get(), "熔岩史莱姆刷怪蛋");
        add(TESpawnEggItems.GOLDEN_SLIME_SPAWN_EGG.get(), "金史莱姆刷怪蛋");
        add(TESpawnEggItems.SPIKED_SLIME_SPAWN_EGG.get(), "尖刺史莱姆刷怪蛋");
        add(TESpawnEggItems.SPIKED_JUNGLE_SLIME_SPAWN_EGG.get(), "尖刺丛林史莱姆刷怪蛋");
        add(TESpawnEggItems.SPIKED_ICE_SLIME_SPAWN_EGG.get(), "尖刺冰雪史莱姆刷怪蛋");

        add(TESpawnEggItems.DEMON_EYE_SPAWN_EGG.get(), "恶魔眼刷怪蛋");
        add(TESpawnEggItems.FLYING_FISH_SPAWN_EGG.get(), "飞鱼刷怪蛋");
        add(TESpawnEggItems.HARPY_SPAWN_EGG.get(), "鸟妖刷怪蛋");
        add(TESpawnEggItems.DEMON_SPAWN_EGG.get(), "恶魔刷怪蛋");
        add(TESpawnEggItems.VOODOO_DEMON_SPAWN_EGG.get(), "巫毒恶魔刷怪蛋");
        add(TESpawnEggItems.GIANT_SHELLY_SPAWN_EGG.get(), "巨型卷壳怪刷怪蛋");
        add(TESpawnEggItems.CRAWDAD_SPAWN_EGG.get(), "龙虾刷怪蛋");
        add(TESpawnEggItems.GIANT_WORM_SPAWN_EGG.get(), "巨型蠕虫刷怪蛋");
        add(TESpawnEggItems.NYMPH_SPAWN_EGG.get(), "宁芙刷怪蛋");
        add(TESpawnEggItems.TOMB_CRAWLER_SPAWN_EGG.get(), "墓穴爬虫刷怪蛋");
        add(TESpawnEggItems.BONE_SERPENT_SPAWN_EGG.get(), "骨蛇刷怪蛋");
        add(TESpawnEggItems.WITHER_BONE_SERPENT_SPAWN_EGG.get(), "凋零骨蛇刷怪蛋");
        add(TESpawnEggItems.ANTLION_SWARMER_SPAWN_EGG.get(), "蚁狮蜂刷怪蛋");
        add(TESpawnEggItems.GIANT_ANTLION_SWARMER_SPAWN_EGG.get(), "巨型蚁狮蜂刷怪蛋");
        add(TESpawnEggItems.GRANTITE_ELEMENTAL_SPAWN_EGG.get(), "花岗精刷怪蛋");

        add(TESpawnEggItems.CAVE_BAT_SPAWN_EGG.get(), "洞穴蝙蝠刷怪蛋");
        add(TESpawnEggItems.ICE_BAT_SPAWN_EGG.get(), "冰雪蝙蝠刷怪蛋");
        add(TESpawnEggItems.JUNGLE_BAT_SPAWN_EGG.get(), "丛林蝙蝠刷怪蛋");
        add(TESpawnEggItems.SNATCHER_SPAWN_EGG.get(), "抓人草刷怪蛋");
        add(TESpawnEggItems.MAN_EATER_SPAWN_EGG.get(), "食人怪刷怪蛋");
        add(TESpawnEggItems.HORNET_SPAWN_EGG.get(), "黄蜂刷怪蛋");
        add(TESpawnEggItems.HELL_BAT_SPAWN_EGG.get(), "地狱蝙蝠刷怪蛋");
        add(TESpawnEggItems.SPORE_BAT_SPAWN_EGG.get(), "孢子蝙蝠刷怪蛋");
        add(TESpawnEggItems.SPORE_SKELETON_SPAWN_EGG.get(), "孢子骷髅刷怪蛋");
        add(TESpawnEggItems.SPORE_ZOMBIE_SPAWN_EGG.get(), "孢子僵尸刷怪蛋");
        add(TESpawnEggItems.HAT_SPORE_ZOMBIE_SPAWN_EGG.get(), "帽子蘑菇僵尸刷怪蛋");
        add(TESpawnEggItems.DRIPPLER_SPAWN_EGG.get(), "滴滴怪刷怪蛋");
        add(TESpawnEggItems.BLOOD_ZOMBIE_SPAWN_EGG.get(), "血腥僵尸刷怪蛋");
        add(TESpawnEggItems.WANDERING_EYE_FISH_SPAWN_EGG.get(), "游荡眼球怪鱼刷怪蛋");
        add(TESpawnEggItems.GHOST_SPAWN_EGG.get(), "鬼魂刷怪蛋");

        add(TESpawnEggItems.BLOOD_CRAWLER_SPAWN_EGG.get(), "血爬虫刷怪蛋");
        add(TESpawnEggItems.BLOODY_SPORE_SPAWN_EGG.get(), "血腥芽孢刷怪蛋");
        add(TESpawnEggItems.CRIMERA_SPAWN_EGG.get(), "猩红喀迈拉刷怪蛋");
        add(TESpawnEggItems.FACE_MONSTER_SPAWN_EGG.get(), "脸怪刷怪蛋");
        add(TESpawnEggItems.FIRE_IMG_SPAWN_EGG.get(), "火焰小鬼刷怪蛋");
        add(TESpawnEggItems.SNOW_FLINX_SPAWN_EGG.get(), "小雪怪刷怪蛋");
        add(TESpawnEggItems.METEOR_HEAD_SPAWN_EGG.get(), "流星头刷怪蛋");

        add(TESpawnEggItems.PIRANHA_SPAWN_EGG.get(), "食人鱼刷怪蛋");
        add(TESpawnEggItems.SHARK_SPAWN_EGG.get(), "鲨鱼刷怪蛋");
        add(TESpawnEggItems.ARAPAIMA_SPAWN_EGG.get(), "巨骨舌鱼刷怪蛋");
        add(TESpawnEggItems.BLUE_JELLYFISH_SPAWN_EGG.get(), "蓝水母刷怪蛋");
        add(TESpawnEggItems.PINK_JELLYFISH_SPAWN_EGG.get(), "粉水母刷怪蛋");
        add(TESpawnEggItems.GREEN_JELLYFISH_SPAWN_EGG.get(), "绿水母刷怪蛋");


        add(TESpawnEggItems.EATER_OF_SOULS_SPAWN_EGG.get(), "噬魂怪刷怪蛋");
        add(TESpawnEggItems.DECAYEDER_SPAWN_EGG.get(), "腐骴刷怪蛋");
        add(TESpawnEggItems.DEVOURER_SPAWN_EGG.get(), "吞噬怪刷怪蛋");

        // 地牢骷髅
        add(TESpawnEggItems.ANGER_BONES_SPAWN_EGG.get(), "愤怒骷髅刷怪蛋");
        add(TESpawnEggItems.SHORT_BONES_SPAWN_EGG.get(), "矮骷髅刷怪蛋");
        add(TESpawnEggItems.BIG_BONES_SPAWN_EGG.get(), "大骷髅刷怪蛋");
        add(TESpawnEggItems.BIG_ANGER_BONES_SPAWN_EGG.get(), "大愤怒骷髅刷怪蛋");
        add(TESpawnEggItems.BIG_MUSCLE_ANGER_BONES_SPAWN_EGG.get(), "大块头愤怒骷髅刷怪蛋");
        add(TESpawnEggItems.BIG_HELMET_ANGER_BONES_SPAWN_EGG.get(), "大头盔愤怒骷髅刷怪蛋");
        add(TESpawnEggItems.CURSED_SKULL_SPAWN_EGG.get(), "诅咒骷髅头刷怪蛋");
        add(TESpawnEggItems.DARK_CASTER_SPAWN_EGG.get(), "暗黑法师刷怪蛋");
        add(TESpawnEggItems.UNDEAD_VIKING_SPAWN_EGG.get(), "亡灵维京海盗刷怪蛋");

        // 宝箱怪
        add(TESpawnEggItems.WOODEN_MIMIC_SPAWN_EGG.get(), "木宝箱怪刷怪蛋");
        add(TESpawnEggItems.GOLDEN_MIMIC_SPAWN_EGG.get(), "金宝箱怪刷怪蛋");
        add(TESpawnEggItems.SHADOW_MIMIC_SPAWN_EGG.get(), "暗影宝箱怪刷怪蛋");
        add(TESpawnEggItems.ICE_MIMIC_SPAWN_EGG.get(), "冰雪宝箱怪刷怪蛋");
        add(TESpawnEggItems.CRIMSON_MIMIC_SPAWN_EGG.get(), "猩红宝箱怪刷怪蛋");
        add(TESpawnEggItems.CORRUPT_MIMIC_SPAWN_EGG.get(), "腐化宝箱怪刷怪蛋");
        add(TESpawnEggItems.HALLOWED_MIMIC_SPAWN_EGG.get(), "神圣宝箱怪刷怪蛋");
        add(TESpawnEggItems.JUNGLE_MIMIC_SPAWN_EGG.get(), "丛林宝箱怪刷怪蛋");

        add(TESpawnEggItems.MUMMY_SPAWN_EGG.get(), "木乃伊刷怪蛋");
        add(TESpawnEggItems.DARK_MUMMY_SPAWN_EGG.get(), "暗黑木乃伊刷怪蛋");
        add(TESpawnEggItems.BLOOD_MUMMY_SPAWN_EGG.get(), "血木乃伊刷怪蛋");
        add(TESpawnEggItems.LIGHT_MUMMY_SPAWN_EGG.get(), "光明木乃伊刷怪蛋");

        add(TESpawnEggItems.DARK_LAMIA_SPAWN_EGG.get(), "黑暗拉弥亚刷怪蛋");
        add(TESpawnEggItems.LIGHT_LAMIA_SPAWN_EGG.get(), "光明拉弥亚刷怪蛋");
        add(TESpawnEggItems.GHOUL_SPAWN_EGG.get(), "食尸鬼刷怪蛋");
        add(TESpawnEggItems.TAINTED_GHOUL_SPAWN_EGG.get(), "红染食尸鬼刷怪蛋");
        add(TESpawnEggItems.VILE_GHOUL_SPAWN_EGG.get(), "腐恶食尸鬼刷怪蛋");
        add(TESpawnEggItems.DREAMER_GHOUL_SPAWN_EGG .get(), "神梦食尸鬼刷怪蛋");
        add(TESpawnEggItems.SAND_POACHER_SPAWN_EGG .get(), "沙贼刷怪蛋");
        add(TESpawnEggItems.DERPLING_SPAWN_EGG.get(), "跳跳兽刷怪蛋");
        add(TESpawnEggItems.HERPLING_SPAWN_EGG.get(), "蹦蹦兽刷怪蛋");


        add(TEMonsterEntities.GOBLIN_SORCERER.get(), "哥布林巫士");
        add(TEMonsterEntities.GOBLIN_ARCHER.get(), "哥布林弓箭手");
        add(TEMonsterEntities.GOBLIN_PEON.get(), "哥布林苦力");
        add(TEMonsterEntities.GOBLIN_WARRIOR.get(), "哥布林战士");
        add(TEMonsterEntities.GOBLIN_THIEF.get(), "哥布林盗贼");
        add(TEMonsterEntities.GOBLIN_SCOUT.get(), "哥布林侦察兵");
        add(TEMonsterEntities.ANGER_GOBLIN.get(), "愤怒哥布林");

        // 动物
        add(TEAnimals.SQUIRREL.get(), "松鼠");
        add(TEAnimals.BUNNY.get(), "兔兔");
        add(TEAnimals.JEWEL_SQUIRREL.get(), "宝石松鼠");
        add(TEAnimals.JEWEL_BUNNY.get(), "宝石兔");
        add(TEAnimals.EXPLOSIVE_BUNNY.get(), "爆炸兔");
        add(TEAnimals.DUCK.get(), "鸭子");
        add(TEAnimals.BIRD.get(), "鸟");
        add(TEAnimals.BLUE_JAY.get(), "冠蓝鸦");
        add(TEAnimals.CARDINAL.get(), "红雀");


        add(TEAnimals.CRAB.get(), "螃蟹");
        add(TEAnimals.GLOWING_SNAIL.get(), "发光蜗牛");
        add(TEAnimals.GRUBBY.get(), "蛆虫");
        add(TEAnimals.MAGGOT.get(), "蝇蛆");
        add(TEAnimals.MAGMA_SNAIL.get(), "岩浆蜗牛");
        add(TEAnimals.SLUGGY.get(), "鼻涕虫");
        add(TEAnimals.SNAIL.get(), "蜗牛");

        add(TEAnimals.BUTTERFLY.get(), "蝴蝶");
        add(TEAnimals.HELL_BUTTERFLY.get(), "地狱蝴蝶");
        add(TEAnimals.DRAGONFLY.get(), "蜻蜓");
        add(TEAnimals.FAIRY.get(), "仙灵");
        add(TEAnimals.FEALING.get(), "飞灵");
        add(TEAnimals.GRASSHOPPER.get(), "蚱蜢");
        add(TEAnimals.LADYBUG.get(), "瓢虫");
        add(TEAnimals.SCORPION.get(), "蝎子");
        add(TEAnimals.WORM.get(), "蠕虫");
        add(TEAnimals.PRISMATIC_LACEWING.get(), "七彩草蛉");


        // boss
        add(TESpawnEggItems.KING_SLIME_SPAWN_EGG.get(), "史莱姆王刷怪蛋");
        add(TESpawnEggItems.EYE_OF_CTHULHU_SPAWN_EGG.get(), "克苏鲁之眼刷怪蛋");
        add(TESpawnEggItems.EATER_OF_WORLD_SPAWN_EGG.get(), "世界吞噬怪刷怪蛋");
        add(TESpawnEggItems.BRAIN_OF_CTHULHU_SPAWN_EGG.get(), "克苏鲁之脑刷怪蛋");
        add(TESpawnEggItems.QUEEN_BEE_SPAWN_EGG.get(), "蜂王刷怪蛋");
        add(TESpawnEggItems.DEERCLOPS_SPAWN_EGG.get(), "独眼巨鹿刷怪蛋");
        add(TESpawnEggItems.SKELETRON_SPAWN_EGG.get(), "骷髅王刷怪蛋");
        add(TESpawnEggItems.DUNGEON_GUARDIAN_SPAWN_EGG.get(), "地牢守卫刷怪蛋");
        add(TESpawnEggItems.WALL_OF_FLESH_SPAWN_EGG.get(), "血肉墙刷怪蛋");
        add(TESpawnEggItems.HILL_OF_FLESH_SPAWN_EGG.get(), "血肉山刷怪蛋");
        add(TESpawnEggItems.RETINAZER_SPAWN_EGG.get(), "激光眼刷怪蛋");
        add(TESpawnEggItems.SPAZMATISM_SPAWN_EGG.get(), "魔焰眼刷怪蛋");
        add(TESpawnEggItems.THE_TWINS_SPAWN_EGG.get(), "双子魔眼刷怪蛋");
        add(TESpawnEggItems.SKELETRON_PRIME_SPAWN_EGG.get(), "机械骷髅王刷怪蛋");
        add(TESpawnEggItems.THE_DESTROYER_SPAWN_EGG.get(), "毁灭者刷怪蛋");
        add(TESpawnEggItems.PLANTERA_SPAWN_EGG.get(), "世纪之花刷怪蛋");


        add(TEBossSummonsItems.KING_SLIME_SUMMONS.get(), "史莱姆皇冠");
        add(TEBossSummonsItems.EYE_OF_CTHULHU_SUMMONS.get(), "可疑眼球");
        add(TEBossSummonsItems.EATER_OF_WORLDS_SUMMONS.get(), "蠕虫诱饵");
        add(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS.get(), "血腥脊椎");
        add(TEBossSummonsItems.QUEEN_BEE_SUMMONS.get(), "憎恶之蜂");
        add(TEBossSummonsItems.SKELETRON_SUMMONS.get(), "服装商巫毒娃娃");
        add(TEBossSummonsItems.WALL_OF_FLESH_SUMMONS.get(), "向导巫毒娃娃(墙)");
        add(TEBossSummonsItems.HILL_OF_FLESH_SUMMONS.get(), "向导巫毒娃娃(山)");


        add(TENpcEntities.GUIDE.get(), "向导");
        add(TENpcEntities.DEMOLITIONIST.get(), "爆破专家");
        add(TENpcEntities.GOBLIN_TINKERER.get(), "哥布林工匠");
        add(TENpcEntities.ARMS_DEALER.get(), "军火商");
        add(TENpcEntities.NURSE.get(), "护士");
        add(TENpcEntities.MERCHANT.get(), "商人");
        add(TENpcEntities.PAINTER.get(), "油漆工");
        add(TENpcEntities.DRYAD.get(), "树妖");
        add(TENpcEntities.DYE_TRADER.get(), "染料商");
        add(TENpcEntities.ANGLER.get(), "渔夫");
        add(TENpcEntities.FEMALE_ANGLER.get(), "渔女");
        add(TENpcEntities.OLD_MAN.get(), "老人");
        add(TENpcEntities.MECHANIC.get(), "机械师");
        add(TENpcEntities.TRAVELING_MERCHANT.get(), "旅商");
        add(TENpcEntities.WITCH_DOCTOR.get(), "巫医");
        add(TENpcEntities.PARTY_GIRL.get(), "派对女孩");
        add(TENpcEntities.CLOTHIER.get(), "服装商");
        add(TENpcEntities.ZOOLOGIST.get(), "动物学家");
        add(TENpcEntities.TRUFFLE.get(), "松露人");

        add(TESpawnEggItems.GOBLIN_SORCERER_SPAWN_EGG.get(), "哥布林术士刷怪蛋");
        add(TESpawnEggItems.GOBLIN_ARCHER_SPAWN_EGG.get(), "哥布林弓箭手刷怪蛋");
        add(TESpawnEggItems.GOBLIN_PEON_SPAWN_EGG.get(), "哥布林苦力刷怪蛋");
        add(TESpawnEggItems.GOBLIN_WARRIOR_SPAWN_EGG.get(), "哥布林战士刷怪蛋");
        add(TESpawnEggItems.GOBLIN_THIEF_SPAWN_EGG.get(), "哥布林盗贼刷怪蛋");
        add(TESpawnEggItems.GOBLIN_SCOUT_SPAWN_EGG.get(), "哥布林侦察兵刷怪蛋");
        add(TESpawnEggItems.ANGER_GOBLIN_SPAWN_EGG.get(), "愤怒哥布林刷怪蛋");

        // 肉后怪物
        add(TESpawnEggItems.WYVERN_SPAWN_EGG.get(), "飞龙刷怪蛋");
        add(TESpawnEggItems.PIXIE_SPAWN_EGG.get(), "妖精刷怪蛋");

        add(TESpawnEggItems.POSSESS_ARMOR_SPAWN_EGG.get(), "装甲幻影魔刷怪蛋");
        add(TESpawnEggItems.WRAITH_SPAWN_EGG.get(), "幻灵刷怪蛋");


        add(TESpawnEggItems.GUIDE_SPAWN_EGG.get(), "向导刷怪蛋");
        add(TESpawnEggItems.DEMOLITIONIST_SPAWN_EGG.get(), "爆破专家刷怪蛋");
        add(TESpawnEggItems.GOBLIN_TINKERER_SPAWN_EGG.get(), "哥布林工匠刷怪蛋");
        add(TESpawnEggItems.ARMS_DEALER_SPAWN_EGG.get(), "军火商刷怪蛋");
        add(TESpawnEggItems.NURSE_SPAWN_EGG.get(), "护士刷怪蛋");
        add(TESpawnEggItems.MERCHANT_SPAWN_EGG.get(), "商人刷怪蛋");
        add(TESpawnEggItems.PAINTER_SPAWN_EGG.get(), "油漆工刷怪蛋");
        add(TESpawnEggItems.DRYAD_SPAWN_EGG.get(), "树妖刷怪蛋");
        add(TESpawnEggItems.DYE_TRADER_SPAWN_EGG.get(), "染料商刷怪蛋");
        add(TESpawnEggItems.ANGLER_SPAWN_EGG.get(), "渔夫刷怪蛋");
        add(TESpawnEggItems.OLD_MAN_SPAWN_EGG.get(), "老人刷怪蛋");
        add(TESpawnEggItems.MECHANIC_SPAWN_EGG.get(), "机械师刷怪蛋");
        add(TESpawnEggItems.TRAVELING_MERCHANT_SPAWN_EGG.get(), "旅商刷怪蛋");
        add(TESpawnEggItems.WITCH_DOCTOR_SPAWN_EGG.get(), "巫医刷怪蛋");
        add(TESpawnEggItems.PARTY_GIRL_SPAWN_EGG.get(), "派对女孩刷怪蛋");
        add(TESpawnEggItems.CLOTHIER_SPAWN_EGG.get(), "服装商刷怪蛋");
        add(TESpawnEggItems.ZOOLOGIST_SPAWN_EGG.get(), "动物学家刷怪蛋");
        add(TESpawnEggItems.TRUFFLE_SPAWN_EGG.get(), "松露人刷怪蛋");


        // 动物
        add(TESpawnEggItems.SQUIRREL_SPAWN_EGG.get(), "松鼠刷怪蛋");
        add(TESpawnEggItems.JEWEL_SQUIRREL_SPAWN_EGG.get(), "宝石松鼠刷怪蛋");
        add(TESpawnEggItems.EXPLOSIVE_BUNNY_SPAWN_EGG.get(), "炸弹兔刷怪蛋");
        add(TESpawnEggItems.BUNNY_SPAWN_EGG.get(), "兔兔刷怪蛋");
        add(TESpawnEggItems.JEWEL_BUNNY_SPAWN_EGG.get(), "宝石兔刷怪蛋");
        add(TESpawnEggItems.DUCK_SPAWN_EGG.get(), "鸭子刷怪蛋");
        add(TESpawnEggItems.BIRD_SPAWN_EGG.get(), "鸟刷怪蛋");
        add(TESpawnEggItems.BLUE_JAY_SPAWN_EGG.get(), "冠蓝鸦刷怪蛋");
        add(TESpawnEggItems.CARDINAL_SPAWN_EGG.get(), "红雀刷怪蛋");


        add(TESpawnEggItems.CRAB_SPAWN_EGG.get(), "螃蟹刷怪蛋");
        add(TESpawnEggItems.GLOWING_SNAIL_SPAWN_EGG.get(), "发光蜗牛刷怪蛋");
        add(TESpawnEggItems.GRUBBY_SPAWN_EGG.get(), "蛆虫刷怪蛋");
        add(TESpawnEggItems.MAGGOT_SPAWN_EGG.get(), "蝇蛆刷怪蛋");
        add(TESpawnEggItems.MAGMA_SNAIL_SPAWN_EGG.get(), "岩浆蜗牛刷怪蛋");
        add(TESpawnEggItems.SLUGGY_SPAWN_EGG.get(), "鼻涕虫刷怪蛋");
        add(TESpawnEggItems.SNAIL_SPAWN_EGG.get(), "蜗牛刷怪蛋");

        add(TESpawnEggItems.BUTTERFLY_SPAWN_EGG.get(), "蝴蝶刷怪蛋");
        add(TESpawnEggItems.HELL_BUTTERFLY_SPAWN_EGG.get(), "地狱蝴蝶刷怪蛋");
        add(TESpawnEggItems.PRISMATIC_LACEWING_SPAWN_EGG.get(), "七彩草蛉刷怪蛋");
        add(TESpawnEggItems.DRAGONFLY_SPAWN_EGG.get(), "蜻蜓刷怪蛋");
        add(TESpawnEggItems.FAIRY_SPAWN_EGG.get(), "仙灵刷怪蛋");
        add(TESpawnEggItems.FEALING_SPAWN_EGG.get(), "飞灵刷怪蛋");
        add(TESpawnEggItems.GRASSHOPPER_SPAWN_EGG.get(), "蚱蜢刷怪蛋");
        add(TESpawnEggItems.LADYBUG_SPAWN_EGG.get(), "瓢虫刷怪蛋");
        add(TESpawnEggItems.SCORPION_SPAWN_EGG.get(), "蝎子刷怪蛋");
        add(TESpawnEggItems.WORM_SPAWN_EGG.get(), "蠕虫刷怪蛋");


        // 召唤杖
        add(TEPetItems.CHESTER_STAFF.get(), "眼骨");
        add(TEPetItems.WALLET.get(), "钱币槽");

        add(TESummonItems.FINCH_STAFF.get(), "雀杖");
        add(TESummonItems.SLIME_STAFF.get(), "史莱姆法杖");
        add(TESummonItems.IRON_GOLEM_STAFF.get(), "铁傀儡法杖");
        add(TESummonItems.HORNET_STAFF.get(), "黄蜂法杖");
        add(TESummonItems.SCULK_WISP_STAFF.get(), "幽匿法杖");
        add(TESummonItems.IMP_STAFF.get(), "小鬼法杖");
        add(TESummonItems.SNOW_FLINX_STAFF.get(), "小雪怪法杖");


        add(TESummonItems.SUMMON_WOODEN_SWORD_STAFF.get(), "青冥");
        add(TESummonItems.SUMMON_STONE_SWORD_STAFF.get(), "破山");
        add(TESummonItems.SUMMON_IRON_SWORD_STAFF.get(), "白虹");
        add(TESummonItems.SUMMON_GOLDEN_SWORD_STAFF.get(), "流星");
        add(TESummonItems.SUMMON_DIAMOND_SWORD_STAFF.get(), "龙渊");
        add(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF.get(), "赤霄");
        add(TESummonItems.TERRAPRISMA.get(), "泰拉棱镜");


        // 鞭子
        add(TEWhipItems.LEATHER_WHIP.get(), "皮鞭");
        add(TEWhipItems.SLUB_WHIP.get(), "竹节鞭");
        add(TEWhipItems.RUBY_WHIP.get(), "红玉鞭");
        add(TEWhipItems.AMBER_WHIP.get(), "琥珀鞭");
        add(TEWhipItems.TOPAZ_WHIP.get(), "黄玉鞭");
        add(TEWhipItems.JADE_WHIP.get(), "翡翠鞭");
        add(TEWhipItems.DIAMOND_WHIP.get(), "钻石鞭");
        add(TEWhipItems.SAPPHIRE_WHIP.get(), "蓝玉鞭");
        add(TEWhipItems.AMETHYST_WHIP.get(), "紫晶鞭");
        add(TEWhipItems.SWAMP_WHIP.get(), "沼泽藤蔓");
        add(TEWhipItems.SNAPTHORN.get(), "荆鞭");
        add(TEWhipItems.SPINAL_TAP.get(), "脊柱骨鞭");
        add(TEWhipItems.FIRECRACKER.get(), "鞭炮");

        // 回旋镖
        add(TEBoomerangItems.WOOD_BOOMERANG.get(), "木回旋镖");
        add(TEBoomerangItems.ENCHANTED_BOOMERANG.get(), "附魔回旋镖");
        add(TEBoomerangItems.SHROOMERANG.get(), "蘑菇回旋镖");
        add(TEBoomerangItems.ICE_BOOMERANG.get(), "冰雪回旋镖");
        add(TEBoomerangItems.TRIMARANG.get(), "三尖回旋镖");
        add(TEBoomerangItems.FLAMARANG.get(), "烈焰回旋镖");
        add(TEBoomerangItems.DEVELOPER_BOOMERANG.get(), "开发者回旋镖");
        add(TEBoomerangItems.COMBAT_WRENCH.get(), "战斗扳手");
        add(TEBoomerangItems.BeiDou_BOOMERANG.get(), "北斗飞镖");

        // 悠悠球
        add(TEYoyosItems.WOODEN_YOYO.get(), "木悠悠球");
        add(TEYoyosItems.RALLY.get(), "对打球");
        add(TEYoyosItems.MALAISE.get(), "抑郁球");
        add(TEYoyosItems.ARTERY.get(), "血脉球");
        add(TEYoyosItems.AMAZON.get(), "亚马逊球");
        add(TEYoyosItems.CODE_1.get(), "代码一号");
        add(TEYoyosItems.HIVE_FIVE.get(), "蜂巢球");
        add(TEYoyosItems.CASCADE.get(), "喷流球");
        add(TEYoyosItems.VALOR.get(), "英勇球");

        // 骑乘
        add(TERideableItems.SLIMY_SADDLE.get(), "粘鞍");
        add(TERideableItems.HONEYED_GOGGLES.get(), "涂蜜护目镜");

        // mob_effect
        add(TEEffects.DEMONIC_THOUGHTS.get(), "邪念");
        add(TEEffects.SUMMON_FOCUS.get(), "狩猎");
        add(TEEffects.HELLFIRE.get(), "狱炎");
        add(TEEffects.FROST_BURN.get(), "霜冻");
        add(TEEffects.CRIMSON_STORM.get(), "猩红风暴");
        add(TEEffects.HORRIFIED.get(), "惊恐");
        add(TEEffects.THE_TONGUE.get(), "狂卷之舌");


        add(TEItems.HOUSE_DETECTOR.get(), "房屋探测器");

        // 弹幕
        add(TEProjectileEntities.BOOMERANG_PROJECTILE.get(), "回旋镖射弹");
        add(TEProjectileEntities.CABBAGE_PROJ.get(), "卷心菜投射物");
        add(TEProjectileEntities.BEE_STICK_PROJ.get(), "蜜蜂棒射弹");
        add(TEProjectileEntities.SUMMON_BEE_STICK_PROJ.get(), "召唤蜜蜂棒射弹");
        add(TEProjectileEntities.SKULL.get(), "骷髅射弹");
        add(TEProjectileEntities.VILE_SPIT_PROJ.get(), "魔唾液射弹");
        add(TEProjectileEntities.DARK_CASTER_PROJ.get(), "黑暗法师射弹");
        add(TEProjectileEntities.FIRE_IMP_PROJ.get(), "火焰小鬼射弹");
        add(TEProjectileEntities.FIRE_BOUND_PROJ.get(), "火焰束缚射弹");
        add(TEProjectileEntities.HARPY_FEATURE_PROJ.get(), "鸟妖羽毛射弹");
        add(TEProjectileEntities.DEMON_SCYTHE_PROJ.get(), "恶魔镰刀射弹");
        add(TEProjectileEntities.LAVA_PILLAR.get(), "熔岩柱");
        add(TEProjectileEntities.THE_DESTROYER_LASER_PROJ.get(), "毁灭者激光");
        add(TEProjectileEntities.SEED.get(), "种子射弹");
        add(TEProjectileEntities.SPORE.get(), "孢子射弹");
        add(TEProjectileEntities.SPIKE_BALL.get(), "刺球射弹");
        add(TEProjectileEntities.WHIP_PROJECTILE.get(), "鞭子射弹");
        add(TEProjectileEntities.TRAIL_PROJECTILE.get(), "轨迹子弹");
        add(TEProjectileEntities.TRAIL_SWORD_PROJECTILE.get(), "轨迹剑气");
        add(TEProjectileEntities.BEE_PROJ.get(), "蜜蜂射弹");
        add(TEProjectileEntities.SLIME_SPIKE.get(), "史莱姆尖刺");
        add(TEProjectileEntities.JUNGLE_SPIKE.get(), "丛林尖刺");
        add(TEProjectileEntities.ICE_SPIKE.get(), "寒冰尖刺");
        add(TEProjectileEntities.YOYO_PROJ.get(), "悠悠球射弹");
        add(TEProjectileEntities.THROWN_ICE_PROJECTILE.get(), "投掷寒冰射弹");
        add(TEProjectileEntities.ICE_PILLAR.get(), "冰刺");
        add(TEProjectileEntities.SHADOW_HAND.get(), "暗影之手射弹");

        add("message.terra_entity.trade.not_enough_items", "你没有足够的物品来交易");


        // config
        add("terra_entity.configuration.boss_clear_when_no_target", "丢失目标时清除BOSS");
        add("terra_entity.configuration.boss_attributes_multiplier_health", "BOSS属性倍率-生命");
        add("terra_entity.configuration.boss_attributes_multiplier_damage", "BOSS属性倍率-伤害");
        add("terra_entity.configuration.boss_no_physics", "BOSS能否穿墙");
        add("terra_entity.configuration.boss_leave_on_day", "特定的BOSS是否在白天离开");
        add("terra_entity.configuration.boss_keep_wandering", "BOSS随机游走");


        add("terra_entity.configuration.display_summon_items", "在创造栏显示召唤物品");
        add("terra_entity.configuration.enhance_all_monster", "增强所有怪物");
        add("terra_entity.configuration.monster_attributes_multiplier_health", "Monster属性倍率-生命");
        add("terra_entity.configuration.monster_attributes_multiplier_damage", "Monster属性倍率-伤害");
        add("terra_entity.configuration.spawn_without_light", "无视光照生成怪物");
        add("terra_entity.configuration.chance_to_spawn_slime_on_zombie_head", "在僵尸头上生成史莱姆的几率");
        add("terra_entity.configuration.enemy_spawn_chance_apply_all", "所有怪物的生成几率");
        add("terra_entity.configuration.enemy_spawn_chance", "Terra Entity怪物生成几率");
        add("terra_entity.configuration.enable_entity_motion_blur", "启用实体移动模糊");
        add("terra_entity.configuration.behavior_tree_web_viewer_server_port", "行为树可视化服务器端口");


        add("terra_entity.configuration.boss_bar_style", "BOSS血条样式");
        add("terra_entity.configuration.boss_bar_number_offset_x", "BOSS血条数字偏移-X");
        add("terra_entity.configuration.boss_bar_number_offset_y", "BOSS血条数字偏移-Y");
        add("terra_entity.configuration.enableNonSpiderModel", "蜘蛛和谐");
        add("terra_entity.configuration.enableNonSpiderModel.tooltip", "如果你对蜘蛛反感或想美化他们，请开启这个选项（该配置更换需要重启游戏！）");

        add("terra_entity.configuration.generate_projectile_particle", "生成弹幕粒子");
        add("terra_entity.configuration.npc_chat_bubble_style", "NPC聊天气泡样式");


        // Tooltip
        add("tooltic.terra_entity.summon_item.desc", "右键长按以解除所有召唤物");

        add("tooltip.terra_entity.summon_item_cost", "仆从占用: %s");
        add("tooltip.terra_entity.summon_item_entity", "仆从类型: %s");
        add("tooltip.terra_entity.summon_info", "仆从栏位: %d / %d");

        add("tooltip.terra_entity.whip.hit_effect", "命中效果: ");
        add("tooltip.terra_entity.whip.hit_effect_beneficial", "农场主的训斥");
        add("tooltip.terra_entity.house_detect.mode", "房屋工具模式:");
        add("tooltip.terra_entity.house_detect.mode.check", "探测");
        add("tooltip.terra_entity.house_detect.mode.check.owner", "所有者");
        add("tooltip.terra_entity.house_detect.mode.add", "添加");
        add("tooltip.terra_entity.house_detect.mode.add.failed", "添加房屋失败，房屋已存在！");
        add("tooltip.terra_entity.house_detect.mode.add.success", "添加房屋成功！");
        add("tooltip.terra_entity.house_detect.mode.delete", "删除");
        add("tooltip.terra_entity.house_detect.mode.delete.success", "删除房屋成功！");
        add("tooltip.terra_entity.house_detect.not_npc", "这不是npc！");
        add("tooltip.terra_entity.house_detect.no_detect", "使用前先探测房屋！");
        add(HouseDetectInfo.DetectType.TOO_LARGE.getTranslationKey(), "这个房间太大了！");
        add(HouseDetectInfo.DetectType.TOO_SMALL.getTranslationKey(), "这个房间太小了！");
        add(HouseDetectInfo.DetectType.NO_DYNAMIC_LIGHT.getTranslationKey(), "房间缺少光源！");
        add(HouseDetectInfo.DetectType.NO_CHAIR.getTranslationKey(), "房间缺少椅子！");
        add(HouseDetectInfo.DetectType.NO_TABLE.getTranslationKey(), "房间缺少桌子！");
        add(HouseDetectInfo.DetectType.FOUND_HOUSE.getTranslationKey(), "这个房间很合适！");
        add("tooltip.terra_entity.house_detect.info", "按下shift+右键 切换模式");
        add("tooltip.terra_entity.chester.desc", "按下shift+右键 切换目标容器. 你可以用它绑定一个容器");
        add("tooltip.terra_entity.chester.current", "当前容器");


        // boomerang
        add("tooltip.terra_entity.boomerang.penetration", "穿透数量");
        add("tooltip.terra_entity.boomerang.on_hit_effects", "命中效果");
        add("tooltip.terra_entity.boomerang.max_count", "分身数量");
        add("tooltip.terra_entity.boomerang.fly_speed", "飞行速度");

        // yoyos
        add("tooltip.terra_entity.yoyo.max_range", "最大射程");
        add("tooltip.terra_entity.yoyo.exist_time", "使用时间");
        add("tooltip.terra_entity.yoyo.hit_effect", "命中效果");

        // rideable
        add("tooltip.terra_entity.rideable_item.desc", "按下快捷键以骑乘。默认 R 键");


        // attribute
        add("attribute.name.player.summon_damage", "召唤伤害");
        add("attribute.name.player.mark_damage", "标记伤害");
        add("attribute.name.player.whip_range", "鞭范围");
        add("attribute.name.player.summon_knockback", "召唤物击退");
        add("attribute.name.player.minion_capacity", "仆从容量");
        add("attribute.name.player.sentry_capacity", "哨兵容量");

        // track
        add("terra_entity.track_type.simple", "简单");
        add("terra_entity.track_type.basis", "基平面");

        // hit effect
        add("terra_entity.effect.strategy.set_fire", "着火啦");
        add("terra_entity.effect.strategy.yoyo_bee", "召唤蜜蜂");


        add("terra_entity.effect.strategy.mud", "泥潭");
        add("terra_entity.effect.strategy.snapthorn", "中毒");
        add("terra_entity.effect.strategy.firecracker", "狱炎");

        add("terra_entity.effect.strategy.bat", "吸血 +1 hp");
        add("terra_entity.effect.strategy.lights_bane", "召唤魔光剑");
        add("terra_entity.effect.strategy.bee_keeper", "召唤蜜蜂");

        add("terra_entity.effect.strategy.frozen_burn_5_sec", "霜冻 10秒");
        add("terra_entity.effect.strategy.frozen_burn_3_sec_50_chance", "50%几率 霜冻 3秒");
        add("terra_entity.effect.strategy.tentacle_spikes", "触手钉锤");
        add("terra_entity.effect.strategy.hunting_4_sec", "狩猎 4秒");
        add("terra_entity.effect.strategy.hell_fire_5_sec", "狱炎 5秒");
        add("terra_entity.effect.strategy.set_fire_5_sec", "着火啦 5秒");
        add("terra_entity.effect.strategy.forzen_burn_5_sec", "霜火 5秒");
        add("terra_entity.effect.strategy.blood_butchered", "血腥屠宰");
        add("terra_entity.effect.strategy.bei_dou", "随机5种效果:\n     霜冻 IV 10秒\n     狱炎 IV 10秒\n     凋零 IV 10秒\n     中毒 IV 10秒\n     瞬间伤害 VIII");

        add("terra_entity.effect.strategy.strength", "愤怒");

        // sound
        add("terra_entity.subtitle.routine_hurt", "怪物：受伤");
        add("terra_entity.subtitle.routine_death", "怪物：死亡");
        add("terra_entity.subtitle.roar", "BOSS：吼叫");
        add("terra_entity.subtitle.hurried_roaring", "BOSS：急促吼叫");
        add("terra_entity.subtitle.blood_crawler_death", "血爬虫：死亡");
        add("terra_entity.subtitle.blood_crawler_free", "血爬虫：血液流动");
        add("terra_entity.subtitle.blood_crawler_hurt", "血爬虫：受伤");
        add("terra_entity.subtitle.bloody_spore_death", "血腥芽孢：死亡");
        add("terra_entity.subtitle.bloody_spore_fuse", "血腥芽孢：孕育");
        add("terra_entity.subtitle.bloody_spore_hit", "血腥芽孢：受伤");
        add("terra_entity.subtitle.drippler_death", "滴滴怪：死亡");
        add("terra_entity.subtitle.drippler_hurt", "滴滴怪：受伤");
        add("terra_entity.subtitle.metal_death", "机械怪物：死亡");
        add("terra_entity.subtitle.metal_hurt", "机械怪物：受伤");
        add("terra_entity.subtitle.visual_neuron_death", "视神经元：死亡");
        add("terra_entity.subtitle.visual_neuron_hurt", "视神经元：受伤");
        add("terra_entity.subtitle.dig_sound", "蠕虫生物：挖掘");
        add("terra_entity.subtitle.giant_shelly_death", "巨型卷壳怪：死亡");
        add("terra_entity.subtitle.giant_shelly_free_0", "巨型卷壳怪：滚动");
        add("terra_entity.subtitle.giant_shelly_free_1", "巨型卷壳怪：爬行");
        add("terra_entity.subtitle.giant_shelly_hurt", "巨型卷壳怪：受伤");
        add("terra_entity.subtitle.tr_zombie_death", "僵尸：死亡");
        add("terra_entity.subtitle.tr_skeleton_hurt", "骷髅：受伤");
        add("terra_entity.subtitle.waving", "玩家：挥动");
        add("terra_entity.subtitle.use_mounts", "玩家：召唤坐骑");
        add("terra_entity.subtitle.decayeder_ambient", "腐骴：摩擦身体");
        add("terra_entity.subtitle.decayeder_death", "腐骴：死亡");
        add("terra_entity.subtitle.decayeder_hurt", "腐骴：受伤");
        add("terra_entity.subtitle.decayeder_step", "腐骴：脚步声");
        add("terra_entity.subtitle.whip_attack", "鞭子：抽打");
        add("terra_entity.subtitle.routine_summon", "召唤物：召唤");
        add("terra_entity.subtitle.summon_hornet", "黄蜂：召唤");
        add("terra_entity.subtitle.summon_eye", "飞行召唤物：召唤");
        add("terra_entity.subtitle.summon_imp", "小鬼：召唤");
        add("terra_entity.subtitle.summon_money_trough", "存钱罐：召唤");
        add("terra_entity.subtitle.antlion_death", "蚁狮：死亡");
        add("terra_entity.subtitle.antlion_hurt", "蚁狮：受伤");
        add("terra_entity.subtitle.antlion_free", "蚁狮：活动");
        add("terra_entity.subtitle.antlion_swarmer_death", "蚁狮蜂：死亡");
        add("terra_entity.subtitle.antlion_swarmer_free", "蚁狮蜂：移动");
        add("terra_entity.subtitle.bat_death", "蝙蝠：死亡");
        add("terra_entity.subtitle.beetle_death", "胭脂虫：死亡");
        add("terra_entity.subtitle.blood_jelly_death", "血水母：死亡");
        add("terra_entity.subtitle.blood_jelly_free", "血水母：漂浮");
        add("terra_entity.subtitle.bone_serpent_death", "骨蛇：死亡");
        add("terra_entity.subtitle.demon_death", "恶魔：死亡");
        add("terra_entity.subtitle.demon_free", "恶魔：嘶吼");
        add("terra_entity.subtitle.demon_hurt", "恶魔：受伤");
        add("terra_entity.subtitle.dungeon_spirit_death", "地牢幽灵：消散");
        add("terra_entity.subtitle.dungeon_spirit_free", "地牢幽灵：飘动");
        add("terra_entity.subtitle.dungeon_spirit_hurt", "地牢幽灵：受击");
        add("terra_entity.subtitle.granite_golem_death", "花岗岩巨人：崩塌");
        add("terra_entity.subtitle.granite_golem_hurt", "花岗岩巨人：受损");
        add("terra_entity.subtitle.granite_golem_free", "花岗岩巨人：行走");
        add("terra_entity.subtitle.jellyfish_death", "水母：爆裂");
        add("terra_entity.subtitle.jellyfish_free", "水母：游动");
        add("terra_entity.subtitle.jellyfish_hurt", "水母：受刺激");
        add("terra_entity.subtitle.pixie_death", "妖精：消失");
        add("terra_entity.subtitle.pixie_free", "妖精：飞舞");
        add("terra_entity.subtitle.pixie_hurt", "妖精：受惊");
        add("terra_entity.subtitle.sand_shoot", "沙子：喷射");
        add("terra_entity.subtitle.soul_death", "灵体：湮灭");
        add("terra_entity.subtitle.tr_zombie_free", "僵尸：嘶吼");
        add("terra_entity.subtitle.unicorn_death", "独角兽：悲鸣");
        add("terra_entity.subtitle.unicorn_hurt", "独角兽：痛鸣");
        add("terra_entity.subtitle.wyvern_death", "飞龙：坠落");
        add("terra_entity.subtitle.wyvern_hurt", "飞龙：咆哮");
        add("terra_entity.subtitle.the_hungry_death", "饿鬼：消亡");
        add("terra_entity.subtitle.the_hungry_hurt", "饿鬼：受击");
        add("terra_entity.subtitle.wall_of_flesh_hurt", "血肉：受创");
        add("terra_entity.subtitle.wall_of_flesh_roar", "血肉：咆哮");
        add("terra_entity.subtitle.wall_of_flesh_summon", "血肉：呕吐");




        // 车万女仆
        add("task.terra_entity.boomerang_attack", "回旋镖攻击");
        add("task.terra_entity.boomerang_attack.desc", "女仆会主动用回旋镖攻击周围的敌对生物");
        add("task.terra_entity.boomerang_attack.condition.has_boomerang", "主手持有回旋镖");

        // curios
        add("curios.identifier." + CuriosHelper.MOUNT_KEY, "坐骑");
        add("curios.identifier." + CuriosHelper.PET_KEY, "宠物");
        add("curios.identifier." + CuriosHelper.LIGHT_PET_KEY, "照明宠物");

        // 附魔
        add("enchantment.terra_entity.multi_boomerang", "影分身");
        add("enchantment.terra_entity.multi_boomerang.desc", "额外发射一个回旋镖");
        add("enchantment.terra_entity.whip_sweep", "横扫之鞭");
        add("enchantment.terra_entity.whip_sweep.desc", "概率造成大范围伤害");
        add("enchantment.terra_entity.summoner_pact", "召唤师契约");
        add("enchantment.terra_entity.summoner_pact.desc", "召唤额外的仆从");

        // npc对话
        add("dialogs.terra_entity.trade", "交易");
        add("dialogs.terra_entity.summon", "召唤");
        add("dialogs.terra_entity.dialog", "对话");
        add("dialogs.terra_entity.quest", "任务");

        add("dialogs.terra_entity.guide.0", "我的工作是为你接下来的任务提供建议。建议你遇到任何困难时都来和我谈谈。");
        add("dialogs.terra_entity.guide.1", "他们说，有个人会告诉你如何在这地方上生存……哦等下。那个人就是我。");
        add("dialogs.terra_entity.guide.2", "晚上你应该呆在家里。黑夜在外面转悠非常危险。");
        add("dialogs.terra_entity.guide.3", "在融合的世界中，你会收获多倍的宝藏，但这也以为着承担多倍的风险。");
        add("dialogs.terra_entity.guide.4", "据我所知这个世界上的人类比我们原来的世界更多。");
        add("dialogs.terra_entity.guide.5", "抱歉，有时候我不得不开门。");
        add("dialogs.terra_entity.guide.6", "那些会爆炸的家伙比一般的地表怪物更具威胁！");
        add("dialogs.terra_entity.guide.7", "草地上的生命蘑菇有时候可以救你一命。");
        add("dialogs.terra_entity.guide.8", "地下有水晶之心，可以用来提高你的最大生命值。你可以用镐来打碎它们。");
        add("dialogs.terra_entity.guide.9", "地底下有一种具有神奇魔力的湖，它非常稀有。");
        add("dialogs.terra_entity.guide.10", "夜晚，星星在坠落，洒满全世界。它们的用途极为广泛。如果你看到了，一定要拿到手，因为星星在日出后就会消失。");
        add("dialogs.terra_entity.guide.11", "无论是什么东西在疯狂蔓延，你都将意识到是时候阻止它们。");
        add("dialogs.terra_entity.guide.12", "如果你想活下来，你需要制造武器和建造房屋。首先要砍树并收集木材。");
        add("dialogs.terra_entity.guide.13", "拥有一把剑后，你可以试试从史莱姆身上收集一些凝胶。用木棍和凝胶制作火把！");
        add("dialogs.terra_entity.guide.14", "如果你拥有了一些矿石，你需要将它铸成矿锭，才能用来制作物品。这需要熔炉！");
        add("dialogs.terra_entity.guide.15", "如果在祭坛上合成晶状体，你也许能够找到方法来召唤一个强大的怪物。不过，最好等到夜晚再用它。");


        add("dialogs.terra_entity.nurse.0", "我要和向导认真谈一谈。你一周到底有多少次被熔岩烫成重伤？");
        add("dialogs.terra_entity.nurse.1", "看到那个在地牢周围转来转去的老人没？他看上去遇到麻烦了。");
        add("dialogs.terra_entity.nurse.2", "嗨，军火商有没有提过要去看医生啥的？就随便问问。");
        add("dialogs.terra_entity.nurse.3", "又惹上混混了？");
        add("dialogs.terra_entity.nurse.4", "别像个孩子似的！我见过更糟的。");
        add("dialogs.terra_entity.nurse.5", "你这么做的时候疼吗？别那么做。");

        add("dialogs.terra_entity.demolitionist.0", "炸药如今十分火爆。马上买一些！");
        add("dialogs.terra_entity.demolitionist.1", "今天是个找死的好日子！");
        add("dialogs.terra_entity.demolitionist.2", "让我看看这样会怎……（轰！）……哦，对不起，你还要那条腿吗？");
        add("dialogs.terra_entity.demolitionist.3", "看看我的商品；都是惊爆价");
        add("dialogs.terra_entity.demolitionist.4", "雷管，这是我特别为你准备的灵丹妙药，包治百病。");
        add("dialogs.terra_entity.demolitionist.5", "想穿过那些邪恶石头，嗯？为什么不用炸药炸掉它！");

        add("dialogs.terra_entity.goblin_tinkerer.0", "哥布林太容易生气了。事实上，他们能为了一些破布发动战争！");
        add("dialogs.terra_entity.goblin_tinkerer.1", "老实说，大部分哥布林都不是真正的火箭科学家。好吧，有一些是。");
        add("dialogs.terra_entity.goblin_tinkerer.2", "你知不知道为什么大家到哪儿都带着这些尖刺球？因为我不知道。");
        add("dialogs.terra_entity.goblin_tinkerer.3", "我刚刚完成了最新的作品！这个版本就算你对着它猛力吹吸也不会猛烈爆炸。");
        add("dialogs.terra_entity.goblin_tinkerer.4", "哥布林盗贼不太擅长偷东西。没上锁的箱子都不会偷！");
        add("dialogs.terra_entity.goblin_tinkerer.5", "唷，我听说你喜欢火箭和跑鞋，所以我在你的跑鞋上加了一些火箭。");

        add("dialogs.terra_entity.arms_dealer.0", "哥们，把手从我的枪上拿开！");
        add("dialogs.terra_entity.arms_dealer.1", "嘿，兄弟，这可不是演电影。需要另行准备弹药。");
        add("dialogs.terra_entity.arms_dealer.2", "我看你在盯着迷你鲨……你绝对想不到它是怎么做成的。");
        add("dialogs.terra_entity.arms_dealer.3", "我想买护士卖的东西。你说啥？她什么也不卖？");
        add("dialogs.terra_entity.arms_dealer.4", "飞鱼？我把它叫作打靶！");
        add("dialogs.terra_entity.arms_dealer.5", "别和爆破专家浪费时间了。我这边有你要的一切。");

        add("dialogs.terra_entity.merchant.0", "剑克纸！赶紧买一把。");
        add("dialogs.terra_entity.merchant.1", "你想要苹果？你想要胡萝卜？你想要菠萝？我们只有火把。");
        add("dialogs.terra_entity.merchant.2", "看看我的土块；它们特别土。");
        add("dialogs.terra_entity.merchant.3", "你是不知道土块能在国外卖多少钱。");
        add("dialogs.terra_entity.merchant.4", "总有一天他们会讲述你的传奇……肯定会是好故事。");
        add("dialogs.terra_entity.merchant.5", "Kosh, kapleck Mog。哦，对不起，这是克林贡语，意思是“要么买，要么死。”");

        add("dialogs.terra_entity.painter.0", "我知道青绿色和蓝绿色之间的差别。但我不会告诉你。");
        add("dialogs.terra_entity.painter.1", "钛白色用完了，别问了。");
        add("dialogs.terra_entity.painter.2", "尝试调合粉色和紫色，肯定管用，我发誓！");
        add("dialogs.terra_entity.painter.3", "不、不、不……灰色也分很多种！别让我开始……");
        add("dialogs.terra_entity.painter.4", "我希望别下雨了，漆还没干。下雨就惨了！");
        add("dialogs.terra_entity.painter.5", "我试过举办一次彩弹大战，但是每个人都只想要食物和装饰品。");

        add("dialogs.terra_entity.dryad.0", "注意安全！两边的世界都需要你！");
        add("dialogs.terra_entity.dryad.1", "时间的沙漏在缓缓流逝。而你并没有优雅地变老。");
        add("dialogs.terra_entity.dryad.2", "两个哥布林走进酒吧，其中一个对另一个说：“来杯啤酒？！");
        add("dialogs.terra_entity.dryad.3", "说我雷声大雨点小是啥意思？");
        add("dialogs.terra_entity.dryad.4", "你必须停止邪恶的蔓延。");
        add("dialogs.terra_entity.dryad.5", "这个世界更为广阔……自然的力量也更强大了");

        add("dialogs.terra_entity.dye_trader.0", "我带给你最丰富的色彩，以换取你的财富。");
        add("dialogs.terra_entity.dye_trader.1", "亲爱的，你的穿着太单调了。你一定得好好学学，怎么给单调的衣服染色！");
        add("dialogs.terra_entity.dye_trader.2", "我唯一愿意染的木材是红木。给任何其他木材染色都是浪费。");
        add("dialogs.terra_entity.dye_trader.3", "噢，不行，不行，这样是不行的。有钱也没用，你必须拿稀有的植物样本来和我交换！");
        add("dialogs.terra_entity.dye_trader.4", "这些染料瓶？抱歉，亲爱的朋友，这些是非卖品。我只接受用最珍稀的植物来交换它们！");
        add("dialogs.terra_entity.dye_trader.5", "你以为可以骗过我的眼睛？我可不这么想！我只接受用最稀有的花来交换这些特别的瓶子。");

        add("dialogs.terra_entity.angler.0", "太可气了！有些鱼可能在我出生之前就灭绝了，真不公平！");
        add("dialogs.terra_entity.angler.1", "什……么？！难道你没看见我在收钓鱼线吗？？");
        add("dialogs.terra_entity.angler.2", "整个%s中都没有厨师，所以我不得不自己烹鱼！");
        add("dialogs.terra_entity.angler.3", "我没有妈妈，也没有爸爸，但我有很多鱼！这就够了！");
        add("dialogs.terra_entity.angler.4", "听听小孩的忠告吧，永远不要用舌头碰冰块！等一下，就当我没说，我就想看你这样做！");
        add("dialogs.terra_entity.angler.5", "听说过会叫的鱼吗？！我没听说过，只是想知道你听说过没！");
        add("dialogs.terra_entity.angler.6", "嘿！当心！我设了许多陷阱，用来实施史上最大的恶作剧！没人会发觉！你敢告诉别人试试！");
        add("dialogs.terra_entity.angler.7", "%s到处都是各种稀奇古怪的鱼！");
        add("dialogs.terra_entity.angler.stat.0", "你知道我已经有了%s条超棒的鱼了吗！？那是因为在跑腿这件事上，你还是发挥了作用！");
        add("dialogs.terra_entity.angler.stat.1", "喂！你打扰我好像有%s次了！如果不是每次都有很酷的鱼，我一定会发飙！");
        add("dialogs.terra_entity.angler.wakeup.0", "谢谢，我想，谢谢你救了我之类的。你是个优秀的得力仆从！");
        add("dialogs.terra_entity.angler.wakeup.1", "啥？你是哪位？我绝对不是溺水之类的！");
        add("dialogs.terra_entity.angler.wakeup.2", "你救了我！你太好了，我可以使唤你……呃，我是说，雇你帮我做些了不起的事！");
        add("dialogs.terra_entity.angler.task_ready.0", "嘿！我有一个活儿给你。不要以为你能拒绝，怎么都不行！");
        add("dialogs.terra_entity.angler.task_ready.1", "我想要一条鱼，你去给我弄一条来！快问我细节！");
        add("dialogs.terra_entity.angler.task_ready.2", "%1$s想让你正式成为%2$s的跑腿官！");
        add("dialogs.terra_entity.angler.task_ready.3", "嘿！你就是我一直在找的牺……我是说称职的钓鱼大师！");
        add("dialogs.terra_entity.angler.task_succeed.0", "哦！谢谢你抓来我要的鱼，可以滚了！");
        add("dialogs.terra_entity.angler.task_succeed.1", "你这跑腿的活干得挺不错嘛！现在走开！");
        add("dialogs.terra_entity.angler.task_succeed.2", "哈哈哈哈！你做到了！你竟然毫发无伤，真没劲！");
        add("dialogs.terra_entity.angler.task_succeed.3", "哇！？你竟然完成了任务，还活了下来！不错，把它交上来，再滚远点！");
        add("dialogs.terra_entity.angler.task_succeed.4", "抓到啦！一切都是按计划进行的！哈哈哈！");
        add("dialogs.terra_entity.angler.task_finished.0", "我的鱼够了！我现在不需要你的帮助！");
        add("dialogs.terra_entity.angler.task_finished.1", "你今天已经让我够开心了，可以走了。");
        add("dialogs.terra_entity.angler.task_finished.2", "现在没有任务要分配给你。");
        add("dialogs.terra_entity.angler.task_finished.3", "一天就一条鱼，请离开！");
        add("dialogs.terra_entity.angler.task_finished.4", "你上次给我的鱼还没用呢。我不需要了。");
        add("dialogs.terra_entity.angler.task_finished.5", "你完蛋了，伟大的%s会解雇你！");

        add("dialogs.terra_entity.female_angler.0", "真可惜呀！有些鱼或许在我出生前就消失了，希望能好好保护剩下的它们～");
        add("dialogs.terra_entity.female_angler.1", "啊……抱歉！是不是打扰到我收钓线了？实在不好意思呀！");
        add("dialogs.terra_entity.female_angler.2", "整个%s里虽然没有厨师，但能自己亲手烹鱼，也是一种幸福呢！");
        add("dialogs.terra_entity.female_angler.3", "我没有爸爸妈妈，但有这么多可爱的鱼儿作伴！我已经很满足啦～");
        add("dialogs.terra_entity.female_angler.4", "听姐姐一句劝哦，千万别用舌头碰冰块啦！会冻伤的，一定要好好照顾自己呀～");
        add("dialogs.terra_entity.female_angler.5", "你听说过会叫的鱼吗？我好像见过哦，要不要和我聊聊呀！");
        add("dialogs.terra_entity.female_angler.6", "哎呀！小心一点～我布置了些小装饰，不是陷阱啦，只是想给大家一点小惊喜，千万别误会哦～");
        add("dialogs.terra_entity.female_angler.7", "%s里到处都是可爱又特别的鱼儿，真的太美好了！");
        add("dialogs.terra_entity.female_angler.stat.0", "你知道吗？我已经收集到%s条超美的鱼了！真的太谢谢你了，没有你的帮忙我肯定做不到的！");
        add("dialogs.terra_entity.female_angler.stat.1", "哎呀！麻烦你%s次了，真的太不好意思啦！不过每次都能见到这么好看的鱼，真的太开心了！");
        add("dialogs.terra_entity.female_angler.wakeup.0", "太感谢你了！真的不知道该怎么报答你，你真的是个超级好的朋友！");
        add("dialogs.terra_entity.female_angler.wakeup.1", "呀？是你呀～刚刚只是脚滑了一下，不是溺水啦，谢谢你这么关心我！");
        add("dialogs.terra_entity.female_angler.wakeup.2", "谢谢你救了我！你真的太好了～如果你不介意的话，我想请你帮我做点小事，当然不愿意也没关系的！");
        add("dialogs.terra_entity.female_angler.task_ready.0", "你好呀～我有个小小的请求，如果你方便的话能不能帮我一下？不方便也完全没关系的！");
        add("dialogs.terra_entity.female_angler.task_ready.1", "我想找一条特别的鱼，如果你愿意帮忙找找的话就太好啦！不急的，慢慢说细节～");
        add("dialogs.terra_entity.female_angler.task_ready.2", "%1$s想正式邀请你成为%2$s的钓鱼小帮手，你愿意吗？");
        add("dialogs.terra_entity.female_angler.task_ready.3", "你好呀！你就是我一直想找的、超厉害的钓鱼大师吧！能请你帮帮忙吗？");
        add("dialogs.terra_entity.female_angler.task_succeed.0", "哇！谢谢你帮我抓到想要的鱼，真的太感谢了！辛苦你啦～");
        add("dialogs.terra_entity.female_angler.task_succeed.1", "你真的做得超棒的！辛苦你了，快歇一歇吧～");
        add("dialogs.terra_entity.female_angler.task_succeed.2", "太开心啦！你安全完成了，真的太好了！我还一直担心你呢！");
        add("dialogs.terra_entity.female_angler.task_succeed.3", "天呐！？你不仅完成了任务，还平平安安的，真的太棒了！快把鱼给我，快去休息一下吧～");
        add("dialogs.terra_entity.female_angler.task_succeed.4", "抓到啦！一切都超顺利的～谢谢你的帮忙，真的太感谢了！");
        add("dialogs.terra_entity.female_angler.task_finished.0", "我的鱼已经足够啦！真的太谢谢你的帮忙，你真的帮了我太多了～");
        add("dialogs.terra_entity.female_angler.task_finished.1", "今天真的谢谢你啦，和你相处超开心的！你快去忙自己的事吧～");
        add("dialogs.terra_entity.female_angler.task_finished.2", "暂时没有需要帮忙的啦，不过还是谢谢你愿意过来～");
        add("dialogs.terra_entity.female_angler.task_finished.3", "今天的鱼已经够啦，辛苦你啦，慢走哦～");
        add("dialogs.terra_entity.female_angler.task_finished.4", "上次你给我的鱼我还好好收着呢，暂时不需要啦，不过还是超感谢你的！");
        add("dialogs.terra_entity.female_angler.task_finished.5", "真的太谢谢你啦！能有你这样的帮手，是%s的幸运呀～");

        add("dialogs.terra_entity.old_man.0", "如果你不解除我的诅咒，我是不会让你进的。");
        add("dialogs.terra_entity.old_man.1", "陌生人，你是否拥有能打败我主人的力量？");
        add("dialogs.terra_entity.old_man.2", "打败我的主人，我就让你进入地牢。");
        add("dialogs.terra_entity.old_man.3", "你要想进去的话就晚上再来。");

        add("dialogs.terra_entity.traveling_merchant.0", "嗯，看上去你会使用天使雕像！他们切片，又切丁，让一切都如此美好！");
        add("dialogs.terra_entity.traveling_merchant.1", "我不会因“买家后悔……”或者任何其它原因退款，绝不退。");
        add("dialogs.terra_entity.traveling_merchant.2", "现在购买还包邮！");
        add("dialogs.terra_entity.traveling_merchant.3", "我卖的商品来自可能根本就不存在的地方！");
        add("dialogs.terra_entity.traveling_merchant.4", "你想要两文钱！？一文钱就成交。");
        add("dialogs.terra_entity.traveling_merchant.5", "既能抽水烟，也能煮咖啡！还能炸切丝薯条！");
        add("dialogs.terra_entity.traveling_merchant.6", "看一看，瞧一瞧！一斤重的鱼！新鲜味美！一斤重的鱼！");
        add("dialogs.terra_entity.traveling_merchant.7", "如果你是在找垃圾，那就来错地方了。");
        add("dialogs.terra_entity.traveling_merchant.8", "旧货店？不，我只卖市场上的尖货。");

        add("dialogs.terra_entity.mechanic.0", "你确定你的设备插好电源了？");
        add("dialogs.terra_entity.mechanic.1", "哦，你知道这个房屋需要什么？需要更多的闪光信号灯。");
        add("dialogs.terra_entity.mechanic.2", "别动。我的隐形眼镜掉了。");
        add("dialogs.terra_entity.mechanic.3", "谢谢！迟早有一天，我的结局也会和地牢里的其他骷髅一样。");
        add("dialogs.terra_entity.mechanic.4", "我记不太清楚里面发生过什么了，三个还是四个很重要的东西...");
        add("dialogs.terra_entity.mechanic.5", "噢是的，信号适配器！它可以很好地把这里的红石和电线连接起来。");

        add("dialogs.terra_entity.witch_doctor.0", "我是啥医？我是巫医。");
        add("dialogs.terra_entity.witch_doctor.1", "认真选，我的商品不稳定，我的黑魔法很神秘。");
        add("dialogs.terra_entity.witch_doctor.2", "魔法的心脏是本质。心脏的本质是魔法。");
        add("dialogs.terra_entity.witch_doctor.3", "我感觉与埃特尼亚黑暗魔法师志趣相投。可惜他们是我们的敌人，我本来希望向他们学习来着。");

        add("dialogs.terra_entity.clothier.0", "再次感谢你帮我解了诅咒。感觉像是什么东西跳起来咬了我一口。");
        add("dialogs.terra_entity.clothier.1", "妈妈总是说我会成为一位伟大的裁缝。");
        add("dialogs.terra_entity.clothier.2", "生活就像一箱衣服；你永远也不知道自己要穿什么！");
        add("dialogs.terra_entity.clothier.3", "刺绣当然难了！如果不难，就没人绣了！所以刺绣是件难能可贵的事。");
        add("dialogs.terra_entity.clothier.4", "他们想了解服装行业，而我无所不知。");
        add("dialogs.terra_entity.clothier.5", "被诅咒后很孤独，于是我用皮革制作了一个朋友。我叫他威尔森。");
        add("dialogs.terra_entity.clothier.6", "我依稀记得把一个女人捆了起来，然后扔到了地牢里。");

        add("dialogs.terra_entity.party_girl.0", "我们得谈谈。这……聚会的事。");
        add("dialogs.terra_entity.party_girl.1", "我不知道我是更喜欢派对还是余兴派对。");
        add("dialogs.terra_entity.party_girl.2", "我们应该办一个闪耀根派对，而且我们还应该办一个余兴派对。");
        add("dialogs.terra_entity.party_girl.3", "装个迪斯科球，我会让你知道怎么开派对。");
        add("dialogs.terra_entity.party_girl.4", "我去过一次瑞典，他们经常开派对，你怎么和他们不一样？");
        add("dialogs.terra_entity.party_girl.5", "我叫派对女孩，但人们叫我派对扫把星。我也搞不懂为啥这样叫我，但听起来酷酷的。");
        add("dialogs.terra_entity.party_girl.6", "你开派对吗？有时开？好吧，那我们谈谈……");

        add("dialogs.terra_entity.zoologist.0", "我以前也像你一样养小动物，后来被一只受诅咒的狐狸咬到了膝盖！");
        add("dialogs.terra_entity.zoologist.1", "我可能学识浅薄……但有关自然、小动物、动物和野生动物之类的话题，我可以给你说上三天三夜……");
        add("dialogs.terra_entity.zoologist.2", "我老哥叫我兽化人。意思是我有一半像动物什么的。不过他都懂，毕竟他常年在外！");
        add("dialogs.terra_entity.zoologist.3", "我非常喜欢动物！有一次我试着抚摸这只长相奇特的狐狸，他竟然咬了我一口，现在我变成了一只动物！酷毙了！");
        add("dialogs.terra_entity.zoologist.4", "别拉扯我的尾巴了，兄弟，是货真价实的……这样拉扯，我很疼的！");
        add("dialogs.terra_entity.zoologist.5", "噢，这两只大耳朵吗？哈哈，是用来听你的，宝贝！");
        add("dialogs.terra_entity.zoologist.6", "这次是在小动物营地，有天早上我醒来，发现一切全变样了！发生这么大的事情，我竟然还睡得着？！");
        add("dialogs.terra_entity.zoologist.7", "哇，我好像从来没见过满月。不知道为什么，一要出现满月我就会昏倒！");
        add("dialogs.terra_entity.zoologist.8", "我不知道我是怎么来到这里的，但这感觉太棒了。");

        add("dialogs.terra_entity.truffle.0", "生活在地下已经够惨的了，像你这样的败类还要趁我睡觉来偷我的孩子。");
        add("dialogs.terra_entity.truffle.1", "有一天，我试着舔了舔自己，看看会发生什么大不了的事，然后全身都开始发蓝光。");
        add("dialogs.terra_entity.truffle.2", "每次看到蓝色，我都感到郁闷和懒散。");
        add("dialogs.terra_entity.truffle.3", "你在这附近看到过猪吗？我弟弟的一条腿被猪叼走了。");
        add("dialogs.terra_entity.truffle.4", "我不知道什么是“肚皮波浪”，所以别问了！");
        add("dialogs.terra_entity.truffle.5", "有个关于我的谣言正在盛传：“如果打不过他，那就吃掉他！");
        add("dialogs.terra_entity.truffle.6", "我感觉这里有更多同类...");

        add("mood.terra_entity.goblin_tinkerer.like.dye_trader", "染料商知道把东西混在一起是多么有趣，我能理解！");
        add("mood.terra_entity.goblin_tinkerer.love.mechanic", "机械师让我像失了魂似的，心神不宁，不过我喜欢这种感觉！");
        add("mood.terra_entity.goblin_tinkerer.dislike.clothier", "我从服装商身上发现了怪异之处，仿佛他们掌握了黑暗秘密。我不喜欢这种感觉。");
        add("mood.terra_entity.guide.hate.painter", "我讨厌油漆工在附近。世界本来挺美好的！");
        add("mood.terra_entity.guide.like.clothier", "我很喜欢服装商，我们有很多共同点。");
        add("mood.terra_entity.guide.like.zoologist", "我很喜欢动物学家，我们有很多共同点。");
        add("mood.terra_entity.arms_dealer.hate.demolitionist", "爆破专家怎么回事啊？难道他没发现我们卖的东西完全不同？");
        add("mood.terra_entity.arms_dealer.love.nurse", "那啥，你觉得护士对我有意思吗");
        add("mood.terra_entity.angler.like.demolitionist", "爆破专家其实知道他们在做什么，不像某些其他人！我挺喜欢的！");
        add("mood.terra_entity.angler.like.party_girl", "派对女孩其实知道他们在做什么，不像某些其他人！我挺喜欢的！");
        add("mood.terra_entity.female_angler.dislike.demolitionist", "爆破专家太吵了！都没人来看我的鱼了！");
        add("mood.terra_entity.female_angler.dislike.party_girl", "派对女孩在派对上经常抢我的风头，没人看我的鱼！");
        add("mood.terra_entity.dye_trader.like.arms_dealer", "军火商善于发现鲜艳的颜色和商机，对吧？我喜欢。");
        add("mood.terra_entity.dye_trader.like.painter", "油漆工善于发现鲜艳的颜色和商机，对吧？我喜欢。”");
        add("mood.terra_entity.demolitionist.dislike.arms_dealer", "我想把军火商绑到火箭上，看看会发生什么！");
        add("mood.terra_entity.demolitionist.dislike.goblin_tinkerer", "我想把哥布林工匠绑到火箭上，看看会发生什么！");
        add("mood.terra_entity.demolitionist.like.mechanic", "机械师是我的好朋友，还帮我装火药！");
        add("mood.terra_entity.painter.love.dryad", "我真的很想画树妖……当然是因为色彩鲜艳！");
        add("mood.terra_entity.painter.like.party_girl", "派对女孩和我喜欢同样色度的粉红色！在我这里，这就是朋友！");
        add("mood.terra_entity.painter.dislike.truffle", "松露人太无趣了，我不喜欢和无趣的人打交道。");
        add("mood.terra_entity.dryad.dislike.angler", "我不喜欢渔夫不尊重其他生物。");
        add("mood.terra_entity.dryad.like.female_angler", "她捕捉那些鱼是为了更好地研究自然而且尽力不伤害它们，这是好事");
        add("mood.terra_entity.dryad.like.witch_doctor", "我喜欢巫医能与我产生强烈共鸣的感觉！");
        add("mood.terra_entity.dryad.like.truffle", "我喜欢松露人能与我产生强烈共鸣的感觉！");
        add("mood.terra_entity.merchant.like.nurse", "护士赚了很多钱，我喜欢有钱人。");
        add("mood.terra_entity.merchant.hate.angler", "渔夫性情乖戾，真讨人厌！");
        add("mood.terra_entity.merchant.like.female_angler", "这个渔夫只是徒有其表，实际上人挺好！");
        add("mood.terra_entity.nurse.love.arms_dealer", "什么？军火商？我才没有暗恋他！我没有！闭嘴！");
        add("mood.terra_entity.nurse.dislike.dryad", "我不太喜欢树妖，这个人有点奇怪。");
        add("mood.terra_entity.nurse.dislike.party_girl", "我不太喜欢派对女孩，这个人有点奇怪。");
        add("mood.terra_entity.nurse.hate.zoologist", "噢，我讨厌治疗动物学家，太难了！");
        add("mood.terra_entity.truffle.love.guide", "我非常喜欢向导，因为能好好和我说话，而不会莫名其妙地就想吃我。");
        add("mood.terra_entity.truffle.like.dye_trader", "树妖对我很尊重，把我当作大自然的一部分。除了喜欢，我不知道该如何形容这种感觉。");
        add("mood.terra_entity.truffle.dislike.clothier", "服装商很多次想吃掉我。我发誓，有一次他们甚至毫无人性！很显然，我不喜欢这样。");
        add("mood.terra_entity.truffle.hate.witch_doctor", "巫医曾试图把我扔进一个锅里，里头装着其他不寻常的原料。我讨厌这样。");
        add("mood.terra_entity.clothier.love.truffle", "松露人？我从未吃过这么好吃的东西。");
        add("mood.terra_entity.clothier.dislike.nurse", "不知什么原因，和护士在一起会让我感到不安。");
        add("mood.terra_entity.clothier.hate.mechanic", "我讨厌机械师，我也不知道为什么。");
        add("mood.terra_entity.party_girl.dislike.merchant", "我觉得商人在派对上很扫兴。");
        add("mood.terra_entity.party_girl.love.zoologist", "我喜欢动物学家在我的派对上总是光彩夺目。");
        add("mood.terra_entity.witch_doctor.like.dryad", "树妖是志同道合的大自然精灵，有他们在，我的心灵就能得到安宁。");
        add("mood.terra_entity.witch_doctor.like.guide", "向导是志同道合的大自然精灵，有他们在，我的心灵就能得到安宁。");
        add("mood.terra_entity.witch_doctor.dislike.nurse", "我不喜欢护士的做法。用金属和玻璃是做不到真正地治愈的。");
        add("mood.terra_entity.witch_doctor.hate.truffle", "愤怒侵占了我的身体，就像邪恶的种子从污秽的大地中萌芽——我说的是松露人。");
        add("mood.terra_entity.mechanic.love.goblin_tinkerer", "嗯……哥布林工匠让我心跳加速，我要去检查一下！");
        add("mood.terra_entity.mechanic.dislike.arms_dealer", "我真的不喜欢军火商对我的纠缠！");
        add("mood.terra_entity.mechanic.hate.clothier", "我讨厌服装商，他不懂得该如何对待女人！");
        add("mood.terra_entity.zoologist.love.witch_doctor", "我也说不清，但我就是非常喜欢巫医。是因为它的尾巴吗？");
        add("mood.terra_entity.zoologist.dislike.angler", "我不喜欢残酷的渔夫！");
        add("mood.terra_entity.zoologist.like.female_angler", "她并没有真正伤害它们。");
        add("mood.terra_entity.zoologist.hate.arms_dealer", "我真的非常讨厌军火商对动物所做的事情！");


    }
}
