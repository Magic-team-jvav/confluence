package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class SoulStorage implements INBTSerializable<CompoundTag> {
    private static final int INITIAL_SOUL_COUNT = 1;
    private static final int SOUL_PER_COUNT = 20;
    private static final int MAX_SOUL_COUNT = 20;
    private static final float MIN_CURRENT_SOUL = 0.0F;
    private static final float DEFAULT_RECOVERY_RATIO = 0.0F;
    private static final String SOULS_COUNT_TAG = "soulsCount";
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
            buf.writeVarInt(attachment.soulsCount);
            buf.writeFloat(attachment.currentSoul);
            buf.writeFloat(attachment.recoveryRatio);
            buf.writeBoolean(attachment.soulMode);
        }

        @Override
        public SoulStorage read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable SoulStorage previousValue) {
            SoulStorage storage = new SoulStorage(holder);
            storage.soulsCount = buf.readVarInt();
            float currentSoul = buf.readFloat();
            storage.recoveryRatio = buf.readFloat();
            storage.soulMode = buf.readBoolean();
            storage.currentSoul = storage.soulMode ? currentSoul : MIN_CURRENT_SOUL;
            return storage;
        }
    };

    private @Nullable IAttachmentHolder owner;
    private int soulsCount;
    private float currentSoul;
    private float recoveryRatio;
    private boolean soulMode;

    public SoulStorage(IAttachmentHolder owner) {
        this.soulsCount = INITIAL_SOUL_COUNT;
        this.currentSoul = MIN_CURRENT_SOUL;
        this.recoveryRatio = DEFAULT_RECOVERY_RATIO;
        this.soulMode = false;
        this.owner = owner;
    }

    /// 保存灵魂成长、当前值、恢复比例和资源模式。
    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(SOULS_COUNT_TAG, soulsCount);
        nbt.putFloat(CURRENT_SOUL_TAG, currentSoul);
        nbt.putFloat(RECOVERY_RATIO_TAG, recoveryRatio);
        nbt.putBoolean(SOUL_MODE_TAG, soulMode);
        return nbt;
    }

    /// 还原已保存的灵魂数值。
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.soulsCount = nbt.getInt(SOULS_COUNT_TAG);
        this.soulMode = nbt.getBoolean(SOUL_MODE_TAG);
        this.currentSoul = soulMode ? nbt.getFloat(CURRENT_SOUL_TAG) : MIN_CURRENT_SOUL;
        this.recoveryRatio = nbt.getFloat(RECOVERY_RATIO_TAG);
    }

    /// 按当前灵魂上限压低超出上限的当前值。
    public void freshMaxSoul() {
        if (updateMaxSoul()) {
            sync();
        }
    }

    /// 按当前属性上限规范灵魂值，不单独发送同步。
    private boolean updateMaxSoul() {
        float previousSoul = currentSoul;
        this.currentSoul = soulMode ? Mth.clamp(currentSoul, MIN_CURRENT_SOUL, getMaxSoul()) : MIN_CURRENT_SOUL;
        return Float.compare(previousSoul, currentSoul) != 0;
    }

    /// 永久成长容量加实体额外容量，小数部分向下取整。
    public int getMaxSoul() {
        AttributeInstance attribute = owner instanceof LivingEntity living ? living.getAttribute(ConfluenceMagicLib.ADDITIONAL_SOUL) : null;
        double additionalSoul = attribute == null ? ConfluenceMagicLib.ADDITIONAL_SOUL.value().getDefaultValue() : attribute.getValue();
        return Mth.floor(soulsCount * SOUL_PER_COUNT + additionalSoul);
    }

    /// 读取当前灵魂值，魔力模式固定为零。
    public float getCurrentSoul() {
        return soulMode ? currentSoul : MIN_CURRENT_SOUL;
    }

    /// 设置当前灵魂值；非灵魂模式只能保持为零。
    public void setCurrentSoul(float currentSoul) {
        float previousSoul = this.currentSoul;
        this.currentSoul = soulMode ? Mth.clamp(currentSoul, MIN_CURRENT_SOUL, getMaxSoul()) : MIN_CURRENT_SOUL;
        if (Float.compare(previousSoul, this.currentSoul) != 0) {
            sync();
        }
    }

    public boolean isSoulMode() {
        return soulMode;
    }

    /// 切换资源模式，关闭灵魂模式时清空当前灵魂。
    public void setSoulMode(boolean soulMode) {
        if (this.soulMode == soulMode) {
            return;
        }

        this.soulMode = soulMode;
        if (!soulMode) {
            this.currentSoul = MIN_CURRENT_SOUL;
        }
        sync();
    }

    /// 灵魂模式下扣除足量灵魂，成功时返回 true。
    public boolean extract(float amount) {
        if (!soulMode || currentSoul < amount) {
            return false;
        }

        setCurrentSoul(currentSoul - amount);
        return true;
    }

    /// 判断当前灵魂值是否低于上限。
    public boolean canReceive() {return soulMode && currentSoul < getMaxSoul();}

    /// 接收灵魂增量，超过上限时取上限。
    public void receive(float additional) {
        if (canReceive()) {
            setCurrentSoul(currentSoul + additional);
        }
    }

    public int getSoulsCount() {
        return this.soulsCount;
    }

    /// 判断灵魂成长数量是否达到上限。
    public boolean isSoulMaximun() {
        return soulsCount >= MAX_SOUL_COUNT;
    }

    /// 调整成长计数，保持初始容量至永久成长上限。
    public void addSoul(int additional) {
        int newCount = Mth.clamp(soulsCount + additional, INITIAL_SOUL_COUNT, MAX_SOUL_COUNT);
        if (newCount != soulsCount) {
            this.soulsCount = newCount;
            updateMaxSoul();
            sync();
        }
    }

    public float getRecoveryRatio() {
        return this.recoveryRatio;
    }

    public void setRecoveryRatio(float newRecoveryRatio) {
        float previousRatio = recoveryRatio;
        this.recoveryRatio = newRecoveryRatio;
        if (Float.compare(previousRatio, recoveryRatio) != 0) {
            sync();
        }
    }

    /// 死亡时保留已有灵魂值，并同步当前状态。
    public void onDeath() {
        sync();
    }

    /// 复活时刷新容量来源并规范当前灵魂值。
    public void onRespawn() {
        freshMaxSoul();
    }

    /// 仅向附件所属服务端玩家同步当前数值。
    private void sync() {
        if (owner instanceof ServerPlayer player) {
            player.syncData(ModAttachmentTypes.SOUL_STORAGE);
        }
    }

    /// 获取实体的灵魂数值附件，不存在时按默认值创建。
    public static SoulStorage of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.SOUL_STORAGE);
    }
}
