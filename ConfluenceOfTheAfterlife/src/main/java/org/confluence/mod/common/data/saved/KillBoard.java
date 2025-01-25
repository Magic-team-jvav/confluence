package org.confluence.mod.common.data.saved;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class KillBoard implements INBTSerializable<CompoundTag> {
    private boolean eyeOfCthulhu = false;
    private boolean eaterOfWorld_brainOfCthulhu = false;

    public void defeatedEyeOfCthulhu(ConfluenceData data) {
        if (!eyeOfCthulhu) {
            this.eyeOfCthulhu = true;
            data.setDirty();
        }
    }

    public boolean isEyeOfCthulhuDefeated() {
        return eyeOfCthulhu;
    }

    public void defeatedEaterOfWorld_BrainOfCthulhu(ConfluenceData data) {
        if (!eaterOfWorld_brainOfCthulhu) {
            this.eaterOfWorld_brainOfCthulhu = true;
            data.setDirty();
        }
    }

    public boolean isEaterOfWorld_BrainOfCthulhuDefeated() {
        return eaterOfWorld_brainOfCthulhu;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("EyeOfCthulhu", eyeOfCthulhu);
        tag.putBoolean("EaterOfWorld_BrainOfCthulhu", eaterOfWorld_brainOfCthulhu);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.eyeOfCthulhu = nbt.getBoolean("EyeOfCthulhu");
        this.eaterOfWorld_brainOfCthulhu = nbt.getBoolean("EaterOfWorld_BrainOfCthulhu");
    }
}
