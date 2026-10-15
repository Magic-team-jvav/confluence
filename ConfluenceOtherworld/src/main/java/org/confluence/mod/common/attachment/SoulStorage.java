package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.api.event.AdditionalSoulEvent;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class SoulStorage implements INBTSerializable<CompoundTag> {
    private static final int INITIAL_SOUL_COUNT = 1;
    private static final int SOUL_PER_COUNT = 20;
    private static final int INITIAL_BASE_SOUL = INITIAL_SOUL_COUNT * SOUL_PER_COUNT;
    private static final int MAX_BASE_SOUL = 400;
    private static final int MAX_SOUL_COUNT = MAX_BASE_SOUL / SOUL_PER_COUNT;
    private static final float MIN_SOUL = 0.0F;
    private static final float DEFAULT_RECOVERY_RATIO = 0.0F;
    private static final String SOULS_COUNT_TAG = "soulsCount";
    private static final String BASE_SOUL_REMAINDER_TAG = "baseSoulRemainder";
    private static final String ADDITIONAL_SOUL_TAG = "additionalSoul";
    private static final String CURRENT_SOUL_TAG = "currentSoul";
    private static final String RECOVERY_RATIO_TAG = "recoveryRatio";
    private static final String SOUL_MODE_TAG = "soulMode";

    public static final AttachmentSyncHandler<SoulStorage> SYNC_HANDLER = new AttachmentSyncHandler<>() {
        @Override
        public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
            return holder == to;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buf, SoulStorage attachment, boolean initialSync) {
            ByteBufCodecs.COMPOUND_TAG.encode(buf, initialSync ? attachment.save() : attachment.changes);
            if (!initialSync) {
                attachment.changes = new CompoundTag();
            }
        }

        @Override
        public SoulStorage read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable SoulStorage previousValue) {
            SoulStorage storage = previousValue == null ? new SoulStorage() : previousValue;
            CompoundTag nbt = storage.save().merge(ByteBufCodecs.COMPOUND_TAG.decode(buf));
            storage.deserializeNBT(buf.registryAccess(), nbt);
            storage.changes = new CompoundTag();
            return storage;
        }
    };

    private int soulsCount;
    private int baseSoulRemainder;
    private int additionalSoul;
    private float currentSoul;
    private float recoveryRatio;
    private boolean soulMode;
    private transient int maxSoul;
    private transient CompoundTag changes;

    public SoulStorage() {
        this.soulsCount = INITIAL_SOUL_COUNT;
        this.baseSoulRemainder = 0;
        this.additionalSoul = 0;
        this.currentSoul = MIN_SOUL;
        this.recoveryRatio = DEFAULT_RECOVERY_RATIO;
        this.maxSoul = -1;
        this.changes = save();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return save();
    }

    private CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(SOULS_COUNT_TAG, soulsCount);
        nbt.putInt(BASE_SOUL_REMAINDER_TAG, baseSoulRemainder);
        nbt.putInt(ADDITIONAL_SOUL_TAG, additionalSoul);
        nbt.putFloat(CURRENT_SOUL_TAG, currentSoul);
        nbt.putFloat(RECOVERY_RATIO_TAG, recoveryRatio);
        nbt.putBoolean(SOUL_MODE_TAG, soulMode);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.soulsCount = nbt.getInt(SOULS_COUNT_TAG);
        this.baseSoulRemainder = nbt.getInt(BASE_SOUL_REMAINDER_TAG);
        this.additionalSoul = nbt.getInt(ADDITIONAL_SOUL_TAG);
        this.soulMode = nbt.getBoolean(SOUL_MODE_TAG);
        this.currentSoul = soulMode ? nbt.getFloat(CURRENT_SOUL_TAG) : MIN_SOUL;
        float ratio = nbt.getFloat(RECOVERY_RATIO_TAG);
        this.recoveryRatio = Float.isFinite(ratio) && ratio >= DEFAULT_RECOVERY_RATIO ? ratio : DEFAULT_RECOVERY_RATIO;
        freshMaxSoul();
        this.changes = save();
    }

    public void freshMaxSoul() {
        this.maxSoul = soulsCount * SOUL_PER_COUNT + baseSoulRemainder + additionalSoul;
        float value = soulMode ? Mth.clamp(currentSoul, MIN_SOUL, maxSoul) : MIN_SOUL;
        if (currentSoul != value) {
            this.currentSoul = value;
            this.changes.putFloat(CURRENT_SOUL_TAG, currentSoul);
        }
    }

    public int getMaxSoul() {
        if (maxSoul < 0) freshMaxSoul();
        return maxSoul;
    }

    public float getCurrentSoul() {
        return soulMode ? currentSoul : MIN_SOUL;
    }

    public boolean isSoulMode() {
        return soulMode;
    }

    public void setSoulMode(boolean soulMode) {
        if (this.soulMode != soulMode) {
            setCurrentSoul(MIN_SOUL);
            this.soulMode = soulMode;
            this.changes.putBoolean(SOUL_MODE_TAG, soulMode);
        }
    }

    public void setCurrentSoul(float amount) {
        if (!Float.isFinite(amount)) {
            return;
        }
        float value = soulMode ? Mth.clamp(amount, MIN_SOUL, getMaxSoul()) : MIN_SOUL;
        if (currentSoul != value) {
            this.currentSoul = value;
            this.changes.putFloat(CURRENT_SOUL_TAG, currentSoul);
        }
    }

    public boolean canReceive() {return soulMode && currentSoul < getMaxSoul();}

    public boolean tryExtract(float amount) {
        if (!soulMode || !Float.isFinite(amount) || amount < MIN_SOUL || currentSoul < amount) {
            return false;
        }
        setCurrentSoul(currentSoul - amount);
        return true;
    }

    public void receive(float additional) {
        if (Float.isFinite(additional) && canReceive()) {
            float newSoul = Mth.clamp(currentSoul + additional, MIN_SOUL, getMaxSoul());
            if (newSoul != currentSoul) {
                this.currentSoul = newSoul;
                this.changes.putFloat(CURRENT_SOUL_TAG, currentSoul);
            }
        }
    }

    public int getSoulsCount() {
        return this.soulsCount;
    }

    public boolean isSoulMaximun() {
        return (long) soulsCount * SOUL_PER_COUNT + baseSoulRemainder >= MAX_BASE_SOUL;
    }

    public boolean adjustBaseSoul(int delta) {
        int baseSoul = Math.clamp((long) soulsCount * SOUL_PER_COUNT + baseSoulRemainder + delta, INITIAL_BASE_SOUL, MAX_BASE_SOUL);
        int count = baseSoul / SOUL_PER_COUNT;
        int remainder = baseSoul % SOUL_PER_COUNT;
        if (soulsCount == count && baseSoulRemainder == remainder) {
            return false;
        }
        if (soulsCount != count) {
            this.soulsCount = count;
            this.changes.putInt(SOULS_COUNT_TAG, soulsCount);
        }
        if (baseSoulRemainder != remainder) {
            this.baseSoulRemainder = remainder;
            this.changes.putInt(BASE_SOUL_REMAINDER_TAG, baseSoulRemainder);
        }
        freshMaxSoul();
        return true;
    }

    public void addSoul(int additional) {
        if (additional <= 0) {
            return;
        }
        if (!isSoulMaximun()) {
            adjustBaseSoul(Math.min(additional, MAX_SOUL_COUNT) * SOUL_PER_COUNT);
        }
    }

    public void flushAbility(ServerPlayer player) {
        int value = TCUtils.getValue(player, AccessoryItems.ADDITIONAL$SOUL);
        AdditionalSoulEvent event = NeoForge.EVENT_BUS.post(new AdditionalSoulEvent(player, this, value, additionalSoul));
        if (!event.isCanceled() && event.getNeoValue() != additionalSoul) {
            this.additionalSoul = event.getNeoValue();
            this.changes.putInt(ADDITIONAL_SOUL_TAG, additionalSoul);
            freshMaxSoul();
        }
    }

    public float getRecoveryRatio() {
        return this.recoveryRatio;
    }

    public void setRecoveryRatio(float newRecoveryRatio) {
        if (!Float.isFinite(newRecoveryRatio) || newRecoveryRatio < DEFAULT_RECOVERY_RATIO) {
            return;
        }
        if (recoveryRatio != newRecoveryRatio) {
            this.recoveryRatio = newRecoveryRatio;
            this.changes.putFloat(RECOVERY_RATIO_TAG, recoveryRatio);
        }
    }

    public void syncIfNeeded(ServerPlayer player) {
        if (!changes.isEmpty()) {
            player.syncData(ModAttachmentTypes.SOUL_STORAGE);
        }
    }

    public void onDeath(ServerPlayer player) {
        syncIfNeeded(player);
    }

    public void onRespawn(ServerPlayer player) {
        freshMaxSoul();
        syncIfNeeded(player);
    }
}
