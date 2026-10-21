package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

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
    private static final String CONSUMED_SOUL_TAG = "consumedSoul";
    private static final String SOULDANCER_BUFF_END_TAG = "souldancerBuffEnd";
    private static final String RITUAL_COUNT_TAG = "ritualCount";
    private static final String SURGE_BLAST_CHARGE_TAG = "surgeBlastCharge";
    private static final String LIFE_RETURN_LOST_TAG = "lifeReturnLost";
    private static final String MASTER_SWITCH_UNLOCKED_TAG = "masterSwitchUnlocked";
    private static final String CAST_WEAPON_DAMAGE_TAG = "castWeaponDamage";

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
            buf.writeVarInt(attachment.ritualCount);
        }

        @Override
        public SoulStorage read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable SoulStorage previousValue) {
            SoulStorage storage = new SoulStorage(holder);
            storage.soulsCount = buf.readVarInt();
            float currentSoul = buf.readFloat();
            storage.recoveryRatio = buf.readFloat();
            storage.soulMode = buf.readBoolean();
            storage.ritualCount = buf.readVarInt();
            storage.currentSoul = storage.soulMode ? currentSoul : MIN_CURRENT_SOUL;
            return storage;
        }
    };

    private @Nullable IAttachmentHolder owner;
    private int soulsCount;
    private float currentSoul;
    private float recoveryRatio;
    private boolean soulMode;
    /// 累计消耗的灵魂余量，供套装按"每消耗 N 点"结算使用，只存服务端。
    private float consumedSoul;
    /// 魂舞全套增益的结束时间（游戏刻），0 表示未激活。
    private long souldancerBuffEnd;
    /// 已完成的仪式次数，每次提供 {@link SoulSkillParameters#SOUL_PER_RITUAL} 点灵魂上限。
    private int ritualCount;
    /// 灵魂激荡冲击的累计层数，满层时引爆终极爆炸并清零。
    private int surgeBlastCharge;
    /// 生命归还期间累计失去的生命值，结束时按比例转成灵魂。
    private float lifeReturnLost;
    /// 是否用过堕魂星核：解锁后使用魂师主武器 / 魔法武器会在灵魂条与魔力条之间自动互切。
    private boolean masterSwitchUnlocked;
    /// 每个技能最近一次施法时手持武器的灵魂伤害快照，供延时 / 多段攻击保持伤害。
    private final Map<ResourceLocation, Float> castWeaponDamage = new HashMap<>();
    /// 上一刻是否处于灵魂透支（仅内存，用于检测效果结束）。
    private boolean soulDrainActive;
    /// 上一刻是否处于生命归还（仅内存，用于检测效果结束）。
    private boolean lifeReturnActive;

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
        nbt.putFloat(CONSUMED_SOUL_TAG, consumedSoul);
        nbt.putLong(SOULDANCER_BUFF_END_TAG, souldancerBuffEnd);
        nbt.putInt(RITUAL_COUNT_TAG, ritualCount);
        nbt.putInt(SURGE_BLAST_CHARGE_TAG, surgeBlastCharge);
        nbt.putFloat(LIFE_RETURN_LOST_TAG, lifeReturnLost);
        nbt.putBoolean(MASTER_SWITCH_UNLOCKED_TAG, masterSwitchUnlocked);
        CompoundTag weaponDamage = new CompoundTag();
        castWeaponDamage.forEach((id, value) -> weaponDamage.putFloat(id.toString(), value));
        nbt.put(CAST_WEAPON_DAMAGE_TAG, weaponDamage);
        return nbt;
    }

    /// 还原已保存的灵魂数值。
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.soulsCount = nbt.getInt(SOULS_COUNT_TAG);
        this.soulMode = nbt.getBoolean(SOUL_MODE_TAG);
        this.currentSoul = soulMode ? nbt.getFloat(CURRENT_SOUL_TAG) : MIN_CURRENT_SOUL;
        this.recoveryRatio = nbt.getFloat(RECOVERY_RATIO_TAG);
        this.consumedSoul = nbt.getFloat(CONSUMED_SOUL_TAG);
        this.souldancerBuffEnd = nbt.getLong(SOULDANCER_BUFF_END_TAG);
        this.ritualCount = Mth.clamp(nbt.getInt(RITUAL_COUNT_TAG), 0, SoulSkillParameters.MAX_RITUAL_COUNT);
        this.surgeBlastCharge = Math.max(0, nbt.getInt(SURGE_BLAST_CHARGE_TAG));
        this.lifeReturnLost = Math.max(0.0F, nbt.getFloat(LIFE_RETURN_LOST_TAG));
        this.masterSwitchUnlocked = nbt.getBoolean(MASTER_SWITCH_UNLOCKED_TAG);
        this.castWeaponDamage.clear();
        CompoundTag weaponDamage = nbt.getCompound(CAST_WEAPON_DAMAGE_TAG);
        for (String key : weaponDamage.getAllKeys()) {
            ResourceLocation skillId = ResourceLocation.tryParse(key);
            if (skillId != null) {
                castWeaponDamage.put(skillId, weaponDamage.getFloat(key));
            }
        }
    }

    /// 记录某技能本次施法时的武器灵魂伤害。
    public void setCastWeaponDamage(ResourceLocation skillId, float weaponDamage) {
        this.castWeaponDamage.put(skillId, weaponDamage);
    }

    /// 读取某技能最近一次施法时的武器灵魂伤害；没有记录时返回 0。
    public float getCastWeaponDamage(ResourceLocation skillId) {
        return this.castWeaponDamage.getOrDefault(skillId, 0.0F);
    }

    /// 是否已经用过堕魂星核（解锁灵魂条 / 魔力条随武器自动互切）。
    public boolean isMasterSwitchUnlocked() {
        return masterSwitchUnlocked;
    }

    /// 记录已用过堕魂星核。
    public void setMasterSwitchUnlocked(boolean masterSwitchUnlocked) {
        this.masterSwitchUnlocked = masterSwitchUnlocked;
    }

    /// 读取已完成的仪式次数。
    public int getRitualCount() {
        return ritualCount;
    }

    /// 设置仪式次数（0..6），超出上限的部分截断，并按新上限收拢当前灵魂值。
    public void setRitualCount(int ritualCount) {
        int clamped = Mth.clamp(ritualCount, 0, SoulSkillParameters.MAX_RITUAL_COUNT);
        if (this.ritualCount == clamped) {
            return;
        }
        this.ritualCount = clamped;
        updateMaxSoul();
        sync();
    }

    /// 读取灵魂激荡冲击的累计层数。
    public int getSurgeBlastCharge() {
        return surgeBlastCharge;
    }

    /// 设置灵魂激荡冲击的累计层数。
    public void setSurgeBlastCharge(int surgeBlastCharge) {
        this.surgeBlastCharge = Math.max(0, surgeBlastCharge);
    }

    /// 读取生命归还期间累计失去的生命值。
    public float getLifeReturnLost() {
        return lifeReturnLost;
    }

    /// 覆盖生命归还期间累计失去的生命值。
    public void setLifeReturnLost(float lifeReturnLost) {
        this.lifeReturnLost = Math.max(0.0F, lifeReturnLost);
    }

    /// 读取上一刻的灵魂透支状态。
    public boolean isSoulDrainActive() {
        return soulDrainActive;
    }

    /// 记录本刻的灵魂透支状态。
    public void setSoulDrainActive(boolean soulDrainActive) {
        this.soulDrainActive = soulDrainActive;
    }

    /// 读取上一刻的生命归还状态。
    public boolean isLifeReturnActive() {
        return lifeReturnActive;
    }

    /// 记录本刻的生命归还状态。
    public void setLifeReturnActive(boolean lifeReturnActive) {
        this.lifeReturnActive = lifeReturnActive;
    }

    /// 读取魂舞增益的结束时间（游戏刻）。
    public long getSouldancerBuffEnd() {
        return souldancerBuffEnd;
    }

    /// 设置魂舞增益的结束时间（游戏刻）。
    public void setSouldancerBuffEnd(long souldancerBuffEnd) {
        this.souldancerBuffEnd = souldancerBuffEnd;
    }

    /// 读取累计消耗的灵魂余量。
    public float getConsumedSoul() {
        return consumedSoul;
    }

    /// 覆盖累计消耗的灵魂余量。
    public void setConsumedSoul(float consumedSoul) {
        this.consumedSoul = consumedSoul;
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

    /// 永久成长容量 + 实体额外容量 + 仪式提供的容量，小数部分向下取整。
    public int getMaxSoul() {
        AttributeInstance attribute = owner instanceof LivingEntity living ? living.getAttribute(ConfluenceMagicLib.ADDITIONAL_SOUL) : null;
        double additionalSoul = attribute == null ? ConfluenceMagicLib.ADDITIONAL_SOUL.value().getDefaultValue() : attribute.getValue();
        return Mth.floor(soulsCount * SOUL_PER_COUNT + additionalSoul + ritualCount * SoulSkillParameters.SOUL_PER_RITUAL);
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
