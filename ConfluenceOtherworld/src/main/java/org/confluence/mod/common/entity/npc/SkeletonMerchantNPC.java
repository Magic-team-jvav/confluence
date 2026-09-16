package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.ai.NPCCombatActions;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.projectile.NPCProjectileEffects;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class SkeletonMerchantNPC extends BaseNPC {
    private LivingEntity throwTarget;
    private NPCCombatProfile.Values throwValues;
    private int throwDelay;

    public SkeletonMerchantNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile profile) {
        super(type, level, profile);
    }

    @Override
    protected void customServerAiStep() {
        if (getInteractingPlayer() == null && level().getNearestPlayer(this, 48) == null) {
            NPCSpawner.INSTANCE.onNPCRemoved(this);
            discard();
            return;
        }
        super.customServerAiStep();
        if (throwDelay > 0 && --throwDelay == 0) {
            if (throwTarget != null && throwTarget.isAlive() && canAttack(throwTarget) && getSensing().hasLineOfSight(throwTarget)) {
                NPCCombatActions.thrown(() -> new ItemStack(Items.BONE), NPCProjectileEffects.NONE).perform(this, throwTarget, throwValues);
            }
            throwTarget = null;
        }
    }

    public void throwBone(LivingEntity target, NPCCombatProfile.Values values) {
        throwTarget = target;
        throwValues = values;
        throwDelay = 12;
        triggerAnim("attack", "cast");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("cast", RawAnimation.begin().thenPlay("attack.cast")));
    }

    @Override
    protected void tickFindHouse(ServerLevel level) {}

    @Override
    public boolean canAttack(LivingEntity target) {
        return !target.getType().is(EntityTypeTags.SKELETONS) && super.canAttack(target);
    }
}
