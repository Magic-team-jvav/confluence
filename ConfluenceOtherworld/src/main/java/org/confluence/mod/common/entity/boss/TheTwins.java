package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;

/**
 * 双子魔眼——两个机械眼球共享 Boss 条。击败任一眼球后，另一只进入暴怒模式。
 */
public class TheTwins extends BaseBoss {
    private Retinazer retinazer;
    private Spazmatism spazmatism;
    private boolean spawned = false;

    public TheTwins(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 2000;
    }

    public boolean isRetinazerAlive() { return retinazer != null && retinazer.isAlive(); }
    public boolean isSpazmatismAlive() { return spazmatism != null && spazmatism.isAlive(); }

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
                return new WaitAction(100); // passive, eyes fight
            }
        };
    }

    // === Spawn eyes ===

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (!spawned) {
                spawnEyes();
                spawned = true;
            }
            // Die when both eyes dead
            if (!isRetinazerAlive() && !isSpazmatismAlive()) {
                die(damageSources().generic());
            }
        }
    }

    private void spawnEyes() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        retinazer = BossEntities.RETINAZER.get().create(level());
        spazmatism = BossEntities.SPAZMATISM.get().create(level());
        if (retinazer != null) {
            retinazer.setPos(getX() + 3, getY(), getZ());
            retinazer.setMaster(this);
            serverLevel.addFreshEntity(retinazer);
            addSubEntity(retinazer);
        }
        if (spazmatism != null) {
            spazmatism.setPos(getX() - 3, getY(), getZ());
            spazmatism.setMaster(this);
            serverLevel.addFreshEntity(spazmatism);
            addSubEntity(spazmatism);
        }
    }

    // === Invisible manager ===

    @Override public boolean isPickable() { return false; }
    @Override public boolean canBeCollidedWith() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean displayFireAnimation() { return false; }
    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    @Override
    protected boolean shouldDiscardWhenNoTarget() {
        return !isRetinazerAlive() && !isSpazmatismAlive();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.FOLLOW_RANGE, 0.0);
    }
}
