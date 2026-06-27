package org.confluence.mod.common.entity.boss.hillofflesh;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.boss.AbstractTerraBossBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.*;

public class HillOfFlesh extends AbstractTerraBossBase implements Boss {
    public final float innerRadius;
    public final float outerRadius;
    public final int height;
    private final float firePillarDamage;
    private final int summonFleshSlimeCount;
    private final int summonLeechCount;
    private final int summonFirePillarCount;

    float magicDamageInner;
    float magicDamageOuter;
    float magicDamageAttach;


    public int spawnTick = 150;
    int switchCount = 0;
    int spawnLavaParticleCount = 50;
    boolean consumeBreakBlocks = false;
    public float lastRadius = 0;
    HillOfFleshPart[] subEntities;
    Map<String, HillOfFleshPart> namePartMap;
    static final BiMap<EntityDataAccessor<Integer>, Integer> targetMap = HashBiMap.create();
    static final BiMap<EntityDataAccessor<Integer>, Integer> minionMap = HashBiMap.create();
    //    FSMGoal<HillOfFlesh> fsmGoal;
    Set<LivingEntity> innerEntities;
    List<LivingEntity> nearbyLivings;
    SummonLeechGoal summonLeechGoal;
    SummonFleshSlimeGoal summonFleshSlimeGoal;
    GenerateFirePillar generateFirePillarGoal;
    EfficientCylinderDestruction task;

    SkillParams skillParams;
    public int expandingTick = 0;
    public final int expandingDuration = 20 * 30;

    private static final EntityDataAccessor<Boolean> DATA_INIT = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_TARGET_0 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_1 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_2 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_3 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_4 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_5 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_6 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_7 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_8 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_9 = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_OUT_RADIUS = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.FLOAT);
//    private static final EntityDataAccessor<Float> DATA_INNER_RADIUS = SynchedEntityData.defineId(HillOfFlesh.class, EntityDataSerializers.FLOAT);


    static {
        targetMap.put(DATA_TARGET_0, 0);
        targetMap.put(DATA_TARGET_1, 1);
        targetMap.put(DATA_TARGET_2, 2);
        targetMap.put(DATA_TARGET_3, 3);
        targetMap.put(DATA_TARGET_4, 4);
        targetMap.put(DATA_TARGET_5, 5);
        targetMap.put(DATA_TARGET_6, 6);
        targetMap.put(DATA_TARGET_7, 7);
        targetMap.put(DATA_TARGET_8, 8);
        targetMap.put(DATA_TARGET_9, 9);

    }

    public HillOfFlesh(EntityType<? extends HillOfFlesh> type, Level level) {
        super(type, level);
        this.subEntities = new HillOfFleshPart[10];
        this.namePartMap = new HashMap<>();
        this.addPart(new HillOfFleshEye(this, "Eye0", 3, 3));
        this.addPart(new HillOfFleshEye(this, "Eye1", 3, 3));
        this.addPart(new HillOfFleshEye(this, "Eye2", 3, 3));
        this.addPart(new HillOfFleshEye(this, "Eye3", 3, 3));
        this.addPart(new HillOfFleshEye(this, "Eye4", 3, 3));

        this.addPart(new HillOfFleshMouth(this, "Mouth0", 4, 4));
        this.addPart(new HillOfFleshMouth(this, "Mouth1", 4, 4));
        this.addPart(new HillOfFleshMouth(this, "Mouth2", 4, 4));
        this.addPart(new HillOfFleshMouth(this, "Mouth3", 4, 4));
        this.addPart(new HillOfFleshMouth(this, "Mouth4", 4, 4));
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);

        setNoGravity(false);
//        this.fsmGoal = new HillOfFleshFSMGoal(this, DATA_SKILL_INDEX);
        this.innerEntities = new HashSet<>();
        this.nearbyLivings = new ArrayList<>();

        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.HILL_OF_FLESH_PARAMS);

        this.firePillarDamage = this.difficultSelector.switchBy(skillParams.firePillarDamage);
        this.summonLeechCount = this.difficultSelector.switchBy(skillParams.summonLeechCount);
        this.summonFleshSlimeCount = this.difficultSelector.switchBy(skillParams.summonFleshSlimeCount);
        this.summonFirePillarCount = this.difficultSelector.switchBy(skillParams.summonFirePillarCount);
        this.magicDamageInner = skillParams.magicDamageInner;
        this.magicDamageOuter = skillParams.magicDamageOuter;
        this.magicDamageAttach = skillParams.magicDamageAttach;
        this.innerRadius = skillParams.innerRadius;
        this.outerRadius = skillParams.outerRadius;
        this.height = (int) skillParams.height;
        this.xpReward = skillParams.xpReward;

        if (!this.level().isClientSide) {
            this.summonLeechGoal.setMaxCount(this.summonLeechCount);
            this.summonFleshSlimeGoal.setMaxCount(this.summonFleshSlimeCount);
            this.generateFirePillarGoal.setMaxCount(this.summonFirePillarCount);
        }

    }

    public record SkillParams(List<Integer> firePillarDamage,
                              List<Integer> summonLeechCount,
                              List<Integer> summonFleshSlimeCount,
                              List<Integer> summonFirePillarCount,
                              float magicDamageInner,
                              float magicDamageOuter,
                              float magicDamageAttach,
                              float outerRadius,
                              float innerRadius,
                              float height,
                              int xpReward
    ) {
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                TECodecs.INT_LIST_CODEC.fieldOf("fire_pillar_damage").forGetter(SkillParams::firePillarDamage),
                TECodecs.INT_LIST_CODEC.fieldOf("summon_leech_count").forGetter(SkillParams::summonLeechCount),
                TECodecs.INT_LIST_CODEC.fieldOf("summon_flesh_slime_count").forGetter(SkillParams::summonFleshSlimeCount),
                TECodecs.INT_LIST_CODEC.fieldOf("summon_fire_pillar_count").forGetter(SkillParams::summonFirePillarCount),
                Codec.FLOAT.fieldOf("magic_damage_inner").forGetter(SkillParams::magicDamageInner),
                Codec.FLOAT.fieldOf("magic_damage_outer").forGetter(SkillParams::magicDamageOuter),
                Codec.FLOAT.fieldOf("magic_damage_attach").forGetter(SkillParams::magicDamageAttach),
                Codec.FLOAT.fieldOf("outer_radius").forGetter(SkillParams::outerRadius),
                Codec.FLOAT.fieldOf("inner_radius").forGetter(SkillParams::innerRadius),
                Codec.FLOAT.fieldOf("height").forGetter(SkillParams::height),
                Codec.INT.fieldOf("xp_reward").forGetter(s->s.xpReward)

        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams() {
            return new SkillParams(
                    List.of(14, 17, 20, 25),
                    List.of(5, 6, 7, 8),
                    List.of(5, 6, 7, 8),
                    List.of(5, 6, 7, 8),
                    40.0f, 40.0f, 10.0f,
                    75.0f, 14.0f,
                    100.0f, 5000
            );
        }

    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return super.canBeSeenAsEnemy() && !this.isSpawning();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.level().getDifficulty() != Difficulty.PEACEFUL && target.canBeSeenAsEnemy()
                && !(target.getType().is(TETags.EntityTypes.FLESH_ALLIANCE));
    }

    static class SummonLeechGoal extends SummonGoal<HillOfFlesh, BaseWorm> {

        public SummonLeechGoal(HillOfFlesh mob, int interval, int maxCount) {
            super(mob, interval, maxCount);
        }

        @Override
        protected BaseWorm createMinion(LivingEntity target, ServerLevel serverLevel) {
            return TEUtils.spawnEntity(() -> new BaseWorm(TEMonsterEntities.LEECH.get(), this.mob.level(), AbstractPrefab.WARM_BUILDER.get()) {
                @Override
                public BaseWormPart createPart(int index) {
                    return new BaseWormPart(this, index);
                }

                @Override
                public boolean hurt(DamageSource source, float amount) {
                    if (source.getEntity() != null && source.getEntity().is(SummonLeechGoal.this.mob)) {
                        return false;
                    }
                    return super.hurt(source, amount);
                }

                @Override
                public boolean canAttack(LivingEntity entity) {
                    return !entity.getType().is(TETags.EntityTypes.FLESH_ALLIANCE) && SummonLeechGoal.this.mob.canAttack(entity);
                }

                @Override
                public void onRemovedFromLevel() {
                    super.onRemovedFromLevel();
                    SummonLeechGoal.this.count--;
                }

                @Override
                protected void registerGoals() {
                    super.registerGoals();
                    this.targetSelector.addGoal(2, new MutableRangeNearestAttackableTargetGoal<>(this, Player.class, false, LivingEntity::canBeSeenAsEnemy));
                }

                @Override
                protected float getMoveSpeedModifier() {
                    return 2.0f;
                }

                @Override
                public boolean fireImmune() {
                    return true;
                }

            }, serverLevel, this.mob.position().add(this.mob.getForward().normalize().scale(1)));
        }

        @Override
        protected void onMinionSpawned(BaseWorm minion, LivingEntity target) {
            minion.setTarget(target);
            minion.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(
                    TerraEntity.space("hill"), 50, AttributeModifier.Operation.ADD_VALUE
            ));
            minion.level().playSound(null, minion.blockPosition(), TESounds.WALL_OF_FLESH_SUMMON.get(), SoundSource.HOSTILE, 1, 1);
        }

        @Override
        public boolean canUse() {
            return mob.getStage() > 1 && mob.getTarget() != null && mob.tickCount > this.mob.spawnTick;
        }
    }

    static class SummonFleshSlimeGoal extends SummonGoal<HillOfFlesh, FleshSlime> {
        int mouth = 0;
        HillOfFleshMouth currentMouth;

        public SummonFleshSlimeGoal(HillOfFlesh mob, int interval, int maxCount) {
            super(mob, interval, maxCount);
        }

        @Override
        protected FleshSlime createMinion(LivingEntity target, ServerLevel serverLevel) {
            this.mouth++;
            this.mouth = this.mouth % 5 + 5;
            this.setCdReduce(0.9f); // todo debug
            Vec3 spawnPos;
            if (this.mob.subEntities[mouth] != null) {
                currentMouth = (HillOfFleshMouth) this.mob.subEntities[mouth];
                spawnPos = currentMouth.position().offsetRandom(this.mob.random, 0.2f);
            } else {
                spawnPos = this.mob.position().offsetRandom(this.mob.random, 3);
            }
            return TEUtils.spawnEntity(() -> new FleshSlime(TEMonsterEntities.FLESH_SLIME.get(), serverLevel, 0xFF0000,
                    2 * Math.round(this.mob.currentScale)){
                        @Override
                        public void onRemovedFromLevel() {
                            super.onRemovedFromLevel();
                            SummonFleshSlimeGoal.this.count--;
                        }
                    }, serverLevel, spawnPos);
        }

        @Override
        protected void onMinionSpawned(FleshSlime minion, LivingEntity target) {
            minion.setTarget(target);
            minion.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(
                    TerraEntity.space("hill"), 50, AttributeModifier.Operation.ADD_VALUE
            ));
            currentMouth.onSummonFleshSlime(minion);
            minion.getAttribute(Attributes.MAX_HEALTH).setBaseValue(minion.getMaxHealth() * Math.round(this.mob.currentScale));
            minion.setHealth(minion.getMaxHealth());
            minion.level().playSound(null, minion.blockPosition(), TESounds.WALL_OF_FLESH_SUMMON.get(), SoundSource.HOSTILE, 1, 1);
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.tickCount > this.mob.spawnTick;
        }
    }

    static class GenerateFirePillar extends CdGoal<HillOfFlesh> {

        public GenerateFirePillar(HillOfFlesh mob, int interval, int maxCount) {
            super(mob, interval, maxCount);
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && this.mob.getStage() > 1;
        }

        @Override
        protected void doAction(LivingEntity target) {
            int size = this.mob.nearbyLivings.size(); // MT: Resolve IndexOutOfBoundsException, replace: innerEntities -> nearbyLivings
            int random = this.mob.random.nextInt(5);
            size = Math.min(size, random);
            List<LivingEntity> newList = new ArrayList<>(this.mob.nearbyLivings);
            Collections.shuffle(newList);
            List<LivingEntity> targets = newList.subList(0, size);
            for (LivingEntity living : targets) {
                double delta = living.getY() - this.mob.getY();
                if (delta < 3) {
                    LavaPillar proj = TEProjectileEntities.LAVA_PILLAR.get().create(this.mob.level());
                    if (proj != null) {
                        proj.setDamage(this.mob.firePillarDamage);
                        proj.setPos(new Vec3(living.position().x, Math.min(this.mob.getY(), living.getY()), living.position().z));
                        this.mob.level().addFreshEntity(proj);
                    }
                }
            }
        }
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> dynamic) {
        return this.brainProvider().makeBrain(dynamic);
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < this.outerRadius * this.outerRadius * 2;
    }

    @Override
    protected Brain.@NotNull Provider<Frog> brainProvider() {
        return Brain.provider(List.of(), List.of());
    }

    @Override
    protected double getDefaultGravity() {
        return this.isSpawning() ? 0.0f : super.getDefaultGravity();
    }


    @Override
    protected void registerGoals() {
        this.summonLeechGoal = new SummonLeechGoal(this, 300, 5);
        this.summonFleshSlimeGoal = new SummonFleshSlimeGoal(this, 300, 5);
        this.generateFirePillarGoal = new GenerateFirePillar(this, 100, 5);
        this.goalSelector.addGoal(0, this.summonLeechGoal);
        this.goalSelector.addGoal(0, this.summonFleshSlimeGoal);
        this.goalSelector.addGoal(0, this.generateFirePillarGoal);

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_INIT, true);
        targetMap.forEach((key, value) -> builder.define(key, -1));

        builder.define(DATA_OUT_RADIUS, 10f);
//        builder.define(DATA_INNER_RADIUS, 10f);
    }

    public float getOutRadius() {
        return this.entityData.get(DATA_OUT_RADIUS);
    }

    public float getInnerRadius(float partialTicks) {
        if(!this.isExpert()){
            return this.innerRadius;
        }
        // 专家模式会变大
        return Mth.lerp(this.getExpandingProgress(partialTicks), this.innerRadius, this.outerRadius * 0.25f);
    }

    public void setOutRadius(float radius) {
        this.entityData.set(DATA_OUT_RADIUS, Mth.clamp(radius, this.innerRadius, this.outerRadius));
    }

    public float getExpandingScale(float partialTicks){
        return this.getInnerRadius(partialTicks) / this.innerRadius * 2f;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        Integer index = targetMap.get(key);
        if (index != null) {
            if (level().isClientSide) {
                int id = (int) this.entityData.get(key);
                if (id != -1) {
                    if (this.level().getEntity(id) instanceof LivingEntity living) {
                        this.subEntities[index].changeTarget(living);
                        if (living instanceof Player player) {
                            this.applyCrimsonStorm(player);
                        }
                    } else {
                        this.subEntities[index].target = null;
                    }
                } else {
                    this.subEntities[index].target = null;
                }
            }
        }
        if(level().isClientSide && key==DATA_INIT){
            if(!this.entityData.get(DATA_INIT)) {
                this.setOutRadius(this.outerRadius);
            }
        }

    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
//        if(!level().isClientSide){
//            this.goalSelector.addGoal(0, fsmGoal);
//        }
        for (var entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.innerRadius))) {
            this.applyCrimsonStorm(entity);
        }
    }

    void setHunger(TheHungry hungry, int index){
        this.entityData.set(minionMap.inverse().get(index), hungry.getId());
    }

    TheHungry getHunger(int index){
        return this.level().getEntity(this.entityData.get(minionMap.inverse().get(index))) instanceof TheHungry hungry? hungry : null;
    }

    void setTarget(int index, @Nullable LivingEntity target) {
        this.setTarget(index, target, true);
    }

    void setTarget(int index, @Nullable LivingEntity target, boolean applyCrimsonStorm) {
        if (!level().isClientSide) {
            this.subEntities[index].target = target;
            this.entityData.set(targetMap.inverse().get(index), target == null ? -1 : target.getId());
            if (target != null && applyCrimsonStorm) {
                this.innerEntities.add(target);
                this.applyCrimsonStorm(target);
            }
        }
    }

    public void loadTheHungry(int index, TheHungry hungry){
        if(index > 4) {
            this.setTarget(index, hungry, false);
        }
    }

    @Override
    public void aganinSpawn() {
        this.entityData.set(DATA_INIT, false);

    }

    public boolean isSpawning() {
        return this.getSpawnProgress(1) < 1.0f;
    }

    public float getSpawnProgress(float partialTicks) {
        if (!this.entityData.get(DATA_INIT)) {
            return 1.0f;
        }
        float progress = (this.tickCount + partialTicks) / spawnTick;
        return Math.min(progress, 1.0f);
    }

    public float getExpandingProgress(float partialTicks) {
        return Mth.clamp((this.expandingTick + partialTicks) / this.expandingDuration, 0, 1);
    }

    public float getSwitchProgress(float partialTicks) {
        return Mth.clamp((this.switchCount + partialTicks) / 50f, 0, 1);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    private void addPart(HillOfFleshPart part) {
        this.subEntities[this.namePartMap.size()] = part;
        this.namePartMap.put(part.name, part);
    }

    public HillOfFleshPart getPart(String name) {
        return this.namePartMap.get(name);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; ++i) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    @Override
    public void firstSpawn() {
        this.consumeBreakBlocks = true;
    }

    @Override
    public void addSkills() {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 10, state -> {
            if (this.tickCount % 40 == 0) {
                state.resetCurrentAnimation();
                state.animationTick = 0;
            }
            if (this.deathTime <= 0) {
                return state.setAndContinue(DefaultAnimations.IDLE);
            }
            return PlayState.STOP;
        }));

    }

    public boolean hurt(HillOfFleshPart part, DamageSource source, float damage) {
//        this.setHealth(this.getHealth() - damage);
//        return true;
        return this.hurt(source, damage * 0.5f);
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.getEffect() == TEEffects.CRIMSON_STORM) {
            return false;
        }
        return super.addEffect(effectInstance, entity);
    }

    private void applyCrimsonStorm(LivingEntity living) {
        living.getData(TEAttachments.UNSYNC).setFightingHillOfFlesh(this);
        living.addEffect(new MobEffectInstance(TEEffects.CRIMSON_STORM, 200, 0), this);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getDirectEntity() instanceof LivingEntity living){
            this.applyCrimsonStorm(living);
        }
        // 必须进入过肉山空间才能造成伤害
        if (pSource.getEntity() != null && !this.innerEntities.contains(pSource.getEntity())) {
            return false;
        }
        return super.hurt(pSource, pAmount * 0.5f);// 用于打到嘴和眼睛增伤
    }

    private void doMagicDamage() {
        if (this.tickCount % 32 == 0) {
            List<LivingEntity> toRemove = new ArrayList<>();
            for (LivingEntity living : this.innerEntities) {
                double distance = living.position().subtract(this.position()).horizontalDistanceSqr();
                float innerRadius = this.getInnerRadius(0.5f);
                if (distance > this.getOutRadius() * this.getOutRadius()) {
                    living.hurt(TETags.DamageTypes.of(level(), DamageTypes.MAGIC), this.magicDamageOuter);
                    this.generateParticles(living, 10);

                } else if (distance < (innerRadius - 5) * (innerRadius - 5)) {
                    living.hurt(TETags.DamageTypes.of(level(), DamageTypes.MAGIC), this.magicDamageAttach);
                    living.getData(TEAttachments.UNSYNC).triggerInvulnerableStorm(living);
                    this.generateParticles(living, 30);

                } else if (distance < innerRadius * innerRadius) {
                    living.hurt(TETags.DamageTypes.of(level(), DamageTypes.MAGIC), this.magicDamageInner);
                    living.getData(TEAttachments.UNSYNC).triggerInvulnerableStorm(living);
                    this.generateParticles(living, 20);

                }
                if (!living.isAlive()) {
                    toRemove.add(living);
                }
            }
            toRemove.forEach(this.innerEntities::remove);
        }

    }


    private void generateParticles(LivingEntity living, int count) {
        float w = living.getBbWidth();
        float h = living.getBbHeight();
        ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,
                living.getRandomX(0.1),
                living.getRandomY(),
                living.getRandomZ(0.1), (int) (w * h * w * count), w * 0.2, h * 0.5, w * 0.2, 0);
    }

    float circleDx = 0.0f;
    float circleOuterDx = 0.0f;

    private void showCircleParticle(Vec3 center, float radius, float angle, float da) {
        float x = (float) (center.x + radius * Math.cos(angle + da));
        float y = (float) (center.y);
        float z = (float) (center.z + radius * Math.sin(angle + da));
        this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
    }

    private void showCircleParticles(Vec3 center, float radius, float angle, float da, int interval) {
        float d = (float) (Math.PI * 2 / interval);
        for (int i = 0; i < interval; ++i) {
            this.showCircleParticle(center, radius, angle, da + i * d);
        }
    }

    private void getNearbyLivings() {
        if (this.tickCount % 64 == 0) {
            this.nearbyLivings = new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getOutRadius() * 1.2f), e -> {
                if (!(e instanceof Player) && !this.hasLineOfSight(e)) {
                    return false;
                }
                float d = this.getOutRadius() * this.getOutRadius();
                double dx = e.position().subtract(position()).horizontalDistanceSqr();
                boolean canAttack = this.canAttack(e);
                float innerRadius = this.getInnerRadius(0.5f);
                if(dx < innerRadius * innerRadius && canAttack){
                    this.applyCrimsonStorm(e);
                }
                boolean flag = e.isAlive() && (canAttack || e.hasEffect(TEEffects.CRIMSON_STORM))
                        && e.position().distanceToSqr(position().add(0, 10, 0)) < d * 1.2
                        &&  dx <= d;
                if(flag){
                    this.innerEntities.add(e);
                }
                return flag;
            }));
            nearbyLivings.sort(Comparator.comparingDouble(this::distanceToSqr));
        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(this.getOutRadius());
    }

    private void spawnLavaParticle(int count, float speed) {
        ((ServerLevel) this.level()).sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 4, this.getZ(), count,
                5, 5, 5, speed);
    }
    public float currentScaleO = 1f;
    public float currentScale = 1f;
    @Override
    public void aiStep() {
        super.aiStep();
//        if(this.onGround()) {
//            this.jumpFromGround();
//        }
        if (level().isClientSide) {
            this.circleDx += 0.1f;
            this.showCircleParticles(this.position().add(0, 0.2, 0), this.getInnerRadius(0.5f), this.circleDx, 0, 2);
            this.circleOuterDx += 0.02f;
            if (Minecraft.getInstance().player != null && !this.isSpawning()) {
                Vec3 playerPos = Minecraft.getInstance().player.position();
                float height = (float) (playerPos.y - this.position().y);
                for (int i = -8; i <= 8; i++) {
                    this.showCircleParticles(this.position().add(0, i + height, 0), this.getOutRadius() - 0.2F, this.circleOuterDx + i * 0.03f, (float) Math.PI, 3);
                }
            }
        } else {
            this.doMagicDamage();
            this.getNearbyLivings();
            this.destroyBlocks();
            if (!this.isSpawning() && --this.spawnLavaParticleCount <= 0) {
                this.spawnLavaParticle(500, 5);
                this.spawnLavaParticleCount = 30 + this.getRandom().nextInt(70);
            }
        }


        if (this.isDeadOrDying()) {
            if (this.deathTime / this.subEntities.length % 5 == this.deathTime % this.subEntities.length) {
                int index = this.deathTime % this.subEntities.length;
                float f = (this.random.nextFloat() - 0.5F);
                float f1 = (this.random.nextFloat() - 0.5F);
                float f2 = (this.random.nextFloat() - 0.5F);
                var part = this.subEntities[index];
                this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, part.getX() + f, part.getY() + 2.0 + f1, part.getZ() + f2, 0.0, 0.0, 0.0);
            }
        } else {


            this.setYRot(Mth.wrapDegrees(this.getYRot()));
            if (this.isNoAi()) {

            } else {

                if (this.getStage() == 2) {
                    if(this.isExpert()){
                        this.expandingTick++;
                    }
                    this.switchCount++;
                }

                this.yBodyRot = this.getYRot();
                Vec3[] avec3 = new Vec3[this.subEntities.length];

                for (int j = 0; j < this.subEntities.length; ++j) {
                    avec3[j] = new Vec3(this.subEntities[j].getX(), this.subEntities[j].getY(), this.subEntities[j].getZ());

                }
                this.currentScaleO = this.currentScale;
                this.currentScale = this.getExpandingScale(0.5f);

                this.tickPart(this.subEntities[0], 8, 13, 5, 0);
                this.tickPart(this.subEntities[1], -8, 10, 8, 1);
                this.tickPart(this.subEntities[2], 7, 8, -7, 2);
                this.tickPart(this.subEntities[3], 0.5, 6.5, 8, 3);
                this.tickPart(this.subEntities[4], -3, 5.5, -6, 4);

                this.tickPart(this.subEntities[5], 0, 11, 0, 5);
                this.tickPart(this.subEntities[6], -6, 9, -3, 6);
                this.tickPart(this.subEntities[7], 8, 8, 5, 7);
                this.tickPart(this.subEntities[8], -6.5, 4, 8.5, 8);
                this.tickPart(this.subEntities[9], 7, 3, -7, 9);


                for (int k = 0; k < this.subEntities.length; ++k) {
                    this.subEntities[k].tick();
                    this.subEntities[k].xo = avec3[k].x;
                    this.subEntities[k].yo = avec3[k].y;
                    this.subEntities[k].zo = avec3[k].z;
                    this.subEntities[k].xOld = avec3[k].x;
                    this.subEntities[k].yOld = avec3[k].y;
                    this.subEntities[k].zOld = avec3[k].z;
                }
            }
        }

    }

    @Override
    public float getScale() {
        return super.getScale() * this.currentScale;
    }

    private void tickPart(HillOfFleshPart part, double offsetX, double offsetY, double offsetZ, int index) {
        offsetX *= this.currentScale;
        offsetY *= this.currentScale;
        offsetZ *= this.currentScale;
        Vec3 dir = new Vec3(offsetX, offsetY, offsetZ).yRot(-this.getYRot()* Mth.DEG_TO_RAD);
        part.setPos(this.getX() + dir.x, this.getY() + dir.y, this.getZ() + dir.z);
        part.setModelOffset(dir);
        if(this.currentScaleO != this.currentScale) {
            part.setScale(this.currentScale);
        }

        if (!this.isSpawning()) {
            if (HillOfFleshModelAnimationTable.getTable() != null) {
                Vec3KeyframeAnimation animation = HillOfFleshModelAnimationTable.getTable().getPositions(part.name);
                if (animation != null) {
                    part.setPos(position().add(animation.calWithCache((this.tickCount + 10) % 40)
                            .add(0,-1,0)
                            .scale(this.currentScale)
                            .yRot(-this.getYRot()* Mth.DEG_TO_RAD)
                    ));
                }
            }
            part.tickPart(offsetX, offsetY, offsetZ, index);
        }
    }


    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public HillOfFleshPart @NotNull [] getParts() {
        return this.subEntities;
    }


    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 180 && this.deathTime <= 200) {
            float f = (this.random.nextFloat() - 0.5F);
            float f1 = (this.random.nextFloat() - 0.5F);
            float f2 = (this.random.nextFloat() - 0.5F);
            Arrays.stream(this.subEntities).forEach(part -> {
                this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, part.getX() + f, part.getY() + 2.0 + f1, part.getZ() + f2, 0.0, 0.0, 0.0);
            });
        }
        if (this.level() instanceof ServerLevel) {
//            if (this.deathTime == 1 && !this.isSilent()) {
//                this.level().globalLevelEvent(1028, this.blockPosition(), 0);
//            }
        }
        this.move(MoverType.SELF, new Vec3(0.0, 0.10000000149011612, 0.0));
        if (this.deathTime == this.getMaxDeathTime() && this.level() instanceof ServerLevel) {
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    public int getMaxDeathTime() {
        return 200;
    }

    @Override
    public void changeState() {
        if (this.getStage() == 1 && this.getHealth() / getMaxHealth() < 0.5) {
            this.setStage(2);
            if (this.summonLeechGoal != null) {
                this.summonLeechGoal.setCdReduce(0.3f);
            }
            if (this.summonFleshSlimeGoal != null) {
                this.summonFleshSlimeGoal.setCdReduce(0.3f);
            }
            for (HillOfFleshPart part : this.getParts()) {
                part.onParentChangeState(this.getStage());
            }
            if (!this.level().isClientSide) {
                this.spawnLavaParticle(1000, 5);
            }
        }
        this.syncStatus(this.getStage());
    }

    @Override
    protected void initStage(int stage) {
        if (stage == 2) {
            if (this.summonLeechGoal != null) {
                this.summonLeechGoal.setCdReduce(0.3f);
            }
            if (this.summonFleshSlimeGoal != null) {
                this.summonFleshSlimeGoal.setCdReduce(0.3f);
            }
            for (HillOfFleshPart part : this.getParts()) {
                part.onParentChangeState(stage);
            }
        }
    }

    private void destroyBlocks() {
        if (this.consumeBreakBlocks && this.tickCount == 75) {
            BlockPos pos = BlockPos.containing(this.getBoundingBox().getCenter());
            this.task = new EfficientCylinderDestruction(level(), pos.getX(), pos.getZ(), this.blockPosition().getY()-1, pos.getY() + this.height-1, (int) this.outerRadius + 1);
            this.task.startDestruction();
        }
        if (this.task != null) {

            task.onTick(1);
            this.setOutRadius(task.getCurrentRadius());

            if (task.tryStopDestruction()) {
                task = null;
            }
        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("OutRadius", this.getOutRadius());

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("OutRadius")){
            this.setOutRadius(tag.getFloat("OutRadius"));
            this.setOutRadius(this.getOutRadius());
        }
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return TESounds.WALL_OF_FLESH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.WALL_OF_FLESH_ROAR.get();
    }

}
