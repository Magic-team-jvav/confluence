package org.confluence.mod.common.data.saved;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.NotNull;

public final class ConfluenceData extends SavedData {
    private int revealStep = -1;
    private int evilBrokenCount = 0;
    private boolean stopAskForSoftcore = false;

    ConfluenceData() {}

    ConfluenceData(CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        this.revealStep = nbt.getInt("revealStep");
        this.evilBrokenCount = nbt.getInt("evilBrokenCount");
        this.stopAskForSoftcore = nbt.getBoolean("stopAskForSoftcore");
    }

    public static ConfluenceData get(ServerLevel serverLevel) {
        return OverworldUtils.getLevel(serverLevel.getServer()).getDataStorage().computeIfAbsent(new Factory<>(ConfluenceData::new, ConfluenceData::new), Confluence.MODID);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        nbt.putInt("revealStep", revealStep);
        nbt.putInt("evilBrokenCount", evilBrokenCount);
        nbt.putBoolean("stopAskForSoftcore", stopAskForSoftcore);
        return nbt;
    }

    public boolean increaseRevealStep() {
        BlockState[][] pairs = StepRevealingBlock.PAIRS.get();
        if (revealStep < pairs.length - 1) {
            GlobalCloakData.INSTANCE.reveal(pairs[++this.revealStep]);
            setDirty();
            return true;
        }
        return false;
    }

    /// 一般为[-1, 8]
    public int getRevealStep() {
        return revealStep;
    }

    public boolean updateEvilBrokenCount() {
        this.evilBrokenCount++;
        setDirty();
        return evilBrokenCount % 3 == 0;
    }

    public int getEvilBrokenCount() {
        return evilBrokenCount;
    }

    public void setStopAskForSoftcore(boolean stop) {
        this.stopAskForSoftcore = stop;
        setDirty();
    }

    public boolean isStopAskForSoftcore() {
        return stopAskForSoftcore;
    }
}
