package org.confluence.mod.common.data.gen.data_map;

import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Objects;
import java.util.stream.Collectors;

public final class LivingInvulnerableEffectsSubProvider {
    static final Holder<MobEffect> POISON = MobEffects.POISON;
    static final Holder<MobEffect> CONFUSED = TCEffects.CONFUSED;
    static final Holder<MobEffect> HELLFIRE = TEEffects.HELLFIRE;
    static final Holder<MobEffect> FROST_BURN = TEEffects.FROST_BURN;
    static final Holder<MobEffect> FROSTBITE = ModEffects.FROSTBITE;
    static final Holder<MobEffect> SHADOWFLAME = ModEffects.SHADOWFLAME;
    static LivingInvulnerableEffects allHarmfulEffect = new LivingInvulnerableEffects(HolderSet.direct(BuiltInRegistries.MOB_EFFECT.stream().filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL).map(Holder::direct).collect(Collectors.toList())));

    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                .add(TEMonsterEntities.ANGER_BONES, POISON)
                .add(TEMonsterEntities.SHORT_BONES, POISON)
                .add(TEMonsterEntities.BIG_BONES, POISON)
                .add(TEMonsterEntities.BIG_ANGER_BONES, POISON)
                .add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, POISON)
                .add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, POISON)
                // TODO 歪尾真菌 蚁狮 蚁狮马/巨型蚁狮马 蚁狮幼虫 蚁狮蜂/巨型蚁狮蜂
                .add(TEMonsterEntities.LITTLE_HORNET, POISON, CONFUSED)
                .add(TEMonsterEntities.BLACK_SLIME, POISON)
                .add(TEMonsterEntities.BLOOD_CRAWLER, POISON)
                // TODO 蓝水母 粉水母 绿水母 血水母 蘑菇鱼
                .add(TEMonsterEntities.BLUE_SLIME, POISON)
                // TODO 骨蛇 胭脂虫 螃蟹 龙虾
                .add(TEMonsterEntities.CRIMSON_KEMERA, CONFUSED)
                .add(TEMonsterEntities.CURSED_SKULL, POISON, CONFUSED)
                .add(TEBossEntities.DUNGEON_GUARDIAN, allHarmfulEffect.effects())
                .add(TEMonsterEntities.DUNGEON_SLIME, POISON)
                .add(TEMonsterEntities.EATER_OF_SOULS, CONFUSED)
                .add(TEMonsterEntities.FACE_MONSTER, POISON)
                .add(TEMonsterEntities.FIRE_IMP, CONFUSED, HELLFIRE)
                // TODO 冰冻僵尸 真菌球怪 鬼魂
                .add(TEMonsterEntities.GIANT_SHELLY, CONFUSED)
                .add(TEMonsterEntities.GIANT_WORM, CONFUSED)
                // TODO 侏儒
                .add(TEMonsterEntities.GRANITE_ELEMENTAL, POISON, CONFUSED, HELLFIRE)
                // TODO 花岗岩巨人
                .add(TEMonsterEntities.GREEN_SLIME, POISON)
                .add(TEMonsterEntities.HARPY, POISON)
                .add(TEMonsterEntities.HELL_BAT, HELLFIRE)
                // TODO 装甲步兵
                .add(TEMonsterEntities.HORNET, POISON, CONFUSED)
                .add(TEMonsterEntities.ICE_BAT, FROST_BURN, FROSTBITE)
                .add(TEMonsterEntities.ICE_SLIME, FROST_BURN, FROSTBITE, POISON)
                .add(TEMonsterEntities.JUNGLE_SLIME, POISON)
                // TODO 紫胶虫
                .add(TEMonsterEntities.LAVA_SLIME, POISON)
                .add(TEMonsterEntities.MAN_EATER, POISON, CONFUSED)
                // TODO 流星头
                .add(TEMonsterEntities.PINK_SLIME, POISON)
                .add(TEMonsterEntities.PIRANHA, CONFUSED)
                .add(TEMonsterEntities.PURPLE_SLIME, POISON)
                // TODO 乌鸦
                .add(TEMonsterEntities.DESERT_SLIME, POISON)
                // TODO 海蜗牛 鲨鱼 骷髅全家桶
                .add(TEMonsterEntities.SNATCHER, CONFUSED)
                .add(TEMonsterEntities.SNOW_FLINX, FROST_BURN, FROSTBITE)
                // TODO 尖刺冰雪史莱姆 尖刺丛林史莱姆
                .add(TEMonsterEntities.SPORE_SKELETON, POISON)
                // TODO 乌贼 蒂姆
                .add(TEMonsterEntities.TOMB_CRAWLER, CONFUSED)
                // TODO 不死矿工
                .add(TEMonsterEntities.UNDEAD_VIKING, FROST_BURN, FROSTBITE, POISON)
                .add(TEMonsterEntities.VOODOO_DEMON, CONFUSED, SHADOWFLAME, HELLFIRE)
                // TODO 秃鹰 爬墙蜘蛛
                .add(TEMonsterEntities.YELLOW_SLIME, POISON)
        ;
    }

    public static class Builder extends DataMapProvider.Builder<LivingInvulnerableEffects, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, HolderSet<MobEffect> effects) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(effects), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(IHolderExtension<EntityType<?>> holder, Holder<MobEffect>... effects) {
            super.add(Objects.requireNonNull(holder.getKey()), new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }

        public Builder add(TagKey<EntityType<?>> tagKey, HolderSet<MobEffect> effects) {
            super.add(tagKey, new LivingInvulnerableEffects(effects), false);
            return this;
        }

        @SafeVarargs
        public final Builder add(TagKey<EntityType<?>> tagKey, Holder<MobEffect>... effects) {
            super.add(tagKey, new LivingInvulnerableEffects(HolderSet.direct(effects)), false);
            return this;
        }
    }
}
