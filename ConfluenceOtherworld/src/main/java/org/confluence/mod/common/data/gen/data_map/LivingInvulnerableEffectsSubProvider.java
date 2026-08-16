package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.terra_curio.common.init.TCEffects;
import org.mesdag.portlib.datamap.PortDataMapProvider;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public final class LivingInvulnerableEffectsSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender, HolderLookup.Provider provider) {
        MobEffect[] poison_confused_hellfire = {MobEffects.POISON, TCEffects.CONFUSED.get(), ModEffects.HELLFIRE.get()};
        Supplier<MobEffect> poison = () -> MobEffects.POISON;
        appender.create()
                .add(MonsterEntities.ANGER_BONES, MobEffects.POISON)
                .add(MonsterEntities.SHORT_BONES, MobEffects.POISON)
                .add(MonsterEntities.BIG_BONES, MobEffects.POISON)
                .add(MonsterEntities.BIG_ANGER_BONES, MobEffects.POISON)
                .add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, MobEffects.POISON)
                .add(MonsterEntities.BIG_HELMET_ANGER_BONES, MobEffects.POISON)
                // TODO 歪尾真菌 蚁狮 蚁狮马/巨型蚁狮马 蚁狮幼虫
                .add(MonsterEntities.LITTLE_HORNET, MobEffects.POISON, TCEffects.CONFUSED.get())
                .add(MonsterEntities.BLACK_SLIME, MobEffects.POISON)
                .add(MonsterEntities.BLOOD_CRAWLER, MobEffects.POISON)
                .add(MonsterEntities.BLUE_JELLYFISH, TCEffects.CONFUSED)
                .add(MonsterEntities.PINK_JELLYFISH, TCEffects.CONFUSED)
                .add(MonsterEntities.GREEN_JELLYFISH, TCEffects.CONFUSED)
                // TODO 血水母 蘑菇鱼
                .add(MonsterEntities.BLUE_SLIME, MobEffects.POISON)
                .add(MonsterEntities.BONE_SERPENT, MobEffects.POISON, TCEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())
                .add(MonsterEntities.WITHER_BONE_SERPENT, MobEffects.POISON, MobEffects.WITHER, TCEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())
                // TODO 胭脂虫 螃蟹
                .add(MonsterEntities.CRIMERA, TCEffects.CONFUSED)
                .add(MonsterEntities.CRAWDAD, TCEffects.CONFUSED)
                .add(CritterEntities.CRAB, TCEffects.CONFUSED)
                .add(MonsterEntities.CURSED_SKULL, MobEffects.POISON, TCEffects.CONFUSED.get())
                .add(BossEntities.DUNGEON_GUARDIAN, new AnyHolderSet<>(provider.lookupOrThrow(Registries.MOB_EFFECT)), LivingInvulnerableEffects.Category.HARMFUL)
                .add(MonsterEntities.DUNGEON_SLIME, MobEffects.POISON)
                .add(MonsterEntities.EATER_OF_SOULS, TCEffects.CONFUSED)
                .add(MonsterEntities.FACE_MONSTER, MobEffects.POISON)
                .add(MonsterEntities.FIRE_IMP, TCEffects.CONFUSED, ModEffects.HELLFIRE)
                // TODO 冰冻僵尸 真菌球怪
                .add(MonsterEntities.GHOST, ModEffects.ACID_VENOM, ModEffects.FROSTBITE, ModEffects.SHADOWFLAME, TCEffects.CONFUSED, ModEffects.ICHOR, poison, ModEffects.FROST_BURN, ModEffects.HELLFIRE) //TODO 涂油 破晓
                .add(MonsterEntities.GIANT_SHELLY, TCEffects.CONFUSED)
                .add(MonsterEntities.GIANT_WORM, TCEffects.CONFUSED)
                // TODO 侏儒
                .add(MonsterEntities.GRANITE_ELEMENTAL, MobEffects.POISON, TCEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())
                // TODO 花岗岩巨人
                .add(MonsterEntities.GREEN_SLIME, MobEffects.POISON)
                .add(MonsterEntities.HARPY, MobEffects.POISON)
                .add(MonsterEntities.HELL_BAT, ModEffects.HELLFIRE)
                // TODO 装甲步兵
                .add(MonsterEntities.HORNET, MobEffects.POISON, TCEffects.CONFUSED.get())
                .add(MonsterEntities.ICE_BAT, ModEffects.FROST_BURN, ModEffects.FROSTBITE)
                .add(MonsterEntities.ICE_SLIME, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)
                .add(MonsterEntities.JUNGLE_SLIME, MobEffects.POISON)
                // TODO 紫胶虫
                .add(MonsterEntities.LAVA_SLIME, MobEffects.POISON)
                .add(MonsterEntities.MAN_EATER, MobEffects.POISON, TCEffects.CONFUSED.get())
                .add(MonsterEntities.METEOR_HEAD, MobEffects.POISON, TCEffects.CONFUSED.get(), ModEffects.HELLFIRE.get())
                .add(MonsterEntities.PINK_SLIME, MobEffects.POISON)
                .add(MonsterEntities.PIRANHA, TCEffects.CONFUSED)
                .add(MonsterEntities.PURPLE_SLIME, MobEffects.POISON)
                // TODO 乌鸦
                .add(MonsterEntities.DESERT_SLIME, MobEffects.POISON)
                .add(MonsterEntities.SHARK, TCEffects.CONFUSED)
                // TODO 海蜗牛  骷髅全家桶
                .add(MonsterEntities.SNATCHER, TCEffects.CONFUSED)
                .add(MonsterEntities.SNOW_FLINX, ModEffects.FROST_BURN, ModEffects.FROSTBITE)
                .add(MonsterEntities.SPIKED_SLIME, MobEffects.POISON, ModEffects.SHIMMER.get())
                .add(MonsterEntities.SPIKED_JUNGLE_SLIME, MobEffects.POISON)
                .add(MonsterEntities.SPIKED_ICE_SLIME, MobEffects.POISON, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get())
                .add(MonsterEntities.SPORE_SKELETON, MobEffects.POISON)
                // TODO 乌贼 蒂姆
                .add(MonsterEntities.TOMB_CRAWLER, TCEffects.CONFUSED)
                // TODO 不死矿工
                .add(MonsterEntities.UNDEAD_VIKING, ModEffects.FROST_BURN.get(), ModEffects.FROSTBITE.get(), MobEffects.POISON)
                .add(MonsterEntities.VOODOO_DEMON, TCEffects.CONFUSED, ModEffects.SHADOWFLAME, ModEffects.HELLFIRE)
                // TODO 秃鹰 爬墙蜘蛛
                .add(MonsterEntities.YELLOW_SLIME, MobEffects.POISON)
                // TODO 琵琶鱼 愤怒捕手 巨骨舌鱼 装甲骷髅 装甲维京海盗 黑隐士 嗜血怪 拜月教忠教徒 邪教徒弓箭手 蓝装甲骷髅 骷髅李 混沌精 爬藤怪
                .add(MonsterEntities.WOODEN_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.GOLDEN_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.SHADOW_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.CORRUPT_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.JUNGLE_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.CRIMSON_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.HALLOWED_MIMIC, poison_confused_hellfire)
                .add(MonsterEntities.ICE_MIMIC, poison, TCEffects.CONFUSED, ModEffects.HELLFIRE, ModEffects.FROST_BURN, ModEffects.FROSTBITE)
                .add(MonsterEntities.CORRUPT_SLIME, MobEffects.POISON)
                .add(MonsterEntities.ARAPAIMA, TCEffects.CONFUSED)
                // TODO 腐化者
                .add(MonsterEntities.CRIMSLIME, MobEffects.POISON)
                /*
                TODO 猩红斧 诅咒锤 跳跳兽 沙漠幽魂 魔教徒 挖掘怪 沙虫 附魔剑 恶心浮游怪 腹足怪 巨型诅咒骷髅头 巨型真菌球怪 地狱装甲骷髅
                 弹跳杰克南瓜灯 冰雪精 冰雪陆龟 灵液黏黏怪 冰雪人鱼 夜明蝙蝠 夜明史莱姆 丛林蜘蛛 熔岩蝙蝠 丛林蜥蜴 火星探测器 蛇发女妖 苔藓黄蜂 蛾
                  圣骑士 装甲幻影魔 褴褛邪教徒法师 红魔鬼 岩石巨人 符文巫师 生锈装甲骷髅 骷髅弓箭手 骷髅突击手 骷髅狙击手 小史莱姆 恶翅史莱姆
                   恶翅史莱姆(无翅膀) 骷髅特警 毒泥 吞世怪
                 */
                .add(MonsterEntities.WYVERN, TCEffects.CONFUSED)
                .add(MonsterEntities.GREEN_DUMPLING_SLIME, MobEffects.POISON)
                .add(MonsterEntities.GOLDEN_SLIME, ModEffects.SHIMMER)
                .add(MonsterEntities.HERPLING, TCEffects.CONFUSED)
                .add(MonsterEntities.DERPLING, TCEffects.CONFUSED)
                .add(MonsterEntities.SAND_POACHER, MobEffects.POISON)
                //boss
                .add(BossEntities.BRAIN_OF_CTHULHU, TCEffects.CONFUSED)
                .add(BossEntities.EATER_OF_WORLDS, TCEffects.CONFUSED)
//                .add(BossEntities.EATER_OF_WORLDS_SEGMENT, TCEffects.CONFUSED)
                .add(BossEntities.EYE_OF_CTHULHU, TCEffects.CONFUSED)
                .add(BossEntities.KING_SLIME, ModEffects.SHIMMER.get(), TCEffects.CONFUSED.get(), MobEffects.POISON)
                .add(BossEntities.QUEEN_BEE, MobEffects.POISON, TCEffects.CONFUSED.get())
                .add(BossEntities.DEERCLOPS, MobEffects.POISON, ModEffects.SHIMMER.get(), TCEffects.CONFUSED.get())
                .add(BossEntities.HILL_OF_FLESH, TCEffects.CONFUSED, ModEffects.HELLFIRE)
                .add(BossEntities.WALL_OF_FLESH, TCEffects.CONFUSED, ModEffects.HELLFIRE)
                .add(BossEntities.THE_TWINS, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.RETINAZER, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.SPAZMATISM, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.THE_DESTROYER, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                // 探针
                .add(BossEntities.SKELETRON_PRIME, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.SKELETRON_PRIME_ARM, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.PLANTERA, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
//                .add(BossEntities.PLANTERA_HOOK, TCEffects.CONFUSED, MobEffects.POISON, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                .add(BossEntities.PLANTERA_TENTACLE, TCEffects.CONFUSED, poison, ModEffects.BLEEDING, ModEffects.BLOOD_BUTCHERED, ModEffects.TENTACLE_SPIKES)
                //boss servant
                .add(MonsterEntities.LEECH, TCEffects.CONFUSED)
                .add(BossEntities.SERVANT_OF_CTHULHU, ModEffects.SHIMMER, TCEffects.CONFUSED)
                .add(MonsterEntities.THE_HUNGRY, ModEffects.SHIMMER, TCEffects.CONFUSED)
        ;
    }

    public static class Builder extends PortDataMapProvider.Builder<LivingInvulnerableEffects, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        }

        public Builder add(Supplier<? extends EntityType<?>> holder, HolderSet<MobEffect> effects, LivingInvulnerableEffects.Category... categories) {
            super.add(Objects.requireNonNull(holder.get().builtInRegistryHolder().getKey()), new LivingInvulnerableEffects(effects, categories), false);
            return this;
        }

        public final Builder add(Supplier<? extends EntityType<?>> holder, MobEffect... effects) {
            HolderSet<MobEffect> set = HolderSet.direct(Arrays.stream(effects).map(ForgeRegistries.MOB_EFFECTS::getDelegateOrThrow).toList());
            super.add(Objects.requireNonNull(holder.get().builtInRegistryHolder().getKey()), new LivingInvulnerableEffects(set), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(Supplier<? extends EntityType<?>> holder, Supplier<? extends MobEffect>... effects) {
            return add(holder, Arrays.stream(effects).map(Supplier::get).toArray(MobEffect[]::new));
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
