package org.confluence.terraentity.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import org.confluence.terraentity.init.TEEntities;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BloodySpore extends Creeper implements GeoEntity {
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;

    public BloodySpore(EntityType<? extends Creeper> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.FOLLOW_RANGE, 32)             // 跟随距离
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.01)  // 召唤物品的几率
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);     // 击退抗性
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

            for (int i = 0; i < number; i++){
                //summon
                Entity summon = TEEntities.BLOOD_TUMORS.get().create(level());
                summon.setPos(this.getX(), this.getY(), this.getZ());
                float f = this.random.nextFloat();
                Vec3 dir = new Vec3(Math.sin(f*360)*0.3,f*0.5+0.2f,Math.cos(f*360)*0.3);
                summon.addDeltaMovement(dir);
                level().addFreshEntity(summon);
            }
            this.discard();
        }
    }
    public static boolean checkBloodySporeSpawn(EntityType<? extends BloodySpore> type, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false; // 如果不满足基本生成规则，返回 false
        }

        int y = pPos.getY();
        return y < 260; // 不能生成在 y = 260 或更高的位置
    }

    @Override
    public float getSwelling(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
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
