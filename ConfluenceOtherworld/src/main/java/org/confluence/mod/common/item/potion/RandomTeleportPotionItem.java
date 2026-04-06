package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;

public class RandomTeleportPotionItem extends AbstractPotionItem {
    public static final double RANGE = 10000;

    public RandomTeleportPotionItem() {
        super(new Properties());
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        double nx = living.getX() + (living.getRandom().nextDouble() - 0.5) * RANGE;
        int min = level.getMinBuildHeight();
        int max = min + serverLevel.getLogicalHeight() - 1;
        double ny = living.getRandom().nextIntBetweenInclusive(min, max);
        double nz = living.getZ() + (living.getRandom().nextDouble() - 0.5) * RANGE;
        if (living.isPassenger()) {
            living.stopRiding();
        }

        Vec3 vec3 = living.position();
        level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(living));

        int cx = SectionPos.blockToSectionCoord(nx);
        int cz = SectionPos.blockToSectionCoord(nz);
        long chunkPos = ChunkPos.asLong(cx, cz);
        boolean loaded = LibUtils.getChunkIfLoaded(serverLevel, cx, cz) != null ||
                serverLevel.getForcedChunks().contains(chunkPos);
        if (!loaded) serverLevel.setChunkForced(cx, cz, true);
        teleport:
        {
            BlockPos.MutableBlockPos pos = BlockPos.containing(nx, ny, nz).mutable();
            while (pos.getY() > min) {
                if (available(level, pos)) {
                    living.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    break teleport;
                } else {
                    pos.setY(pos.getY() - 1);
                }
            }
            pos.setY(Mth.floor(ny));
            while (pos.getY() < max) {
                if (available(level, pos)) {
                    living.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    break teleport;
                } else {
                    pos.setY(pos.getY() + 1);
                }
            }
        }
        if (!loaded) serverLevel.setChunkForced(cx, cz, false);

        if (living instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }
    }

    private static boolean available(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockPos below = pos.below();
        return !level.getBlockState(above).isSolidRender(level, above) &&
                !level.getBlockState(pos).isSolidRender(level, pos) &&
                level.getBlockState(below).isSolidRender(level, below);
    }
}
