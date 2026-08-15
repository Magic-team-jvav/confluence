package org.confluence.mod.common.item.sword;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileCost;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.lib.api.projectile.ProjectileFireAction;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.AreaSwordProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 将剑气组件转换为 MagicLib 通用弹幕发射动作。
 *
 * <p>这里是汇流来世自身的玩法适配层：MagicLib 只负责发射事务、伤害快照和资源消耗，
 * 不需要知道具体剑气实体或泰拉瑞亚式的武器数值。后续新增剑气武器时，只要声明组件，
 * 就能复用同一套服务端发射流程。</p>
 */
public final class SwordProjectileActionFactory {
    private SwordProjectileActionFactory() {}

    /**
     * 根据当前服务端手持状态创建一次不可变的发射动作。
     *
     * <p>近战攻击伤害只在这里读取一次，再乘以剑气自身的倍率。暴击、护甲穿透、击退等后续由
     * MagicLib 从快照中解析。剑气固定使用近战通道，避免被误读为远程伤害或远程弹速。</p>
     */
    public static ProjectileFireAction create(
            ProjectileFireContext context,
            SwordProjectileComponent configuration
    ) {
        Objects.requireNonNull(context, "Projectile fire context must not be null");
        Objects.requireNonNull(configuration, "Sword projectile configuration must not be null");

        double attackDamage = context.player().getAttributeValue(LibAttributes.getAttackDamage());
        double scaledDamage = attackDamage * configuration.damageFactor();
        if (!Double.isFinite(scaledDamage) || scaledDamage < 0.0 || scaledDamage > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Sword projectile action damage must be finite and non-negative");
        }

        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.MELEE,
                        ProjectileCost.none(),
                        (fireContext, snapshot) -> createLaunches(fireContext, snapshot, configuration))
                .baseDamage((float) scaledDamage)
                .baseVelocity(configuration.baseSpeed())
                .baseKnockback(configuration.baseKnockback())
                .cooldownTicks(configuration.getAttackSpeed(context.player()))
                .successAction(fireContext -> fireContext.level().playSound(
                        null,
                        fireContext.player().getX(),
                        fireContext.player().getY(),
                        fireContext.player().getZ(),
                        configuration.getSoundEvent(),
                        SoundSource.AMBIENT,
                        1.0F,
                        1.0F))
                .build();
    }

    /**
     * 按组件声明创建完整的剑气布局。
     *
     * <p>布局每次需要弹幕时都会重新调用实体工厂，因此以后可以自然支持一次动作生成多枚剑气。
     * 返回前会再次校验实体类型，防止自定义布局绕过剑气核心约束。</p>
     */
    public static List<ProjectileLaunch> createLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            SwordProjectileComponent configuration
    ) {
        Objects.requireNonNull(context, "Projectile fire context must not be null");
        Objects.requireNonNull(snapshot, "Projectile combat snapshot must not be null");
        Objects.requireNonNull(configuration, "Sword projectile configuration must not be null");

        List<ProjectileLaunch> launches = configuration.generation().createLaunches(
                context.player(),
                snapshot.resolvedVelocity(),
                () -> createProjectile(context, configuration));
        if (launches == null) {
            throw new IllegalStateException("Sword projectile generation returned null");
        }
        List<ProjectileLaunch> copy = List.copyOf(launches);
        ArrayList<ProjectileLaunch> normalizedLaunches = new ArrayList<>(copy.size());
        for (ProjectileLaunch launch : copy) {
            if (!(launch.projectile() instanceof SwordProjectile projectile)) {
                throw new IllegalArgumentException("Sword projectile generation returned a non-sword entity");
            }
            projectile.setProjComponent(configuration);
            projectile.setProjectileDirection(launch.direction());
            if (projectile instanceof AreaSwordProjectile areaProjectile) {
                normalizedLaunches.add(new ProjectileLaunch(
                        projectile,
                        areaProjectile.initialCenter(context.player(), launch.direction()),
                        launch.direction(),
                        launch.velocityMultiplier()));
            } else {
                normalizedLaunches.add(launch);
            }
        }
        return List.copyOf(normalizedLaunches);
    }

    /**
     * 创建一枚尚未加入世界、并已安装运动配置的剑气实体。
     */
    public static SwordProjectile createProjectile(
            ProjectileFireContext context,
            SwordProjectileComponent configuration
    ) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(configuration.projType())
                .orElseThrow(() -> new IllegalStateException(
                        "Unknown sword projectile entity type id: " + configuration.projType()));
        Entity created = entityType.create(context.level());
        if (!(created instanceof SwordProjectile projectile)) {
            throw new IllegalArgumentException(
                    "Sword projectile entity type must create SwordProjectile: " + configuration.projType());
        }
        projectile.setProjComponent(configuration);
        return projectile;
    }
}
