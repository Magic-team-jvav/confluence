package org.confluence.mod.common.data.saved;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.data.StarPhase;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.confluence.mod.network.s2c.StarPhasesPacketS2C;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ConfluenceData extends SavedData {
    public static final int STAR_PHASES_SIZE = 10;

    private boolean initialized = false;
    private final Int2ObjectMap<StarPhase> starPhases = new Int2ObjectArrayMap<>();
    private int revealStep = -1;
    private final MeteoriteTracker meteoriteTracker = MeteoriteTracker.INSTANCE;
    private int evilBrokenCount = 0;
    private boolean stopAskForSoftcore = false;

    ConfluenceData() {
        fillDefaultStarPhases();
    }

    ConfluenceData(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");
        // 风已于本版本迁移到 IGlobalData（写进 level.dat），这里只负责把旧格式的 key 交给它迁移
        WindState.INSTANCE.migrateLegacy(nbt.getFloat("windSpeedX"), nbt.getFloat("windSpeedZ"));
        for (Tag tag : nbt.getList("starPhases", Tag.TAG_COMPOUND)) {
            CompoundTag phase = (CompoundTag) tag;
            starPhases.put(phase.getInt("index"), new StarPhase(phase));
        }
        this.revealStep = nbt.getInt("revealStep");
        this.meteoriteTracker.deserialize(nbt);
        this.evilBrokenCount = nbt.getInt("evilBrokenCount");
        this.stopAskForSoftcore = nbt.getBoolean("stopAskForSoftcore");
    }

    /// 保证星相映射始终包含固定的十个槽位。
    private void fillDefaultStarPhases() {
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            starPhases.put(i, StarPhase.DEFAULT);
        }
    }

    public static ConfluenceData get(ServerLevel serverLevel) {
        /// 这些字段描述整台服务器的 Terraria 世界进度，而不是单个维度：陨石只在主世界落地，
        /// 邪恶方块破坏计数、揭示步骤和软核提示同样全局生效。调用者即使身处下界/末地，
        /// 也必须访问主世界 DataStorage；否则多个 SavedData 会争用 MeteoriteTracker 枚举单例，
        /// 后加载的维度将覆盖先加载维度的状态。
        ServerLevel canonicalLevel = serverLevel.getServer().overworld();
        ConfluenceData data = canonicalLevel.getDataStorage().computeIfAbsent(ConfluenceData::new, ConfluenceData::new, Confluence.MODID);
        initialize(canonicalLevel, data);
        return data;
    }

    private static void initialize(ServerLevel serverLevel, ConfluenceData data) {
        if (!data.initialized) {
            RandomSource random = RandomSource.create(serverLevel.getSeed());
            List<Float> raList = new ArrayList<>();
            int wEarth = 3 + random.nextInt(3);
            float up = (float) Math.pow(5.0, 1.0 / (wEarth - 1));
            float low = (float) Math.pow(9.0, 1.0 / (10 - wEarth));
            float q = Mth.nextFloat(random, 1.2F, Math.min(up, low));
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                float Q = (float) ((i < wEarth - 1) ? (1.0F / Math.pow(q, wEarth - 1 - i)) : q * Math.pow(q, i + 2 - wEarth));
                float ra = 100.0F * Q;
                ra += random.nextFloat() * (ra * 0.1F) * (random.nextBoolean() ? 1 : -1);
                raList.add(random.nextInt(raList.size() + 1), ra);
            }
            for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                data.starPhases.put(i, new StarPhase(180 - random.nextInt(361), raList.get(i), 20.0F - random.nextFloat() * 40.0F));
            }
            data.initialized = true;
            data.setDirty();
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean("initialized", initialized);
        // 旧格式的键继续写回（由 WindState 换算）：单方面回滚到旧版本时不会读出一堆 0
        float normalized = WindState.INSTANCE.speed() / WindMath.REF_SPEED;
        nbt.putFloat("windSpeedX", WindState.INSTANCE.dirX() * normalized);
        nbt.putFloat("windSpeedZ", WindState.INSTANCE.dirZ() * normalized);
        ListTag listTag = new ListTag();
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("index", i);
            starPhases.getOrDefault(i, StarPhase.DEFAULT).writeTo(tag);
            listTag.add(tag);
        }
        nbt.put("starPhases", listTag);
        nbt.putInt("revealStep", revealStep);
        meteoriteTracker.serialize(nbt);
        nbt.putInt("evilBrokenCount", evilBrokenCount);
        nbt.putBoolean("stopAskForSoftcore", stopAskForSoftcore);
        return nbt;
    }

    public boolean setStarPhase(int index, int timeOffset, float radius, float angle) {
        if (index >= STAR_PHASES_SIZE || !CommonConfigs.STAR_PHASE.get()) return false;
        starPhases.put(index, new StarPhase(timeOffset, radius, angle));
        StarPhasesPacketS2C.sendToAll(index, timeOffset, radius, angle);
        setDirty();
        return true;
    }

    public StarPhase getStarPhase(int index) {
        if (index < 0 || index >= STAR_PHASES_SIZE || !CommonConfigs.STAR_PHASE.get()) return null;
        return starPhases.getOrDefault(index, StarPhase.DEFAULT);
    }

    public Int2ObjectMap<StarPhase> getStarPhases() {
        return starPhases;
    }

    public boolean increaseRevealStep() {
        BlockState[][] pairs = StepRevealingBlock.PAIRS.get();
        int nextStep = revealStep + 1;
        if (nextStep >= 0 && nextStep < pairs.length) {
            this.revealStep = nextStep;
            GlobalCloakData.INSTANCE.reveal(pairs[nextStep]);
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
