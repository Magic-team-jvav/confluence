package org.confluence.mod.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.*;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.mixed.IPlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin implements IPlayerAdvancements {
    @Shadow
    protected abstract void applyFrom(ServerAdvancementManager advancementManager, PlayerAdvancements.Data data);

    @Shadow
    public abstract boolean award(AdvancementHolder advancement, String criterionKey);

    @Shadow
    private ServerPlayer player;

    @Shadow
    protected abstract <T extends CriterionTriggerInstance> void registerListener(AdvancementHolder advancement, String criterionKey, Criterion<T> criterion);

    @Override
    public void confluence$load(ServerAdvancementManager manager, PlayerAdvancements.Data data) {
        applyFrom(manager, data);
        data.forEach((id, progress) -> {
            AdvancementHolder holder = manager.get(id);
            if (holder == null) return;

            Advancement advancement = holder.value();
            if (advancement.criteria().isEmpty()) {
                award(holder, "");
                advancement.rewards().grant(player);
            }

            if (!progress.isDone()) {
                for (Map.Entry<String, Criterion<?>> entry : holder.value().criteria().entrySet()) {
                    CriterionProgress criterion = progress.getCriterion(entry.getKey());
                    if (criterion != null && !criterion.isDone()) {
                        registerListener(holder, entry.getKey(), entry.getValue());
                    }
                }
            }
        });
    }

    @ModifyExpressionValue(method = "lambda$asData$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;hasProgress()Z"))
    private static boolean skipConfluence(boolean original, @Local(argsOnly = true) AdvancementHolder holder) {
        if (original && AchievementOffsetLoader.getDisplayOffset().containsKey(holder.id())) { // 跳过保存汇流来世成就
            return false;
        }
        return original;
    }
}
