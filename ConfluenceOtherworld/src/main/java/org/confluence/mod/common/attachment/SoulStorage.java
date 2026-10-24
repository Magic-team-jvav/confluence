package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.mesdag.portlib.attachment.PortAttachmentSyncHandler;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class SoulStorage implements IPortNBTSerializable<CompoundTag> {
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
    private static final String STAR_LINK_MAGIC_BONUS_TAG = "starLinkMagicBonus";

    public static final PortAttachmentSyncHandler<SoulStorage> SYNC_HANDLER = new PortAttachmentSyncHandler<>() {
        @Override
        public boolean sendToPlayer(IPortAttachmentHolder holder, ServerPlayer to) {
            return holder == to;
        }

        @Override
        public void write(PortRegistryFriendlyByteBuf buf, SoulStorage attachment, boolean initialSync) {
            buf.writeVarInt(attachment.soulsCount);
            buf.writeFloat(attachment.currentSoul);
            buf.writeFloat(attachment.recoveryRatio);
            buf.writeBoolean(attachment.soulMode);
            buf.writeVarInt(attachment.ritualCount);
        }

        @Override
        public SoulStorage read(IPortAttachmentHolder holder, PortRegistryFriendlyByteBuf buf, @Nullable SoulStorage previousValue) {
            SoulStorage storage = new SoulStorage(holder);
            storage.soulsCount = buf.readVarInt();
            float currentSoul = buf.readFloat();
            storage.recoveryRatio = buf.readFloat();
            storage.soulMode = buf.readBoolean();
            storage.ritualCount = buf.readVarInt();
            /// 灵魂值不再随模式清零：切到魔力条时保留原值，方便灵魂手环系列来回切换。
            storage.currentSoul = currentSoul;
            return storage;
        }
    };

    private @Nullable IPortAttachmentHolder owner;
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
    private int surgeBlastCharge;
    private float lifeReturnLost;
    /// 是否用过堕魂星核：解锁后使用魂师主武器 / 魔法武器会在灵魂条与魔力条之间自动互切。
    private boolean masterSwitchUnlocked;
    /// 每个技能最近一次施法时手持武器的灵魂伤害快照，供延时 / 多段攻击保持伤害。
    private final Map<ResourceLocation, Float> castWeaponDamage = new HashMap<>();
    /// 星魂连结：从灵魂增伤增益继承到「魔法增伤」增益上的数值（百分比，0.06 = +6%）。
    private float starLinkMagicBonus;
    /// 星魂连结：来源增益的完整时长记录，结束瞬间按一半写进魔法增伤增益（仅内存）。
    private int enhancedSoulFullDuration;
    private int goOverFullDuration;
    /// 上一刻是否持有对应灵魂增伤增益（仅内存，用于检测结束）。
    private boolean enhancedSoulActive;
    private boolean goOverActive;
    private boolean soulDrainActive;
    private boolean lifeReturnActive;

    public SoulStorage(IPortAttachmentHolder owner) {
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
        nbt.putFloat(STAR_LINK_MAGIC_BONUS_TAG, starLinkMagicBonus);
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
        /// 灵魂值不再随模式清零：切到魔力条时保留原值，方便灵魂手环系列来回切换。
        this.currentSoul = nbt.getFloat(CURRENT_SOUL_TAG);
        this.recoveryRatio = nbt.getFloat(RECOVERY_RATIO_TAG);
        this.consumedSoul = nbt.getFloat(CONSUMED_SOUL_TAG);
        this.souldancerBuffEnd = nbt.getLong(SOULDANCER_BUFF_END_TAG);
        this.ritualCount = Mth.clamp(nbt.getInt(RITUAL_COUNT_TAG), 0, SoulSkillParameters.MAX_RITUAL_COUNT);
        this.surgeBlastCharge = Math.max(0, nbt.getInt(SURGE_BLAST_CHARGE_TAG));
        this.lifeReturnLost = Math.max(0.0F, nbt.getFloat(LIFE_RETURN_LOST_TAG));
        this.masterSwitchUnlocked = nbt.getBoolean(MASTER_SWITCH_UNLOCKED_TAG);
        this.starLinkMagicBonus = Math.max(0.0F, nbt.getFloat(STAR_LINK_MAGIC_BONUS_TAG));
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

    public int getRitualCount() {
        return ritualCount;
    }

    public void setRitualCount(int ritualCount) {
        int clamped = Mth.clamp(ritualCount, 0, SoulSkillParameters.MAX_RITUAL_COUNT);
        if (this.ritualCount == clamped) {
            return;
        }
        this.ritualCount = clamped;
        updateMaxSoul();
        sync();
    }

    public int getSurgeBlastCharge() {
        return surgeBlastCharge;
    }

    public void setSurgeBlastCharge(int surgeBlastCharge) {
        this.surgeBlastCharge = Math.max(0, surgeBlastCharge);
    }

    public float getLifeReturnLost() {
        return lifeReturnLost;
    }

    public void setLifeReturnLost(float lifeReturnLost) {
        this.lifeReturnLost = Math.max(0.0F, lifeReturnLost);
    }

    public boolean isSoulDrainActive() {
        return soulDrainActive;
    }

    public void setSoulDrainActive(boolean soulDrainActive) {
        this.soulDrainActive = soulDrainActive;
    }

    public boolean isLifeReturnActive() {
        return lifeReturnActive;
    }

    public void setLifeReturnActive(boolean lifeReturnActive) {
        this.lifeReturnActive = lifeReturnActive;
    }

    /// 星魂连结继承的魔法增伤数值。
    public float getStarLinkMagicBonus() {
        return starLinkMagicBonus;
    }

    /// 设置星魂连结继承的魔法增伤数值。
    public void setStarLinkMagicBonus(float starLinkMagicBonus) {
        this.starLinkMagicBonus = Math.max(0.0F, starLinkMagicBonus);
    }

    /// 读取 / 记录强力附魂增益的完整时长与上一刻状态。
    public int getEnhancedSoulFullDuration() {
        return enhancedSoulFullDuration;
    }

    public void setEnhancedSoulFullDuration(int ticks) {
        this.enhancedSoulFullDuration = Math.max(0, ticks);
    }

    public boolean isEnhancedSoulActive() {
        return enhancedSoulActive;
    }

    public void setEnhancedSoulActive(boolean active) {
        this.enhancedSoulActive = active;
    }

    /// 读取 / 记录复习增益的完整时长与上一刻状态。
    public int getGoOverFullDuration() {
        return goOverFullDuration;
    }

    public void setGoOverFullDuration(int ticks) {
        this.goOverFullDuration = Math.max(0, ticks);
    }

    public boolean isGoOverActive() {
        return goOverActive;
    }

    public void setGoOverActive(boolean active) {
        this.goOverActive = active;
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
        this.currentSoul = Mth.clamp(currentSoul, MIN_CURRENT_SOUL, getMaxSoul());
        return Float.compare(previousSoul, currentSoul) != 0;
    }

    /// 永久成长容量加实体额外容量，小数部分向下取整。
    public int getMaxSoul() {
        AttributeInstance attribute = owner instanceof LivingEntity living ? living.getAttribute(ConfluenceMagicLib.ADDITIONAL_SOUL) : null;
        double additionalSoul = attribute == null ? ConfluenceMagicLib.ADDITIONAL_SOUL.value().getDefaultValue() : attribute.getValue();
        return Mth.floor(soulsCount * SOUL_PER_COUNT + additionalSoul + ritualCount * SoulSkillParameters.SOUL_PER_RITUAL);
    }

    /// 读取当前灵魂值；数值在两种资源模式之间保留，是否显示由 UI 判断。
    public float getCurrentSoul() {
        return currentSoul;
    }

    /// 设置当前灵魂值并夹在 0..上限之间；非灵魂模式下也保留该值，供切回灵魂条时使用。
    public void setCurrentSoul(float currentSoul) {
        float previousSoul = this.currentSoul;
        this.currentSoul = Mth.clamp(currentSoul, MIN_CURRENT_SOUL, getMaxSoul());
        if (Float.compare(previousSoul, this.currentSoul) != 0) {
            sync();
        }
    }

    public boolean isSoulMode() {
        return soulMode;
    }

    /// 切换资源模式；灵魂值保留，由切换入口决定是否清空（灵魂手环系列不清空）。
    public void setSoulMode(boolean soulMode) {
        if (this.soulMode == soulMode) {
            return;
        }

        this.soulMode = soulMode;
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

