package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModTags;

import java.util.ArrayList;
import java.util.List;

public class NotTheBees extends SecretSeed {
    private static final List<TagKey<Biome>> skip = Util.make(new ArrayList<>(), list -> {
        list.add(Tags.Biomes.IS_DESERT);
        list.add(Tags.Biomes.IS_AQUATIC);
        list.add(Tags.Biomes.IS_UNDERGROUND);
        list.add(ModTags.Biomes.IS_CONFLUENCE);
    });

    public NotTheBees(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "notthebees".equals(seed) || "not the bees".equals(seed) || "not the bees!".equals(seed);
    }

    public static Holder<Biome> replaceBiome(int x, int y, int z, Holder<Biome> original, List<Holder<Biome>> jungle) {
        if (jungle.isEmpty() || skip.stream().anyMatch(original::is)) return original;
        return Util.getRandom(jungle, new LegacyRandomSource(BlockPos.asLong(x, y, z)));
    }
}
