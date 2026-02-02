package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.UnknownNullability;

public class SoulStorage implements INBTSerializable<CompoundTag> {
    private int soulsCount;
    private int additionalSoul;
    private float currentSoul;
    private float recoveryRatio;
    private transient int maxSoul;

    public SoulStorage() {
        this.soulsCount = 1;
        this.additionalSoul = 0;
        this.currentSoul = 10;
        this.recoveryRatio = 0;
        this.maxSoul = -1;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("soulsCount", soulsCount);
        nbt.putInt("additionalSoul", additionalSoul);
        nbt.putFloat("currentSoul", currentSoul);
        nbt.putFloat("recoveryRatio", recoveryRatio);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.soulsCount = nbt.getInt("soulsCount");
        this.additionalSoul = nbt.getInt("additionalSoul");
        this.currentSoul = nbt.getFloat("currentSoul");
        this.recoveryRatio = nbt.getFloat("recoveryRatio");
    }

    public void freshMaxSoul() {
        this.maxSoul = soulsCount * 10 + additionalSoul;
        if (currentSoul > maxSoul) this.currentSoul = maxSoul;
    }

    public int getMaxSoul() {
        if (maxSoul < 0) freshMaxSoul();
        return maxSoul;
    }

    public float getCurrentSoul() {
        return this.currentSoul;
    }

    public boolean canReceive() {return currentSoul < getMaxSoul();}

    public void receive(float additional) {
        if (canReceive()) {
            if ((currentSoul + additional) <= getMaxSoul()) this.currentSoul += additional;
            else this.currentSoul = getMaxSoul();
        }
    }

    public int getSoulsCount() {
        return this.soulsCount;
    }

    public boolean isSoulMaximun() {
        return soulsCount >= 20;
    }

    public void addSoul(int additional) {
        if (!isSoulMaximun()) {
            if ((soulsCount + additional) <= 20) this.soulsCount += additional;
            else this.soulsCount = 20;
        }
    }

    public float getRecoveryRatio() {
        return this.recoveryRatio;
    }

    public void setRecoveryRatio(float newRecoveryRatio) {
        this.recoveryRatio = newRecoveryRatio;
    }

    public static SoulStorage of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.SOUL_STORAGE);
    }
}
