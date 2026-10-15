package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.ApiStatus;

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

    public boolean decreaseStar() {
        if (stars <= 1) return false;
        float oldMax = getMaxMana();
        this.stars--;
        this.currentMana = Mth.clamp(currentMana - 20.0F, 0.0F, oldMax);
        freshMaxMana();
        return true;
    }

    @ApiStatus.Internal
    public void clearStars() {
        this.stars = 1;
        freshMaxMana();
    }

    public boolean isStarMaximum() {
        return stars >= 10;
    }

    public void flushAbility(ServerPlayer player) {
        this.fastManaRegeneration = TCUtils.hasType(player, AccessoryItems.FAST$MANA$GENERATION);
        int value = TCUtils.getValue(player, AccessoryItems.ADDITIONAL$MANA);
        if (player.hasEffect(ModEffects.CLAIRVOYANCE)) value += 20;
        AdditionalManaEvent event = NeoForge.EVENT_BUS.post(new AdditionalManaEvent(player, this, value, additionalMana));
        if (!event.isCanceled() && event.getNeoValue() != additionalMana) {
            this.additionalMana = event.getNeoValue();
            freshMaxMana();
            PlayerUtils.syncMana2Client(player, this);
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

    public static ManaStorage of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.MANA_STORAGE);
    }
}
