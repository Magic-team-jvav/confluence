package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModSecretSeeds;

public class NeverSleep extends SecretSeed {
    public NeverSleep(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "never sleep".equals(seed);
    }

    @Override
    public boolean isHided() {
        return true;
    }

    public static boolean boom(MinecraftServer server, BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof BedBlock bedBlock && ModSecretSeeds.NEVER_SLEEP.match(server)) {
            level.removeBlock(pos, false);
            BlockPos blockpos = pos.relative(state.getValue(BedBlock.FACING).getOpposite());
            if (level.getBlockState(blockpos).is(bedBlock)) {
                level.removeBlock(blockpos, false);
            }
            Vec3 vec3 = blockpos.getCenter();
            level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return true;
        }
        return false;
    }
}
