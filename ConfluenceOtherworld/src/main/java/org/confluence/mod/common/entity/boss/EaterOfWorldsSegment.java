package org.confluence.mod.common.entity.boss;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.entity.proj.LineProj;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEProjectileEntities;

/**
 * 世吞体节
 */
public class EaterOfWorldsSegment extends AbstractTerraBossBase implements Boss.BossPart {


    private int _shootTick;
    private int shootTick;
    public float segmentInternal = 2.8f;
    public EaterOfWorlds head;
    public AbstractTerraBossBase lastSegment;

    public boolean ifTail = false;
    public static final EntityDataAccessor<Boolean> DATA_TAIL = SynchedEntityData.defineId(EaterOfWorldsSegment.class, EntityDataSerializers.BOOLEAN);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TAIL, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
         if(key == DATA_TAIL){
             ifTail = entityData.get(DATA_TAIL);
         }
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 100F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    public void setHead(EaterOfWorlds head){
        this.head = head;
    }
    public void setLastSegment(AbstractTerraBossBase lastSegment){
        this.lastSegment = lastSegment;
    }

    public EaterOfWorldsSegment(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;

    }

    public EaterOfWorldsSegment(EaterOfWorlds head, Level level) {
        this(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), level);
        this.head = head;
        this._discardTimer = random.nextInt(25) + 100;

        this.xpReward = head.getXpReward();
        this.shootTick = head.shootInterval;
        this._shootTick = this.shootTick;
    }

    public Vec3 getNextPos(){
        if(distanceToSqr(lastSegment)<1f) return position();
        this.lookAt(lastSegment,500,500);
        return lastSegment.position().add(position().subtract(lastSegment.position()).normalize().scale(segmentInternal));
    }

    @Override
    public boolean isNoGravity(){
        return true;
    }

    @Override
    public void addSkills() { }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean shouldBeSaved(){
        return false;
    }

    int discardTimer = 0;
    int _discardTimer;

    @Override
    public void tick(){
        super.tick();

        if(!level().isClientSide) {
            if (lastSegment != null && lastSegment.isAlive())
                this.setPos(getNextPos());
            if(head == null || !head.isAlive() ){
                discardTimer++;
                if(discardTimer > _discardTimer) {
                    discard();
                }
                return;
            }
            discardTimer = 0;

            if(target != null && this.level().canSeeSky(this.blockPosition())){
                if(--this.shootTick <= 0){
                    shootTick = _shootTick + random.nextInt(50);
                    LineProj projectile = TEProjectileEntities.VILE_SPIT_PROJ.get().create(level());
                    if(projectile != null) {
                        projectile.setOwner(this);
                        projectile.setPos(position());
                        projectile.setDamage(this.head.projDamage);
                        projectile.shoot(target.getX() - getX(), target.getY() + 1 - getY(), target.getZ() - getZ(), 0.3F, 3);
                        level().addFreshEntity(projectile);
                    }
                }
            }
        }
    }



    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof EaterOfWorldsSegment)  && !(target instanceof EaterOfWorlds);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!CommonHooks.onLivingDeath(this, damageSource)) {
            if (!this.isRemoved() && !this.dead) {
                LivingEntity livingentity = this.getKillCredit();
                if (this.deathScore >= 0 && livingentity != null) {
                    livingentity.awardKillScore(this, this.deathScore, damageSource);
                }
                this.dead = true;
                this.getCombatTracker().recheckStatus();
                Level var5 = this.level();
                if (var5 instanceof ServerLevel) {
                    ServerLevel serverlevel = (ServerLevel)var5;
                    this.gameEvent(GameEvent.ENTITY_DIE);
                    this.lastHurtByPlayerTime = 10;
                    if(damageSource.getEntity() instanceof Player player){
                        this.lastHurtByPlayer = player;
                    }
                    this.dropAllDeathLoot(serverlevel, damageSource);
                    this.createWitherRose(livingentity);
                    this.level().broadcastEntityEvent(this, (byte)3);
                }
                this.setPose(Pose.DYING);
            }
        }
    }

    @Override
    public boolean shouldShowBossBar(){return false;};

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.DROWN);
    }

}
