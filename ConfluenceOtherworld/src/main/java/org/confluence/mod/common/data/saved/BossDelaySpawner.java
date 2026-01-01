package org.confluence.mod.common.data.saved;

import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import org.apache.commons.lang3.stream.Streams;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.ReturnException;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.api.entity.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.boss.EyeOfCthulhu;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.entity.TEBossEntities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;

public final class BossDelaySpawner {
    public static final BossDelaySpawner INSTANCE = new BossDelaySpawner();
    public static final int SUCCESS = 0;
    public static final int CONTINUE = -1;
    public static final int CANCEL = -2;
    private final List<Delayed<AbstractTerraBossBase>> bossQueue = new ArrayList<>();

    private BossDelaySpawner() {}

    public void tick(ServerLevel level) {
        if (bossQueue.isEmpty()) return;
        Iterator<Delayed<AbstractTerraBossBase>> iterator = bossQueue.iterator();
        while (iterator.hasNext()) l:{
            Delayed<AbstractTerraBossBase> delayed = iterator.next();
            if (--delayed.delay >= 0) continue;
            for (ServerPlayer player : level.players()) {
                int state = delayed.predicate.applyAsInt(player);
                if (state == SUCCESS) {
                    ModUtils.summonBoss(level, player.blockPosition(), delayed.entity.create(level));
                    iterator.remove();
                    break l;
                } else if (state == CANCEL) {
                    iterator.remove();
                    break l;
                } else if (state != CONTINUE) {
                    delayed.delay = state;
                    break;
                }
            }
            iterator.remove();
        }
    }

    /// @param predicate 等于[BossDelaySpawner#SUCCESS]时成功
    ///                                                    等于[BossDelaySpawner#CONTINUE]时跳过该玩家
    ///                                                    等于[BossDelaySpawner#CANCEL]时取消生成
    ///                                                    大于0时设置下次检测延时
    public void pushBoss(int delay, EntityType<? extends AbstractTerraBossBase> boss, ToIntFunction<ServerPlayer> predicate) {
        if (bossQueue.size() == 8) bossQueue.removeFirst();
        bossQueue.add(new Delayed<>(delay, boss, predicate));
    }

    public boolean hasSameTypeInQueue(EntityType<?> type) {
        return bossQueue.stream().anyMatch(delayed -> delayed.entity == type);
    }

    public void clear() {
        bossQueue.clear();
    }

    public static void spawnEyeOfCthulhu(ServerLevel level) {
        EntityType<EyeOfCthulhu> type = TEBossEntities.EYE_OF_CTHULHU.get();
        if (!KillBoard.INSTANCE.isDefeated(type) && !BossDelaySpawner.INSTANCE.hasSameTypeInQueue(type)) {
            for (ServerPlayer player : level.players()) {
                if (!BossDelaySpawner.eyeOfCthulhuChecker(player) || level.random.nextFloat() >= 0.3333F) {
                    continue;
                }
                BossDelaySpawner.INSTANCE.pushBoss(1350, TEBossEntities.EYE_OF_CTHULHU.get(), player1 ->
                        player1.getY() > OverworldUtils.getSurfaceY() && LibDateUtils.isNight(player1.level()) && BossDelaySpawner.eyeOfCthulhuChecker(player1)
                                ? BossDelaySpawner.SUCCESS
                                : 20
                );
                level.getServer().getPlayerList().broadcastSystemMessage(
                        Component.translatable("event.confluence.eye_of_cthulhu").withColor(GlobalColors.MESSAGE.get()),
                        false
                );
                break;
            }
        }
    }

    public static boolean eyeOfCthulhuChecker(ServerPlayer player) {
        if (player.getMaxHealth() < 40 || player.getArmorValue() < 10) return false;
        NPCSpawner.Region region = NPCSpawner.getNpcSpawnRegion(player);
        return NPCSpawner.INSTANCE.getAliveNpcCount(region, entityType -> true) >= 4; /* todo 骷髅商人不计入 */
    }

    public static void spawnDeerClops(ServerLevel level) {
        if (!BossDelaySpawner.INSTANCE.hasSameTypeInQueue(TEBossEntities.DEERCLOPS.get())) {
            for (ServerPlayer player : level.players()) {
                if (!BossDelaySpawner.deerclopsChecker(player)) continue;
                BossDelaySpawner.INSTANCE.pushBoss(0, TEBossEntities.DEERCLOPS.get(), player1 ->
                        BossDelaySpawner.deerclopsChecker(player1)
                                ? BossDelaySpawner.SUCCESS
                                : BossDelaySpawner.CONTINUE
                );
                break;
            }
        }
    }

    public static boolean deerclopsChecker(ServerPlayer player) {
        if (player.getY() < OverworldUtils.getSurfaceY()) return false;
        if (player.getMaxHealth() < 40 && player.getArmorValue() < 9) return false;
        ServerLevel level = player.serverLevel();
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        if (!biome.is(Tags.Biomes.IS_SNOWY) && !biome.is(Tags.Biomes.IS_ICY)) return false;
        MomentInstanceManager manager = MomentInstanceManager.of(level);
        int size = manager.getRunMoments().size();
        if (size != 0 && (size != 1 || !manager.hasMoment(TMMoments.BLOOD_MOON.getKey()))) {
            return false;
        }
        try {
            level.getEntities().get(new AABB(player.blockPosition()).inflate(80), entity -> {
                if (entity instanceof AbstractTerraNPC) {
                    throw new ReturnException();
                }
            });
        } catch (Exception ignored) {
            return false;
        }
        return Streams.of(level.getAllEntities()).noneMatch(entity -> entity instanceof Boss);
    }

    static class Delayed<E extends Entity> {
        int delay;
        final EntityType<? extends E> entity;
        final ToIntFunction<ServerPlayer> predicate;

        Delayed(int delay, EntityType<? extends E> entity, ToIntFunction<ServerPlayer> predicate) {
            this.delay = delay;
            this.entity = entity;
            this.predicate = predicate;
        }
    }
}
