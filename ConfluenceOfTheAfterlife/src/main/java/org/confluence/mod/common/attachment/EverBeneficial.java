package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class EverBeneficial implements INBTSerializable<CompoundTag> {
    private int lifeCrystals;
    private int lifeFruits;

    private boolean vitalCrystal;
    private boolean aegisApple;
    private boolean ambrosia;
    private boolean gummyWorm;
    private boolean galaxyPearl;
    private boolean minecartUpgradeKit;

    public EverBeneficial() {
        this.lifeCrystals = 0;
        this.lifeFruits = 0;

        this.vitalCrystal = false;
        this.aegisApple = false;
        this.ambrosia = false;
        this.gummyWorm = false;
        this.galaxyPearl = false;
        this.minecartUpgradeKit = false;
    }

    public boolean increaseCrystals() {
        if (!isLifeCrystalsMaximum()) {
            this.lifeCrystals++;
            return true;
        }
        return false;
    }

    public int getUsedLifeCrystals() {
        return lifeCrystals;
    }

    public boolean isLifeCrystalsMaximum() {
        return lifeCrystals >= 15;
    }

    public boolean increaseFruits() {
        if (!isLifeFruitsMaximum()) {
            this.lifeFruits++;
            return true;
        }
        return false;
    }

    public int getUsedLifeFruits() {
        return lifeFruits;
    }

    public boolean isLifeFruitsMaximum() {
        return lifeFruits >= 20;
    }

    public boolean setVitalCrystalUsed() {
        if (vitalCrystal) return false;
        this.vitalCrystal = true;
        return true;
    }

    public boolean isVitalCrystalUsed() {
        return vitalCrystal;
    }

    public boolean setAegisAppleUsed() {
        if (aegisApple) return false;
        this.aegisApple = true;
        return true;
    }

    public boolean isAegisAppleUsed() {
        return aegisApple;
    }

    public boolean setAmbrosiaUsed() {
        if (ambrosia) return false;
        this.ambrosia = true;
        return true;
    }

    public boolean isAmbrosiaUsed() {
        return ambrosia;
    }

    public boolean setGummyWormUsed() {
        if (gummyWorm) return false;
        this.gummyWorm = true;
        return true;
    }

    public boolean isGummyWormUsed() {
        return gummyWorm;
    }

    public boolean setGalaxyPearlUsed() {
        if (galaxyPearl) return false;
        this.galaxyPearl = true;
        return true;
    }

    public boolean isGalaxyPearlUsed() {
        return galaxyPearl;
    }

    public boolean setMinecartUpgradeKitUsed() {
        this.minecartUpgradeKit = true;
        return true;
    }

    public boolean isMinecartUpgradeKitUsed() {
        return minecartUpgradeKit;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("lifeCrystals", lifeCrystals);
        nbt.putInt("lifeFruits", lifeFruits);

        nbt.putBoolean("vitalCrystal", vitalCrystal);
        nbt.putBoolean("aegisApple", aegisApple);
        nbt.putBoolean("ambrosia", ambrosia);
        nbt.putBoolean("gummyWorm", gummyWorm);
        nbt.putBoolean("galaxyPearl", galaxyPearl);
        nbt.putBoolean("minecartUpgradeKit", minecartUpgradeKit);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.lifeCrystals = nbt.getInt("lifeCrystals");
        this.lifeFruits = nbt.getInt("lifeFruits");

        this.vitalCrystal = nbt.getBoolean("vitalCrystal");
        this.aegisApple = nbt.getBoolean("aegisApple");
        this.ambrosia = nbt.getBoolean("ambrosia");
        this.gummyWorm = nbt.getBoolean("gummyWorm");
        this.galaxyPearl = nbt.getBoolean("galaxyPearl");
        this.minecartUpgradeKit = nbt.getBoolean("minecartUpgradeKit");
    }
}
