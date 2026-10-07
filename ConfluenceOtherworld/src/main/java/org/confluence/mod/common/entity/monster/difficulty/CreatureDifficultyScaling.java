package org.confluence.mod.common.entity.monster.difficulty;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;

/// 本体敌怪难度属性的唯一应用入口；模板、阶段和状态均使用专家基准。
public final class CreatureDifficultyScaling {
    private static final ResourceLocation HEALTH_ID = id("creature_difficulty_health");
    private static final ResourceLocation DAMAGE_ID = id("creature_difficulty_damage");
    private static final ResourceLocation PENETRATION_ID = id("creature_difficulty_penetration");
    private static final ResourceLocation TOUGHNESS_ID = id("creature_difficulty_toughness");

    private CreatureDifficultyScaling() {}

    private static ResourceLocation id(String path) {return Confluence.asResource(path);}

    /// 按登记类别筛选，原版、第三方、NPC 和小动物不参与本体难度表。
    public static boolean isManaged(Entity entity) {
        var definition = ModEntities.creatureAttributes(entity.getType());
        return definition != null && definition.enemy();
    }

    public static boolean isBoss(Entity entity) {
        var definition = ModEntities.creatureAttributes(entity.getType());
        return definition != null && definition.boss();
    }

    /// 使用本体已有的有效难度判定，包含传奇种子。
    public static CreatureDifficultyRules.Difficulty difficulty(Entity entity) {
        return LibUtils.switchByDifficulty(entity.level(), entity.blockPosition(),
                CreatureDifficultyRules.Difficulty.CLASSIC, CreatureDifficultyRules.Difficulty.EXPERT,
                CreatureDifficultyRules.Difficulty.MASTER, CreatureDifficultyRules.Difficulty.LEGENDARY);
    }

    /// 固定 ID 替换自身修饰符；只有新生成且原本满血的实体才补齐生命。
    public static void apply(LivingEntity entity, boolean freshSpawn) {
        if (entity.level().isClientSide || !isManaged(entity)) return;
        float health = entity.getHealth(), maximum = entity.getMaxHealth();
        boolean full = health > 0 && Math.abs(health - maximum) < 0.001F;
        var rule = difficulty(entity);
        boolean boss = isBoss(entity);
        multiply(entity, Attributes.MAX_HEALTH, HEALTH_ID, rule.health(boss));
        multiply(entity, Attributes.ATTACK_DAMAGE, DAMAGE_ID, rule.attack(boss));
        offset(entity, LibAttributes.getArmorPenetration(), PENETRATION_ID, rule, true);
        offset(entity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID, rule, false);
        if (entity.getMaxHealth() != maximum)
            entity.setHealth(freshSpawn && full ? entity.getMaxHealth() : Math.min(health, entity.getMaxHealth()));
    }

    private static void multiply(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double multiplier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier previous = instance.getModifier(id);
        if (multiplier == 1.0D) {
            if (previous != null) instance.removeModifier(id);
            return;
        }
        double amount = multiplier - 1.0D;
        if (previous != null && previous.amount() == amount
                && previous.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) return;
        if (previous != null) instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    /// 难度偏移在基础值之上应用，不回写基础值，反复加载不会累加。
    private static void offset(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, CreatureDifficultyRules.Difficulty rule, boolean penetration) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        AttributeInstance preview = new AttributeInstance(attribute, ignored -> {});
        preview.replaceFrom(instance);
        preview.removeModifier(id);
        double base = preview.getValue();
        double target = penetration ? rule.penetration(base) : rule.toughness(base);
        AttributeModifier.Operation operation = base > 0 ? AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL : AttributeModifier.Operation.ADD_VALUE;
        double amount;
        if (base > 0) amount = target / base - 1;
        else {
            preview.setBaseValue(preview.getBaseValue() + 1);
            double coefficient = preview.getValue() - base;
            amount = coefficient > 0 ? (target - base) / coefficient : 0;
        }
        AttributeModifier previous = instance.getModifier(id);
        if (previous != null && previous.amount() == amount && previous.operation() == operation)
            return;
        instance.removeModifier(id);
        if (amount != 0)
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }

    /// 部件继承主体的完整属性倍率时，先除去自己已应用的难度倍率。
    public static double healthMultiplier(Entity entity) {return isManaged(entity) ? difficulty(entity).health(isBoss(entity)) : 1;}

    public static double attackMultiplier(Entity entity) {return isManaged(entity) ? difficulty(entity).attack(isBoss(entity)) : 1;}

    /// 攻击属性派生射弹已包含接触倍率，替换为特殊攻击倍率而不是再乘一次。
    public static double projectileDamage(Entity owner, double damage, boolean fromAttackAttribute) {
        if (damage < 0 || !isManaged(owner)) return damage;
        var rule = difficulty(owner);
        boolean boss = isBoss(owner);
        return rule.projectileDamage(damage, boss, fromAttackAttribute);
    }
}
