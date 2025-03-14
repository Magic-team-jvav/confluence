package org.confluence.mod.mixin.integration.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.llamalad7.mixinextras.sugar.Local;
import org.confluence.mod.integration.touhou_little_maid.TaskBoomerangAttack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager")
public class TouhouLittleMaidMixin {

    // 注册任务
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/task/TaskAttack;<init>()V"))
    private static void initMixin(CallbackInfo ci, @Local TaskManager manager) {
        manager.add(new TaskBoomerangAttack());
    }
}
