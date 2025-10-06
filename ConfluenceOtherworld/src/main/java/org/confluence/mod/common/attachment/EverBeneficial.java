package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.init.ModAttachmentTypes;
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
    private boolean artisanLoafUsed;

    public EverBeneficial() {
        this.lifeCrystals = 0;
        this.lifeFruits = 0;

        this.vitalCrystal = false;
        this.aegisApple = false;
        this.ambrosia = false;
        this.gummyWorm = false;
        this.galaxyPearl = false;
        this.minecartUpgradeKit = false;
        this.artisanLoafUsed = false;
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
        return this.vitalCrystal = true;
    }

    public boolean isVitalCrystalUsed() {
        return vitalCrystal;
    }

    public boolean setAegisAppleUsed() {
        if (aegisApple) return false;
        return this.aegisApple = true;
    }

    public boolean isAegisAppleUsed() {
        return aegisApple;
    }

    public boolean setAmbrosiaUsed() {
        if (ambrosia) return false;
        return this.ambrosia = true;
    }

    public boolean isAmbrosiaUsed() {
        return ambrosia;
    }

    public boolean setGummyWormUsed() {
        if (gummyWorm) return false;
        return this.gummyWorm = true;
    }

    public boolean isGummyWormUsed() {
        return gummyWorm;
    }

    public boolean setGalaxyPearlUsed() {
        if (galaxyPearl) return false;
        return this.galaxyPearl = true;
    }

    public boolean isGalaxyPearlUsed() {
        return galaxyPearl;
    }

    public boolean setMinecartUpgradeKitUsed() {
        return this.minecartUpgradeKit = true;
    }

    public boolean isMinecartUpgradeKitUsed() {
        return minecartUpgradeKit;
    }

    public boolean setArtisanLoafUsed() {
        if (artisanLoafUsed) return false;
        return this.artisanLoafUsed = true;
    }

    public boolean isArtisanLoafUsed() {
        return artisanLoafUsed;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("lifeCrystals", lifeCrystals);
        nbt.putInt("lifeFruits", lifeFruits);

        nbt.putBoolean("vitalCrystal", vitalCrystal);
        nbt.putBoolean("aegisApple", aegisApple);
        nbt.putBoolean("ambrosia", ambrosia);
        nbt.putBoolean("gummyWorm", gummyWorm);
        nbt.putBoolean("galaxyPearl", galaxyPearl);
        nbt.putBoolean("minecartUpgradeKit", minecartUpgradeKit);
        nbt.putBoolean("artisanLoafUsed", artisanLoafUsed);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.lifeCrystals = nbt.getInt("lifeCrystals");
        this.lifeFruits = nbt.getInt("lifeFruits");

        this.vitalCrystal = nbt.getBoolean("vitalCrystal");
        this.aegisApple = nbt.getBoolean("aegisApple");
        this.ambrosia = nbt.getBoolean("ambrosia");
        this.gummyWorm = nbt.getBoolean("gummyWorm");
        this.galaxyPearl = nbt.getBoolean("galaxyPearl");
        this.minecartUpgradeKit = nbt.getBoolean("minecartUpgradeKit");
        this.artisanLoafUsed = nbt.getBoolean("artisanLoafUsed");
    }

    public static EverBeneficial of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.EVER_BENEFICIAL);
    }
}
