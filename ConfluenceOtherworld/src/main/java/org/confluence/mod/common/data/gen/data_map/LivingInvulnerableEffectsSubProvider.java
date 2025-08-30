package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Objects;

public final class LivingInvulnerableEffectsSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender, HolderLookup.Provider provider) {
        appender.create()
                .add(TEMonsterEntities.ANGER_BONES, MobEffects.POISON)
                .add(TEMonsterEntities.SHORT_BONES, MobEffects.POISON)
                .add(TEMonsterEntities.BIG_BONES, MobEffects.POISON)
                .add(TEMonsterEntities.BIG_ANGER_BONES, MobEffects.POISON)
                .add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, MobEffects.POISON)
                .add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, MobEffects.POISON)
                // TODO 歪尾真菌 蚁狮 蚁狮马/巨型蚁狮马 蚁狮幼虫 蚁狮蜂/巨型蚁狮蜂
                .add(TEMonsterEntities.LITTLE_HORNET, MobEffects.POISON, TCEffects.CONFUSED)
                .add(TEMonsterEntities.BLACK_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.BLOOD_CRAWLER, MobEffects.POISON)
                .add(TEMonsterEntities.BLUE_JELLYFISH, TCEffects.CONFUSED)
                .add(TEMonsterEntities.PINK_JELLYFISH, TCEffects.CONFUSED)
                .add(TEMonsterEntities.GREEN_JELLYFISH, TCEffects.CONFUSED)
                // TODO 血水母 蘑菇鱼
                .add(TEMonsterEntities.BLUE_SLIME, MobEffects.POISON)
                // TODO 骨蛇 胭脂虫 螃蟹 龙虾
                .add(TEMonsterEntities.CRIMSON_KEMERA, TCEffects.CONFUSED)
                .add(TEMonsterEntities.CURSED_SKULL, MobEffects.POISON, TCEffects.CONFUSED)
                .add(TEBossEntities.DUNGEON_GUARDIAN, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL)
                .add(TEMonsterEntities.DUNGEON_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.EATER_OF_SOULS, TCEffects.CONFUSED)
                .add(TEMonsterEntities.FACE_MONSTER, MobEffects.POISON)
                .add(TEMonsterEntities.FIRE_IMP, TCEffects.CONFUSED, TEEffects.HELLFIRE)
                // TODO 冰冻僵尸 真菌球怪
                .add(TEMonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, TCEffects.CONFUSED, ModEffects.ICHOR, MobEffects.POISON, TEEffects.FROST_BURN, TEEffects.HELLFIRE) //TODO 涂油 破晓
                .add(TEMonsterEntities.GIANT_SHELLY, TCEffects.CONFUSED)
                .add(TEMonsterEntities.GIANT_WORM, TCEffects.CONFUSED)
                // TODO 侏儒
                .add(TEMonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, TCEffects.CONFUSED, TEEffects.HELLFIRE)
                // TODO 花岗岩巨人
                .add(TEMonsterEntities.GREEN_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.HARPY, MobEffects.POISON)
                .add(TEMonsterEntities.HELL_BAT, TEEffects.HELLFIRE)
                // TODO 装甲步兵
                .add(TEMonsterEntities.HORNET, MobEffects.POISON, TCEffects.CONFUSED)
                .add(TEMonsterEntities.ICE_BAT, TEEffects.FROST_BURN, ModEffects.FROSTBITE)
                .add(TEMonsterEntities.ICE_SLIME, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)
                .add(TEMonsterEntities.JUNGLE_SLIME, MobEffects.POISON)
                // TODO 紫胶虫
                .add(TEMonsterEntities.LAVA_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.MAN_EATER, MobEffects.POISON, TCEffects.CONFUSED)
                // TODO 流星头
                .add(TEMonsterEntities.PINK_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.PIRANHA, TCEffects.CONFUSED)
                .add(TEMonsterEntities.PURPLE_SLIME, MobEffects.POISON)
                // TODO 乌鸦
                .add(TEMonsterEntities.DESERT_SLIME, MobEffects.POISON)
                // TODO 海蜗牛 鲨鱼 骷髅全家桶
                .add(TEMonsterEntities.SNATCHER, TCEffects.CONFUSED)
                .add(TEMonsterEntities.SNOW_FLINX, TEEffects.FROST_BURN, ModEffects.FROSTBITE)
                // TODO 尖刺冰雪史莱姆 尖刺丛林史莱姆
                .add(TEMonsterEntities.SPORE_SKELETON, MobEffects.POISON)
                // TODO 乌贼 蒂姆
                .add(TEMonsterEntities.TOMB_CRAWLER, TCEffects.CONFUSED)
                // TODO 不死矿工
                .add(TEMonsterEntities.UNDEAD_VIKING, TEEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)
                .add(TEMonsterEntities.VOODOO_DEMON, TCEffects.CONFUSED, ModEffects.SHADOWFLAME, TEEffects.HELLFIRE)
                // TODO 秃鹰 爬墙蜘蛛
                .add(TEMonsterEntities.YELLOW_SLIME, MobEffects.POISON)
                // TODO 琵琶鱼 愤怒捕手 巨骨舌鱼 装甲骷髅 装甲维京海盗 黑隐士 嗜血怪 拜月教忠教徒 邪教徒弓箭手 蓝装甲骷髅 骷髅李 混沌精 爬藤怪 宝箱怪 冰雪宝箱怪 腐化宝箱怪 猩红宝箱怪 神圣宝箱怪 丛林宝箱怪
                .add(TEMonsterEntities.CORRUPT_SLIME, MobEffects.POISON)
                // TODO 腐化者
                .add(TEMonsterEntities.CRIMSLIME, MobEffects.POISON)
                /*
                TODO 猩红斧 诅咒锤 跳跳兽 沙漠幽魂 魔教徒 挖掘怪 沙虫 附魔剑 恶心浮游怪 腹足怪 巨型诅咒骷髅头 巨型真菌球怪 地狱装甲骷髅 蹦蹦兽
                 弹跳杰克南瓜灯 冰雪精 冰雪陆龟 灵液黏黏怪 冰雪人鱼 夜明蝙蝠 夜明史莱姆 丛林蜘蛛 熔岩蝙蝠 丛林蜥蜴 火星探测器 蛇发女妖 苔藓黄蜂 蛾
                  圣骑士 装甲幻影魔 褴褛邪教徒法师 红魔鬼 岩石巨人 符文巫师 生锈装甲骷髅 沙贼 骷髅弓箭手 骷髅突击手 骷髅狙击手 小史莱姆 恶翅史莱姆
                   恶翅史莱姆(无翅膀) 骷髅特警 毒泥 吞世怪
                 */
                .add(TEMonsterEntities.WYVERN, TCEffects.CONFUSED)
                .add(TEMonsterEntities.GREEN_DUMPLING_SLIME, MobEffects.POISON)
                .add(TEMonsterEntities.GOLDEN_SLIME, ModEffects.SHIMMER)
                //boss
                .add(TEBossEntities.BRAIN_OF_CTHULHU, TCEffects.CONFUSED)
                .add(TEBossEntities.EATER_OF_WORLDS, TCEffects.CONFUSED)
                .add(TEBossEntities.EATER_OF_WORLDS_SEGMENT, TCEffects.CONFUSED)
                .add(TEBossEntities.EYE_OF_CTHULHU, TCEffects.CONFUSED)
                .add(TEBossEntities.KING_SLIME, ModEffects.SHIMMER, TCEffects.CONFUSED, MobEffects.POISON)
                .add(TEBossEntities.QUEEN_BEE, MobEffects.POISON, TCEffects.CONFUSED)
                .add(TEBossEntities.HILL_OF_FLESH, TCEffects.CONFUSED, TEEffects.HELLFIRE)
                .add(TEBossEntities.WALL_OF_FLESH, TCEffects.CONFUSED, TEEffects.HELLFIRE)
                //boss servant
                .add(TEMonsterEntities.VISUAL_NEURON, TCEffects.CONFUSED)
                .add(TEMonsterEntities.LEECH, TCEffects.CONFUSED)
                .add(TEMonsterEntities.SERVANT_OF_CTHULHU, ModEffects.SHIMMER, TCEffects.CONFUSED)
                .add(TEMonsterEntities.THE_HUNGRY, ModEffects.SHIMMER, TCEffects.CONFUSED)
        ;
    }

    public static class Builder extends DataMapProvider.Builder<LivingInvulnerableEffects, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, HolderSet<MobEffect> effects, LivingInvulnerableEffects.Category... categories) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(effects, categories), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(IHolderExtension<EntityType<?>> holder, Holder<MobEffect>... effects) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }

        public Builder add(TagKey<EntityType<?>> tagKey, HolderSet<MobEffect> effects, LivingInvulnerableEffects.Category... categories) {
            super.add(tagKey, new LivingInvulnerableEffects(effects, categories), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(TagKey<EntityType<?>> tagKey, Holder<MobEffect>... effects) {
            super.add(tagKey, new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }
    }
}
