package nowebsite.makertechno.the_trackers.core.track;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import nowebsite.makertechno.the_trackers.client.gui.TGui;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.core.track.states.TrackerStepState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 追踪实体的主逻辑类，刷新并标记追踪的实体，有缓存能力。
 */
public class EntityTracker {
    /** 缓存的渲染队列 */
    private static final Map<UUID, TrackerStepState> RENDERING = new HashMap<>();
    /** 规则组 */
    private static final List<Pair<EntityType<?>, Supplier<? extends TRenderCursor>>> ALL_GROUPS = new ArrayList<>();
    /** 框选距离 */
    public static final AABB PICK_RANGE = new AABB(
            - 64.0D,
            - 48.0D,
            - 64.0D,
            + 64.0D,
            + 64.0D,
            + 64.0D
    );

    /** 获取缓存的指针对队列 */
    public static Map<UUID, TrackerStepState> getRENDERING() {
        return RENDERING;
    }

    /** 主处理逻辑: 获取范围内所有实体，先提供给外部，后自己筛选，获取UUID并依此清除已缓存指针中的已消失实体，更新对应的缓存指针并尝试为新扫入的实体创建指针 */
    public static @Nullable Map<UUID, Entity> process() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = TGui.getCameraPlayer(minecraft);
        if (minecraft.level == null || player == null) return null;

        Set<Entity> allScanned = minecraft.level.getEntities(null, PICK_RANGE.move(player.position())).stream()
                .filter(entity -> !(entity instanceof PartEntity<?>))
                .collect(Collectors.toSet());

        List<Entity> nearestEntities = allScanned.stream()
                .filter(EntityTracker::isTargetEntity)
                .sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .limit(TConfig.maxTrackingQuantity)
                .toList(); // 最近N个实体
        // TODO: 加入优先级筛表

        Set<UUID> retained = nearestEntities.stream()
                .map(Entity::getUUID)
                .collect(Collectors.toSet()); // 所有扫到的实体的UUID

        RENDERING.keySet().removeIf(key -> !retained.contains(key)); // 移除不存在对应UUID的缓存指针

        nearestEntities.stream()
                .filter(entity -> !processExisting(entity)) // 刷新已缓存部分
                .forEach(EntityTracker::tryCreateNew); // 创建指针

        return allScanned.stream().collect(Collectors.toMap(Entity::getUUID, entity -> entity));
    }

    /**
     * 更新已存在的指针对。
     * @return 缓存中存在则返回true，缓存中不存在则返回false
     */
    private static boolean processExisting(@NotNull Entity entity) {
        TrackerStepState state = RENDERING.get(entity.getUUID());
        if (state != null) {
            state.updatePos(entity.getRopeHoldPosition(0));
            return true;
        }
        return false;
    }

    /**
     * 判断实体种类并尝试为其创建对应种类的指针对。
     */
    private static void tryCreateNew(Entity entity) {
        for (Pair<EntityType<?>, Supplier<? extends TRenderCursor>> pair : ALL_GROUPS) {
            if (isTargetType(pair.getFirst(), entity)) {
                TrackerStepState state = new TrackerStepState(pair.getSecond().get());
                state.updatePos(entity.getRopeHoldPosition(0));
                RENDERING.put(entity.getUUID(), state);
            }
        }
    }

    /**
     * 所有目标实体类型和指针类型的绑定。当配置变更时，需要重新计算该表
     */
    public static void reCalcAllEntityGroups() {
        ALL_GROUPS.clear();
        if (TConfig.centerRelativeAvailable) ALL_GROUPS.addAll(TConfig.CRCursorWithEntities);
        if (TConfig.trackFullAvailable) ALL_GROUPS.addAll(TConfig.DTCursorWithEntities);
        //all.addAll(else);
    }

    /**
     * 判断是否为定位目标
     */
    private static boolean isTargetEntity(Entity entity) {
        return ALL_GROUPS.stream().anyMatch(pair -> isTargetType(pair.getFirst(), entity));
    }

    /**
     * 检查实体类型
     */
    private static <T extends Entity> boolean isTargetType(@NotNull EntityType<T> entityType, @NotNull Entity entity) {
        return entityType == entity.getType();
    }
}
