package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/// NPC 虚拟弹药的命中策略注册表；弹体只持有策略 ID，不依赖具体效果实现。
public final class NPCProjectileEffects {
    private static final Map<ResourceLocation, ImpactEffect> EFFECTS = new HashMap<>();

    /// 普通单体伤害，不附加额外状态。
    public static final ResourceLocation NONE = register("none", (context, target) -> {
        if (target != null) context.hurt(target);
    });
    /// 单体伤害成功后施加 3 到 5 秒中毒。
    public static final ResourceLocation POISON = register("poison", (context, target) -> {
        if (target instanceof LivingEntity living && context.hurt(living)) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON,
                    60 + context.projectile().getRandom().nextInt(41)));
        }
    });
    /// 在命中点造成范围伤害、声音和爆炸粒子，但不破坏方块。
    public static final ResourceLocation EXPLOSIVE = register("explosive", (context, target) -> context.areaDamage(2.5));
    /// 只命中受伤城镇 NPC 的固定 20 点治疗策略。
    public static final ResourceLocation HEAL = register("heal", new ImpactEffect() {
        /// 为命中的受伤城镇 NPC 恢复固定 20 点生命。
        @Override
        public void apply(Context context, @Nullable Entity target) {
            if (target instanceof BaseNPC npc)
                npc.setHealth(Math.min(npc.getMaxHealth(), npc.getHealth() + 20));
        }

        /// 治疗弹只接受仍存活、受伤且不是发射者本人的城镇 NPC。
        @Override
        public boolean canHit(Context context, Entity target) {
            return target instanceof BaseNPC npc && npc != context.owner() && npc.isAlive()
                    && npc.getHealth() < npc.getMaxHealth();
        }
    });

    private NPCProjectileEffects() {}

    /// 在本体命名空间下登记一个命中策略，并返回弹体持久化使用的稳定 ID。
    public static ResourceLocation register(String name, ImpactEffect effect) {
        ResourceLocation id = Confluence.asResource(name);
        if (EFFECTS.putIfAbsent(id, effect) != null)
            throw new IllegalStateException("Duplicate NPC projectile effect " + id);
        return id;
    }

    /// 按 ID 获取命中策略；数据中出现未知 ID 时安全退回普通伤害。
    public static ImpactEffect get(ResourceLocation id) {
        return EFFECTS.getOrDefault(id, EFFECTS.get(NONE));
    }

    @FunctionalInterface
    public interface ImpactEffect {
        /// 处理一次实体或方块命中；方块命中时 target 为 null。
        void apply(Context context, @Nullable Entity target);

        /// 判断该策略是否允许弹体命中指定实体；默认沿用 NPC 友军过滤。
        default boolean canHit(Context context, Entity target) {
            return !(context.owner() instanceof BaseNPC npc && target instanceof LivingEntity living)
                    || npc.canAttack(living);
        }
    }

    /// 向命中策略提供弹体、所有者和伤害，并封装统一伤害来源与友军过滤。
    public record Context(NPCWeaponProjectile projectile, @Nullable LivingEntity owner,
                          float damage) {
        /// 使用该 NPC 弹体的统一远程伤害来源攻击指定实体。
        public boolean hurt(Entity target) {
            return target.hurt(projectile.damageSources().mobProjectile(projectile, owner), damage);
        }

        /// 在弹体周围造成不破坏方块且不会伤害 NPC 友军的范围伤害。
        public void areaDamage(double radius) {
            if (projectile.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.EXPLOSION, projectile.getX(), projectile.getY(), projectile.getZ(), 1, 0, 0, 0, 0);
                level.playSound(null, projectile.blockPosition(), SoundEvents.GENERIC_EXPLODE, projectile.getSoundSource(), 0.7F, 1.2F);
            }
            for (LivingEntity target : projectile.level().getEntitiesOfClass(LivingEntity.class, new AABB(projectile.blockPosition()).inflate(radius))) {
                if (owner instanceof BaseNPC npc && !npc.canAttack(target)) continue;
                hurt(target);
            }
        }
    }
}
