package org.confluence.mod.mixed;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public interface Immunity {
    record ImmunityOverride(DamageSource source, Immunity cause) {}

    ThreadLocal<ImmunityOverride> CURRENT_HIT = new ThreadLocal<>();

    @Nullable
    static Immunity getCause(DamageSource damageSource) {
        if (isEnemyProjectile(damageSource)) return null;
        ImmunityOverride hit = CURRENT_HIT.get();
        if (hit != null && hit.source() == damageSource) return hit.cause();
        Entity directEntity = damageSource.getDirectEntity();
        if (directEntity instanceof Projectile projectile && directEntity instanceof Immunity immunity
                && ModUtils.isFromConfluence(BuiltInRegistries.ENTITY_TYPE, projectile.getType())) {
            return immunity;
        }
        ItemStack weaponItemStack = damageSource.getWeaponItem();
        if (weaponItemStack != null) {
            if (weaponItemStack.isEmpty()) {
                return directEntity instanceof Player ? (Immunity) weaponItemStack : null;
            }

            Item weaponItem = weaponItemStack.getItem();
            boolean fromConfluence = ModUtils.isFromConfluence(BuiltInRegistries.ITEM, weaponItem);
            if (fromConfluence && weaponItem instanceof SwordItem && directEntity instanceof Projectile) { // 汇流剑气
                return directEntity instanceof Immunity immunity ? immunity : null;
            } else if (weaponItem instanceof Immunity im) { // 非汇流但是实现了Immunity
                return switch (im.confluence$getImmunityType()) {
                    case STATIC -> im;
                    case LOCAL -> (Immunity) weaponItemStack;
                };
            } else if (fromConfluence) { // 其他所有汇流武器
                return (Immunity) weaponItemStack;
            }
        }
        return null;
    }

    // 敌怪射弹与接触伤害共用玩家的受伤保护，十字项链可以延长这段时间。
    private static boolean isEnemyProjectile(DamageSource source) {
        return source.getDirectEntity() instanceof Projectile && source.getEntity() instanceof Enemy;
    }

    static void calculateInvTicks(DamageSource damageSource, LivingEntity victim) {
        Immunity cause = getCause(damageSource);
        if (cause != null) apply(cause, damageSource, victim);
    }

    static boolean isActive(@Nullable Immunity cause, LivingEntity victim) {
        return cause != null && ILivingEntity.of(victim).confluence$getImmunityDeadlines().getLong(keyOf(cause)) > victim.level().getGameTime();
    }

    /// 为无法从 {@link DamageSource} 反查来源对象的伤害写入无敌时间。
    ///
    /// 非实体召唤物没有可放入伤害来源的直接实体，因此由运行对象在成功命中后显式调用；
    /// 普通实体与弹幕仍由伤害事件自动调用 {@link #calculateInvTicks(DamageSource, LivingEntity)}。
    static void apply(Immunity cause, DamageSource damageSource, LivingEntity victim) {
        int duration = cause.confluence$getImmunityDuration(damageSource);
        if (duration > 0) {
            ILivingEntity.of(victim).confluence$getImmunityDeadlines().put(keyOf(cause), victim.level().getGameTime() + duration);
        }
    }

    /// 在同步伤害结算期间显式指定本次命中的来源。
    /// 非实体运行对象不能写入 DamageSource 的直接实体字段，通过这个作用域仍可复用受伤事件；仅同一个 DamageSource 使用此覆盖。
    static <T> T withCause(Immunity cause, DamageSource source, Supplier<T> action) {
        ImmunityOverride previous = CURRENT_HIT.get();
        CURRENT_HIT.set(new ImmunityOverride(source, cause));
        try {
            return action.get();
        } finally {
            if (previous == null) CURRENT_HIT.remove();
            else CURRENT_HIT.set(previous);
        }
    }

    static boolean hurtWithoutKnockback(Entity target, DamageSource source, float amount) {
        Vec3 movement = target.getDeltaMovement();
        boolean hurt = target.hurt(source, amount);
        target.setDeltaMovement(movement);
        return hurt;
    }

    /// 按来源声明的共享方式结算伤害，同时保留目标原有的原版受伤帧。
    static boolean hurt(Immunity cause, LivingEntity target, DamageSource source, float amount) {
        if (isEnemyProjectile(source)) return hurtWithoutKnockback(target, source, amount);
        if (isActive(cause, target)) return false;
        int invulnerableTime = target.invulnerableTime;
        target.invulnerableTime = 0;
        try {
            return withCause(cause, source, () -> hurtWithoutKnockback(target, source, amount));
        } finally {
            target.invulnerableTime = invulnerableTime;
        }
    }

    // 非生命体部件仍由部件接收伤害，无敌帧统一存放在其生命体所属者上。
    static boolean hurt(Immunity cause, Entity recipient, LivingEntity owner, DamageSource source, float amount) {
        if (recipient instanceof LivingEntity living) return hurt(cause, living, source, amount);
        if (isEnemyProjectile(source)) return hurtWithoutKnockback(recipient, source, amount);
        if (isActive(cause, owner)) return false;
        int invulnerableTime = owner.invulnerableTime;
        owner.invulnerableTime = 0;
        try {
            boolean hurt = withCause(cause, source, () -> hurtWithoutKnockback(recipient, source, amount));
            if (hurt) apply(cause, source, owner);
            return hurt;
        } finally {
            owner.invulnerableTime = invulnerableTime;
        }
    }

    static void tick(Entity entity) {
        // 额外保护使用剩余刻数；来源冷却在命中时按世界时间判定，这里只清理到期记录。
        if (entity instanceof ILivingEntity living) {
            int extraInvulnerableTicks = living.confluence$getExtraInvulnerableTicks();
            if (extraInvulnerableTicks > 0) {
                living.confluence$setExtraInvulnerableTicks(extraInvulnerableTicks - 1);
            }

            var deadlines = living.confluence$getImmunityDeadlines();
            if (!deadlines.isEmpty()) {
                long now = entity.level().getGameTime();
                deadlines.values().removeIf((long deadline) -> deadline <= now);
            }
        }
    }

    private static Object keyOf(Immunity cause) {
        return cause.confluence$getImmunityType() == Type.LOCAL ? cause : cause.confluence$getStaticImmunityKey();
    }

    /// 静态冷却使用稳定的共享对象。非实体来源默认按实现类共享，可覆盖为注册类型。
    default Object confluence$getStaticImmunityKey() {
        if (this instanceof Entity entity) return entity.getType();
        if (this instanceof Item || this instanceof EntityType<?>) return this;
        return getClass();
    }

    Type confluence$getImmunityType();

    @SuppressWarnings("all")
    default int confluence$getImmunityDuration(DamageSource damageSource) {
        Entity causeEntity = damageSource.getEntity();
        // 自身是汇流近战武器且使用者有攻击速度属性
        Object self = this;
        if (self instanceof ItemStack weaponItemStack && weaponItemStack.getItem() instanceof SwordItem weaponItem
                && ModUtils.isFromConfluence(BuiltInRegistries.ITEM, weaponItem)
                && causeEntity instanceof LivingEntity living && living.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)
        ) {
            double speed = living.getAttribute(Attributes.ATTACK_SPEED).getValue();
            int time = (int) (20 / speed) - 1;
            return Math.max(0, time);
        }
        return 0;
    }

    enum Type implements StringRepresentable {
        /// 静态无敌帧：同一目标上，共享标识相同的来源共用冷却，例如同类型的魔刺射弹。
        STATIC,
        /// 局部无敌帧：同一目标上，每个来源实例独立计时，可以分别命中。
        LOCAL;

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
