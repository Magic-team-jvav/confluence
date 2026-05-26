package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.Comparator;
import java.util.List;

public class AccumulatingEnergyEntity extends Entity {
    protected static final EntityDataAccessor<Integer> ATTACHED_ENTITY = SynchedEntityData.defineId(AccumulatingEnergyEntity.class, EntityDataSerializers.INT);

    protected ParticleEmitter emitter;
    protected @Nullable LightningBolt lightningBolt;

    public AccumulatingEnergyEntity(EntityType<? extends AccumulatingEnergyEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AccumulatingEnergyEntity(EntityType<? extends AccumulatingEnergyEntity> entityType, Level level, @Nullable LightningBolt lightningBolt) {
        super(entityType, level);
        this.lightningBolt = lightningBolt;
    }

    @Override
    public void tick() {
        super.tick();

        if (!isInWaterOrBubble() && getAttachedEntity() == null && level().getBlockState(getOnPos()).getFluidState().is(Tags.Fluids.WATER)) {
            setPos(position().add(0, -0.75, 0));
        }

        if (!level().isClientSide) {
            Entity attachedEntity = getAttachedEntity();
            boolean ftw = ModSecretSeeds.FOR_THE_WORTHY.match();
            if (attachedEntity == null) {
                double dist = ftw ? 6 : 3;
                level().getEntities(this, new AABB(
                                getX() - dist,
                                getY() - dist,
                                getZ() - dist,
                                getX() + dist,
                                getY() + dist,
                                getZ() + dist
                        ), Entity::isAlive).stream()
                        .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(this)))
                        .ifPresent(this::setAttachedEntity);
                attachedEntity = getAttachedEntity();
            }
            if (attachedEntity != null) {
                setPos(attachedEntity.position());
            }
            if (tickCount > 160) {
                if (lightningBolt == null) {
                    this.lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level());
                }
                lightningBolt.setPos(position());
                lightningBolt.setVisualOnly(true);
                lightningBolt.setDamage(0);
                level().addFreshEntity(lightningBolt);
                boolean inWaterOrBubble = isInWaterOrBubble();
                AABB boundingBox = inWaterOrBubble ? new AABB(
                        getX() - 23,
                        getY() - 23,
                        getZ() - 23,
                        getX() + 23,
                        getY() + 23,
                        getZ() + 23
                ) : new AABB(
                        getX() - 6,
                        getY() - 3,
                        getZ() - 6,
                        getX() + 6,
                        getY() + 9,
                        getZ() + 6
                );
                if (inWaterOrBubble) {
                    EmitterCreationPacketS2C packet = new EmitterCreationPacketS2C(Confluence.asResource("in_water_lightning_bolt"), position().toVector3f(), MolangExp.EMPTY, -1);
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level(), chunkPosition(), packet);
                }
                List<Entity> entities = level().getEntities(this, boundingBox, entity -> entity.isAlive() && inWaterOrBubble == entity.isInWaterOrBubble());
                for (Entity entity : entities) {
                    if (entity.getType() == EntityType.PLAYER) {
                        if (ftw) {
                            lightningBolt.setDamage(LibUtils.switchByDifficulty(level(), entity.blockPosition(), 16, 32, 48, 80));
                        } else {
                            lightningBolt.setDamage(LibUtils.switchByDifficulty(level(), entity.blockPosition(), 8, 16, 32, 48));
                        }
                    } else {
                        lightningBolt.setDamage(LibUtils.switchByDifficulty(level(), entity.blockPosition(), 100, 200, 300));
                    }
                    if (EventHooks.onEntityStruckByLightning(entity, lightningBolt)) continue;
                    entity.thunderHit((ServerLevel) level(), lightningBolt);
                }

                lightningBolt.hitEntities.addAll(entities);
                lightningBolt.setVisualOnly(false);
                lightningBolt.setDamage(0);
                discard();
            }
        }

        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("accumulating_energy"));
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            emitter.parentSpace = new Matrix4f().setTranslation(0, 0.5F, 0);
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
    }

    public void setAttachedEntity(@Nullable Entity entity) {
        entityData.set(ATTACHED_ENTITY, entity == null ? -1 : entity.getId());
    }

    public @Nullable Entity getAttachedEntity() {
        return level().getEntity(entityData.get(ATTACHED_ENTITY));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ATTACHED_ENTITY, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = compound.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", tickCount);
    }
}
