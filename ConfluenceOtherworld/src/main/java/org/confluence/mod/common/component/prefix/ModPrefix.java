package org.confluence.mod.common.component.prefix;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.common.init.TCAttributes;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

@SuppressWarnings("unused")
public interface ModPrefix {
    PrefixComponent createComponent(PrefixType prefixType);

    String name();

    boolean canBeMercy();

    ResourceLocation getModifierId();

    record Accessory(String name, float armor, float criticalChance, float attackDamage, float attackSpeed, float movementSpeed, int additionalMana, int tier, float value) implements ModPrefix {
        public static final Map<String, Accessory> VALUES = ModPrefix.registerGroup("accessory");
        public static final ResourceLocation ID = Confluence.asResource("accessory_prefix");

        public static final Accessory HARD = register("hard", 1, 0, 0, 0, 0, 0, 1, 0.1025F), // 坚硬
                GUARDING = register("guarding", 2, 0, 0, 0, 0, 0, 1, 0.21F), // 守护
                ARMORED = register("armored", 3, 0, 0, 0, 0, 0, 1, 0.3225F), // 装甲
                WARDING = register("warding", 4, 0, 0, 0, 0, 0, 2, 0.44F), // 护佑
                PRECISE = register("precise", 0, 0.02F, 0, 0, 0, 0, 1, 0.21F), // 精确
                LUCKY = register("lucky", 0, 0.04F, 0, 0, 0, 0, 2, 0.44F), // 幸运
                JAGGED = register("jagged", 0, 0, 0.01F, 0, 0, 0, 1, 0.1025F), // 锯齿
                SPIKED = register("spiked", 0, 0, 0.02F, 0, 0, 0, 1, 0.21F), // 尖刺
                ANGRY = register("angry", 0, 0, 0.03F, 0, 0, 0, 1, 0.3225F), // 愤怒
                MENACING = register("menacing", 0, 0, 0.04F, 0, 0, 0, 2, 0.44F), // 险恶
                WILD = register("wild", 0, 0, 0, 0.01F, 0, 0, 1, 0.1025F), // 狂野
                RASH = register("rash", 0, 0, 0, 0.02F, 0, 0, 1, 0.21F), // 鲁莽
                INTREPID = register("intrepid", 0, 0, 0, 0.03F, 0, 0, 1, 0.3225F), // 勇猛
                VIOLENT = register("violent", 0, 0, 0, 0.04F, 0, 0, 2, 0.44F), // 暴力
                BRISK = register("brisk", 0, 0, 0, 0, 0.01F, 0, 1, 0.1025F), // 轻快
                FLEETING = register("fleeting", 0, 0, 0, 0, 0.02F, 0, 1, 0.21F), // 快速
                HASTY = register("hasty", 0, 0, 0, 0, 0.03F, 0, 1, 0.3225F), // 急速
                QUICK = register("quick", 0, 0, 0, 0, 0.04F, 0, 2, 0.44F), // 迅捷
                ARCANE = register("arcane", 0, 0, 0, 0, 0, 20, 1, 0.3225F); // 奥秘

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (armor != 0) builder.put(Attributes.ARMOR, new AttributeModifier(ID, armor, ADD_VALUE));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (attackDamage != 0.0F) builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ID, attackDamage, ADD_MULTIPLIED_TOTAL));
            if (attackSpeed != 0.0F) builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ID, attackSpeed, ADD_MULTIPLIED_TOTAL));
            if (movementSpeed != 0.0F) builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(ID, movementSpeed, ADD_MULTIPLIED_TOTAL));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), 0.0F, additionalMana, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return false;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Accessory register(String name, float armor, float criticalChance, float attackDamage, float attackSpeed, float movementSpeed, int additionalMana, int tier, float value) {
            Accessory accessory = new Accessory(name, armor / 2, criticalChance, attackDamage, attackSpeed, movementSpeed, additionalMana, tier, value);
            VALUES.put(name, accessory);
            return accessory;
        }

        private static void init() {}
    }

    record Universal(String name, float attackDamage, float criticalChance, float knockBack, int tier, float value) implements ModPrefix {
        public static final Map<String, Universal> VALUES = ModPrefix.registerGroup("universal");
        public static final ResourceLocation ID = Confluence.asResource("universal_prefix");

        public static final Universal KEEN = register("keen", 0, 0.03F, 0, 1, 0.1236F), // 锐利
                SUPERIOR = register("superior", 0.1F, 0.03F, 0.1F, 2, 0.6451F), // 高端
                FORCEFUL = register("forceful", 0, 0, 0.15F, 1, 0.3225F), // 强力
                BROKEN = register("broken", -0.3F, 0, -0.2F, -2, -0.6864F), // 碎裂
                DAMAGED = register("damaged", -0.15F, 0, 0, -1, -0.2775F), // 破损
                SHODDY = register("shoddy", -0.1F, 0, -0.15F, -2, -0.4148F), // 粗劣
                HURTFUL = register("hurtful", 0.1F, 0, 0, 1, 0.21F), // 致伤
                STRONG = register("strong", 0, 0, 0.15F, 1, 0.21F), // 强劲
                UNPLEASANT = register("unpleasant", 0.05F, 0, 0.15F, 2, 0.4581F), // 粗鲁
                WEAK = register("weak", 0, 0, -0.2F, -2, -0.36F), // 虚弱
                RUTHLESS = register("ruthless", 0.18F, 0, -0.1F, 1, 0.1278F), // 无情
                GODLY = register("godly", 0.15F, 0.05F, 0.15F, 2, 1.1163F), // 神级
                DEMONIC = register("demonic", 0.15F, 0.05F, 0, 2, 0.6002F), // 恶魔
                ZEALOUS = register("zealous", 0, 0.05F, 0, 1, 0.21F); // 狂热

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (attackDamage != 0.0F) builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ID, attackDamage, ADD_MULTIPLIED_TOTAL));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (knockBack != 0.0F) builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ID, knockBack, ADD_VALUE));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), 0.0F, 0, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return tier < 0;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Universal register(String name, float attackDamage, float criticalChance, float knockBack, int tier, float value) {
            Universal universal = new Universal(name, attackDamage, criticalChance, knockBack, tier, value);
            VALUES.put(name, universal);
            return universal;
        }

        private static void init() {}
    }

    record Common(String name, float attackDamage, float attackSpeed, float criticalChance, float knockBack, int tier, float value) implements ModPrefix {
        public static final Map<String, Common> VALUES = ModPrefix.registerGroup("common");
        public static final ResourceLocation ID = Confluence.asResource("common_prefix");

        public static final Common QUICK = register("quick", 0, 0.1F, 0, 0, 1, 0.21F), // 迅捷
                DEADLY = register("deadly", 0.1F, 0.1F, 0, 0, 2, 0.4641F), // 致命
                AGILE = register("agile", 0, 0.1F, 0.03F, 0, 1, 0.3596F), // 灵活
                NIMBLE = register("nimble", 0, 0.05F, 0, 0, 1, 0.1025F), // 灵巧
                MURDEROUS = register("murderous", 0.07F, 0.06F, 0.03F, 0, 2, 0.4454F), // 残暴
                SLOW = register("slow", 0, -0.15F, 0, 0, -1, -0.2775F), // 缓慢
                SLUGGISH = register("sluggish", 0, -0.2F, 0, 0, -2, -0.36F), // 迟钝
                LAZY = register("lazy", 0, -0.08F, 0, 0, -1, -0.1536F), // 呆滞
                ANNOYING = register("annoying", -0.2F, -0.15F, 0, 0, -2, -0.5376F), // 惹恼
                NASTY = register("nasty", 0.05F, 0.1F, 0.02F, -0.1F, 1, 0.1687F); // 凶险

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (attackDamage != 0.0F) builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ID, attackDamage, ADD_MULTIPLIED_TOTAL));
            if (attackSpeed != 0) builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ID, attackSpeed, ADD_MULTIPLIED_TOTAL));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (knockBack != 0.0F) builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ID, knockBack, ADD_VALUE));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), 0.0F, 0, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return tier < 0 && this != ANNOYING;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Common register(String name, float attackDamage, float attackSpeed, float criticalChance, float knockBack, int tier, float value) {
            Common common = new Common(name, attackDamage, attackSpeed, criticalChance, knockBack, tier, value);
            VALUES.put(name, common);
            return common;
        }

        private static void init() {}
    }

    record Melee(String name, float attackDamage, float attackSpeed, float criticalChance, float size, float knockBack, int tier, float value) implements ModPrefix {
        public static final Map<String, Melee> VALUES = ModPrefix.registerGroup("melee");
        public static final ResourceLocation ID = Confluence.asResource("melee_prefix");

        public static final Melee LARGE = register("large", 0, 0, 0, 0.12F, 0, 1, 0.2544F), // 大
                MASSIVE = register("massive", 0, 0, 0, 0.18F, 0, 1, 0.3924F), // 巨大
                DANGEROUS = register("dangerous", 0.05F, 0, 0.02F, 0.05F, 0, 1, 0.3147F), // 危险
                SAVAGE = register("savage", 0.1F, 0, 0, 0.1F, 0.1F, 2, 0.7716F), // 凶险
                SHARP = register("sharp", 0.15F, 0, 0, 0, 0, 1, 0.3225F), // 锋利
                POINTY = register("pointy", 0.1F, 0, 0, 0, 0, 1, 0.21F), // 尖锐
                TINY = register("tiny", 0, 0, 0, -0.18F, 0, -1, -0.3276F), // 微小
                TERRIBLE = register("terrible", -0.15F, 0, 0, -0.13F, -0.15F, -2, -0.6049F), // 可怕
                SMALL = register("small", 0, 0, 0, -0.1F, 0, -1, -0.19F), // 小
                DULL = register("dull", -0.15F, 0, 0, 0, 0, -1, -0.2775F), // 钝
                UNHAPPY = register("unhappy", 0, -0.1F, 0, -0.1F, -0.1F, -2, -0.4686F), // 倒霉
                BULKY = register("bulky", 0.05F, -0.15F, 0, 0.1F, 0.1F, 1, 0.1662F), // 笨蛋
                SHAMEFUL = register("shameful", -0.1F, 0, 0, 0.1F, -0.2F, -2, -0.3727F), // 可耻
                HEAVY = register("heavy", 0, -0.1F, 0, 0, 0.15F, 0, 0.0712F), // 重
                LIGHT = register("light", 0, 0.15F, 0, 0, -0.1F, 0, 0.0712F), // 轻
                LEGENDARY = register("legendary", 0.15F, 0.1F, 0.05F, 0.1F, 0.15F, 2, 2.0985F), // 传奇
                LEGENDARY2 = new Melee("legendary2", 0.17F, 0.0F, 0.08F, 0.0F, 0.17F, 2, 2.0985F); // todo 泰拉悠悠球独享传奇

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (attackDamage != 0.0F) builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ID, attackDamage, ADD_MULTIPLIED_TOTAL));
            if (attackSpeed != 0) builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ID, attackSpeed, ADD_MULTIPLIED_TOTAL));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (size != 0.0F) builder.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ID, size, ADD_MULTIPLIED_TOTAL));
            if (knockBack != 0.0F) builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ID, knockBack, ADD_VALUE));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), 0.0F, 0, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return tier < 0 && this != SHAMEFUL;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Melee register(String name, float attackDamage, float attackSpeed, float criticalChance, float size, float knockBack, int tier, float value) {
            Melee melee = new Melee(name, attackDamage, attackSpeed, criticalChance, size, knockBack, tier, value);
            VALUES.put(name, melee);
            return melee;
        }

        private static void init() {}
    }

    record Ranged(String name, float rangedDamage, float attackSpeed, float criticalChance, float velocity, float knockBack, int tier, float value) implements ModPrefix {
        public static final Map<String, Ranged> VALUES = ModPrefix.registerGroup("ranged");
        public static final ResourceLocation ID = Confluence.asResource("ranged_prefix");

        public static final Ranged SIGHTED = register("sighted", 0.1F, 0, 0.03F, 0, 0, 1, 0.3596F), // 精准
                RAPID = register("rapid", 0, 0.15F, 0, 0.1F, 0, 2, 0.6002F), // 迅速
                HASTY = register("hasty", 0, 0.1F, 0, 0.15F, 0, 2, 0.6002F), // 急速
                INTIMIDATING = register("intimidating", 0, 0, 0, 0.05F, 0.15F, 2, 0.4581F), // 恐怖
                DEADLY = register("deadly", 0.1F, 0.05F, 0.02F, 0.05F, 0.05F, 2, 0.7538F), // 致命
                STAUNCH = register("staunch", 0.1F, 0, 0, 0, 0.15F, 2, 0.6002F), // 可靠
                AWFUL = register("awful", -0.15F, 0, 0, -0.1F, -0.1F, -2, -0.526F), // 可畏
                LETHARGIC = register("lethargic", 0, -0.15F, 0, -0.1F, 0, -2, -0.4148F), // 无力
                AWKWARD = register("awkward", 0, -0.1F, 0, 0, -0.2F, -2, -0.4816F), // 粗笨
                POWERFUL = register("powerful", 0.15F, -0.1F, 0.01F, 0, 0, 1, 0.1145F), // 强大
                FRENZYING = register("frenzying", -0.15F, 0.15F, 0, 0, 0, 0, -0.0445F), // 暴怒
                UNREAL = register("unreal", 0.15F, 0.1F, 0.05F, 0.1F, 0.15F, 2, 2.0985F); // 虚幻

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (rangedDamage != 0.0F) builder.put(TCAttributes.getRangedDamage(), new AttributeModifier(ID, rangedDamage, ADD_MULTIPLIED_TOTAL));
            if (attackSpeed != 0) builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ID, attackSpeed, ADD_MULTIPLIED_TOTAL));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (value != 0.0F) builder.put(TCAttributes.getRangedVelocity(), new AttributeModifier(ID, velocity, ADD_MULTIPLIED_TOTAL));
            if (knockBack != 0.0F) builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ID, knockBack, ADD_VALUE));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), 0.0F, 0, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return tier < 0 && this != FRENZYING;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Ranged register(String name, float rangedDamage, float attackSpeed, float criticalChance, float velocity, float knockBack, int tier, float value) {
            Ranged ranged = new Ranged(name, rangedDamage, attackSpeed, criticalChance, velocity, knockBack, tier, value);
            VALUES.put(name, ranged);
            return ranged;
        }

        private static void init() {}
    }

    record Magic(String name, float rangedDamage, float attackSpeed, float criticalChance, float manaCost, float knockBack, int tier, float value) implements ModPrefix {
        public static final Map<String, Magic> VALUES = ModPrefix.registerGroup("magic");
        public static final ResourceLocation ID = Confluence.asResource("magic_prefix");

        public static final Magic MYTHIC = register("mythic", 0.1F, 0, 0, -0.15F, 0, 2, 0.6002F), // 神秘
                ADEPT = register("adept", 0, 0, 0, -0.15F, 0, 1, 0.3225F), // 精巧
                MASTERFUL = register("masterful", 0.15F, 0, 0, -0.15F, 0.05F, 2, 0.9263F), // 精湛
                INEPT = register("inept", 0, 0, 0, 0.1F, 0, -1, -0.19F), // 笨拙
                IGNORANT = register("ignorant", -0.1F, 0, 0, 0.2F, 0, -2, -0.4816F), // 无知
                DERANGED = register("deranged", -0.1F, 0, 0, 0, -0.1F, -1, -0.3439F), // 错乱
                INTENSE = register("intense", 0.1F, 0, 0, 0.15F, 0, -1, -0.1258F), // 威猛
                TABOO = register("taboo", 0, 0.1F, 0, 0.1F, 0.1F, 1, 0.1859F), // 禁忌
                CELESTIAL = register("celestial", 0.1F, -0.1F, 0, -0.1F, 0.1F, 1, 0.1194F), // 天界
                FURIOUS = register("furious", 0.15F, 0, 0, 0.2F, 0.15F, 1, 0.1194F), // 狂怒
                MANIC = register("manic", -0.1F, 0.1F, 0, -0.1F, 0, 1, 0.1859F), // 狂躁
                MYTHICAL = register("mythical", 0.15F, 0.1F, 0.05F, -0.1F, 0.15F, 2, 2.0985F); // 神话

        @Override
        public PrefixComponent createComponent(PrefixType prefixType) {
            ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();
            if (rangedDamage != 0.0F) builder.put(TCAttributes.getRangedDamage(), new AttributeModifier(ID, rangedDamage, ADD_MULTIPLIED_TOTAL));
            if (attackSpeed != 0) builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ID, attackSpeed, ADD_MULTIPLIED_TOTAL));
            if (criticalChance != 0.0F) builder.put(TCAttributes.getCriticalChance(), new AttributeModifier(ID, criticalChance, ADD_VALUE));
            if (knockBack != 0.0F) builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ID, knockBack, ADD_VALUE));
            return new PrefixComponent(prefixType, name, new AttributeModifiersValue(builder.build()), manaCost, 0, tier, value);
        }

        @Override
        public boolean canBeMercy() {
            return tier < 0 && this != INTENSE;
        }

        @Override
        public ResourceLocation getModifierId() {
            return ID;
        }

        private static Magic register(String name, float rangedDamage, float attackSpeed, float criticalChance, float manaCost, float knockBack, int tier, float value) {
            Magic magic = new Magic(name, rangedDamage, attackSpeed, criticalChance, manaCost, knockBack, tier, value);
            VALUES.put(name, magic);
            return magic;
        }

        private static void init() {}
    }

    Map<String, Map<String, ? extends ModPrefix>> GROUPS = new HashMap<>();

    static <T extends ModPrefix> Map<String, T> registerGroup(String name) {
        HashMap<String, T> v = new HashMap<>();
        GROUPS.put(name, v);
        return v;
    }

    HashBiMap<Integer, ModPrefix> ID_MAP = Util.make(HashBiMap.create(), map -> {
        map.put(1, Melee.LARGE);
        map.put(2, Melee.MASSIVE);
        map.put(3, Melee.DANGEROUS);
        map.put(4, Melee.SAVAGE);
        map.put(5, Melee.SHARP);
        map.put(6, Melee.POINTY);
        map.put(7, Melee.TINY);
        map.put(8, Melee.TERRIBLE);
        map.put(9, Melee.SMALL);
        map.put(10, Melee.DULL);
        map.put(11, Melee.UNHAPPY);
        map.put(12, Melee.BULKY);
        map.put(13, Melee.SHAMEFUL);
        map.put(14, Melee.HEAVY);
        map.put(15, Melee.LIGHT);
        map.put(16, Ranged.SIGHTED);
        map.put(17, Ranged.RAPID);
        map.put(18, Ranged.HASTY);
        map.put(19, Ranged.INTIMIDATING);
        map.put(20, Ranged.DEADLY);
        map.put(21, Ranged.STAUNCH);
        map.put(22, Ranged.AWFUL);
        map.put(23, Ranged.LETHARGIC);
        map.put(24, Ranged.AWKWARD);
        map.put(25, Ranged.POWERFUL);
        map.put(26, Magic.MYTHIC);
        map.put(27, Magic.ADEPT);
        map.put(28, Magic.MASTERFUL);
        map.put(29, Magic.INEPT);
        map.put(30, Magic.IGNORANT);
        map.put(31, Magic.DERANGED);
        map.put(32, Magic.INTENSE);
        map.put(33, Magic.TABOO);
        map.put(34, Magic.CELESTIAL);
        map.put(35, Magic.FURIOUS);
        map.put(36, Universal.KEEN);
        map.put(37, Universal.SUPERIOR);
        map.put(38, Universal.FORCEFUL);
        map.put(39, Universal.BROKEN);
        map.put(40, Universal.DAMAGED);
        map.put(41, Universal.SHODDY);
        map.put(42, Common.QUICK);
        map.put(43, Common.DEADLY);
        map.put(44, Common.AGILE);
        map.put(45, Common.NIMBLE);
        map.put(46, Common.MURDEROUS);
        map.put(47, Common.SLOW);
        map.put(48, Common.SLUGGISH);
        map.put(49, Common.LAZY);
        map.put(50, Common.ANNOYING);
        map.put(51, Common.NASTY);
        map.put(52, Magic.MANIC);
        map.put(53, Universal.HURTFUL);
        map.put(54, Universal.STRONG);
        map.put(55, Universal.UNPLEASANT);
        map.put(56, Universal.WEAK);
        map.put(57, Universal.RUTHLESS);
        map.put(58, Ranged.FRENZYING);
        map.put(59, Universal.GODLY);
        map.put(60, Universal.DEMONIC);
        map.put(61, Universal.ZEALOUS);
        map.put(62, Accessory.HARD);
        map.put(63, Accessory.GUARDING);
        map.put(64, Accessory.ARMORED);
        map.put(65, Accessory.WARDING);
        map.put(66, Accessory.ARCANE);
        map.put(67, Accessory.PRECISE);
        map.put(68, Accessory.LUCKY);
        map.put(69, Accessory.JAGGED);
        map.put(70, Accessory.SPIKED);
        map.put(71, Accessory.ANGRY);
        map.put(72, Accessory.MENACING);
        map.put(73, Accessory.BRISK);
        map.put(74, Accessory.FLEETING);
        map.put(75, Accessory.HASTY);
        map.put(76, Accessory.QUICK);
        map.put(77, Accessory.WILD);
        map.put(78, Accessory.RASH);
        map.put(79, Accessory.INTREPID);
        map.put(80, Accessory.VIOLENT);
        map.put(81, Melee.LEGENDARY);
        map.put(82, Ranged.UNREAL);
        map.put(83, Magic.MYTHICAL);
        map.put(84, Melee.LEGENDARY2);
    });

    static void initialize() {
        Accessory.init();
        Universal.init();
        Common.init();
        Melee.init();
        Ranged.init();
        Magic.init();
    }
}
