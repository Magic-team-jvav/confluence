package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.ApiStatus;

public class ManaStorage implements INBTSerializable<CompoundTag> {
    private final LivingEntity owner;
    private int stars;
    private float currentMana;
    private transient int regenerateDelay;

    private boolean arcaneCrystalUsed;

    public ManaStorage(IAttachmentHolder owner) {
        this.stars = 1;
        this.currentMana = 20;
        this.regenerateDelay = 0;

        this.arcaneCrystalUsed = false;
        this.owner = (LivingEntity) owner;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("stars", stars);
        nbt.putFloat("currentMana", currentMana);
        nbt.putBoolean("arcaneCrystalUsed", arcaneCrystalUsed);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.stars = nbt.contains("stars") ? nbt.getInt("stars") : 1;
        this.currentMana = nbt.getFloat("currentMana");
        this.arcaneCrystalUsed = nbt.getBoolean("arcaneCrystalUsed");
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

    /// 读取当前魔力；数值在两种资源模式之间保留，是否显示由 UI 判断。
    public float getCurrentMana() {
        return currentMana;
    }

    /// 设置当前魔力并夹在 0..上限之间；灵魂模式下也保留该值，供切回魔力条时使用。
    public void setCurrentMana(float currentMana) {
        this.currentMana = owner.level().isClientSide ? currentMana : Mth.clamp(currentMana, 0.0F, getMaxMana());
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

    public float getMaxMana() {
        return (float) owner.getAttributeValue(ConfluenceMagicLib.MAX_MANA);
    }

    public boolean clampCurrentMana() {
        float maxMana = getMaxMana();
        if (currentMana > maxMana) {
            this.currentMana = maxMana;
            return true;
        }
        return false;
    }

    public boolean canExtract() {
        return !isSoulMode() && currentMana > 0;
    }

    public boolean canReceive() {
        return !isSoulMode() && currentMana < getMaxMana();
    }

    /// 只读取已存在的灵魂附件，避免构造和读档阶段相互创建。
    private boolean isSoulMode() {
        return SoulStorage.of(owner).isSoulMode();
    }

    public boolean addStar() {
        if (!isStarMaximum()) {
            this.stars++;
            restoreCrystalModifier();
            return true;
        }
        return false;
    }

    public boolean decreaseStar() {
        if (stars <= 1) {
            return false;
        }
        this.stars--;
        restoreCrystalModifier();
        setCurrentMana(currentMana - 20.0F);
        return true;
    }

    @ApiStatus.Internal
    public void clearStars() {
        this.stars = 1;
        restoreCrystalModifier();
        clampCurrentMana();
    }

    public boolean isStarMaximum() {
        return stars >= 10;
    }

    public void restoreCrystalModifier() {
        AttributeInstance attribute = owner.getAttribute(ConfluenceMagicLib.MAX_MANA);
        var id = Confluence.asResource("mana_crystal");
        AttributeModifier previous = attribute.getModifier(id);
        int amount = (stars - 1) * 20;
        if (amount == 0) {
            if (previous != null) {
                attribute.removeModifier(id);
            }
        } else if (previous == null || previous.amount() != amount) {
            attribute.addOrReplacePermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public boolean setArcaneCrystalUsed() {
        if (arcaneCrystalUsed) return false;
        this.arcaneCrystalUsed = true;
        return true;
    }

    public boolean isArcaneCrystalUsed() {
        return arcaneCrystalUsed;
    }

    public static ManaStorage of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.MANA_STORAGE);
    }
}
