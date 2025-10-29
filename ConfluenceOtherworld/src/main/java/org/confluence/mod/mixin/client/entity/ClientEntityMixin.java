package org.confluence.mod.mixin.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.effect.GlowingHelper;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ClientEntityMixin implements IEntity {
    @Unique
    private Vec3 confluence$deathMotion;

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void getTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ModEffects.HUNTER)) {
            GlowingHelper helper = GlowingHelper.INSTANCE;
            //自定义狩猎药水表
            for (var entry : helper.colorMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(confluence$self().getClass())) {
                    cir.setReturnValue(entry.getValue().color().getRGB());
                    return;
                }
            }

            //敌人
            if (confluence$self() instanceof Enemy) {
                cir.setReturnValue(helper.enemyColor.getRGB());
                return;
            }

            //中立生物
            if (confluence$self() instanceof NeutralMob) {
                /*todo 添加愤怒颜色
                if((te$getSelf() instanceof EnderMan) && ((EnderMan) te$getSelf()).isCreepy()){
                        cir.setReturnValue(helper.angerColor.getRGB());
                        return;
                }*/
                cir.setReturnValue(helper.neutralColor.getRGB());
                return;
            }
            cir.setReturnValue(helper.defaultColor.getRGB());
        }
    }

    @Override
    public Vec3 confluence$deathMotion(Vec3... motion) {
        if (motion != null && motion.length > 0) {
            confluence$deathMotion = motion[0];
        }
        return confluence$deathMotion;
    }
}
