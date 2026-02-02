package com.xiaohunao.enemybanner.banner;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.EnemyBanner;
import com.xiaohunao.enemybanner.blocks.EnemyBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;

@EventBusSubscriber(modid = EnemyBanner.MODID, value = Dist.DEDICATED_SERVER)
public class BannerManager {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final int BASIC_RANGE = 16;

    private static final LinkedHashMap<ResourceKey<Level>, BlockPos> bannerBlockList = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Pre event) {
        List<BlockPos> bannerList = getBannerOfEntity(event.getEntity());
        for (BlockPos blockPos : bannerList) {
            if (event.getEntity().level().getBlockEntity(blockPos) instanceof EnemyBannerBlockEntity blockEntity) {
                String silksId = blockEntity.getParameters().getSilksId();
                String monsterId = blockEntity.getParameters().getMonsterId();
                BannerBehavior bannerBehavior = BannerBehaviorProvider.getInstance().getBannerBehavior(silksId);
                if (event.getEntity() instanceof Player player) {
                    if (event.getSource().getEntity() != null && monsterId.equals(EntityType.getKey(event.getSource().getEntity().getType()).toString())) {
                        bannerBehavior.onPlayerDamage(event.getContainer(), event.getSource().getEntity(), player);
                    }
                } else if (event.getSource().getEntity() instanceof Player player) {
                    if (monsterId.equals(EntityType.getKey(event.getEntity().getType()).toString())) {
                        bannerBehavior.onEntityDamage(event.getContainer(), event.getEntity(), player);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerKillLivingEntity(LivingDeathEvent event) {
        if (event.getSource() != null && event.getSource().getEntity() instanceof Player player) {
            ifInRangeEntity(player, blockEntity -> BannerBehaviorProvider.getInstance().getBannerBehavior(blockEntity.getParameters().getSilksId()).onPlayerKillLivingEntity(player, event.getEntity()));
        }
    }

    @SubscribeEvent
    public static void onInhibit(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        ifInRangeEntity(entity, blockEntity -> {
            if ("inhibit_silks".equals(blockEntity.getParameters().getSilksId()))
                event.setCanceled(true);
        });
    }

    private static void ifInRangeEntity(Entity entity, Consumer<EnemyBannerBlockEntity> function) {
        for (BlockPos blockPos : getBannerOfEntity(entity)) {
            EnemyBannerBlockEntity blockEntity = (EnemyBannerBlockEntity) entity.level().getBlockEntity(blockPos);
            if (blockEntity != null && blockEntity.getParameters().getMonsterId().equals(EntityType.getKey(entity.getType()).toString())) {
                function.accept(blockEntity);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (bannerBlockList.isEmpty()) return;
        Iterator<Map.Entry<ResourceKey<Level>, BlockPos>> iterator = bannerBlockList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceKey<Level>, BlockPos> entry = iterator.next();
            ServerLevel level = event.getServer().getLevel(entry.getKey());
            if (level == null) {
                iterator.remove();
                return;
            }

            BlockPos pos = entry.getValue();
            if (!level.isLoaded(pos)) continue;
            if (!(level.getBlockEntity(pos) instanceof EnemyBannerBlockEntity entity)) {
                iterator.remove();
                return;
            }

            for (Player player : level.players()) {
                BlockPos blockPos = player.blockPosition();
                if (Math.abs(blockPos.getX() - pos.getX()) < BASIC_RANGE &&
                        Math.abs(blockPos.getY() - pos.getY()) < BASIC_RANGE &&
                        Math.abs(blockPos.getZ() - pos.getZ()) < BASIC_RANGE &&
                        !player.hasEffect(EnemyBanner.ENEMY_BANNER_EFFECT)
                ) {
                    player.addEffect(new MobEffectInstance(EnemyBanner.ENEMY_BANNER_EFFECT, MobEffectInstance.INFINITE_DURATION, 0, false, false, true));
                }
            }

            String silksId = entity.getParameters().getSilksId();
            BannerBehavior bannerBehavior = BannerBehaviorProvider.getInstance().getBannerBehavior(silksId);
            bannerBehavior.onTick(level, pos, event.getServer().getTickCount());
        }
    }

    /**
     * 获取能够影响到实体的旗帜的坐标列表
     * @param entity 实体
     * @return 旗帜的坐标列表
     */
    public static List<BlockPos> getBannerOfEntity(Entity entity) {
        List<BlockPos> result = new ArrayList<>();
        for (Map.Entry<ResourceKey<Level>, BlockPos> bannerEntry : bannerBlockList.entrySet()) {
            if (bannerEntry.getKey() == entity.level().dimension() && isInBannerRange(bannerEntry.getValue(), entity))
                result.add(new BlockPos(bannerEntry.getValue()));
        }
        return result;
    }

    public static boolean isInBannerRange(BlockPos bannerPos, Entity entity) {
        int range = 0;
        boolean flag = false;
        if (entity.level().getBlockEntity(bannerPos) instanceof EnemyBannerBlockEntity blockEntity) {
            String silksId = blockEntity.getParameters().getSilksId();
            BannerBehavior bannerBehavior = BannerBehaviorProvider.getInstance().getBannerBehavior(silksId);
            if (bannerBehavior != null) {
                range = bannerBehavior.getRange();
            }
        }
        if (Math.abs(bannerPos.getX() - entity.blockPosition().getX()) < range &&
                Math.abs(bannerPos.getY() - entity.blockPosition().getY()) < range &&
                Math.abs(bannerPos.getZ() - entity.blockPosition().getZ()) < range
        )
            flag = true;
        return flag;
    }

    /**
     * 判断是否在任意最近的旗帜范围内
     * @param entity 判断实体
     * @return 是否在任意最近的旗帜范围内
     */
    public static boolean isInBannerRange(Entity entity) {
        boolean flag = false;
        BlockPos recentBanner = getRecentBanner(entity.level(), entity.blockPosition());
        if (recentBanner != null) {
            flag = isInBannerRange(recentBanner, entity);
        }
        return flag;
    }

    public static BlockPos getRecentBanner(Level level, BlockPos pos) {
        BlockPos recentPos = null;
        double min = Double.MAX_VALUE;
        for (Map.Entry<ResourceKey<Level>, BlockPos> blockEntity : bannerBlockList.entrySet()) {
            double v = blockEntity.getValue().distSqr(pos);
            if (level.dimension() == blockEntity.getKey() && v < min) {
                min = v;
                recentPos = blockEntity.getValue();
            }
        }
        return recentPos;
    }

    public static void add(Level level, BlockPos pos) {
        bannerBlockList.put(level.dimension(), pos);
    }
}
