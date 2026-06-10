package org.confluence.mod.common.attachment;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.UnknownNullability;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

public class EverBeneficial implements IPortNBTSerializable<CompoundTag> {
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
        if (isLifeCrystalsMaximum() && !isLifeFruitsMaximum()) {
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
        if (artisanLoaf) return false;
        return this.artisanLoaf = true;
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
        return PortEntityExtension.getAttach(living, ModAttachmentTypes.EVER_BENEFICIAL);
    }
}
