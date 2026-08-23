package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4x3f;
import org.mesdag.particlestorm.ParticleStorm;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AccumulatingEnergyEntity extends Entity {
    private static final int STRIKE_AGE = 160;
    protected static final EntityDataAccessor<Integer> ATTACHED_ENTITY = SynchedEntityData.defineId(AccumulatingEnergyEntity.class, EntityDataSerializers.INT);

    protected ParticleEmitter emitter;
    protected @Nullable LightningBolt lightningBolt;
    /// 实体数字 ID 只用于双端同步；跨区块存档恢复必须使用稳定 UUID。
    protected @Nullable UUID attachedEntityUUID;

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

        if (!isInWaterOrBubble() && getAttachedEntity() == null && level().getBlockState(getOnPos()).getFluidState().is(PortTags.Fluids.WATER)) {
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
            if (tickCount > STRIKE_AGE) {
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
                    ParticleStorm.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level().getChunk(chunkPosition().x, chunkPosition().z)), packet);
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
                    if (net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, lightningBolt))
                        continue;
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
            emitter.setLocalSpace(new Matrix4x3f().setTranslation(0, 0.5F, 0));
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
    }

    public void setAttachedEntity(@Nullable Entity entity) {
        this.attachedEntityUUID = entity == null ? null : entity.getUUID();
        entityData.set(ATTACHED_ENTITY, entity == null ? -1 : entity.getId());
    }

    public @Nullable Entity getAttachedEntity() {
        Entity byRuntimeId = level().getEntity(entityData.get(ATTACHED_ENTITY));
        if (byRuntimeId != null && (attachedEntityUUID == null || attachedEntityUUID.equals(byRuntimeId.getUUID()))) {
            if (attachedEntityUUID == null) attachedEntityUUID = byRuntimeId.getUUID();
            return byRuntimeId;
        }
        if (attachedEntityUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity byUuid = serverLevel.getEntity(attachedEntityUUID);
            if (byUuid != null) {
                entityData.set(ATTACHED_ENTITY, byUuid.getId());
                return byUuid;
            }
        }
        return null;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACHED_ENTITY, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = Mth.clamp(compound.getInt("Age"), 0, STRIKE_AGE + 1);
        this.attachedEntityUUID = compound.hasUUID("AttachedEntity")
                ? compound.getUUID("AttachedEntity")
                : null;
        // 旧运行时 ID 在新进程中没有意义，等待 UUID 懒解析后重新同步。
        entityData.set(ATTACHED_ENTITY, -1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", Mth.clamp(tickCount, 0, STRIKE_AGE + 1));
        Entity attached = getAttachedEntity();
        UUID uuid = attached == null ? attachedEntityUUID : attached.getUUID();
        if (uuid != null) compound.putUUID("AttachedEntity", uuid);
    }
}
