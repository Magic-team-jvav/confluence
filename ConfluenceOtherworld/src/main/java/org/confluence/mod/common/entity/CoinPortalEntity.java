package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class CoinPortalEntity extends Entity {
    private static final int MIN_AMOUNT = 5;
    private static final int MAX_AMOUNT = 15;
    private static final int MAX_SAVED_AGE = 20 + (MAX_AMOUNT - 1) * 10;
    private static final EntityDataAccessor<Integer> DATA_AMOUNT = SynchedEntityData.defineId(CoinPortalEntity.class, EntityDataSerializers.INT);
    private int age = 0;
    private int amount = 0;
    private ParticleEmitter emitter;

    public CoinPortalEntity(EntityType<CoinPortalEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (!pLevel.isClientSide) {
            this.amount = pLevel.random.nextInt(MIN_AMOUNT, MAX_AMOUNT + 1);
            entityData.set(DATA_AMOUNT, amount);
        }
    }

    public CoinPortalEntity(Level level, Vec3 position) {
        super(ModEntities.COIN_PORTAL.get(), level);
        setNoGravity(true);
        setPos(position);
        setDeltaMovement(0.0, 0.5, 0.0);
        this.amount = level.random.nextInt(MIN_AMOUNT, MAX_AMOUNT + 1);
        entityData.set(DATA_AMOUNT, amount);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && amount <= 0) {
            // 损坏或被截断的当前格式存档不得凭空再生成一枚金币。
            discard();
            return;
        }
        if (level().isClientSide && (emitter == null || emitter.isRemoved()) && amount != 0) {
            MolangExp expression = new MolangExp("variable.amount", amount);
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("coin_portal"), expression);
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
        setDeltaMovement(getDeltaMovement().scale(0.96));
        move(MoverType.SELF, getDeltaMovement());
        if (!level().isClientSide && age >= 20 && age % 10 == 0) {
            LibEntityUtils.createItemEntity(ModItems.GOLD_COIN.get().getDefaultInstance(), getX(), getY(), getZ(), level(), 0);
            this.amount--;
            entityData.set(DATA_AMOUNT, amount);
            if (amount <= 0) {
                discard();
                return;
            }
        }
        this.age++;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_AMOUNT, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_AMOUNT.equals(key)) {
            this.amount = entityData.get(DATA_AMOUNT);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // 仅接受当前格式；数值边界同时限制损坏存档造成的额外奖励和异常长寿命。
        this.age = Mth.clamp(tag.getInt("Age"), 0, MAX_SAVED_AGE);
        this.amount = Mth.clamp(tag.getInt("Amount"), 0, MAX_AMOUNT);
        entityData.set(DATA_AMOUNT, amount);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", Mth.clamp(age, 0, MAX_SAVED_AGE));
        tag.putInt("Amount", Mth.clamp(amount, 0, MAX_AMOUNT));
    }
}
