package org.confluence.mod.common.entity.npc.mood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.*;
import org.confluence.mod.common.init.ModTags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// 心情环境以房屋为邻居距离锚点，无房者使用实体位置；城镇归属可在此层接入。
public record MoodEnvironment(List<BaseNPC> neighbors, int distantCount, Holder<Biome> biome,
                              HousingStatus housing, boolean evilBiome) {
    private static final double NEIGHBOR_DISTANCE_SQR = 25.0 * 25.0;
    private static final double TOWN_DISTANCE_SQR = 120.0 * 120.0;
    private static final int QUERY_RADIUS = 140;

    public enum HousingStatus {
        HOUSED,
        HOMELESS,
        FAR_FROM_HOME
    }

    public static MoodEnvironment around(BaseNPC owner, ServerLevel level) {
        BlockPos anchor = anchorOf(owner);
        HousingStatus housing = !owner.getHouse().isValid() ? HousingStatus.HOMELESS
                : owner.blockPosition().distSqr(anchor) > TOWN_DISTANCE_SQR ? HousingStatus.FAR_FROM_HOME
                : HousingStatus.HOUSED;
        List<BaseNPC> neighbors = new ArrayList<>();
        int distantCount = 0;
        for (BaseNPC other : level.getEntitiesOfClass(BaseNPC.class, new AABB(anchor).inflate(QUERY_RADIUS))) {
            if (other == owner || !other.isAlive() || other instanceof TravelingMerchantNPC
                    || other instanceof SkeletonMerchantNPC || other instanceof OldManNPC
                    || other instanceof TownSlimeNPC) continue;
            double distance = anchor.distSqr(anchorOf(other));
            if (distance <= NEIGHBOR_DISTANCE_SQR) neighbors.add(other);
            else if (distance <= TOWN_DISTANCE_SQR) distantCount++;
        }
        neighbors.sort(Comparator.comparingInt(BaseNPC::getId));
        Holder<Biome> biome = level.getBiome(owner.blockPosition());
        boolean evilBiome = biome.is(ModTags.Biomes.THE_CORRUPTION) || biome.is(ModTags.Biomes.THE_CRIMSON);
        return new MoodEnvironment(List.copyOf(neighbors), distantCount, biome, housing, evilBiome);
    }

    private static BlockPos anchorOf(BaseNPC npc) {
        return npc.getHouse().isValid() ? npc.getHouse().center() : npc.blockPosition();
    }
}
