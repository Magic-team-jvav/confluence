package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.ApiStatus;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

public class ManaStorage implements IPortNBTSerializable<CompoundTag> {
    private static final int MIN_STARS = 1;
    private static final int MAX_STARS = 10;
    private int stars;
    private int additionalMana;
    private float currentMana;
    private transient int regenerateDelay;
    private transient int maxMana;
    private boolean fastManaRegeneration;

    public ManaStorage() {
        this.stars = 1;
        this.additionalMana = 0;
        this.currentMana = 20;
        this.regenerateDelay = 0;
        this.maxMana = -1;
        this.fastManaRegeneration = false;

    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("stars", stars);
        nbt.putInt("additionalMana", additionalMana);
        nbt.putFloat("currentMana", currentMana);
        nbt.putBoolean("fastManaRegeneration", fastManaRegeneration);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        // 对缺失或损坏字段采用安全默认值；1.20 侧只维护当前格式，不承担任何旧存档迁移。
        if (nbt.contains("stars")) {
            this.stars = Mth.clamp(nbt.getInt("stars"), MIN_STARS, MAX_STARS);
        }
        if (nbt.contains("additionalMana")) {
            int maximumAdditionalMana = Integer.MAX_VALUE - this.stars * 20;
            this.additionalMana = Mth.clamp(nbt.getInt("additionalMana"), 0, maximumAdditionalMana);
        }
        if (nbt.contains("currentMana")) {
            float savedMana = nbt.getFloat("currentMana");
            this.currentMana = Float.isFinite(savedMana) ? Math.max(0.0F, savedMana) : 0.0F;
        }
        this.fastManaRegeneration = nbt.getBoolean("fastManaRegeneration");
        // maxMana 是运行时缓存，不从 NBT 信任；重算同时把当前魔力收敛到合法上限。
        this.maxMana = -1;
        freshMaxMana();
    }

    public boolean receiveMana(FloatSupplier sup) {
        if (!canReceive()) return false;
        this.currentMana = Mth.clamp(sup.getAsFloat() + currentMana, 0.0F, getMaxMana());
        return true;
    }

    public boolean extractMana(FloatSupplier sup, ServerPlayer serverPlayer) {
        if (!canExtract()) return false;
        float extract = sup.getAsFloat() * (1.0F - TCUtils.getValue(serverPlayer, AccessoryItems.MANA$USE$REDUCE));
        if (PlayerUtils.applyAutoGetMana(serverPlayer, currentMana, extract)) return false;
        this.currentMana = Mth.clamp(currentMana - extract, 0.0F, getMaxMana());
        if (extract > 0.0F) setRegenerateDelay();
        EnchantmentUtils.repairPlayerItems(serverPlayer, extract);
        return true;
    }

    public boolean forceExtractMana(FloatSupplier sup) {
        if (!canExtract()) return false;
        float extract = sup.getAsFloat();
        if (currentMana < extract) return false;
        this.currentMana = Mth.clamp(currentMana - extract, 0.0F, getMaxMana());
        if (extract > 0.0F) setRegenerateDelay();
        return true;
    }

    /// 为统一弹幕事务提交一笔已经预先解析的魔力成本。
    ///
    /// <p>调用方必须传入准备阶段观察到的魔力值；若期间有事件修改了魔力，本方法会拒绝提交，
    /// 防止基于过期状态透支。自动魔力药水只把恢复量作为本次提交的临时输入，药水物品本身仍由
    /// Otherworld 的具体成本实现负责扣除和回滚。</p>
    ///
    /// @return 是否从准备阶段的同一状态精确完成了提交
    @ApiStatus.Internal
    public boolean commitProjectileCost(float expectedCurrentMana, float restoredByPotion, float amount) {
        requireFiniteMana(expectedCurrentMana, "Expected current mana");
        requireFiniteMana(restoredByPotion, "Potion mana restoration");
        requireFiniteMana(amount, "Projectile mana cost");
        if (Float.compare(currentMana, expectedCurrentMana) != 0) {
            return false;
        }
        float available = Math.min(getMaxMana(), currentMana + restoredByPotion);
        if (available < amount) {
            return false;
        }
        currentMana = available - amount;
        if (amount > 0.0F) {
            setRegenerateDelay();
        }
        return true;
    }

    /// 精确恢复统一弹幕事务提交前的魔力与再生延迟。
    ///
    /// <p>仅供同一服务端线程中的补偿回滚使用；它不会触发药水、附魔或饰品副作用。</p>
    @ApiStatus.Internal
    public void restoreProjectileCostState(float mana, int delay) {
        requireFiniteMana(mana, "Restored current mana");
        if (mana > getMaxMana()) {
            throw new IllegalArgumentException("Restored current mana must not exceed maximum mana");
        }
        currentMana = mana;
        regenerateDelay = delay;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public int getRegenerateDelay() {
        return regenerateDelay;
    }

    public void setRegenerateDelay(int regenerateDelay) {
        this.regenerateDelay = regenerateDelay;
    }

    public void setRegenerateDelay() {
        this.regenerateDelay = Mth.ceil(0.7F * ((1 - currentMana / getMaxMana()) * 240 + 45));
    }

    public int getMaxMana() {
        if (maxMana < 0) {
            freshMaxMana();
        }
        return maxMana;
    }

    public void freshMaxMana() {
        this.maxMana = stars * 20 + additionalMana;
        if (currentMana > maxMana) {
            this.currentMana = maxMana;
        }
    }

    public boolean canExtract() {
        return currentMana > 0;
    }

    public boolean canReceive() {
        return currentMana < getMaxMana();
    }

    public boolean addStar() {
        if (!isStarMaximum()) {
            this.stars++;
            freshMaxMana();
            return true;
        }
        return false;
    }

    /// MagicLib 永久升级 API 使用的零基等级：0 代表初始一颗星，9 代表十颗星上限。
    public int getStarUpgrades() {
        return stars - MIN_STARS;
    }

    /// 自定义 levelAccess 的权威写入口。正向升级保留当前魔力；反向回溯按每颗星 20 点同步扣减当前魔力，
    /// 与 1.21 的 decreaseStar 语义一致，最后统一刷新并夹紧最大魔力缓存。
    public void setStarUpgrades(int upgrades) {
        int targetStars = Mth.clamp(upgrades + MIN_STARS, MIN_STARS, MAX_STARS);
        if (targetStars < this.stars) {
            this.currentMana = Math.max(0.0F, this.currentMana - (this.stars - targetStars) * 20.0F);
        }
        this.stars = targetStars;
        freshMaxMana();
    }

    @ApiStatus.Internal
    public void clearStars() {
        this.stars = 1;
        freshMaxMana();
    }

    public boolean isStarMaximum() {
        return stars >= MAX_STARS;
    }

    public void flushAbility(ServerPlayer player) {
        this.fastManaRegeneration = TCUtils.hasType(player, AccessoryItems.FAST$MANA$GENERATION);
        int value = TCUtils.getValue(player, AccessoryItems.ADDITIONAL$MANA);
        if (player.hasEffect(ModEffects.CLAIRVOYANCE.get())) value += 20;
        AdditionalManaEvent event = PortEventHandler.postEventWithReturn(new AdditionalManaEvent(player, this, value, additionalMana));
        if (!event.isCanceled() && event.getNeoValue() != additionalMana) {
            this.additionalMana = event.getNeoValue();
            freshMaxMana();
            PlayerUtils.syncMana2Client(player, this);
        }
    }

    public boolean isFastManaRegeneration() {
        return fastManaRegeneration;
    }

    public static ManaStorage of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.MANA_STORAGE);
    }

    private static void requireFiniteMana(float value, String fieldName) {
        if (!Float.isFinite(value) || value < 0.0F) {
            throw new IllegalArgumentException(fieldName + " must be finite and non-negative");
        }
    }
}
