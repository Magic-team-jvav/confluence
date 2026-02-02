package org.confluence.terraentity.entity.summon;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IMeleeAttackPartGoal;
import org.confluence.terraentity.api.entity.IOriented;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.api.entity.ai.ISkill;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.entity.ai.goal.skill.SkillCooldownManager;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;
import org.confluence.terraentity.entity.util.KeyframeAnimationCounter;
import org.confluence.terraentity.entity.util.trail.SummonSwordTrail;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.TEEntityDataSerializers;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.utils.OBB;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SummonSword extends AbstractSummonMob implements IOriented, FlyingAnimal {

    public SummonSwordTrail trail;

    public int sequence;
    public Item modelItem;
    IEffectStrategy effectStrategy;

    public int backTicks;
    public int backTicksMax = 20;

    protected int rgb;

    // 服务端控制客户端的x旋转(对于模型剑和贴图剑，x和z轴是反的)
    public KeyframeAnimationCounter anim_x;



    protected SkillCooldownManager cooldownManager;

    protected static final EntityDataAccessor<Boolean> DATA_BACK = SynchedEntityData.defineId(SummonSword.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_SEQUENCE = SynchedEntityData.defineId(SummonSword.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<KeyframeAnimationCounter> DATA_KEYFRAME_X = SynchedEntityData.defineId(SummonSword.class, TEEntityDataSerializers.KEYFRAME_ANIMATION_SERIALIZER.get());


    public SummonSword(EntityType<? extends TamableAnimal> entityType,  Level level,Supplier<Item> modelItem,  int rgb) {
        this(entityType, level, modelItem, rgb, null, 0.15f);
    }
    public SummonSword(EntityType<? extends TamableAnimal> entityType, Level level, Supplier<Item> modelItem,  int rgb, IEffectStrategy effectStrategy, float width) {
        super(entityType, level);
        this.noPhysics = true;
//        this.setNoGravity(true);
        this.setDiscardFriction(true);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(16);
        this.getAttribute(Attributes.GRAVITY).setBaseValue(0.0);
        if(modelItem!= null) {
            this.modelItem = modelItem.get();
        }

        this.rgb = rgb;
        this.trail = new SummonSwordTrail(1, width, rgb);
        this.effectStrategy = effectStrategy;

    }

    public int getRgb() {
        return rgb;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
//        builder.define(DATA_ROTATE_Z_ID, 0.0f);
        builder.define(DATA_BACK, false);
        builder.define(DATA_SEQUENCE, 0);
        builder.define(DATA_KEYFRAME_X, new KeyframeAnimationCounter(0, KeyframeAnimation.builder()
                .addKeyframe(0,0)
                .addKeyframe(1,0)
                .build()));

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_BACK){
            boolean back = this.entityData.get(DATA_BACK);
            if(!back) {
                this.backTicks = 20;
            }else{
                this.backTicks = 0;
            }

        }else if(key == DATA_SEQUENCE){
            this.sequence = this.entityData.get(DATA_SEQUENCE);
        }else if(key == DATA_KEYFRAME_X){
            this.anim_x = this.entityData.get(DATA_KEYFRAME_X);
            this.anim_x.setStartTime(tickCount );
        }

    }


    @Override
    protected void registerGoals() {
        this.cooldownManager = new SkillCooldownManager();
        SwordSlashGoal skill1 = new SwordSlashGoal(this, 1, 10, 150);
        this.cooldownManager.addSkill(skill1);
        this.goalSelector.addGoal(0, skill1);

        registerCommonSwordGoals();
    }

    protected void registerCommonSwordGoals(){
        this.goalSelector.addGoal(1, new SwordAttackGoal(this));
        this.goalSelector.addGoal(2, new SwordFollowOwnerGoal(this));
        this.summon_registerTargetGoals();
    }

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            if(this.entityData.get(DATA_BACK)){
                this.backTicks++;
                this.trail.trailsQueue.poll();
//                this.trail.generateTrail(this, tickCount);
            }else{
                this.backTicks--;
                this.trail.generateTrail(this, tickCount);
            }
        }else{
            this.cooldownManager.update(1);
        }

    }

    @Override
    public float summon_getDistanceToTeleportToOwner(){
        return 16 * 16;
    }


    /**
     * 普攻ai
     */
    static class SwordAttackGoal extends Goal implements IMeleeAttackPartGoal {

        SummonSword sword;
        protected SwordAttackGoal(SummonSword sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));

        }
        @Override
        public boolean canUse() {
            Entity actualTarget = getActualTarget(sword);
            return actualTarget != null && !sword.summon_shouldTryTeleportToOwner();
        }

        @Override
        public void start() {

        }

        @Override
        public void tick(){
            Entity actualTarget = getActualTarget(sword);
            if(actualTarget == null){
                return;
            }

            // 获取目标位置（支持 PartEntity）
            Vec3 targetPos = getTargetPosition(sword, actualTarget);
            if (targetPos == null) {
                return;
            }

            // 如果目标是 LivingEntity，使用其眼睛位置；否则使用实际位置
            if (actualTarget instanceof LivingEntity living) {
                sword.lookAt(living, 30, 85);
                sword.lookControl.setLookAt(living);
            } else {
                sword.lookControl.setLookAt(targetPos.x, targetPos.y, targetPos.z);
            }

            Vec3 dir = targetPos.subtract(sword.getEyePosition()).normalize();
            double angle = TEUtils.angleBetween(sword.getLookAngle(), dir);
            if(angle < 0.05f){
                sword.addDeltaMovement(dir);
                sword.setDeltaMovement(sword.getDeltaMovement().normalize().scale(0.6f));

            }else{
                sword.setDeltaMovement(sword.getDeltaMovement().scale(0.9f));
            }

        }

        @Override
        public boolean canMeleeAttackTarget(Entity target) {
            if (target instanceof PartEntity<?> partEntity) {
                Entity parent = partEntity.getParent();
                if (parent instanceof LivingEntity living) {
                    return sword.canAttack(living);
                }
                return false;
            }
            if (target instanceof LivingEntity living) {
                return sword.canAttack(living);
            }
            return false;
        }
    }

    /**
     * 带有冷却时间的技能ai
     */
    protected static class AbstactSkillGoal<T extends SummonSword> extends Goal implements ISkill {
        protected T sword;
        protected int skillIndex;
        int ticks = 0;
        int _ticks = 0;
        protected int skillCooldown = 0;
        protected int _skillCooldown = 0;
        /**
         *
         * @param skillIndex 技能索引
         * @param ticks 持续时间
         * @param skillCooldown 技能冷却时间
         */
        protected AbstactSkillGoal(T sword, int skillIndex, int ticks, int skillCooldown) {
            this.sword = sword;
            this.skillIndex = skillIndex;
            this.setFlags(EnumSet.of(Flag.MOVE));
            this._ticks = ticks;
            this._skillCooldown = skillCooldown;
        }
        @Override
        public boolean canUse() {
            if (!sword.cooldownManager.canTriggerSkill(this)) {
                return false;
            }
            // 支持 PartEntity 目标
            if (sword instanceof IPartEntityTargetable targetable) {
                Entity actualTarget = targetable.getActualTargetEntity();
                if (actualTarget != null) {
                    return true;
                }
            }
            return sword.getTarget() != null;
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        @Override
        public boolean canContinueToUse() {
            return this.canUse() && ticks < _ticks;
        }

        /**
         * 技能结束后刷新冷却
         */
        @Override
        public void stop(){
            ticks = 0;
            sword.cooldownManager.triggerSkill(this);
        }

        @Override
        public int getCooldown() {
            return skillCooldown;
        }

        @Override
        public void update(int delta) {
            this.skillCooldown = Math.max(0, skillCooldown - delta);
        }

        @Override
        public int getIndex() {
            return skillIndex;
        }

        @Override
        public void reset() {
            this.skillCooldown = _skillCooldown + sword.getRandom().nextInt((int) (_skillCooldown * 0.3f));
        }
    }

    /**
     * 移动到目标处，挥剑向下砍
     */
    protected static class SwordSlashGoal<T extends SummonSword> extends AbstactSkillGoal<T>{

        boolean triggered = false;
        ResourceLocation attackModifier;
        protected SwordSlashGoal(T sword, int skillIndex, int ticks, int skillCooldown) {
            super(sword, skillIndex, ticks, skillCooldown);
            this.attackModifier = TerraEntity.space("skill1");
        }

        @Override
        public void tick(){

            Entity actualTarget = null;
            if (sword instanceof IPartEntityTargetable targetable) {
                actualTarget = targetable.getActualTargetEntity();
            }
            if (actualTarget == null) {
                actualTarget = sword.getTarget();
            }
            if (actualTarget == null) {
                return;
            }

            Vec3 baseTargetPos = actualTarget.getEyePosition();


            Vec3 dist = baseTargetPos.subtract(sword.position());
            Vec3 skill = new Vec3(0, 10 - ticks, 0);
            Vec3 targetPos = baseTargetPos.add(skill);
            Vec3 lookDir = targetPos.subtract(sword.getEyePosition());

            if(dist.length() > 3 && !triggered){
                sword.setDeltaMovement(dist.normalize().scale(0.5f));
                return;
            }
            sword.lookControl.setLookAt(targetPos);
            sword.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);

            triggered = true;
            ticks++;
            sword.setDeltaMovement(sword.getDeltaMovement().scale(0.7f));
        }

        @Override
        public void stop(){
            super.stop();
            triggered = false;
            this.triggerZRot();
            Objects.requireNonNull(this.sword.getAttribute(Attributes.ATTACK_DAMAGE)).removeModifier(attackModifier);

        }

        @Override
        public void start(){
            super.start();
            Objects.requireNonNull(this.sword.getAttribute(Attributes.ATTACK_DAMAGE)).addOrUpdateTransientModifier(new AttributeModifier(attackModifier, 0.3f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }

        protected void triggerZRot(){
            if(sword.random.nextFloat() < 0.5f){
                sword.entityData.set(DATA_KEYFRAME_X, new KeyframeAnimationCounter(KeyframeAnimation.builder()
                        .addKeyframe(0, 0)
                        .addKeyframe(15, 360)
                        .build()), true);

            }
        }
    }

    /**
     * 跟随主人身后
     */
    static class SwordFollowOwnerGoal extends Goal{
        SummonSword sword;
        protected SwordFollowOwnerGoal(SummonSword sword) {
            this.sword = sword;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }
        @Override
        public boolean canUse() {
            return sword.getOwner() != null;
        }

        @Override
        public void start() {
//            sword.setSharedFlag(6, false);
            sword.entityData.set(DATA_BACK, true, true);
        }
        @Override
        public void stop() {
//            sword.setSharedFlag(6, false);
            sword.entityData.set(DATA_BACK, false, true);
        }

        @Override
        public void tick(){
            LivingEntity owner = sword.getOwner();
            if(owner == null){
                return;
            }

            // 玩家正对方向
            Vec3 d = Vec3.directionFromRotation(new Vec2(0, owner.yBodyRot));
            // 玩家视角正前方
            Vec3 forward = d.multiply(1,0,1).normalize();
            // 玩家视角右侧方向
            Vec3 right = d.cross(new Vec3(0,1,0)).normalize();

            // 将要移动的位置
            Vec3 targetPos = owner.position().subtract(forward.scale(0.6 - 0.05f * (sword.sequence - 1))).add(0,1,0) // 后方，往上一点
                    .add(right.scale(0.2f * (sword.sequence / 2) * ((sword.sequence & 1) == 0 ? 1 : -1))); // 根据sequence调整左右移动

            Vec3 swordPos = sword.position();

            // 剑视角朝向
            Vec3 lookPos = swordPos.subtract(forward.scale(5))
                    .add(0,-8 - (sword.sequence - 1)/2 ,0) // 向下看
                    .add(swordPos.subtract(targetPos).scale(20)); //模拟披风

            sword.lookControl.setLookAt(lookPos);
            sword.lookAt(EntityAnchorArgument.Anchor.FEET, lookPos);

            // 计算距离
            double dist = targetPos.distanceTo(swordPos) * 0.5;
            dist = Math.min(dist, 1);
            if(dist == 0){
                return;
            }

            // 缓入
//            dist = dist * dist;

            // 移动的方向
            Vec3 dir = targetPos.subtract(swordPos).normalize();
            sword.addDeltaMovement(dir);

            // 限制速度
            sword.setDeltaMovement(sword.getDeltaMovement().normalize().scale(dist));

            // 随机扰动
            Vec3 wiggle = new Vec3(sword.random.nextGaussian() * 0.01, sword.random.nextGaussian() * 0.01, sword.random.nextGaussian() * 0.01);
            sword.addDeltaMovement(wiggle.scale(1));



        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }


    @Override
    public OBB getOrientedBoundingBox() {
        return buildObb().updateVertex();
    }

    protected OBB buildObb(){
        Vec3 pos = position();
        return new OBB(pos, 0.75, 0.75, 1.5 * lengthScale(), getXRot(), getYRot()).offsetAlongAxisZ(0.75 * (lengthScale() - 1));
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        LivingEntity owner = this.getOwner();
        if(!level().isClientSide && owner instanceof Player player && this.sequence == 0){
            SummonerAttachment data = player.getData(TEAttachments.SUMMONER_STORAGE.get());
            Iterator<Integer> it = data.getIds().iterator();
            List<Integer> ids = new ArrayList<>();
            while(it.hasNext()){
                int id = it.next();
                if(this.getId() != id && level().getEntity(id) instanceof SummonSword sword){
                    ids.add(sword.sequence);
                }
            }
            if(ids.isEmpty()){
                this.sequence = 1;
                data.prismaIDs.add(this.sequence);
//                System.out.println("no sequence found, setting to 1");
                this.entityData.set(DATA_SEQUENCE, this.sequence, true);
                return;
            }
            ids.sort(Comparator.naturalOrder());
            int last = ids.get(0);
            for(int i = 1; i < ids.size(); i++){
                if(ids.get(i) - last > 1){
                    this.sequence = last + 1;
                    data.prismaIDs.add(this.sequence);
//                    System.out.println("sequence found, setting to " + this.sequence);
                    this.entityData.set(DATA_SEQUENCE, this.sequence, true);
                    return;
                }
                last = ids.get(i);
            }
            this.sequence = last + 1;
            data.prismaIDs.add(this.sequence);
//            System.out.println("sequence found, setting to " + this.sequence);
            this.entityData.set(DATA_SEQUENCE, this.sequence, true);
        }
    }

    private double lengthScale() {
        return 1.5;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public void doCollisionAttack(Predicate<Entity> filter, Consumer<Entity> attackCallback){
        if(!shouldDoCollision() || this.level().isClientSide) return;
        getCollisionProperties().reduceAttackInterval();
        if (canCollisionHurt() && !this.level().isClientSide && getCollisionProperties().canAttack()) {
            OBB obb = getOrientedBoundingBox();
            AABB border = this.getBoundingBox().inflate(lengthScale() * 2);
            List<Entity> entities = this.level().getEntities(this, border, EntitySelector.NO_SPECTATORS.and(e -> {
                if (e instanceof Player || (!(e instanceof LivingEntity) && !(e instanceof PartEntity<?>))) return false;
                return filter.test(e) && obb.inflate(10).collide(e.getBoundingBox(), this.getDeltaMovement(), e.getDeltaMovement());
            }));
            for (Entity entity : entities) {
                attackCallback.accept(entity);
                getCollisionProperties().rewind();
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if(effectStrategy != null && entity instanceof LivingEntity living){
            effectStrategy.getEffect().accept(this, living);
        }
        return super.doHurtTarget(entity);
    }
}
