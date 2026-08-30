package org.confluence.mod.mixin.world.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.effect.GlowingHelper;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terraentity.api.entity.IMinion;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Entity.class)
public abstract class LocalEntityMixin implements SelfGetter<Entity> {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void getTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity thiz = confluence$self();
        if (thiz instanceof IMinion || thiz instanceof ISummonMob) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.HUNTER)) return;
        GlowingHelper helper = GlowingHelper.INSTANCE;
        // 自定义狩猎药水表
        for (Map.Entry<Class<? extends Entity>, GlowingHelper.Data> entry : helper.colorMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(thiz.getClass())) {
                cir.setReturnValue(entry.getValue().color().get());
                return;
            }
        }

        // 敌人
        if (thiz instanceof Enemy) {
            cir.setReturnValue(helper.enemyColor.get());
            return;
        }

        // 中立生物
        if (thiz instanceof NeutralMob) {
                /*todo 添加愤怒颜色
                if((te$getSelf() instanceof EnderMan) && ((EnderMan) te$getSelf()).isCreepy()){
                        cir.setReturnValue(helper.angerColor.getRGB());
                        return;
                }*/
            cir.setReturnValue(helper.neutralColor.get());
            return;
        }
        cir.setReturnValue(helper.defaultColor.get());
    }
}
