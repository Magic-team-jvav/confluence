package org.confluence.terraentity.entity.summon;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.entity.ai.goal.skill.SkillCooldownManager;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;
import org.confluence.terraentity.entity.util.KeyframeAnimationCounter;
import org.confluence.terraentity.init.TEEntityDataSerializers;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.integration.veil.VeilHelper;
import org.confluence.terraentity.utils.OBB;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 泰拉棱镜
 */
public class Terraprisma extends SummonSword {

    int scaleTick = 0;
    int scaleXTick = 0;

    // 客户端动态颜色
    float colorProgress = 0;
    float sliderProgress = 0;
    int colorFrom = 0x1FE6C0;
    int colorTo = 0xC67C28;

    public KeyframeAnimationCounter anim_y;
    public KeyframeAnimationCounter anim_z;

    // 客户端动态颜色，貌似没必要同步
    protected static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(Terraprisma.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(Terraprisma.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_SCALE_Y = SynchedEntityData.defineId(Terraprisma.class, EntityDataSerializers.FLOAT);

//    protected static final EntityDataAccessor<Integer> DATA_SCALE_TICK = SynchedEntityData.defineId(Terraprisma.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<KeyframeAnimationCounter> DATA_KEYFRAME_Y = SynchedEntityData.defineId(Terraprisma.class, TEEntityDataSerializers.KEYFRAME_ANIMATION_SERIALIZER.get());
    protected static final EntityDataAccessor<KeyframeAnimationCounter> DATA_KEYFRAME_Z = SynchedEntityData.defineId(Terraprisma.class, TEEntityDataSerializers.KEYFRAME_ANIMATION_SERIALIZER.get());

    Object light = null;
    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        if(ModChecker.veil.isLoaded()) {
            VeilHelper.addLight(level(), light);
        }
    }

    public Terraprisma(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level, null, 0xFFFFFF, null, 0.25f);

        this.rgb = getRandomColor();
        this.entityData.set(DATA_COLOR, this.rgb);

        if(ModChecker.veil.isLoaded()) {
            light = VeilHelper.initLight(level);
        }

    }

    @Override
    public void onRemovedFromLevel(){
        if(ModChecker.veil.isLoaded()) {
            VeilHelper.removeLight(level(), light);
        }
        super.onRemovedFromLevel();
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide){
            if(ModChecker.veil.isLoaded()) {
                VeilHelper.setLight(level(), light, this.getX(), this.getY(), this.getZ(), 0.01F, 17, this.rgb);
            }
            float d = (getRandom().nextFloat() - 0.5f) * 0.05f;

            colorProgress = Mth.clamp(colorProgress + d + this.sliderProgress, 0, 1);
            float speed = 0.01f;
            if(colorProgress >= 1){
                this.sliderProgress = -speed;
            }else if(colorProgress <= 0){
                this.sliderProgress = speed;
            }
            this.rgb = lerpColor(colorFrom, colorTo, colorProgress);
        }else{
            if(--scaleTick <= 0){
                this.entityData.set(DATA_SCALE, 1f, true);
            }
            if(--scaleXTick <= 0){
                this.entityData.set(DATA_SCALE_Y, 1f, true);
            }
        }
    }

    protected int getRandomColor() {
        this.colorProgress = getRandom().nextFloat();
        int color = lerpColor(colorFrom, colorTo,this.colorProgress );

        return color;
    }

    protected int lerpColor(int from, int to, float t) {
        int r = (int) ((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t);
        int g = (int) ((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t);
        int b = (int) ((from & 255) + ((to & 255) - (from & 255)) * t);
        return (r << 16) + (g << 8) + b;
    }


    @Override
    protected void registerGoals() {
        this.cooldownManager = new SkillCooldownManager();

        TerraprismaSlashGoal slash = new TerraprismaSlashGoal(this, 1, 10, 120);
        this.cooldownManager.addSkill(slash);
        this.goalSelector.addGoal(0, slash);


        TerraprismaRotateGoal rotate = new TerraprismaRotateGoal(this, 1, 10, 80);
        this.cooldownManager.addSkill(rotate);
        this.goalSelector.addGoal(0, rotate);

        registerCommonSwordGoals();
    }

    /**
     * 相比于下位剑，攻击完以后旋转X轴时间更久
     */
    protected static class TerraprismaSlashGoal extends SwordSlashGoal<Terraprisma>{

        protected TerraprismaSlashGoal(Terraprisma sword, int skillIndex, int ticks, int skillCooldown) {
            super(sword, skillIndex, ticks, skillCooldown);

        }

        // 由于棱镜拖尾贴合比较好，可以多旋转几圈
        @Override
        protected void triggerZRot(){
            Entity actualTarget = sword instanceof IPartEntityTargetable t ? t.getActualTargetEntity() : null;
            if (actualTarget == null) actualTarget = sword.getTarget();
            boolean targetValid = actualTarget instanceof PartEntity<?> p && p.getParent() instanceof LivingEntity parent && parent.isAlive() || actualTarget instanceof LivingEntity living && living.isAlive();

            if(targetValid && sword.getRandom().nextBoolean() || sword.getRandom().nextFloat() < 0.1f && !targetValid){
                int cycle  = sword.getRandom().nextIntBetweenInclusive(2, 5);
                sword.getEntityData().set(DATA_KEYFRAME_X, new KeyframeAnimationCounter(KeyframeAnimation.builder()
                        .addKeyframe(new Keyframe(0, 0, 0, 0.5f, 0, 10f))
                        .addKeyframe(new Keyframe(12 * cycle, (sword.getRandom().nextBoolean() ? 1: -1)  * 360 * cycle, 0, 30f, 0, 3f))
                        .build()), true);
                this.sword.entityData.set(DATA_SCALE_Y, 2F, true);
            }
        }
    }


    /**
     * 旋转技能：先旋转Z轴，暂时提高攻击力，然后旋转Y轴和Z轴，扩大攻击范围
     */
    static class TerraprismaRotateGoal extends AbstactSkillGoal<Terraprisma> {

        ResourceLocation attackModifierId = TerraEntity.space("skill2");
        /**
         * @param skillIndex    技能索引
         * @param ticks         持续时间
         * @param skillCooldown 技能冷却时间
         */
        protected TerraprismaRotateGoal(Terraprisma sword, int skillIndex, int ticks, int skillCooldown) {
            super(sword, skillIndex, ticks, skillCooldown);
        }

        @Override
        public void tick(){
            super.tick();
            this.ticks++;
        }

        @Override
        public void start() {
            super.start();
            this.sword.getEntityData().set(DATA_KEYFRAME_Z, new KeyframeAnimationCounter(KeyframeAnimation.builder()
                    .addKeyframe(0,0)
                    .addKeyframe(10,1080)
                    .build()), true);
            Objects.requireNonNull(this.sword.getAttribute(Attributes.ATTACK_DAMAGE)).addOrUpdateTransientModifier(new AttributeModifier(attackModifierId, 0.3f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        }


        @Override
        public void stop(){
            super.stop();
            Objects.requireNonNull(this.sword.getAttribute(Attributes.ATTACK_DAMAGE)).removeModifier(attackModifierId);

            Entity actualTarget = sword instanceof IPartEntityTargetable t ? t.getActualTargetEntity() : null;
            if (actualTarget == null) actualTarget = sword.getTarget();
            boolean targetValid = actualTarget instanceof PartEntity<?> p && p.getParent() instanceof LivingEntity parent && parent.isAlive() || actualTarget instanceof LivingEntity living && living.isAlive();

            if(targetValid && sword.getRandom().nextBoolean() || sword.getRandom().nextFloat() < 0.1f && !targetValid){
                this.sword.getEntityData().set(DATA_KEYFRAME_Y, new KeyframeAnimationCounter(KeyframeAnimation.builder() // 插值似乎和理想的情况不一样
                        .addKeyframe(new Keyframe(0, 0, 0, 0.5f, 0, 100f))
                        .addKeyframe(new Keyframe(30, 720, -5, 100f, -1, 3f))
                        .build()), true);
                this.sword.getEntityData().set(DATA_KEYFRAME_Z, new KeyframeAnimationCounter(KeyframeAnimation.builder()
                        .addKeyframe(0, 0)
                        .addKeyframe(5, 90)
                        .addKeyframe(25, 90)
                        .addKeyframe(30, 0)
                        .build()), true);
                this.sword.getEntityData().set(DATA_SCALE, 2f);
                this.sword.scaleTick = 30;

            }
        }

    }

    @Override
    protected OBB buildObb(){
        return super.buildObb().scale(Mth.clamp(this.entityData.get(DATA_SCALE), 0.5f, 3f))
                .scale(1,Mth.clamp(this.entityData.get(DATA_SCALE_Y), 0.5f, 3f),1);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, 0);
        builder.define(DATA_SCALE, 1f);
        builder.define(DATA_SCALE_Y, 1f);
        builder.define(DATA_KEYFRAME_Y, new KeyframeAnimationCounter(0, KeyframeAnimation.builder()
                .addKeyframe(0,0)
                .addKeyframe(1,0)
                .build()));
        builder.define(DATA_KEYFRAME_Z, new KeyframeAnimationCounter(0, KeyframeAnimation.builder()
                .addKeyframe(0,0)
                .addKeyframe(1,0)
                .build()));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_COLOR){
            this.rgb = this.entityData.get(DATA_COLOR);
        }else if(key == DATA_KEYFRAME_Y){
            this.anim_y = this.entityData.get(DATA_KEYFRAME_Y);
            this.anim_y.setStartTime(tickCount);
        }else if(key == DATA_KEYFRAME_Z){
            this.anim_z = this.entityData.get(DATA_KEYFRAME_Z);
            this.anim_z.setStartTime(tickCount);
        }
    }

}
