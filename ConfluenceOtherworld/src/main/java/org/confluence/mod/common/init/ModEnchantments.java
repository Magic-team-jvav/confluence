package org.confluence.mod.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.enchantment.*;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.common.PortTags;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, Confluence.MODID);

    public static final RegistryObject<Enchantment> MANA_REGENERATION = ENCHANTMENTS.register("mana_regeneration", () -> new ManaIOEnchantment(Categories.ARMOR_N_MANA, LibEnchantmentUtils.SlotGroups.ARMOR_N_MAINHAND, 3));
    public static final RegistryObject<Enchantment> EFFICIENT_MAGIC = ENCHANTMENTS.register("efficient_magic", () -> new ManaIOEnchantment(Categories.MANA, LibEnchantmentUtils.SlotGroups.MAINHAND, 1));
    public static final RegistryObject<Enchantment> MANA_MENDING = ENCHANTMENTS.register("mana_mending", ManaMendingEnchantment::new);
    public static final RegistryObject<Enchantment> CELESTIAL_ABSORPTION = ENCHANTMENTS.register("celestial_absorption", () -> new ManaAffectiveEnchantment(2, (attacker, victim, level) -> {
        int count = LibMathUtils.multiplyInt(1, 0.1F * level, attacker.getRandom1211());
        if (count < 1) return;
        ItemEntity itemEntity = new ItemEntity(attacker.level(), victim.getX(), victim.getEyeY(), victim.getZ(), ModItems.STAR.toStack(count));
        itemEntity.setNoPickUpDelay();
        attacker.level().addFreshEntity(itemEntity);
    }));
    public static final RegistryObject<Enchantment> SOOTHED_MANA = ENCHANTMENTS.register("soothed_mana", () -> new ManaAffectiveEnchantment(2, (attacker, victim, level) -> {}));
    public static final RegistryObject<Enchantment> ARCANE_PROTECTION = ENCHANTMENTS.register("arcane_protection", ArcaneProtectionEnchantment::new);
    public static final RegistryObject<Enchantment> SPELL_DESPERATION = ENCHANTMENTS.register("spell_desperation", ManaAttackEnchantment::new);
    public static final RegistryObject<Enchantment> MYSTIC_SURGE = ENCHANTMENTS.register("mystic_surge", ManaAttackEnchantment::new);
    public static final RegistryObject<Enchantment> WHIP_SWEEP = ENCHANTMENTS.register("whip_sweep", WhipSweepEnchantment::new);
    public static final RegistryObject<Enchantment> MULTI_BOOMERANG = ENCHANTMENTS.register("multi_boomerang", MultiBoomerangEnchantment::new);
    public static final RegistryObject<Enchantment> SUMMONER_PACT = ENCHANTMENTS.register("summoner_pact", SummonerPactEnchantment::new);
    public static final RegistryObject<Enchantment> FLAIL_WIND_BURST = ENCHANTMENTS.register("flail_wind_burst", WindBurstEnchantment::new);
    public static final RegistryObject<Enchantment> FLAIL_TURBINE = ENCHANTMENTS.register("flail_turbine", TurbineEnchantment::new);

    // region 新增附魔
    // 全部走 SimpleEnchantment：只登记类别 / 槽位 / 最高等级 / 互斥，效果统一由 EnchantmentBehaviors 结算。
    // 这批附魔暂不开放附魔台（SimpleEnchantment#canApplyAtEnchantingTable 恒为 false）。

    /// 食物储备：使用治疗药水获得随机食物。
    public static final RegistryObject<Enchantment> FOOD_RESERVE = simple("food_reserve", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 1,
            oneOf("food_reserve", "surprise_gift"));
    /// 莽撞：不提供防御，护甲值转化为近战伤害。
    public static final RegistryObject<Enchantment> RECKLESS = simple("reckless", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 1);
    /// 灵魂疏导：收集灵魂时回复生命。
    public static final RegistryObject<Enchantment> SOUL_CONDUIT = simple("soul_conduit", EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 3);
    /// 束魂：提升生物死亡时的灵魂获取概率。
    public static final RegistryObject<Enchantment> SOUL_BIND = simple("soul_bind", EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 3);
    /// 万兽之王：提升召唤伤害。
    public static final RegistryObject<Enchantment> BEAST_LORD = simple("beast_lord", EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 3,
            oneOf("beast_lord", "beast_inspire"));
    /// 万兽鼓舞：使用治疗药水时提升召唤物伤害。
    public static final RegistryObject<Enchantment> BEAST_INSPIRE = simple("beast_inspire", EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 1,
            oneOf("beast_lord", "beast_inspire"));
    /// 冷却：降低法器冷却。
    public static final RegistryObject<Enchantment> ARTIFACT_COOLDOWN = simple("artifact_cooldown", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 3);
    /// 偏转：概率闪避投射物，与所有保护类附魔冲突。
    public static final RegistryObject<Enchantment> DEFLECT = simple("deflect", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 3,
            ModEnchantments::isProtectionFamily);
    /// 药水护盾：使用治疗药水后短时间获得防御。
    public static final RegistryObject<Enchantment> POTION_SHIELD = simple("potion_shield", EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST}, 3);
    /// 舍己为人：自身受到攻击时治疗周围队友。
    public static final RegistryObject<Enchantment> SELFLESS_SACRIFICE = simple("selfless_sacrifice", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 3);
    /// 惊喜礼物：使用治疗药水时随机获得奖励。
    public static final RegistryObject<Enchantment> SURPRISE_GIFT = simple("surprise_gift", EnchantmentCategory.ARMOR, LibEnchantmentUtils.SlotGroups.ARMOR, 1,
            oneOf("food_reserve", "surprise_gift"));
    /// 死亡交易：本次死亡不掉落物品，触发后移除，与保护类附魔冲突。
    public static final RegistryObject<Enchantment> DEATH_BARGAIN = simple("death_bargain", EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS}, 1,
            ModEnchantments::isProtectionFamily);
    /// 灵魂促行：收集灵魂后短时间提升移速。
    public static final RegistryObject<Enchantment> SOUL_HASTE = simple("soul_haste", EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 3);
    /// 速度协同：使用法器后短时间提升移速。
    public static final RegistryObject<Enchantment> SPEED_SYNERGY = simple("speed_synergy", EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET}, 3);

    /// 法器协同：使用法器后强化下一次攻击。
    public static final RegistryObject<Enchantment> ARTIFACT_SYNERGY = simple("artifact_synergy", Categories.GENERAL_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 致命一击：提升暴击伤害倍率。
    public static final RegistryObject<Enchantment> CRITICAL_STRIKE = simple("critical_strike", Categories.GENERAL_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 精力充沛：位移叠层强化下一次攻击。
    public static final RegistryObject<Enchantment> VIGOROUS = simple("vigorous", Categories.GENERAL_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 灵魂共振：收集灵魂后短时间提升暴击伤害。
    public static final RegistryObject<Enchantment> SOUL_RESONANCE = simple("soul_resonance", Categories.GENERAL_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 虚空强袭：给目标叠加易伤。
    public static final RegistryObject<Enchantment> VOID_STRIKE = simple("void_strike", Categories.GENERAL_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 回击：攻击后立刻回满攻速条。
    public static final RegistryObject<Enchantment> FLANKING_STRIKE = simple("flanking_strike", Categories.MELEE_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 眩晕：概率使敌人短暂失去移动与攻击能力。
    public static final RegistryObject<Enchantment> STUN = simple("stun", Categories.MELEE_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 灵魂虹吸：命中敌人提升灵魂获取概率。
    public static final RegistryObject<Enchantment> SOUL_SIPHON = simple("soul_siphon", Categories.MELEE_WEAPON, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);

    /// 爆裂弓弦：位移后自动向周围敌人发射箭矢。
    public static final RegistryObject<Enchantment> BURST_BOWSTRING = simple("burst_bowstring", Categories.BOW_OR_CROSSBOW, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 冷却射击：满蓄力射击缩短法器冷却。
    public static final RegistryObject<Enchantment> COOLDOWN_SHOT = simple("cooldown_shot", EnchantmentCategory.BOW, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 多级蓄力：满蓄力后可继续蓄力多轮。
    public static final RegistryObject<Enchantment> MULTI_CHARGE = simple("multi_charge", EnchantmentCategory.BOW, LibEnchantmentUtils.SlotGroups.MAINHAND, 3,
            oneOf("multi_charge", "super_charge"));
    /// 快速蓄力：减少蓄力时间。
    public static final RegistryObject<Enchantment> RAPID_CHARGE = simple("rapid_charge", EnchantmentCategory.BOW, LibEnchantmentUtils.SlotGroups.MAINHAND, 3);
    /// 超级蓄力：满蓄力射击提升伤害。
    public static final RegistryObject<Enchantment> SUPER_CHARGE = simple("super_charge", EnchantmentCategory.BOW, LibEnchantmentUtils.SlotGroups.MAINHAND, 3,
            oneOf("multi_charge", "super_charge"));
    // endregion

    /// 按注册名判定互斥：用名字而不是 RegistryObject 常量，避免静态初始化期的自引用/前向引用。
    private static java.util.function.Predicate<Enchantment> oneOf(String... paths) {
        return other -> {
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(other);
            if (id == null) return false;
            for (String path : paths) {
                if (id.getPath().equals(path) && Confluence.MODID.equals(id.getNamespace())) {
                    return true;
                }
            }
            return false;
        };
    }

    private static RegistryObject<Enchantment> simple(String name, EnchantmentCategory category, EquipmentSlot[] slots, int maxLevel) {
        return ENCHANTMENTS.register(name, () -> new SimpleEnchantment(Enchantment.Rarity.VERY_RARE, category, slots, maxLevel));
    }

    private static RegistryObject<Enchantment> simple(String name, EnchantmentCategory category, EquipmentSlot[] slots, int maxLevel, java.util.function.Predicate<Enchantment> incompatible) {
        return ENCHANTMENTS.register(name, () -> new SimpleEnchantment(Enchantment.Rarity.VERY_RARE, category, slots, maxLevel, incompatible));
    }

    /// 保护类附魔：原版四种保护 + 本模组的奥术防护。
    private static boolean isProtectionFamily(Enchantment other) {
        return other instanceof ProtectionEnchantment || other instanceof ArcaneProtectionEnchantment;
    }


    @SuppressWarnings("deprecation")
    public static class Categories {
        public static final EnchantmentCategory ARMOR_N_MANA = EnchantmentCategory.create("CONFLUENCE_ARMOR_N_MANA", item -> {
            Holder<Item> holder = item.builtInRegistryHolder();
            return holder.is(PortTags.Items.ARMORS) || holder.is(ModTags.Items.MANA_WEAPON);
        });
        public static final EnchantmentCategory MANA = EnchantmentCategory.create("CONFLUENCE_MANA", item -> item.builtInRegistryHolder().is(ModTags.Items.MANA_WEAPON));
        public static final EnchantmentCategory WHIP = EnchantmentCategory.create("CONFLUENCE_WHIP", item -> item.builtInRegistryHolder().is(ModTags.Items.WHIP));
        public static final EnchantmentCategory BOOMERANG = EnchantmentCategory.create("CONFLUENCE_BOOMERANG", item -> item.builtInRegistryHolder().is(ModTags.Items.BOOMERANG));
        public static final EnchantmentCategory FLAIL = EnchantmentCategory.create("CONFLUENCE_FLAIL", item -> item.builtInRegistryHolder().is(ModTags.Items.FLAIL));
        /// 通用武器：任意武器类型都能吃到的附魔。
        public static final EnchantmentCategory GENERAL_WEAPON = EnchantmentCategory.create("CONFLUENCE_GENERAL_WEAPON", item -> item.builtInRegistryHolder().is(ModTags.Items.GENERAL_WEAPON_ENCHANTABLE));
        /// 近战武器：剑 / 斧 / 长矛 / 悠悠球 / 回旋镖 / 连枷 / 鞭子。
        public static final EnchantmentCategory MELEE_WEAPON = EnchantmentCategory.create("CONFLUENCE_MELEE_WEAPON", item -> item.builtInRegistryHolder().is(ModTags.Items.MELEE_WEAPON_ENCHANTABLE));
        /// 弓或弩。
        public static final EnchantmentCategory BOW_OR_CROSSBOW = EnchantmentCategory.create("CONFLUENCE_BOW_OR_CROSSBOW", item -> EnchantmentCategory.BOW.canEnchant(item) || EnchantmentCategory.CROSSBOW.canEnchant(item));
    }
}
