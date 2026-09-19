package org.confluence.mod.common.entity.animal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.TownSlimeNPC;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.item.common.BugNetItem;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MysticFrog extends Frog implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    public MysticFrog(EntityType<? extends Frog> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public void purify() {
        if (level() instanceof ServerLevel server && TownSlimeNPC.unlock(server, NpcEntities.MYSTIC_SLIME.get(), position()) != null)
            discard();
    }

    public void escapeNet() {
        if (!(level() instanceof ServerLevel server)) return;
        server.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.2, getZ(), 8, 0.2, 0.2, 0.2, 0.02);
        for (int attempt = 0; attempt < 16; attempt++) {
            double x = getX() + random.nextInt(17) - 8;
            double y = getY() + random.nextInt(9) - 4;
            double z = getZ() + random.nextInt(17) - 8;
            if (randomTeleport(x, y, z, true)) return;
        }
        discard();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() instanceof BugNetItem) {
            escapeNet();
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return InteractionResult.PASS;
    }
}
