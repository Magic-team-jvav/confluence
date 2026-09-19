package org.confluence.mod.common.entity.monster;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.animal.BaseCritter;
import org.confluence.mod.common.entity.monster.slime.SweetSlime;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

/// 独立于难度、游戏阶段与特殊状态的普通敌怪全局倍率；固定 UUID 保证读档、重载不会累乘。
public final class MonsterAttributeScaling {
    private MonsterAttributeScaling() {}

    /// 所有实体加入世界的路径共用此入口，包括自然生成、刷怪蛋、脚本及区块读档。
    /// 仅新生成且原本满血的实体补齐新生命上限；读档与配置重载均不治疗。
    public static void apply(LivingEntity entity, boolean freshSpawn) {
        if (entity.level().isClientSide || CommonConfigs.SPEC == null || !CommonConfigs.SPEC.isLoaded())
            return;
        boolean eligible = isEligible(entity);
        float health = entity.getHealth();
        float maximum = entity.getMaxHealth();
        boolean wasFull = Math.abs(health - maximum) < 0.001F;
        for (Multiplier multiplier : Multiplier.values()) {
            AttributeInstance attribute = entity.getAttribute(multiplier.attribute.get());
            if (attribute != null) {
                updateModifier(attribute, multiplier.id, eligible ? multiplier.config.get().get() : 1.0D);
            }
        }
        if (entity.getMaxHealth() != maximum) {
            entity.setHealth(freshSpawn && wasFull && health > 0 ? entity.getMaxHealth() : Math.min(health, entity.getMaxHealth()));
        }
    }

    /// 开启跨模组开关也只包含敌对 Mob，不把玩家、NPC、小动物、召唤物及 Boss 纳入普通敌怪倍率。
    private static boolean isEligible(LivingEntity entity) {
        if (!(entity instanceof Mob) || entity instanceof Boss || entity instanceof OwnedSummon
                || entity instanceof BaseNPC || entity instanceof BaseCritter || entity instanceof SweetSlime
                || entity instanceof EnderDragon || entity instanceof WitherBoss || entity.getType().is(Tags.EntityTypes.BOSSES))
            return false;
        if (!(entity instanceof Enemy) && entity.getType().getCategory() != MobCategory.MONSTER)
            return false;
        return CommonConfigs.ENHANCE_ALL_MONSTER.get()
                || Confluence.MODID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
    }

    /// 加载与重载配置后在服务器线程更新已加载实体；关闭扩展范围时也移除旧倍率。
    public static void reload() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        server.execute(() -> {
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof LivingEntity living) apply(living, false);
                }
            }
        });
    }

    /// 只替换本功能自己的最终乘算项，保留阶段、装备及状态修饰符；倍率为 1 时删除该项。
    static void updateModifier(AttributeInstance attribute, UUID id, double multiplier) {
        AttributeModifier previous = attribute.getModifier(id);
        if (multiplier == 1.0D) {
            if (previous != null) attribute.removeModifier(id);
            return;
        }
        double amount = multiplier - 1.0D;
        if (previous != null && previous.getAmount() == amount && previous.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL)
            return;
        if (previous != null) attribute.removeModifier(id);
        attribute.addPermanentModifier(new AttributeModifier(id, "Confluence monster config multiplier", amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private enum Multiplier {
        HEALTH(() -> Attributes.MAX_HEALTH, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH),
        DAMAGE(() -> LibAttributes.getAttackDamage().value(), () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE),
        ARMOR(() -> Attributes.ARMOR, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR),
        ARMOR_TOUGHNESS(() -> Attributes.ARMOR_TOUGHNESS, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR_TOUGHNESS),
        MOVEMENT_SPEED(() -> Attributes.MOVEMENT_SPEED, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_MOVEMENT_SPEED),
        FLYING_SPEED(() -> Attributes.FLYING_SPEED, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_FLYING_SPEED),
        KNOCKBACK_RESISTANCE(() -> Attributes.KNOCKBACK_RESISTANCE, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_KNOCKBACK_RESISTANCE),
        FOLLOW_RANGE(() -> Attributes.FOLLOW_RANGE, () -> CommonConfigs.MONSTER_ATTRIBUTES_MULTIPLIER_FOLLOW_RANGE);

        private final Supplier<Attribute> attribute;
        private final Supplier<ForgeConfigSpec.DoubleValue> config;
        private final UUID id;

        Multiplier(Supplier<Attribute> attribute, Supplier<ForgeConfigSpec.DoubleValue> config) {
            this.attribute = attribute;
            this.config = config;
            id = UUID.nameUUIDFromBytes(("confluence.monster_config." + name()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
