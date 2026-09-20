package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

public final class WindyBalloon extends BaseFlyingMonster {
    public WindyBalloon(EntityType<? extends WindyBalloon> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData group, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, group, tag);
        if (reason == MobSpawnType.NATURAL && level.getLevel().noCollision(this, getBoundingBox().move(0.0, 6.0, 0.0)))
            setPos(getX(), getY() + 6.0, getZ());
        var slime = MonsterEntities.BLUE_SLIME.get().create(level.getLevel());
        if (slime != null) {
            slime.moveTo(getX(), getY() - 1.5, getZ(), getYRot(), 0.0F);
            slime.finalizeSpawn(level, difficulty, MobSpawnType.JOCKEY, null, null);
            if (level.getLevel().addFreshEntity(slime)) slime.startRiding(this, true);
        }
        return result;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        ConfluenceData data = ConfluenceData.get((ServerLevel) level());
                        setDeltaMovement(new Vec3(data.getWindSpeedX() * 0.18, -0.025, data.getWindSpeedZ() * 0.18));
                        boolean slimeTouchingGround = getPassengers().stream().anyMatch(passenger -> passenger.isInWaterOrBubble() || passenger.isInLava()
                                || !level().noCollision(passenger, passenger.getBoundingBox().move(0.0, -0.05, 0.0)));
                        if (onGround() || slimeTouchingGround || isInWaterOrBubble() || isInLava() || level().getNearestPlayer(WindyBalloon.this, 4.0) != null)
                            pop();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        move.accept(passenger, getX(), getY() - passenger.getBbHeight() - 0.5, getZ());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source) || amount <= 0.0F) return false;
        if (!level().isClientSide) pop();
        return true;
    }

    private void pop() {
        if (isRemoved()) return;
        for (Entity passenger : getPassengers()) {
            Vec3 location = passenger.position();
            passenger.stopRiding();
            passenger.setPos(location);
            passenger.setDeltaMovement(getDeltaMovement());
        }
        playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0F, 1.7F);
        discard();
    }
}
