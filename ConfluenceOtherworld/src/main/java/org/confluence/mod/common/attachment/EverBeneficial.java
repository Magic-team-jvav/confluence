package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class EverBeneficial implements INBTSerializable<CompoundTag> {

    private final Map<ResourceLocation, Integer> data = new HashMap<>();

    private static final ResourceLocation LIFE_CRYSTAL = Confluence.asResource("life_crystal");
    private static final ResourceLocation LIFE_FRUIT = Confluence.asResource("life_fruit");
    private static final ResourceLocation VITAL_CRYSTAL = Confluence.asResource("vital_crystal");
    private static final ResourceLocation GUMMY_WORM = Confluence.asResource("gummy_worm");
    private static final ResourceLocation MINECART_UPGRADE_KIT = Confluence.asResource("minecart_upgrade_kit");
    private static final ResourceLocation AEGIS_APPLE = Confluence.asResource("aegis_apple");
    private static final ResourceLocation AMBROSIA = Confluence.asResource("ambrosia");
    private static final ResourceLocation ARTISAN_LOAF = Confluence.asResource("artisan_loaf");
    private static final ResourceLocation GALAXY_PEARL = Confluence.asResource("galaxy_pearl");

    public EverBeneficial() {}

    public boolean tryIncrease(ResourceLocation id, int max) {
        int current = getLevel(id);
        if (current < max) {
            data.put(id, current + 1);
            return true;
        }
        return false;
    }

    public int getLevel(ResourceLocation id) {
        return data.getOrDefault(id, 0);
    }

    public boolean isLifeCrystalsMaximum() {
        return getLevel(LIFE_CRYSTAL) >= 15;
    }

    public boolean isLifeFruitsMaximum() {
        return getLevel(LIFE_FRUIT) >= 20;
    }

    public boolean isVitalCrystalUsed() {
        return getLevel(VITAL_CRYSTAL) > 0;
    }

    public boolean isGummyWormUsed() {
        return getLevel(GUMMY_WORM) > 0;
    }

    public boolean isMinecartUpgradeKitUsed() {
        return getLevel(MINECART_UPGRADE_KIT) > 0;
    }

    public boolean isAegisAppleUsed() {
        return getLevel(AEGIS_APPLE) > 0;
    }

    public boolean isAmbrosiaUsed() {
        return getLevel(AMBROSIA) > 0;
    }

    public boolean isArtisanLoafUsed() {
        return getLevel(ARTISAN_LOAF) > 0;
    }

    public boolean isGalaxyPearlUsed() {
        return getLevel(GALAXY_PEARL) > 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        data.forEach((id, val) -> nbt.putInt(id.toString(), val));
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        data.clear();
        for (String key : nbt.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null) {
                data.put(id, nbt.getInt(key));
            }
        }
    }

    public static EverBeneficial of(LivingEntity living) {
        return living.getData(ModAttachmentTypes.EVER_BENEFICIAL);
    }
}
