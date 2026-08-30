package org.confluence.mod.common.entity.animal;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// 小动物基类 —— 不可繁殖、无食物、行为树驱动。
public abstract class BaseCritter extends Animal implements GeoEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean behaviorTreeRegistered;

    public BaseCritter(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        String key = variantSaveKey();
        if (key != null && (tag == null || !tag.contains(key))) {
            initializeSpawnVariant();
        }
        return result;
    }

    /// 用于区分“明确请求的变体”和普通自然生成的 NBT 键。
    protected @Nullable String variantSaveKey() {
        return null;
    }

    /// 为没有显式变体数据的自然生成实体选择初始外观。
    ///
    /// 具有变体的环境生物只需覆盖该方法；已有 NBT 明确指定变体时不会再次随机选择。
    protected void initializeSpawnVariant() {}

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide && !behaviorTreeRegistered) {
            BTRoot behaviorTree = Objects.requireNonNull(createBT(), () -> "Missing behavior tree for " + getType());
            goalSelector.addGoal(0, behaviorTree);
            behaviorTreeRegistered = true;
        }
    }

    /// 子类重写以提供自己的行为树
    protected abstract BTRoot createBT();

    /// 为被动小动物包装可抢占的恐慌分支。
    ///
    /// 原版 Panic 使用受伤时间戳区分一次新的攻击；不能只检查攻击者引用，否则小动物会在
    /// 受击结束后仍永久重复逃跑。优先级选择器每 tick 探测该目标，既能立即抢占巡游，也会
    /// 在一轮逃生完成后恢复日常行为，并保留着火时寻找水源等原版处理。
    protected final BTNode withPassivePanic(BTNode routine, double panicSpeed) {
        return SelectorNode.of(new VanillaGoalAction(new PanicGoal(this, panicSpeed)), routine);
    }

    /// 创建 1.21 地面小动物共用的日常行为。
    ///
    /// 漂浮始终具有最高优先级；物种可把繁殖、食物吸引或跟随亲代等动作插入其后；
    /// 最后再执行避水巡游、观察玩家和随机转头。共享顺序集中在基类中，新增同类生物
    /// 不需要复制一整套原版动作，也不会遗漏落水逃生。
    protected final BTNode createGroundCritterRoutine(double strollSpeed, BTNode... speciesActions) {
        List<BTNode> actions = new ArrayList<>();
        actions.add(new VanillaGoalAction(new FloatGoal(this)));
        actions.addAll(List.of(speciesActions));
        actions.add(new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, strollSpeed)));
        actions.add(new VanillaGoalAction(new LookAtPlayerGoal(this, Player.class, 6.0F)));
        actions.add(new VanillaGoalAction(new RandomLookAroundGoal(this)));
        return new SelectorNode(actions);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    /// 地面小动物沿用 1.21 简单动物与兔类的较低声音音量。
    ///
    /// 飞行动物和鸭子的原版继承值不同，由对应中间基类或具体实体覆盖；
    /// 这样新增地面小动物无需重复声明相同常量。
    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    public static AttributeSupplier.Builder createCritterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18);
    }

    /// 创建昆虫与同尺寸小型生物的基础属性。
    ///
    /// 该配置独立于普通小动物，避免新增昆虫时误用十点生命的通用配置。
    public static AttributeSupplier.Builder createInsectAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(PortAttributesExtension.stepHeight().get(), 0.3);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkController(this));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    public ResourceLocation getModelPath() {
        return getType().builtInRegistryHolder().key().location().withPrefix("geo/entity/animal/");
    }

    public ResourceLocation getTexturePath() {
        return getType().builtInRegistryHolder().key().location().withPrefix("textures/entity/animal/").withSuffix(".png");
    }
}
