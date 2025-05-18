package org.confluence.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.confluence.lib.util.ReturnException;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IChunkSection;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class DynamicBiomeUtils {
    public static final int BIOME_THRESHOLD = 256;

    /**
     * 判断传入的子区块应该被判定成哪种群系
     *
     * @param radical 激进意味着尽量匹配<code>preference</code>，否则尽量避开<code>preference</code>。
     *                如果<code>preference</code>为空，则是激进尽量找邪恶，否则尽量找纯净
     * @return 如果是邪恶则返回 {@link Holder}，纯净返回null
     */
    @SuppressWarnings("unchecked")
    public static Holder<Biome> getTypicalBiome(LevelChunkSection section, boolean radical, Holder<Biome> preference){
        if(!(section.getBiomes() instanceof PalettedContainer<Holder<Biome>> biomes)){
            return null;
        }
        AtomicReference<Holder<Biome>> evil = new AtomicReference<>();
        AtomicBoolean matchPref = new AtomicBoolean(true);
        AtomicBoolean hasPure = new AtomicBoolean(false);
        try{
            biomes.getAll(biome->{
                if(radical){
                    if((preference == null && biome.is(ModTags.Biomes.SPREADABLE)) || preference == biome){
                        throw new ReturnException(biome); // 提前返回
                    }
                }else{
                    evil.set(biome);
                    if(preference == null && !biome.is(ModTags.Biomes.SPREADABLE)){
                        hasPure.set(true);
                    }else if(biome != preference){
                        matchPref.set(false);
                    }
                }
            });
        }catch(ReturnException e){
            return (Holder<Biome>) e.getValue();
        }

        if(radical) return null;
        if(preference == null){
            return hasPure.get() ? null : evil.get();
        }else {
            return matchPref.get() /*尽量避开但是真的避不开*/? evil.get() : null;
        }
    }

    /**
     * @param counts 0猩红，1腐化，2神圣，3向日葵
     * @return 平衡的结果，纯净返回null
     */
    public static Holder<Biome> balanceEvil(int[] counts, IChunkSection biomeSource){
        int sunflower = counts[3] * 64;
        int crimson = Math.max(0, counts[0] - sunflower);
        int corrupt = Math.max(0, counts[1] - sunflower);
        int hallow = counts[2];

        // (假设)同时存在400个猩红块和400个腐化块的时候，只要400个神圣块就能完全抵消，邪恶不会相加
        int evil = Math.max(crimson, corrupt);
        crimson -= hallow;
        corrupt -= hallow;
        hallow -= evil;

        counts[0] = crimson;
        counts[1] = corrupt;
        counts[2] = hallow;

        if(corrupt >= BIOME_THRESHOLD && corrupt >= crimson){
            return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CORRUPTION);
        }else if(crimson >= BIOME_THRESHOLD){
            return biomeSource.confluence$getBiomeByKey(ModBiomes.TR_CRIMSON);
        }else if(hallow >= BIOME_THRESHOLD){
            return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_HALLOW);
        }else{
            return null;
        }
    }

    public static Holder<Biome> judgeSection(LevelChunkSection section){
        IChunkSection counter = (IChunkSection) section;
        return balanceEvil(new int[]{counter.confluence$getCrimson(), counter.confluence$getCorrupt(), counter.confluence$getHallow(),counter.confluence$getSunflower()}, counter);
    }

    public static LevelChunkSection getSection(Level level, BlockPos pos) {
        return level.getChunk(pos).getSection(level.getSectionIndex(pos.getY()));
    }

    public static IChunkSection getISection(Level level, BlockPos pos) {
        return (IChunkSection) getSection(level, pos);
    }
}
