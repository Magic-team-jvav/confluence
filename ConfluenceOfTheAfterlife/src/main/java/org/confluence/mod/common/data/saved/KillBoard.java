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
    private boolean eaterOfWorlds_brainOfCthulhu = false;

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

    public void defeatedEaterOfWorlds_BrainOfCthulhu(ConfluenceData data) {
        if (!eaterOfWorlds_brainOfCthulhu) {
            this.eaterOfWorlds_brainOfCthulhu = true;
            data.setDirty();
        }
    }

    public boolean isEaterOfWorlds_BrainOfCthulhuDefeated() {
        return eaterOfWorlds_brainOfCthulhu;
    }

    public void defeated(EntityType<?> type, ConfluenceData data) {
        if (type == TEEntities.KING_SLIME.get()) {
            defeatedKingSlime(data);
        } else if (type == TEEntities.EYE_OF_CTHULHU.get()) {
            defeatedEyeOfCthulhu(data);
        } else if (type == TEEntities.EATER_OF_WORLDS.get() || type == TEEntities.BRAIN_OF_CTHULHU.get()) {
            defeatedEaterOfWorlds_BrainOfCthulhu(data);
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("KingSlime", kingSlime);
        tag.putBoolean("EyeOfCthulhu", eyeOfCthulhu);
        tag.putBoolean("EaterOfWorlds_BrainOfCthulhu", eaterOfWorlds_brainOfCthulhu);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.kingSlime = nbt.getBoolean("KingSlime");
        this.eyeOfCthulhu = nbt.getBoolean("EyeOfCthulhu");
        this.eaterOfWorlds_brainOfCthulhu = nbt.getBoolean("EaterOfWorlds_BrainOfCthulhu");
    }
}
