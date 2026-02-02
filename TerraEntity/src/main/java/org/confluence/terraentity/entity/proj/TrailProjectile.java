package org.confluence.terraentity.entity.proj;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.entity.TEProjectileEntities;

import java.util.LinkedList;
import java.util.List;

public class TrailProjectile extends LineProj {
    private static final EntityDataAccessor<Integer> DATA_TRAIL_COLOR = SynchedEntityData.defineId(TrailProjectile.class, EntityDataSerializers.INT);

    private final List<Vec3> trails = new LinkedList<>();
    protected Vec3 posO = Vec3.ZERO;
    public TrailProjectile(EntityType<TrailProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setExistTick(20*8);
    }

    public TrailProjectile(Level level, int trailColor) {
        this(TEProjectileEntities.TRAIL_PROJECTILE.get(),level);
        setTrailColor(trailColor);
    }

    @Override
    public void tick() {
        // 客户端轨迹渲染
        if (this.level().isClientSide) {
            if (trails.isEmpty()) {
                trails.add(this.position());
            }
            trails.add(this.position());
            if (trails.size() > 5 || posO == this.position()) {
                trails.removeFirst();
            }
            posO = this.position();
        }
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TRAIL_COLOR, 0xFFFFFF);
    }

    public void setTrailColor(int color) {
        entityData.set(DATA_TRAIL_COLOR, color);
    }

    public int getTrailColor() {
        return entityData.get(DATA_TRAIL_COLOR);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TrailColor", getTrailColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTrailColor(tag.getInt("TrailColor"));
    }

    public List<Vec3> getTrails() {
        return trails;
    }
} 