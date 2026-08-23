package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 会瞬移并投掷火球的地狱法师。
///
/// <p>火焰免疫、环境火星和火球点燃效果均属于该生物自身，不由通用法师基类猜测。</p>
public class FireImp extends BaseCasterMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation CAST = RawAnimation.begin().thenLoop("attack.cast");

    public FireImp(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCasterMonster.createCasterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    protected EntityType<HostileParticleProjectile> projectileType() {
        return ModEntities.FIRE_IMP_PROJECTILE.get();
    }

    /// 火焰小鬼资源没有普通行走动画，移动和等待阶段都维持悬浮待机。
    ///
    /// <p>施法时仍使用与其他法师一致的挥手窗口。这里覆盖通用法师控制器，避免移动时
    /// 请求不存在的 {@code move.walk} 并持续输出 GeckoLib 警告。</p>
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "imp_state", 5, state -> state.setAndContinue(swingTime > 0 ? CAST : IDLE)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            return;
        }
        if (random.nextInt(24) == 0 && !isSilent()) {
            level().playLocalSound(getX() + 0.5, getY() + 0.5, getZ() + 0.5, SoundEvents.BLAZE_BURN, getSoundSource(), 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
        level().addParticle(ParticleTypes.FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.02, 0.0);
    }
}
