package org.confluence.mod.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.init.ModBlockCounters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements SelfGetter<Mob> {
    /// 只补原版同维度无人时不执行距离消失判定的空档；不覆盖持久化和 Forge 的保护结果。
    @ModifyExpressionValue(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;canEntityDespawn(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/ServerLevelAccessor;)Lnet/minecraftforge/eventbus/api/Event$Result;", remap = false))
    private Event.Result confluence$despawnInEmptyDimension(Event.Result result) {
        Mob mob = confluence$self();
        if (result != Event.Result.DEFAULT || !mob.isAlive()
                || mob.getType().getCategory() != MobCategory.MONSTER
                || mob.hasCustomName() || mob instanceof Boss
                || mob instanceof BossOwnedEntity owned && owned.getBossOwner() != null
                || !mob.removeWhenFarAway(Double.POSITIVE_INFINITY)
                || !mob.level().players().isEmpty()) {
            return result;
        }
        /// 沿用原版的闲置计时和随机消失概率，不另设无人倒计时或距离规则。
        if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0)
            return Event.Result.ALLOW;
        return result;
    }

    @Inject(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getLightLevelDependentMagicValue()F"), cancellable = true)
    private void checkGraveyard(CallbackInfoReturnable<Boolean> cir) {
        if (ModBlockCounters.isGraveyard(confluence$self().level(), confluence$self().blockPosition())) {
            cir.setReturnValue(false);
        }
    }
}
