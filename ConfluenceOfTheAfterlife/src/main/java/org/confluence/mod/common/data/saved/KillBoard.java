package org.confluence.mod.common.data.saved;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.terraentity.init.TEEntities;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class KillBoard implements INBTSerializable<CompoundTag> {
    private boolean kingSlime = false;
    private boolean eyeOfCthulhu = false;
    private boolean eaterOfWorld_brainOfCthulhu = false;

    public void defeatedKingSlime(ConfluenceData data) {
        if (!kingSlime) {
            this.kingSlime = true;
            data.setDirty();
        }
    }

    public boolean isKingSlimeDefeated() {
        return kingSlime;
    }

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

    public void defeated(EntityType<?> type, ConfluenceData data) {
        if (type == TEEntities.KING_SLIME.get()) {
            defeatedKingSlime(data);
        } else if (type == TEEntities.EYE_OF_CTHULHU.get()) {
            defeatedEyeOfCthulhu(data);
        } else if (type == TEEntities.EATER_OF_WORLD.get() || type == TEEntities.BRAIN_OF_CTHULHU.get()) {
            defeatedEaterOfWorld_BrainOfCthulhu(data);
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("KingSlime", kingSlime);
        tag.putBoolean("EyeOfCthulhu", eyeOfCthulhu);
        tag.putBoolean("EaterOfWorld_BrainOfCthulhu", eaterOfWorld_brainOfCthulhu);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.kingSlime = nbt.getBoolean("KingSlime");
        this.eyeOfCthulhu = nbt.getBoolean("EyeOfCthulhu");
        this.eaterOfWorld_brainOfCthulhu = nbt.getBoolean("EaterOfWorld_BrainOfCthulhu");
    }
}
