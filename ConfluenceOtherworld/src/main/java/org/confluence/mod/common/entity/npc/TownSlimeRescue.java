package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ToolItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TownSlimeRescue extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final boolean balloon;

    public TownSlimeRescue(EntityType<? extends TownSlimeRescue> type, Level level, boolean balloon) {
        super(type, level);
        this.balloon = balloon;
        setNoGravity(balloon);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (balloon) setDeltaMovement(0, Math.sin(tickCount * 0.05) * 0.01, 0);
        else if (onGround() && tickCount % 50 == 0) {
            Player player = level().getNearestPlayer(this, 16);
            if (player != null) {
                var direction = player.position().subtract(position()).multiply(1, 0, 1).normalize();
                setDeltaMovement(direction.scale(0.2));
            }
            jumpFromGround();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (balloon && amount > 0 && !isInvulnerableTo(source) && level() instanceof ServerLevel server
                && TownSlimeNPC.unlock(server, NpcEntities.CLUMSY_SLIME.get(), position()) != null) {
            discard();
            return true;
        }
        return !balloon && !(source.getEntity() instanceof Player) && super.hurt(source, amount);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack key = ItemStack.EMPTY;
        if (!balloon) {
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (stack.is(ToolItems.GOLDEN_KEY.get())) {
                    key = stack;
                    break;
                }
            }
        }
        if (!key.isEmpty()) {
            if (level() instanceof ServerLevel server && TownSlimeNPC.unlock(server, NpcEntities.ELDER_SLIME.get(), position()) != null) {
                if (!player.getAbilities().instabuild) key.shrink(1);
                discard();
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }
}
