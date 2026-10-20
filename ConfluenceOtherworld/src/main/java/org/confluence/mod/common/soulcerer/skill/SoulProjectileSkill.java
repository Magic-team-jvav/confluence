package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/// 通用弹幕技能：一次施放生成 1..n 枚 `SoulLureProjectile`，可选附带自身增益与命中范围爆炸。
///
/// 引魂、强力引魂、附魂印记、引魂激荡、迷魂孢雾、灵魂突击与灵魂引发的连锁弹幕都由它派生，
/// 差异只在 {@link Builder} 里的参数——新增同类技能不需要再写类。
public class SoulProjectileSkill extends SoulSkill {
    private final ResourceLocation particle;
    private final float speed;
    private final double gravity;
    private final int count;
    private final double spread;
    private final double spawnAbove;
    private final double aoeRadius;
    private final float aoeDamage;
    private final float refundChance;
    private final @Nullable Supplier<MobEffect> selfEffect;
    private final int selfEffectTicks;

    public SoulProjectileSkill(ResourceLocation icon, ResourceLocation id, Builder builder) {
        super(icon, id, builder.damage, Kind.ACTIVE, builder.soulCost, builder.cooldown);
        this.particle = builder.particle;
        this.speed = builder.speed;
        this.gravity = builder.gravity;
        this.count = Math.max(1, builder.count);
        this.spread = builder.spread;
        this.spawnAbove = builder.spawnAbove;
        this.aoeRadius = builder.aoeRadius;
        this.aoeDamage = builder.aoeDamage;
        this.refundChance = builder.refundChance;
        this.selfEffect = builder.selfEffect;
        this.selfEffectTicks = builder.selfEffectTicks;
    }

    public static Builder builder(ResourceLocation particle) {
        return new Builder(particle);
    }

    /// 服务端生成弹幕（多枚时按扇形散布），并按需给自身附加增益。
    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float damage = PlayerUtils.getSoulSkillDamage(player, skillStack);
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        RandomSource random = RandomSource.create();
        for (int i = 0; i < count; i++) {
            Vec3 direction = spread <= 0.0 || count == 1 ? look : jitter(look, random);
            Vec3 origin = spawnAbove > 0.0
                    ? eye.add(look.scale(2.0)).add(0.0, spawnAbove, 0.0)
                    : eye;
            SoulLureProjectile projectile = new SoulLureProjectile(serverPlayer, origin, direction, damage, particle, speed,
                    gravity, SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, aoeRadius, aoeDamage);
            modifyProjectile(projectile);
            player.level().addFreshEntity(projectile);
        }
        if (refundChance > 0.0F && random.nextFloat() < refundChance) {
            PlayerUtils.adjustSoul(player, 1.0F);
        }
        if (selfEffect != null && selfEffectTicks > 0) {
            player.addEffect(new MobEffectInstance(selfEffect.get(), selfEffectTicks));
        }
    }

    /// 生成弹幕后的定制钩子，派生类可在这里给弹幕打标记（默认不改）。
    protected void modifyProjectile(SoulLureProjectile projectile) {}

    /// 以视线为轴做锥形散布，用于孢子弹幕一类多枚齐射。
    private Vec3 jitter(Vec3 look, RandomSource random) {
        double dx = (random.nextDouble() - 0.5) * spread;
        double dy = (random.nextDouble() - 0.5) * spread;
        double dz = (random.nextDouble() - 0.5) * spread;
        return look.add(dx, dy, dz).normalize();
    }

    public static final class Builder {
        private final ResourceLocation particle;
        private float damage = SoulSkillParameters.ACTIVE_DAMAGE;
        private float soulCost = SoulSkillParameters.ACTIVE_SOUL_COST;
        private int cooldown = SoulSkillParameters.ACTIVE_COOLDOWN_TICKS;
        private float speed = SoulSkillParameters.SOUL_LURE_SPEED;
        private double gravity;
        private int count = 1;
        private double spread;
        private double spawnAbove;
        private double aoeRadius;
        private float aoeDamage;
        private float refundChance;
        private @Nullable Supplier<MobEffect> selfEffect;
        private int selfEffectTicks;

        private Builder(ResourceLocation particle) {
            this.particle = particle;
        }

        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder cost(float soulCost, int cooldown) {
            this.soulCost = soulCost;
            this.cooldown = cooldown;
            return this;
        }

        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder gravity(double gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder count(int count, double spread) {
            this.count = count;
            this.spread = spread;
            return this;
        }

        public Builder spawnAbove(double spawnAbove) {
            this.spawnAbove = spawnAbove;
            return this;
        }

        public Builder aoe(double aoeRadius, float aoeDamage) {
            this.aoeRadius = aoeRadius;
            this.aoeDamage = aoeDamage;
            return this;
        }

        public Builder refund(float refundChance) {
            this.refundChance = refundChance;
            return this;
        }

        public Builder selfEffect(Supplier<MobEffect> effect, int ticks) {
            this.selfEffect = effect;
            this.selfEffectTicks = ticks;
            return this;
        }

        public SoulProjectileSkill build(ResourceLocation icon, ResourceLocation id) {
            return new SoulProjectileSkill(icon, id, this);
        }
    }
}
