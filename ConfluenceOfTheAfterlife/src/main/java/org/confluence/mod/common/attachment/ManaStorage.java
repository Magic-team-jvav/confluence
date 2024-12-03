package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public class ManaStorage implements INBTSerializable<CompoundTag> {
    private int stars;
    private int additionalMana;
    private int currentMana;
    private transient int regenerateDelay;
    private transient Integer maxMana;
    private boolean fastManaRegeneration;

    private boolean arcaneCrystalUsed;

    public ManaStorage() {
        this.stars = 1;
        this.additionalMana = 0;
        this.currentMana = 20;
        this.regenerateDelay = 0;
        this.fastManaRegeneration = false;

        this.arcaneCrystalUsed = false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("stars", stars);
        nbt.putInt("additionalMana", additionalMana);
        nbt.putInt("currentMana", currentMana);
        nbt.putBoolean("fastManaRegeneration", fastManaRegeneration);
        nbt.putBoolean("arcaneCrystalUsed", arcaneCrystalUsed);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.stars = nbt.getInt("stars");
        this.additionalMana = nbt.getInt("additionalMana");
        this.currentMana = nbt.getInt("currentMana");
        this.fastManaRegeneration = nbt.getBoolean("fastManaRegeneration");
        this.arcaneCrystalUsed = nbt.getBoolean("arcaneCrystalUsed");
    }

    public boolean receiveMana(IntSupplier sup) {
        if (!canReceive()) return false;
        int received = Math.min(sup.getAsInt(), getMaxMana() - currentMana);
        this.currentMana += received;
        return true;
    }

    public boolean extractMana(IntSupplier sup, ServerPlayer serverPlayer) {
        if (!canExtract()) return false;
        int extract = (int) (sup.getAsInt() * (1.0F - TCUtils.getAccessoriesValue(serverPlayer, AccessoryItems.MANA$USE$REDUCE)));
        if (currentMana < extract) {
            if (!TCUtils.hasAccessoriesType(serverPlayer, AccessoryItems.AUTO$GET$MANA)) return false;
            ItemStack toUse = null;
            for (ItemStack itemStack : serverPlayer.getInventory().items) {
                if (itemStack.getItem() instanceof ManaPotionItem manaPotion) {
                    int amount = manaPotion.getAmount();
                    if (currentMana + amount < extract) continue;
                    if (toUse == null || amount < ((ManaPotionItem) toUse.getItem()).getAmount()) toUse = itemStack;
                    if (amount == 50) break;
                }
            }
            if (toUse == null) return false;
            toUse.finishUsingItem(serverPlayer.level(), serverPlayer);
        }
        this.currentMana -= extract;
        return true;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getRegenerateDelay() {
        return regenerateDelay;
    }

    public void setRegenerateDelay(int regenerateDelay) {
        this.regenerateDelay = regenerateDelay;
    }

    public int getMaxMana() {
        if (maxMana == null) {
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
        if (value != additionalMana) {
            this.additionalMana = value;
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
