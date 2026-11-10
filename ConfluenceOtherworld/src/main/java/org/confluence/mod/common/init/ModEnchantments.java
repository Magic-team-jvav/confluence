package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.enchantment.SummonItemEffect;
import org.confluence.mod.common.enchantment.WindBurstEnchantments;

import java.util.List;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> MANA_REGENERATION = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_regeneration");
    public static final ResourceKey<Enchantment> EFFICIENT_MAGIC = Confluence.asResourceKey(Registries.ENCHANTMENT, "efficient_magic");
    public static final ResourceKey<Enchantment> MANA_MENDING = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_mending");
    public static final ResourceKey<Enchantment> CELESTIAL_ABSORPTION = Confluence.asResourceKey(Registries.ENCHANTMENT, "celestial_absorption");
    public static final ResourceKey<Enchantment> SOOTHED_MANA = Confluence.asResourceKey(Registries.ENCHANTMENT, "soothed_mana");
    public static final ResourceKey<Enchantment> ARCANE_PROTECTION = Confluence.asResourceKey(Registries.ENCHANTMENT, "arcane_protection");
    public static final ResourceKey<Enchantment> SPELL_DESPERATION = Confluence.asResourceKey(Registries.ENCHANTMENT, "spell_desperation");
    public static final ResourceKey<Enchantment> MYSTIC_SURGE = Confluence.asResourceKey(Registries.ENCHANTMENT, "mystic_surge");
    public static final ResourceKey<Enchantment> WHIP_SWEEP = Confluence.asResourceKey(Registries.ENCHANTMENT, "whip_sweep");
    public static final ResourceKey<Enchantment> SUMMONER_PACT = Confluence.asResourceKey(Registries.ENCHANTMENT, "summoner_pact");

    /** 风暴锤 — 连枷专属风爆附魔，仅实体命中触发 */
    public static final ResourceKey<Enchantment> FLAIL_WIND_BURST = Confluence.asResourceKey(Registries.ENCHANTMENT, "flail_wind_burst");
    /** 涡轮 — 连枷专属附魔，挥舞越久伤害和速度越高 */
    public static final ResourceKey<Enchantment> FLAIL_TURBINE = Confluence.asResourceKey(Registries.ENCHANTMENT, "flail_turbine");

    public static final ResourceKey<Enchantment> BEAST_LORD = Confluence.asResourceKey(Registries.ENCHANTMENT, "beast_lord");

    public static final ResourceKey<Enchantment> ARTIFACT_COOLDOWN = Confluence.asResourceKey(Registries.ENCHANTMENT, "artifact_cooldown");

    // region 盔甲附魔

    /// 食物储备：使用治疗药水获得随机食物。
    public static final ResourceKey<Enchantment> FOOD_RESERVE = Confluence.asResourceKey(Registries.ENCHANTMENT, "food_reserve");
    /// 莽撞：不提供防御，护甲值转化为近战伤害。
    public static final ResourceKey<Enchantment> RECKLESS = Confluence.asResourceKey(Registries.ENCHANTMENT, "reckless");
    /// 灵魂疏导：收集灵魂时回复生命。
    public static final ResourceKey<Enchantment> SOUL_CONDUIT = Confluence.asResourceKey(Registries.ENCHANTMENT, "soul_conduit");
    /// 束魂：提升生物死亡时的灵魂获取概率。
    public static final ResourceKey<Enchantment> SOUL_BIND = Confluence.asResourceKey(Registries.ENCHANTMENT, "soul_bind");
    /// 万兽鼓舞：使用治疗药水时提升召唤物伤害。
    public static final ResourceKey<Enchantment> BEAST_INSPIRE = Confluence.asResourceKey(Registries.ENCHANTMENT, "beast_inspire");
    /// 偏转：概率闪避投射物。
    public static final ResourceKey<Enchantment> DEFLECT = Confluence.asResourceKey(Registries.ENCHANTMENT, "deflect");
    /// 药水护盾：使用治疗药水后短时间获得防御。
    public static final ResourceKey<Enchantment> POTION_SHIELD = Confluence.asResourceKey(Registries.ENCHANTMENT, "potion_shield");
    /// 舍己为人：自身受到攻击时治疗周围队友。
    public static final ResourceKey<Enchantment> SELFLESS_SACRIFICE = Confluence.asResourceKey(Registries.ENCHANTMENT, "selfless_sacrifice");
    /// 惊喜礼物：使用治疗药水时随机获得奖励。
    public static final ResourceKey<Enchantment> SURPRISE_GIFT = Confluence.asResourceKey(Registries.ENCHANTMENT, "surprise_gift");
    /// 死亡交易（护腿）：本次死亡不掉落物品，触发后移除。
    public static final ResourceKey<Enchantment> DEATH_BARGAIN = Confluence.asResourceKey(Registries.ENCHANTMENT, "death_bargain");
    /// 灵魂促行（靴子）：收集灵魂后短时间提升移速。
    public static final ResourceKey<Enchantment> SOUL_HASTE = Confluence.asResourceKey(Registries.ENCHANTMENT, "soul_haste");
    /// 速度协同（靴子）：使用法器后短时间提升移速。
    public static final ResourceKey<Enchantment> SPEED_SYNERGY = Confluence.asResourceKey(Registries.ENCHANTMENT, "speed_synergy");

    // endregion

    // region 武器附魔

    /// 法器协同：使用法器后强化下一次攻击。
    public static final ResourceKey<Enchantment> ARTIFACT_SYNERGY = Confluence.asResourceKey(Registries.ENCHANTMENT, "artifact_synergy");
    /// 致命一击：提升暴击伤害倍率。
    public static final ResourceKey<Enchantment> CRITICAL_STRIKE = Confluence.asResourceKey(Registries.ENCHANTMENT, "critical_strike");
    /// 精力充沛：跳跃/冲刺/纵向位移叠层，强化下一次攻击。
    public static final ResourceKey<Enchantment> VIGOROUS = Confluence.asResourceKey(Registries.ENCHANTMENT, "vigorous");
    /// 灵魂共振：收集灵魂后短时间提升暴击伤害。
    public static final ResourceKey<Enchantment> SOUL_RESONANCE = Confluence.asResourceKey(Registries.ENCHANTMENT, "soul_resonance");
    /// 虚空强袭：给目标叠加「虚空强袭」易伤。
    public static final ResourceKey<Enchantment> VOID_STRIKE = Confluence.asResourceKey(Registries.ENCHANTMENT, "void_strike");
    /// 回击（近战）：攻击后立刻回满攻速条。
    public static final ResourceKey<Enchantment> FLANKING_STRIKE = Confluence.asResourceKey(Registries.ENCHANTMENT, "flanking_strike");
    /// 眩晕（近战）：概率使敌人短暂失去移动与攻击能力。
    public static final ResourceKey<Enchantment> STUN = Confluence.asResourceKey(Registries.ENCHANTMENT, "stun");
    /// 灵魂虹吸（近战）：命中敌人提升灵魂获取概率。
    public static final ResourceKey<Enchantment> SOUL_SIPHON = Confluence.asResourceKey(Registries.ENCHANTMENT, "soul_siphon");
    /// 爆裂弓弦（弓/弩）：位移后自动向周围敌人发射箭矢。
    public static final ResourceKey<Enchantment> BURST_BOWSTRING = Confluence.asResourceKey(Registries.ENCHANTMENT, "burst_bowstring");
    /// 冷却射击（弓）：满蓄力射击缩短法器冷却。
    public static final ResourceKey<Enchantment> COOLDOWN_SHOT = Confluence.asResourceKey(Registries.ENCHANTMENT, "cooldown_shot");
    /// 多级蓄力（弓）：满蓄力后可继续蓄力多轮。
    public static final ResourceKey<Enchantment> MULTI_CHARGE = Confluence.asResourceKey(Registries.ENCHANTMENT, "multi_charge");
    /// 快速蓄力（弓）：减少蓄力时间。
    public static final ResourceKey<Enchantment> RAPID_CHARGE = Confluence.asResourceKey(Registries.ENCHANTMENT, "rapid_charge");
    /// 超级蓄力（弓）：满蓄力射击提升伤害。
    public static final ResourceKey<Enchantment> SUPER_CHARGE = Confluence.asResourceKey(Registries.ENCHANTMENT, "super_charge");

    // endregion

    public static final ResourceKey<Enchantment> MULTI_BOOMERANG = Confluence.asResourceKey(Registries.ENCHANTMENT, "multi_boomerang");

    public static final class EffectComponentTypes {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Confluence.MODID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_REGENERATION = registerCommon("mana_regeneration", LootContextParamSets.ENCHANTED_ENTITY);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> EFFICIENT_MAGIC = registerUnit("efficient_magic");
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_MENDING = registerCommon("mana_mending", LootContextParamSets.ENCHANTED_ITEM);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> ATTACK_DROPS_MANA = registerTargeted("attack_drops_mana", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_SICKNESS_DURATION_REDUCE = registerCommon("mana_sickness_duration_reduce", LootContextParamSets.ENCHANTED_ENTITY);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_PROTECTION = registerCommon("mana_protection", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> LESS_MANA_MORE_ATTACK = registerCommon("less_mana_more_attack", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MORE_MANA_MORE_ATTACK = registerCommon("more_mana_more_attack", LootContextParamSets.ENCHANTED_DAMAGE);
        /** 暴风锤效果组件（实体命中触发，非方块） */
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> WIND_BURST_AT_HIT = registerTargeted("wind_burst_at_hit", LootContextParamSets.ENCHANTED_DAMAGE);

        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> registerCommon(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, paramSet).listOf()).build());
        }

        private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> registerUnit(String name) {
            return TYPES.register(name, () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
        }

        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> registerTargeted(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>builder().persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, paramSet).listOf()).build());
        }
    }

    public static final class EntityEffectTypes {
        public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Confluence.MODID);

        static {
            TYPES.register("summon_item", () -> SummonItemEffect.CODEC);
            TYPES.register("wind_burst_at_hit", () -> WindBurstEnchantments.WindBurstAtHitEffect.CODEC);
        }
    }

    public static void register(IEventBus eventBus) {
        EffectComponentTypes.TYPES.register(eventBus);
        EntityEffectTypes.TYPES.register(eventBus);
    }
}
