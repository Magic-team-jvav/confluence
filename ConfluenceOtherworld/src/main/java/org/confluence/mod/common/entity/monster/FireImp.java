package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.animation.RawAnimation;

/// 会瞬移并投掷火球的地狱法师。
///
/// 火焰免疫、环境火星和火球点燃效果均属于该生物自身，不由通用法师基类猜测。
public class FireImp extends BaseCasterMonster {
    private final BossOwnerTracker<WallOfFlesh> wallOwner = new BossOwnerTracker<>(WallOfFlesh.class);

    public enum CombatState {WALL_SERVANT}

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public FireImp(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    public void setWallOwner(WallOfFlesh owner) {
        wallOwner.bind(this, owner);
        setSpecialState(CombatState.WALL_SERVANT, true);
        setHealth(getMaxHealth());
    }

    public boolean isWallServant() {return wallOwner.getOwnerUUID() != null;}

    @Override
    public void tick() {
        if (!level().isClientSide && isWallServant()) {
            wallOwner.tickDependent(this, true, 100);
            if (isRemoved()) return;
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        wallOwner.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        wallOwner.load(tag);
        if (isWallServant()) setSpecialState(CombatState.WALL_SERVANT, true);
    }

    @Override
    public void remove(RemovalReason reason) {
        wallOwner.unbind(this);
        super.remove(reason);
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    protected EntityType<HostileParticleProjectile> projectileType() {
        return ModEntities.FIRE_IMP_PROJECTILE.get();
    }

    @Override
    protected RawAnimation getRestAnimation(boolean moving) {
        return IDLE;
    }

    @Override
    protected SoundEvent getCastSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            return;
        }
        if (random.nextInt(24) == 0 && !isSilent()) {
            level().playLocalSound(getX() + 0.5, getY() + 0.5, getZ() + 0.5, SoundEvents.BLAZE_BURN, getSoundSource(), 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
        level().addParticle(ParticleTypes.FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.02, 0.0);
    }
}
