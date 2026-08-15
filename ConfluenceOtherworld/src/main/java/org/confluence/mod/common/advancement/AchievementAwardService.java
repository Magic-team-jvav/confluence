package org.confluence.mod.common.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端成就授予的唯一入口。
 *
 * <p>调用方只需要提供成就路径，不需要知道数据文件使用了哪些判据名。服务会读取原版
 * {@link AdvancementProgress}，授予全部尚未完成的判据，并以原版玩家成就进度作为唯一完成状态。
 * 找不到数据生成的成就时不会写入任何替代完成标记，避免玩家永久进入“数据称已完成、实际没有
 * 成就”的不一致状态。</p>
 */
public final class AchievementAwardService {
    private static final Set<ResourceLocation> REPORTED_MISSING_ADVANCEMENTS = ConcurrentHashMap.newKeySet();

    private AchievementAwardService() {
    }

    /**
     * 尝试为玩家完成指定本体成就。
     *
     * @param player 服务端玩家
     * @param path   不含 {@code achievements/} 前缀的本体成就路径
     * @return 本次授予结果
     */
    public static Result award(ServerPlayer player, String path) {
        ResourceLocation id = AchievementUtils.asAchievement(path);
        Advancement advancement = player.server.getAdvancements().getAdvancement(id);
        if (advancement == null) {
            if (REPORTED_MISSING_ADVANCEMENTS.add(id)) {
                Confluence.LOGGER.error("Cannot award missing advancement {}", id);
            }
            return Result.MISSING_ADVANCEMENT;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) {
            return Result.ALREADY_COMPLETED;
        }

        List<String> remainingCriteria = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remainingCriteria::add);
        if (remainingCriteria.isEmpty()) {
            Confluence.LOGGER.error("Advancement {} is incomplete but has no remaining criteria", id);
            return Result.NO_REMAINING_CRITERIA;
        }

        for (String criterion : remainingCriteria) {
            player.getAdvancements().award(advancement, criterion);
        }
        AdvancementProgress updatedProgress = player.getAdvancements().getOrStartProgress(advancement);
        /*
         * 原版 award 的布尔值只描述本次单个判据调用是否报告变化，不能替代最终进度。
         * 无网络连接的服务端测试玩家可能已经提交判据，却返回 false；玩家进度仍是唯一权威状态。
         */
        if (!updatedProgress.isDone()) {
            Confluence.LOGGER.error("Failed to complete advancement {}", id);
            return Result.AWARD_FAILED;
        }
        return Result.AWARDED;
    }

    /**
     * 授予结果。缺失成就和无可授予判据属于数据或开发配置错误，不应被当作正常完成。
     */
    public enum Result {
        AWARDED(true),
        ALREADY_COMPLETED(true),
        MISSING_ADVANCEMENT(false),
        NO_REMAINING_CRITERIA(false),
        AWARD_FAILED(false);

        private final boolean completed;

        Result(boolean completed) {
            this.completed = completed;
        }

        /**
         * 返回调用结束后该成就是否已由原版进度确认完成。
         */
        public boolean completed() {
            return completed;
        }
    }
}
