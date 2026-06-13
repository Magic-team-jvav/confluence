package org.confluence.mod.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.mixed.IPlayerAdvancements;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin implements IPlayerAdvancements {
    @Shadow
    public abstract boolean award(Advancement advancement, String criterionKey);

    @Shadow
    private ServerPlayer player;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract void startProgress(Advancement advancement, AdvancementProgress progress);

    @Shadow
    @Final
    private Set<Advancement> progressChanged;

    @Shadow
    protected abstract void markForVisibilityUpdate(Advancement advancement);

    @Shadow
    @Final
    private Path playerSavePath;

    @Override
    public void confluence$load(ServerAdvancementManager manager, Map<ResourceLocation, AdvancementProgress> data) {
        data.forEach((id, advancementProgress) -> {
            Advancement advancement = manager.getAdvancement(id);
            if (advancement == null) {
                LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", id, playerSavePath);
                return;
            }

            startProgress(advancement, advancementProgress);
            progressChanged.add(advancement);
            markForVisibilityUpdate(advancement);

            if (advancement.getCriteria().isEmpty()) {
                award(advancement, "");
                advancement.getRewards().grant(player);
            }

            if (!advancementProgress.isDone()) {
                for (Map.Entry<String, Criterion> entry : advancement.getCriteria().entrySet()) {
                    CriterionProgress criterionProgress = advancementProgress.getCriterion(entry.getKey());
                    if (criterionProgress == null || criterionProgress.isDone()) continue;
                    CriterionTriggerInstance criterionTriggerInstance = entry.getValue().getTrigger();
                    if (criterionTriggerInstance == null) continue;
                    CriterionTrigger<CriterionTriggerInstance> criterionTrigger = CriteriaTriggers.getCriterion(criterionTriggerInstance.getCriterion());
                    if (criterionTrigger == null) continue;
                    criterionTrigger.addPlayerListener(confluence$self(), new CriterionTrigger.Listener<>(criterionTriggerInstance, advancement, entry.getKey()));
                }
            }
        });
    }

    @ModifyExpressionValue(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;hasProgress()Z"))
    private static boolean skipConfluence(boolean original, @Local(name = "entry") Map.Entry<Advancement, AdvancementProgress> entry) {
        if (original && AchievementOffsetLoader.getDisplayOffset().containsKey(entry.getKey().getId())) { // 跳过保存汇流来世成就
            return false;
        }
        return original;
    }
}
