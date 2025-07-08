package org.confluence.mod.common.attachment;

import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.FloatSupplier;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;

public class ManaStorage implements INBTSerializable<CompoundTag> {
    private int stars;
    private int additionalMana;
    private float currentMana;
    private transient int regenerateDelay;
    private transient int maxMana;
    private boolean fastManaRegeneration;

    private boolean arcaneCrystalUsed;

    public ManaStorage() {
        this.stars = 1;
        this.additionalMana = 0;
        this.currentMana = 20;
        this.regenerateDelay = 0;
        this.maxMana = -1;
        this.fastManaRegeneration = false;

        this.arcaneCrystalUsed = false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("stars", stars);
        nbt.putInt("additionalMana", additionalMana);
        nbt.putFloat("currentMana", currentMana);
        nbt.putBoolean("fastManaRegeneration", fastManaRegeneration);
        nbt.putBoolean("arcaneCrystalUsed", arcaneCrystalUsed);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.stars = nbt.getInt("stars");
        this.additionalMana = nbt.getInt("additionalMana");
        this.currentMana = nbt.getInt("currentMana");
        this.fastManaRegeneration = nbt.getBoolean("fastManaRegeneration");
        this.arcaneCrystalUsed = nbt.getBoolean("arcaneCrystalUsed");
    }

    public boolean receiveMana(FloatSupplier sup) {
        if (!canReceive()) return false;
        this.currentMana = Math.min(getMaxMana(), sup.getAsFloat() + currentMana);
        return true;
    }

    public boolean extractMana(FloatSupplier sup, ServerPlayer serverPlayer) {
        if (!canExtract()) return false;
        float extract = sup.getAsFloat() * (1.0F - TCUtils.getAccessoriesValue(serverPlayer, AccessoryItems.MANA$USE$REDUCE));
        if (PlayerUtils.applyAutoGetMana(serverPlayer, currentMana, extract)) return false;
        this.currentMana -= extract;
        EnchantmentUtils.repairPlayerItems(serverPlayer, extract);
        return true;
    }

    public boolean forceExtractMana(FloatSupplier sup) {
        if (!canExtract()) return false;
        float extract = sup.getAsFloat();
        if (currentMana < extract) return false;
        this.currentMana -= extract;
        return true;
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

    public boolean isStarMaximum() {
        return stars >= 10;
    }

    public void flushAbility(ServerPlayer serverPlayer) {
        this.fastManaRegeneration = TCUtils.hasAccessoriesType(serverPlayer, AccessoryItems.FAST$MANA$GENERATION);
        int value = TCUtils.getAccessoriesValue(serverPlayer, AccessoryItems.ADDITIONAL$MANA);
        if (serverPlayer.hasEffect(ModEffects.CLAIRVOYANCE)) value += 20;
        int posted = HookMapManager.postHooks(ModHookTypes.ADDITIONAL_MANA.get(), (owner, hook, original) -> hook.additional(owner, serverPlayer, original), serverPlayer, value);
        AdditionalManaEvent event = NeoForge.EVENT_BUS.post(new AdditionalManaEvent(serverPlayer, this, posted, additionalMana));
        if (!event.isCanceled() && event.getNeoValue() != additionalMana) {
            this.additionalMana = event.getNeoValue();
            freshMaxMana();
            PlayerUtils.syncMana2Client(serverPlayer, this);
        }
    }

    public boolean isFastManaRegeneration() {
        return fastManaRegeneration;
    }

    public boolean setArcaneCrystalUsed() {
        if (arcaneCrystalUsed) return false;
        this.arcaneCrystalUsed = true;
        return true;
    }

    public boolean isArcaneCrystalUsed() {
        return arcaneCrystalUsed;
    }
}
