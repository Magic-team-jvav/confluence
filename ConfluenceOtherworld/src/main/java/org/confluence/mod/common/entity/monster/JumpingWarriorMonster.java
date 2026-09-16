package org.confluence.mod.common.entity.monster;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.goal.EnemyOpenDoorGoal;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

/// 保存战士 AI 敌怪的可选跃击参数和接触效果。
///
/// 只有确实具有跃击技能的实体才传入跃击参数；普通战士 AI 变体传入
/// {@code null}，只复用近战追击以及该实体自己的接触减益。
public final class JumpingWarriorMonster extends BaseWarriorMonster {
    private static final AttributeModifier LOW_HEALTH_SPEED = new PortAttributeModifier(Confluence.asResource("mummy_low_health_speed"), 1.0, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).unwrap();
    private final @Nullable JumpProfile jumpProfile;
    private final ContactProfile contactProfile;

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile) {
        this(type, level, jumpProfile, LandAnimationProfile.WALK_ONLY);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile) {
        this(type, level, jumpProfile, animationProfile, LandSoundProfile.ROUTINE);
    }

    /// 创建同时具有跳跃参数、动画档案和音效档案的陆行怪物变种。
    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile) {
        this(type, level, jumpProfile, animationProfile, soundProfile, 1.0);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed) {
        this(type, level, jumpProfile, animationProfile, soundProfile, meleeSpeed, ContactProfile.NONE);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, @Nullable JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed, ContactProfile contactProfile) {
        super(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, true);
        this.jumpProfile = jumpProfile;
        this.contactProfile = contactProfile;
        if (contactProfile.isMummy() && navigation instanceof GroundPathNavigation groundNavigation) {
            groundNavigation.setCanOpenDoors(true);
            goalSelector.addGoal(-1, new EnemyOpenDoorGoal(this));
        }
    }

    @Override
    protected @Nullable JumpProfile jumpProfile() {
        return jumpProfile;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !contactProfile.isMummy()) return;
        var speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        boolean enraged = getHealth() <= getMaxHealth() * 0.5F;
        if (enraged && speed.getModifier(LOW_HEALTH_SPEED.getId()) == null) {
            speed.addTransientModifier(LOW_HEALTH_SPEED);
        } else if (!enraged) {
            speed.removeModifier(LOW_HEALTH_SPEED.getId());
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean damaged = super.doHurtTarget(target);
        if (damaged && target instanceof LivingEntity living) contactProfile.apply(this, living);
        return damaged;
    }

    private int scaledDuration(int classicTicks) {
        if (LibUtils.isMaster(level(), blockPosition())) return classicTicks * 5 / 2;
        return LibUtils.isAtLeastExpert(level(), blockPosition()) ? classicTicks * 2 : classicTicks;
    }

    /// 只描述同一套木乃伊近战行为之间的接触效果差异，不依赖实体注册名反查逻辑。
    public enum ContactProfile {
        NONE(false) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {}
        },
        MUMMY(true) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                if (mob.random.nextInt(8) == 0)
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, mob.scaledDuration(300)), mob);
            }
        },
        DARK_MUMMY(true) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                if (mob.random.nextInt(5) == 0)
                    target.addEffect(new MobEffectInstance(ModEffects.SILENCED.get(), mob.scaledDuration(140)), mob);
                if (mob.random.nextInt(4) == 0)
                    target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, mob.scaledDuration(300)), mob);
            }
        },
        BLOOD_MUMMY(true) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                if (mob.random.nextInt(5) == 0)
                    target.addEffect(new MobEffectInstance(ModEffects.SILENCED.get(), mob.scaledDuration(140)), mob);
                if (mob.random.nextInt(4) == 0)
                    target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, mob.scaledDuration(300)), mob);
            }
        },
        LIGHT_MUMMY(true) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                if (mob.random.nextInt(14) == 0)
                    target.addEffect(new MobEffectInstance(LibEffects.CONFUSED.get(), mob.scaledDuration(100)), mob);
            }
        },
        VILE_GHOUL(false) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                target.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO.get(), mob.scaledDuration(140)), mob);
            }
        },
        TAINTED_GHOUL(false) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                target.addEffect(new MobEffectInstance(ModEffects.ICHOR.get(), mob.scaledDuration(140)), mob);
            }
        },
        DREAMER_GHOUL(false) {
            @Override
            void apply(JumpingWarriorMonster mob, LivingEntity target) {
                target.addEffect(new MobEffectInstance(LibEffects.CONFUSED.get(), mob.scaledDuration(280)), mob);
            }
        };

        private final boolean mummy;

        ContactProfile(boolean mummy) {
            this.mummy = mummy;
        }

        boolean isMummy() {
            return mummy;
        }

        abstract void apply(JumpingWarriorMonster mob, LivingEntity target);
    }
}
