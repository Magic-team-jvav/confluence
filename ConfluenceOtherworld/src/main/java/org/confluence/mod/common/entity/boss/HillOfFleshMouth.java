package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 肉丘之嘴——定位在肉丘周围，周期性生成饿鬼仆从。
 */
public class HillOfFleshMouth extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private HillOfFlesh master;
    private int summonTimer;

    public HillOfFleshMouth(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(HillOfFlesh master) {
        this.master = master;
        this.summonTimer = 100 + random.nextInt(150);
    }

    @Override
    public void tick() {
        super.tick();
        if (master == null || !master.isAlive()) {
            discard();
            return;
        }
        if (level().isClientSide) return;

        summonTimer--;
        if (summonTimer <= 0) {
            summonTimer = (master.isPhase2() ? 150 : 250) + random.nextInt(80);
            spawnHungry();
        }
    }

    private void spawnHungry() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        Hungry hungry = BossEntities.HUNGRY.get().create(level());
        if (hungry != null) {
            hungry.setPos(position());
            hungry.setMaster(master, position().subtract(master.position()));
            LivingEntity target = master.getTarget();
            if (target != null) hungry.setTarget(target);
            serverLevel.addFreshEntity(hungry);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return master != null && master.hurt(source, amount * 2.0f);
    }

    @Override
    public boolean isPickable() {return true;}

    @Override
    public boolean canBeCollidedWith() {return master != null && master.isAlive();}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag t) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag t) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return new ClientboundAddEntityPacket(this);}

    @Override
    public EntityDimensions getDimensions(Pose p) {return EntityDimensions.scalable(1.5F, 1.5F);}

    @Override
    public boolean is(Entity e) {return this == e;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
