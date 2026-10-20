package org.confluence.mod.common.data.saved;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.confluence.mod.util.OverworldUtils;

public final class ConfluenceData extends SavedData {
    private int revealStep = -1;
    private final MeteoriteTracker meteoriteTracker = MeteoriteTracker.INSTANCE;
    private int evilBrokenCount = 0;
    private boolean stopAskForSoftcore = false;

    ConfluenceData() {}

    ConfluenceData(CompoundTag nbt) {
        this.revealStep = nbt.getInt("revealStep");
        this.meteoriteTracker.deserialize(nbt);
        this.evilBrokenCount = nbt.getInt("evilBrokenCount");
        this.stopAskForSoftcore = nbt.getBoolean("stopAskForSoftcore");
    }

    public static ConfluenceData get(ServerLevel serverLevel) {
        return OverworldUtils.getLevel(serverLevel.getServer()).getDataStorage().computeIfAbsent(ConfluenceData::new, ConfluenceData::new, Confluence.MODID);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("revealStep", revealStep);
        meteoriteTracker.serialize(nbt);
        nbt.putInt("evilBrokenCount", evilBrokenCount);
        nbt.putBoolean("stopAskForSoftcore", stopAskForSoftcore);
        return nbt;
    }

    public boolean increaseRevealStep() {
        if (revealStep >= 8) {
            this.revealStep = -1; // 允许再一轮敲
        }
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

    public void setMeteorite(BlockPos location, int tickUntilLanding) {
        this.meteoriteTracker.location = location;
        this.meteoriteTracker.tickUntilLanding = tickUntilLanding;
        MeteoriteLocationPacketS2C.sendToAll(location, tickUntilLanding);
        setDirty();
    }

    public BlockPos getMeteoriteLocation() {
        return meteoriteTracker.location;
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
