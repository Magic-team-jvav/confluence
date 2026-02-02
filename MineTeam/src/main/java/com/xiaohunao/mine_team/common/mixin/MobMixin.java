package com.xiaohunao.mine_team.common.mixin;


import com.xiaohunao.mine_team.common.entity.goal.TeamOwnerHurtTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends Entity{
    @Shadow @Final public GoalSelector targetSelector;

    public MobMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(EntityType<? extends Mob> entityType, Level level, CallbackInfo ci) {
        this.targetSelector.addGoal(2, new TeamOwnerHurtTargetGoal((Mob) (Object) this));
    }

}