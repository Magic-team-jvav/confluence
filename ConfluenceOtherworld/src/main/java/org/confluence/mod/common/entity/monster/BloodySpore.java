package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonstersEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 血腥芽孢
public class BloodySpore extends Creeper implements GeoEntity {
    private int oldSwell;
    private int swell;
    private final int maxSwell = 30;

    public BloodySpore(EntityType<? extends Creeper> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 20;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        return spawnReason == MobSpawnType.NATURAL; // 无视光照
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.ARMOR, 6)
                .add(Attributes.FOLLOW_RANGE, 32)             // 跟随距离
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.01)  // 召唤物品的几率
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);     // 击退抗性
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BLOODY_SPORE_DEATH.get();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (SoundEvents.CREEPER_PRIMED == sound) {
            sound = ModSoundEvents.BLOODY_SPORE_FUSE.get();
        }
        super.playSound(sound, volume, pitch);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.BLOODY_SPORE_HIT.get();
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int $$0 = this.getSwellDir();
            if ($$0 > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }

            this.swell += $$0;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    private void explodeCreeper() {
        if (!this.level().isClientSide) {
            int $$0 = this.isPowered() ? 2 : 1;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 4.2F * $$0, Level.ExplosionInteraction.NONE);
            int number = (random.nextInt(2, 4)) * $$0;
            float f = this.random.nextFloat() * 2;
            for (int i = 0; i < number; i++) {
                //summon
                Entity summon = MonstersEntities.BLOOD_TUMORS.get().create(level());
                if (summon != null) {
                    summon.setPos(this.getX(), this.getY(), this.getZ());

                    Vec3 dir = new Vec3(Math.sin((f * +i * 1) * 3.14159) * 0.3, random.nextDouble() * 0.5 + 0.2f, Math.cos((f * +i * 1) * 3.14159) * 0.3);
                    summon.addDeltaMovement(dir);
                    level().addFreshEntity(summon);
                }
            }
            this.discard();
        }
    }


    @Override
    public float getSwelling(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkController(this));
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_STRIKE));
    }
}
