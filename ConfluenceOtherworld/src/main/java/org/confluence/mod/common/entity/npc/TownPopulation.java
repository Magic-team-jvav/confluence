package org.confluence.mod.common.entity.npc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.gameevent.SolarEclipseGameEvent;
import org.confluence.mod.common.init.ModBlockCounters;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModMiniBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.util.OverworldUtils;

/// 城镇人口与自然刷怪共用同一局部范围，不依赖住房或跨区域的存档计数。
public final class TownPopulation {
    private TownPopulation() {}

    /// 老人、旅商和宠物参与人口统计；骷髅商人、待救援者及死亡实体不计入。
    public static int count(ServerLevel level, BlockPos pos) {
        return level.getEntitiesOfClass(BaseNPC.class, new AABB(pos).inflate(
                ModMiniBiomes.TOWN.horizontalRadius(), ModMiniBiomes.TOWN.verticalRadius(),
                ModMiniBiomes.TOWN.horizontalRadius()), TownPopulation::isResident).size();
    }

    private static boolean isResident(BaseNPC npc) {
        return npc.isAlive() && !npc.requiresRescue() && !(npc instanceof SkeletonMerchantNPC);
    }

    /// 只过滤自然生成的敌怪；事件生成与 Boss 不受城镇保护影响。
    public static boolean suppressesSpawn(ServerLevel level, Mob mob) {
        boolean rareCritter = mob.getType().is(ModTags.EntityTypes.TOWN_SUPPRESSED_CRITTERS)
                || mob.getType() == CritterEntities.WORM.get() && !level.isRaining();
        if (!(mob instanceof Enemy) && !rareCritter || mob instanceof Boss) return false;
        ServerPlayer nearest = null;
        double distance = 128.0 * 128.0;
        for (ServerPlayer player : level.players()) {
            if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
            double next = player.distanceToSqr(mob);
            if (next < distance) {
                nearest = player;
                distance = next;
            }
        }
        if (nearest == null) return false;
        return suppressesSpawn(nearest);
    }

    /// 自定义自然生成器按玩家整轮判定，避免重试位置时绕过城镇抑制。
    public static boolean suppressesSpawn(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (protectionDisabled(player)) return false;
        BlockPos pos = player.blockPosition();
        int residents = Math.min(count(level, pos), 3);
        if (residents == 0) return false;
        double chance = enemySpawnChance(residents, level.dimension() == OverworldUtils.underworld(), ModBlockCounters.isGraveyard(level, pos), LibUtils.isAtLeastExpert(level));
        return level.random.nextDouble() >= chance;
    }

    /// 手持或附近点亮的暗影蜡烛取消城镇保护；事件也保留原来的自然生成规则。
    public static boolean protectionDisabled(ServerPlayer player) {
        if (player.hasEffect(ModEffects.SHADOW_CANDLE)
                || player.getMainHandItem().is(FunctionalBlocks.SHADOW_CANDLE.asItem())
                || player.getOffhandItem().is(FunctionalBlocks.SHADOW_CANDLE.asItem())
                || GameEventSystem.anyInvasionStarted()) return true;
        return player.getY() >= OverworldUtils.getSurfaceY() && (BloodMoonGameEvent.INSTANCE.started()
                || SolarEclipseGameEvent.INSTANCE.started() || SlimeRainGameEvent.INSTANCE.started());
    }

    public static double critterChance(ServerPlayer player) {
        if (protectionDisabled(player)) return 0;
        ServerLevel level = player.serverLevel();
        int residents = count(level, player.blockPosition());
        if (residents == 0) return 0;
        return critterChance(residents, level.dimension() == OverworldUtils.underworld(),
                ModBlockCounters.isGraveyard(level, player.blockPosition()), LibUtils.isAtLeastExpert(level));
    }

    /// 小动物池的启用概率与敌怪保留概率分别计算，稀有小动物不进入此池。
    static double critterChance(int residents, boolean underworld, boolean graveyard, boolean expert) {
        if (residents <= 0) return 0;
        int count = Math.min(residents, NPCSpawner.TOWN_NPC_THRESHOLD);
        if (underworld) return count < 3 ? count / 10.0 : 1.0 / 3.0;
        if (graveyard) return switch (count) {
            case 1 -> 1.0 / 9.0;
            case 2 -> 1.0 / 6.0;
            default -> 1.0 / 3.0;
        };
        if (NPCSpawner.INSTANCE.isTown(residents)) return expert ? 29.0 / 30.0 : 1.0;
        return count / 3.0;
    }

    /// 将普通生成池和小动物池的分流合并为敌怪最终保留概率。
    static double enemySpawnChance(int residents, boolean underworld, boolean graveyard, boolean expert) {
        if (residents <= 0) return 1.0;
        int count = Math.min(residents, 3);
        if (underworld) return switch (count) {
            case 1 -> 18.0 / 25.0;
            case 2 -> 8.0 / 15.0;
            default -> 1.0 / 3.0;
        };
        if (graveyard) return switch (count) {
            case 1 -> 8.0 / 15.0;
            case 2 -> 5.0 / 14.0;
            default -> 2.0 / 9.0;
        };
        if (NPCSpawner.INSTANCE.isTown(residents)) return expert ? 1.0 / 30.0 : 0.0;
        return count == 1 ? 1.0 / 3.0 : 1.0 / 9.0;
    }
}
