package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.entity.monster.BaseWarriorMonster;
import org.confluence.mod.common.init.entity.MonsterEntities;

public class AntlionEggBlock extends Block {
    public AntlionEggBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        hatch(level, pos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        hatch(level, pos);
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        hatch(level, hit.getBlockPos());
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        spawnLarvae(level, pos);
    }

    private void hatch(Level level, BlockPos pos) {
        if (!level.isClientSide && level.getBlockState(pos).is(this) && level.removeBlock(pos, false)) {
            spawnLarvae(level, pos);
        }
    }

    private void spawnLarvae(Level level, BlockPos pos) {
        if (!org.confluence.lib.util.LibUtils.isDev()) return;
        if (!(level instanceof ServerLevel server)) return;
        int count = 1 + level.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            BaseWarriorMonster larva = MonsterEntities.ANTLION_LARVA.get().create(level);
            if (larva == null) continue;
            larva.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360, 0);
            larva.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null, null);
            if (server.addFreshEntity(larva)) {
                larva.setDeltaMovement((level.random.nextDouble() - 0.5) * 0.3, 0.3, (level.random.nextDouble() - 0.5) * 0.3);
            }
        }
    }
}
