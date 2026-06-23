package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 血爬虫
 */
public class BloodCrawler extends Spider implements GeoEntity {
    private static final int ATTACK_DAMAGE = 15;
    private static final int MAX_HEALTH = 31;
    private static final int ARMOR = 8;

    public BloodCrawler(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.setHealth(MAX_HEALTH);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        return spawnReason == MobSpawnType.NATURAL; // 无视光照
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.targetSelector.removeAllGoals(a->!(a instanceof HurtByTargetGoal));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class,false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), ATTACK_DAMAGE)  // 攻击力
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)        // 生命值
                .add(Attributes.ARMOR, ARMOR)                 // 防御值
                .add(Attributes.MOVEMENT_SPEED, 0.38)          // 移动速度
                .add(Attributes.FOLLOW_RANGE, 32)             // 跟随距离
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.01)  // 召唤物品的几率
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);     // 击退抗性
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BLOOD_CRAWLER_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.BLOOD_CRAWLER_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return ModSoundEvents.BLOOD_CRAWLER_HURT.get();
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
