package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.entity.projectile.bomb.BaseGrenadeEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.ConsumableItems;

import java.util.List;

public class NoTraps extends SecretSeed {
    public NoTraps(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "notraps".equals(seed) || "no traps".equals(seed);
    }

    public static void dropBombWhenLeavesDestroy(ServerPlayer serverPlayer, BlockState blockState, BlockPos pos) {
        if (blockState.is(BlockTags.LEAVES) && (!blockState.hasProperty(BlockStateProperties.PERSISTENT) || !blockState.getValue(BlockStateProperties.PERSISTENT))) {
            ServerLevel level = serverPlayer.serverLevel();
            if (ModSecretSeeds.FOR_THE_WORTHY.match(serverPlayer.server) && blockState.is(BlockTags.LEAVES) && level.random.nextFloat() < 0.25F) {
                BaseBombEntity bomb = new BaseBombEntity(ModEntities.BOMB_ENTITY.get(), level);
                bomb.setPos(pos.getCenter());
                level.addFreshEntity(bomb);
            }
        }
    }

    public static void breakClimbable(LivingEntity living) {
        if (living instanceof ServerPlayer serverPlayer && ModSecretSeeds.NO_TRAPS.match(serverPlayer.server)) {
            if (living.level().getGameTime() % 20 == 0 && living.getRandom().nextFloat() < 0.3F) {
                living.level().destroyBlock(living.blockPosition(), true);
            }
        }
    }

    public static void entityInvulnerableToExplosion(Level level, List<Entity> affectedEntities) {
        if (level instanceof ServerLevel serverLevel && ModSecretSeeds.NO_TRAPS.match(serverLevel)) {
            affectedEntities.removeIf(entity -> !(entity instanceof ServerPlayer));
        }
    }

    public static void entityDropsGrenade(LivingEntity living) {
        if (living.level() instanceof ServerLevel serverLevel && ModSecretSeeds.NO_TRAPS.match(serverLevel) && living.getRandom().nextFloat() < 0.03F) {
            Player nearestPlayer = serverLevel.getNearestPlayer(living, 16);
            if (nearestPlayer != null) {
                BaseGrenadeEntity grenade = new BaseGrenadeEntity(living);
                grenade.setItem(ConsumableItems.GRENADE.get().getDefaultInstance());
                Vec3 vectorA2B = LibMathUtils.getVectorA2B(living, nearestPlayer);
                double size = living.getBoundingBox().getSize() + 1.5;
                grenade.moveTo(living.position().add(vectorA2B.x * size, vectorA2B.y * size + 0.5, vectorA2B.z * size));
                float[] rots = LibMathUtils.dirToRot(vectorA2B, true);
                grenade.shootFromRotation(living, rots[1], rots[0], 0.0F, living.distanceTo(nearestPlayer) / 4, 0);
                serverLevel.addFreshEntity(grenade);
            }
        }
    }
}
