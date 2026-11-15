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
    private boolean artisanLoaf;
//    private boolean fallenSoulCore;

    public EverBeneficial() {
        this.lifeCrystals = 0;
        this.lifeFruits = 0;

        this.vitalCrystal = false;
        this.aegisApple = false;
        this.ambrosia = false;
        this.gummyWorm = false;
        this.galaxyPearl = false;
        this.minecartUpgradeKit = false;
        this.artisanLoaf = false;
//        this.fallenSoulCore = false;
    }

    public void setUsedLifeCrystals(int lifeCrystals) {
        this.lifeCrystals = lifeCrystals;
    }

    public int getUsedLifeCrystals() {
        return lifeCrystals;
    }

    public boolean isLifeCrystalsMaximum() {
        return lifeCrystals >= 15;
    }

    public void setUsedLifeFruits(int lifeFruits) {
        this.lifeFruits = lifeFruits;
    }

    public int getUsedLifeFruits() {
        return lifeFruits;
    }

    public boolean isLifeFruitsMaximum() {
        return lifeFruits >= 20;
    }

    public void setVitalCrystalUsed(boolean used) {
        this.vitalCrystal = used;
    }

    public boolean isVitalCrystalUsed() {
        return vitalCrystal;
    }

    public void setAegisAppleUsed(boolean used) {
        this.aegisApple = used;
    }

    public boolean isAegisAppleUsed() {
        return aegisApple;
    }

    public void setAmbrosiaUsed(boolean used) {
        this.ambrosia = used;
    }

    public boolean isAmbrosiaUsed() {
        return ambrosia;
    }

    public void setGummyWormUsed(boolean used) {
        this.gummyWorm = used;
    }

    public boolean isGummyWormUsed() {
        return gummyWorm;
    }

    public void setGalaxyPearlUsed(boolean used) {
        this.galaxyPearl = used;
    }

    public boolean isGalaxyPearlUsed() {
        return galaxyPearl;
    }

    public void setMinecartUpgradeKitUsed(boolean used) {
        this.minecartUpgradeKit = used;
    }

    public boolean isMinecartUpgradeKitUsed() {
        return minecartUpgradeKit;
    }

    public void setArtisanLoafUsed(boolean used) {
        this.artisanLoaf = used;
    }

    public boolean isArtisanLoafUsed() {
        return artisanLoaf;
    }


//    public void changeFallenSoulCore() {this.fallenSoulCore = !fallenSoulCore;}
//
//    public void setFallenSoulCore(boolean fallenSoulCore) {
//        this.fallenSoulCore = fallenSoulCore;
//    }
//
//    public boolean getFallenSoulCore() {return fallenSoulCore;}

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
        nbt.putBoolean("artisanLoaf", artisanLoaf);
//        nbt.putBoolean("fallenSoulCore", fallenSoulCore);
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
        this.artisanLoaf = nbt.getBoolean("artisanLoaf");
//        this.fallenSoulCore = nbt.getBoolean("fallenSoulCore");
    }

    public static EverBeneficial of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.EVER_BENEFICIAL);
    }
}
