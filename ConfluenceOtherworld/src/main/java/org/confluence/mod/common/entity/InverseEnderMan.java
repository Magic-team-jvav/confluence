package org.confluence.mod.common.entity;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.lib.mixed.ILibEntity;

public class InverseEnderMan extends EnderMan {
    public InverseEnderMan(EntityType<? extends InverseEnderMan> type, Level level) {
        super(type, level);
        ILibEntity.of(this).confluence$setShouldRot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        Attribute value = PortAttributesExtension.gravity().get();
        return EnderMan.createAttributes().add(value, -value.getDefaultValue());
    }

    public static boolean checkInverseEnderManSpawnRules(EntityType<? extends InverseEnderMan> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && isDarkEnoughToSpawn(level, pos, random)
                && checkMobSpawnRules(type, level, spawnType, pos);
    }

    public static boolean checkMobSpawnRules(EntityType<? extends InverseEnderMan> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos) {
        BlockPos above = pos.above();
        return spawnType == MobSpawnType.SPAWNER || level.getBlockState(above).isValidSpawn(level, above, type);
    }
}
