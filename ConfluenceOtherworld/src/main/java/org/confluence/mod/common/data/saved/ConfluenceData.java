package org.confluence.mod.common.data.saved;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.saveddata.SavedData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.confluence.mod.network.s2c.StarPhasesPacketS2C;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.confluence.phase_journey.common.util.PhaseUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ConfluenceData extends SavedData {
    public static final int STAR_PHASES_SIZE = 10;

    private boolean initialized = false;
    private final Vector3f windSpeed = new Vector3f();
    private final Int2ObjectMap<StarPhase> starPhases = new Int2ObjectArrayMap<>();
    private int revealStep = -1;
    private final MeteoriteTracker meteoriteTracker = MeteoriteTracker.INSTANCE;
    private int evilBrokenCount = 0;

    ConfluenceData() {
        for (int i = 0; i < STAR_PHASES_SIZE; i++) {
            starPhases.put(i, StarPhase.DEFAULT);
        }
    }

    ConfluenceData(CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        this.initialized = nbt.getBoolean("initialized");
        this.windSpeed.x = nbt.getFloat("windSpeedX");
        this.windSpeed.z = nbt.getFloat("windSpeedZ");
        for (Tag tag : nbt.getList("starPhases", Tag.TAG_COMPOUND)) {
            CompoundTag phase = (CompoundTag) tag;
            starPhases.put(phase.getInt("index"), new StarPhase(phase));
        }
        this.revealStep = nbt.getInt("revealStep");
        this.meteoriteTracker.deserialize(nbt);
        this.evilBrokenCount = nbt.getInt("evilBrokenCount");
    }

    public static ConfluenceData get(ServerLevel serverLevel) {
        ConfluenceData data = serverLevel.getDataStorage().computeIfAbsent(new Factory<>(ConfluenceData::new, ConfluenceData::new), Confluence.MODID);
        initialize(serverLevel, data);
        return data;
    }

    private static void initialize(ServerLevel serverLevel, ConfluenceData data) {
        if (!data.initialized) {
            RandomSource random = new LegacyRandomSource(serverLevel.getSeed());
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
    public @NotNull CompoundTag save(CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        nbt.putBoolean("initialized", initialized);
        nbt.putFloat("windSpeedX", windSpeed.x);
        nbt.putFloat("windSpeedZ", windSpeed.z);
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
        return nbt;
    }

    public void setWindSpeed(float x, float z) {
        this.windSpeed.x = x;
        this.windSpeed.z = z;
        WindSpeedPacketS2C.sendToAll(x, z);
        setDirty();
    }

    public float getWindSpeedX() {
        return windSpeed.x;
    }

    public float getWindSpeedZ() {
        return windSpeed.z;
    }

    public boolean setStarPhase(int index, int timeOffset, float radius, float angle) {
        if (index >= STAR_PHASES_SIZE || !CommonConfigs.STAR_PHASE.get()) return false;
        starPhases.put(index, new StarPhase(timeOffset, radius, angle));
        StarPhasesPacketS2C.sendToAll(index, timeOffset, radius, angle);
        setDirty();
        return true;
    }

    public StarPhase getStarPhase(int index) {
        if (index >= STAR_PHASES_SIZE || !CommonConfigs.STAR_PHASE.get()) return null;
        return starPhases.getOrDefault(index, StarPhase.DEFAULT);
    }

    public Int2ObjectMap<StarPhase> getStarPhases() {
        return starPhases;
    }

    public boolean increaseRevealStep(ServerLevel serverLevel) {
        if (revealStep < 8) {
            serverLevel.players().forEach(serverPlayer -> PhaseUtils.achievePhase(serverPlayer, Confluence.asResource("reveal_step_" + this.revealStep++), true));
            setDirty();
            return true;
        }
        return false;
    }

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
}
