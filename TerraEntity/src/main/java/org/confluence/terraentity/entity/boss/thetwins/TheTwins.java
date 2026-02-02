package org.confluence.terraentity.entity.boss.thetwins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 双子魔眼控制实体
 */
public class TheTwins extends AbstractTerraBossBase implements Boss {


    public Spazmatism spazmatism;
    public Retinazer retinazer;

    UUID spazmatismUUID;
    UUID retinazerUUID;

    float mhp1 = 0, mhp2 = 0;

    // 获取客户端id，用于渲染
    public static final EntityDataAccessor<Integer> DATA_SPAZMATISM_ID = SynchedEntityData.defineId(TheTwins.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_RETINAZER_ID = SynchedEntityData.defineId(TheTwins.class, EntityDataSerializers.INT);

    public record SkillParams(Spazmatism.SkillParams spazmatismParams, Retinazer.SkillParams retinazerParams){
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance-> instance.group(
                Spazmatism.SkillParams.CODEC.fieldOf("spazmatismParams").forGetter(SkillParams::spazmatismParams),
                Retinazer.SkillParams.CODEC.fieldOf("retinazerParams").forGetter(SkillParams::retinazerParams)
        ).apply(instance, SkillParams::new));
        public static SkillParams getDefaultParams() {
            return new SkillParams(Spazmatism.SkillParams.getDefaultParams(), Retinazer.SkillParams.getDefaultParams());
        }
    }

    public TheTwins(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }


    @Override
    protected void registerGoals() {

    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void addSkills() {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SPAZMATISM_ID, 0);
        builder.define(DATA_RETINAZER_ID, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(level().isClientSide) {
            if(key == DATA_SPAZMATISM_ID) {
                if(level().getEntity(this.entityData.get(DATA_SPAZMATISM_ID)) instanceof Spazmatism sp) {
                    spazmatism = sp;
                }
            }
            if(key == DATA_RETINAZER_ID) {
                if(level().getEntity(this.entityData.get(DATA_RETINAZER_ID)) instanceof Retinazer re) {
                    retinazer = re;
                }
            }
        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
       if(this.spazmatism == null || this.retinazer == null) {
           return super.getBoundingBoxForCulling();
       }
       return new AABB(this.spazmatism.position(), this.retinazer.position());
    }

    @Override
    public void tick() {
        super.tick();

        // 暂时使用延迟同步id的方法，但如果区块卸载后再加载会出问题
        if(tickCount == 50) {
            if(level() instanceof ServerLevel serverLevel ) {
                if(spazmatismUUID == null || retinazerUUID == null) {
                    this.discard();
                }else{
                    if(serverLevel.getEntity(retinazerUUID) instanceof Retinazer re) {
                        this.retinazer = re;
                        this.entityData.set(DATA_RETINAZER_ID, retinazer.getId());
                        re.ownerUUID = this.getUUID();
                    }
                    if(serverLevel.getEntity(spazmatismUUID) instanceof Spazmatism sp && sp.isAlive()) {
                        this.spazmatism = sp;
                        this.entityData.set(DATA_SPAZMATISM_ID, spazmatism.getId());
                        sp.ownerUUID = this.getUUID();
                    }


                }

            }
        }

        // 更新位置
        if(this.tickCount > 50 && !level().isClientSide && (this.tickCount & 31) == 0) {
            if(spazmatism != null && spazmatism.isAlive() && retinazer != null && retinazer.isAlive()) {
                Vec3 pos = spazmatism.position().add(retinazer.position()).scale(0.5f);
                this.setPos(pos);
                if(spazmatism.getTarget() == null && retinazer.getTarget() == null) {
                    // 无目标时相互靠近
                    if(spazmatism.distanceTo(retinazer) > 50) {
                        Vec3 dir = spazmatism.position().subtract(position()).normalize();
                        spazmatism.addDeltaMovement(dir.scale(-1));
                        retinazer.addDeltaMovement(dir);
                    }
                }

            }else{
                if(spazmatism != null && spazmatism.isAlive()) {
                    this.setPos(spazmatism.position().add(0, 5, 0));
                }else if(retinazer != null && retinazer.isAlive()) {
                    this.setPos(retinazer.position().add(0, 5, 0));
                }else{
                    this.discard();
                }
            }

        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putUUID("spazmatismUUID", spazmatismUUID);
        compound.putUUID("retinazerUUID", retinazerUUID);

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("spazmatismUUID")) {
            spazmatismUUID = tag.getUUID("spazmatismUUID");
        }
        if(tag.contains("retinazerUUID")) {
            retinazerUUID = tag.getUUID("retinazerUUID");
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();


    }

    @Override
    public boolean canCollisionHurt() {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if(level() instanceof ServerLevel serverLevel){
            Vec3 pos1 = TEUtils.sphere(5, this.random.nextFloat() * 6.28f, this.random.nextFloat() * 0.7f);
            spazmatism = TEUtils.spawnEntity(TEBossEntities.SPAZMATISM.get(), serverLevel, this.position().add(pos1));
            if(spazmatism == null) {
                this.discard();

            }else{
                spazmatismUUID = spazmatism.getUUID();
                Vec3 pos2 = TEUtils.sphere(5, this.random.nextFloat() * 6.28f, this.random.nextFloat() * 0.7f);
                retinazer = TEUtils.spawnEntity(TEBossEntities.RETINAZER.get(), serverLevel, this.position().add(pos2));
                if(retinazer == null) {
                    this.discard();

                }else{
                    retinazerUUID = retinazer.getUUID();
                }
            }

        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }


    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        return !TEUtils.isPassInvulnerableDamageSource(source, damageSources());
    }

    public void onPartDie(Spazmatism spazmatism) {
        if(spazmatism == this.spazmatism) {
            this.spazmatism = null;
            this.spazmatismUUID = null;
        }else if(spazmatism == this.retinazer) {
            this.retinazer = null;
            this.retinazerUUID = null;
        }

        if(this.spazmatism == null && this.retinazer == null) {
            this.die(damageSources().genericKill());
        }
    }

    @Override
    public float[] getBossEventProgress(){
        float hp1=0, hp2=0;
        if(spazmatism != null) {
            hp1 = spazmatism.getHealth();
            mhp1 = spazmatism.getMaxHealth();
        }
        if(retinazer != null) {
            hp2 = retinazer.getHealth();
            mhp2 = retinazer.getMaxHealth();
        }
        return new float[]{hp1+hp2, mhp1+mhp2};
    }

    @Override
    public boolean isPickable() {
        return false;
    }


}
