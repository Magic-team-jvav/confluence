package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.terra_curio.mixed.IEntity;

public class InverseEnderMan extends EnderMan {
    public InverseEnderMan(EntityType<? extends InverseEnderMan> type, Level level) {
        super(type, level);
        IEntity.of(this).terra_curio$setShouldRot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes().add(Attributes.GRAVITY, -Attributes.GRAVITY.value().getDefaultValue());
    }

    public static boolean checkInverseEnderManSpawnRules(EntityType<? extends InverseEnderMan> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(level, pos, random))
                && checkMobSpawnRules(type, level, spawnType, pos);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends InverseEnderMan> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos) {
        BlockPos above = pos.above();
        return spawnType == MobSpawnType.SPAWNER || level.getBlockState(above).isValidSpawn(level, above, type);
    }
}
