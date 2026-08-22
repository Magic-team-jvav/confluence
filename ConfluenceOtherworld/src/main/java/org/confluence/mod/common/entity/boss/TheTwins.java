package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 双子魔眼——两个机械眼球共享 Boss 条，并分别执行各自的战斗阶段。
public class TheTwins extends BaseBoss {
    private static final int RETINAZER_DEFEATED = 1;
    private static final int SPAZMATISM_DEFEATED = 2;
    private static final int BOTH_DEFEATED = RETINAZER_DEFEATED | SPAZMATISM_DEFEATED;
    private static final String SPAWNED_TAG = "EyesSpawned";
    private static final String DEFEATED_EYES_TAG = "DefeatedEyes";
    private static final String RETINAZER_UUID_TAG = "RetinazerUUID";
    private static final String SPAZMATISM_UUID_TAG = "SpazmatismUUID";

    private Retinazer retinazer;
    private Spazmatism spazmatism;
    private UUID retinazerUUID;
    private UUID spazmatismUUID;
    private boolean spawned = false;
    private int defeatedEyes;

    public TheTwins(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 2000;
    }

    /// 双子魔眼的管理本体不应被原版重力拖动。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public boolean isRetinazerAlive() {
        return (defeatedEyes & RETINAZER_DEFEATED) == 0
                && (retinazerUUID != null || retinazer != null && retinazer.isAlive());
    }

    public boolean isSpazmatismAlive() {
        return (defeatedEyes & SPAZMATISM_DEFEATED) == 0
                && (spazmatismUUID != null || spazmatism != null && spazmatism.isAlive());
    }

    // === Boss bar ===

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        float totalMax = (retinazer != null ? retinazer.getMaxHealth() : 0)
                + (spazmatism != null ? spazmatism.getMaxHealth() : 0);
        float total = (retinazer != null && retinazer.isAlive() ? retinazer.getHealth() : 0)
                + (spazmatism != null && spazmatism.isAlive() ? spazmatism.getHealth() : 0);
        bossEvent.setProgress(totalMax > 0 ? total / totalMax : 0);
    }

    // === BT (idle — eyes do all the work) ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(100);
            }
        };
    }

    // === Spawn eyes ===

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        if (!level().isClientSide) {
            applyRecordedEyeDeaths();
            recoverEyes();
            if (!spawned) {
                spawnMissingEyes();
                spawned = true;
            } else {
                spawnMissingEyes();
            }
            if (tickCount > 50 && (tickCount & 31) == 0) {
                updateManagerPosition();
            }
            // 管理实体不参与受击，只在两个权威眼球都已记录死亡后结算 Boss。
            if (defeatedEyes == BOTH_DEFEATED && isAlive()) {
                die(damageSources().generic());
            }
        }
    }

    /// 保留 1.21 侧管理实体的空间职责：双眼都存在时位于二者中点，只剩一只时
    /// 位于存活眼球上方。双眼均未索敌且相距过远时，还会互相拉近以防止永久失散。
    void updateManagerPosition() {
        boolean retinazerAlive = retinazer != null && retinazer.isAlive();
        boolean spazmatismAlive = spazmatism != null && spazmatism.isAlive();
        if (retinazerAlive && spazmatismAlive) {
            setPos(retinazer.position().add(spazmatism.position()).scale(0.5));
            if (retinazer.getTarget() == null && spazmatism.getTarget() == null && retinazer.distanceTo(spazmatism) > 50.0F) {
                Vec3 direction = spazmatism.position().subtract(position()).normalize();
                spazmatism.addDeltaMovement(direction.scale(-1.0));
                retinazer.addDeltaMovement(direction);
            }
        } else if (retinazerAlive) {
            setPos(retinazer.position().add(0.0, 5.0, 0.0));
        } else if (spazmatismAlive) {
            setPos(spazmatism.position().add(0.0, 5.0, 0.0));
        }
    }

    private void recoverEyes() {
        if (retinazer != null && !retinazer.isAlive()) retinazer = null;
        if (spazmatism != null && !spazmatism.isAlive()) spazmatism = null;

        if (level() instanceof ServerLevel serverLevel) {
            if (retinazer == null && retinazerUUID != null) {
                Entity candidate = serverLevel.getEntity(retinazerUUID);
                if (candidate instanceof Retinazer eye && eye.isAlive() && eye.isOwnedBy(this)) {
                    retinazer = eye;
                }
            }
            if (spazmatism == null && spazmatismUUID != null) {
                Entity candidate = serverLevel.getEntity(spazmatismUUID);
                if (candidate instanceof Spazmatism eye && eye.isAlive() && eye.isOwnedBy(this)) {
                    spazmatism = eye;
                }
            }
        }

        for (Entity subordinate : getSubEntities()) {
            if ((defeatedEyes & RETINAZER_DEFEATED) == 0
                    && (retinazer == null || !retinazer.isAlive())
                    && subordinate instanceof Retinazer eye && eye.isAlive() && eye.isOwnedBy(this)
                    && (retinazerUUID == null || retinazerUUID.equals(eye.getUUID()))) {
                retinazer = eye;
                retinazerUUID = eye.getUUID();
            } else if ((defeatedEyes & SPAZMATISM_DEFEATED) == 0
                    && (spazmatism == null || !spazmatism.isAlive())
                    && subordinate instanceof Spazmatism eye && eye.isAlive() && eye.isOwnedBy(this)
                    && (spazmatismUUID == null || spazmatismUUID.equals(eye.getUUID()))) {
                spazmatism = eye;
                spazmatismUUID = eye.getUUID();
            }
        }

    }

    private void spawnMissingEyes() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        if (needsRetinazerSpawn(serverLevel)) {
            Retinazer created = BossEntities.RETINAZER.get().create(level());
            if (created != null) {
                created.setPos(position().add(createEyeSpawnOffset()));
                created.setMaster(this);
                if (getTarget() != null) created.setTarget(getTarget());
                if (prepareEyeSpawn(serverLevel, created) && serverLevel.addFreshEntity(created)) {
                    retinazer = created;
                    retinazerUUID = created.getUUID();
                } else {
                    created.discard();
                }
            }
        }
        if (needsSpazmatismSpawn(serverLevel)) {
            Spazmatism created = BossEntities.SPAZMATISM.get().create(level());
            if (created != null) {
                created.setPos(position().add(createEyeSpawnOffset()));
                created.setMaster(this);
                if (getTarget() != null) created.setTarget(getTarget());
                if (prepareEyeSpawn(serverLevel, created) && serverLevel.addFreshEntity(created)) {
                    spazmatism = created;
                    spazmatismUUID = created.getUUID();
                } else {
                    created.discard();
                }
            }
        }
    }

    /// 与 1.21 保持一致：双眼在半径五格、顶角不超过 0.7 弧度的球冠上生成。
    private Vec3 createEyeSpawnOffset() {
        double theta = random.nextFloat() * 6.28F;
        double beta = random.nextFloat() * 0.7F;
        double horizontal = 5.0 * Math.sin(beta);
        return new Vec3(horizontal * Math.cos(theta), 5.0 * Math.cos(beta), horizontal * Math.sin(theta));
    }

    private boolean needsRetinazerSpawn(ServerLevel serverLevel) {
        if ((defeatedEyes & RETINAZER_DEFEATED) != 0) return false;
        if (retinazer != null && retinazer.isAlive()) return false;
        if (retinazerUUID != null) {
            Entity candidate = serverLevel.getEntity(retinazerUUID);
            if (candidate instanceof Retinazer eye && eye.isAlive() && eye.isOwnedBy(this)) {
                retinazer = eye;
                return false;
            }
            /// 已保存的 UUID 代表精确身份。实体可能只是所在区块尚未加载，不能在等待期间
            /// 生成替身，否则重载后会出现两个身份不同但同属一个管理实体的眼球。
            return false;
        }
        return true;
    }

    private boolean needsSpazmatismSpawn(ServerLevel serverLevel) {
        if ((defeatedEyes & SPAZMATISM_DEFEATED) != 0) return false;
        if (spazmatism != null && spazmatism.isAlive()) return false;
        if (spazmatismUUID != null) {
            Entity candidate = serverLevel.getEntity(spazmatismUUID);
            if (candidate instanceof Spazmatism eye && eye.isAlive() && eye.isOwnedBy(this)) {
                spazmatism = eye;
                return false;
            }
            /// 与激光眼相同，UUID 存在时优先等待原实体加载，避免补出替身破坏持久化身份。
            return false;
        }
        return true;
    }

    private static boolean prepareEyeSpawn(ServerLevel serverLevel, Mob mob) {
        mob.yHeadRot = mob.getYRot();
        mob.yBodyRot = mob.getYRot();
        ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWNER, null, null);
        if (mob.isSpawnCancelled()) {
            mob.discard();
            return false;
        }
        return true;
    }

    private void applyRecordedEyeDeaths() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (UUID defeatedUUID : BossChildDeathLedger.consume(serverLevel, getUUID())) {
            if (defeatedUUID.equals(retinazerUUID)) {
                markTwinDefeated(true);
            } else if (defeatedUUID.equals(spazmatismUUID)) {
                markTwinDefeated(false);
            }
        }
    }

    void onTwinDefeated(boolean wasRetinazer, Entity defeatedEye) {
        if (wasRetinazer) {
            if (retinazer == defeatedEye) retinazer = null;
        } else {
            if (spazmatism == defeatedEye) spazmatism = null;
        }
        markTwinDefeated(wasRetinazer);
    }

    private void markTwinDefeated(boolean wasRetinazer) {
        if (wasRetinazer) {
            defeatedEyes |= RETINAZER_DEFEATED;
            retinazer = null;
            retinazerUUID = null;
        } else {
            defeatedEyes |= SPAZMATISM_DEFEATED;
            spazmatism = null;
            spazmatismUUID = null;
        }
    }

    public Retinazer getRetinazer() {return retinazer;}

    public Spazmatism getSpazmatism() {return spazmatism;}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(SPAWNED_TAG, spawned);
        tag.putInt(DEFEATED_EYES_TAG, defeatedEyes);
        if (retinazerUUID != null) tag.putUUID(RETINAZER_UUID_TAG, retinazerUUID);
        if (spazmatismUUID != null) tag.putUUID(SPAZMATISM_UUID_TAG, spazmatismUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        spawned = tag.getBoolean(SPAWNED_TAG);
        defeatedEyes = tag.getInt(DEFEATED_EYES_TAG) & BOTH_DEFEATED;
        retinazerUUID = tag.hasUUID(RETINAZER_UUID_TAG) ? tag.getUUID(RETINAZER_UUID_TAG) : null;
        spazmatismUUID = tag.hasUUID(SPAZMATISM_UUID_TAG) ? tag.getUUID(SPAZMATISM_UUID_TAG) : null;
        retinazer = null;
        spazmatism = null;
    }

    // === Invisible manager ===

    @Override public boolean isPickable() { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean displayFireAnimation() { return false; }
    @Override public boolean causeFallDamage(float f, float m, DamageSource s) {return false;}

    /// 管理实体本身不移动，战斗范围必须覆盖两个眼球的实际位置。
    @Override
    protected double getCombatPlayerRange() {
        return 64.0;
    }

    /// 玩家死亡或多人目标切换时，把管理实体选出的权威目标同步给仍存活的两个眼球；目标为空
    /// 则立即让眼球停止攻击，随后由本体统一完成 10 秒撤离计时。
    @Override
    protected void onCombatTargetChanged(@Nullable Player target) {
        super.onCombatTargetChanged(target);
        if (retinazer != null && retinazer.isAlive()) retinazer.setTarget(target);
        if (spazmatism != null && spazmatism.isAlive()) spazmatism.setTarget(target);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.FOLLOW_RANGE, 0.0);
    }
}
