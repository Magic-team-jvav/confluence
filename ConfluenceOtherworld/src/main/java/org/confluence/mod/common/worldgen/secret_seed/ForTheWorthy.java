package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;

public class ForTheWorthy extends SecretSeed {
    public static final BlockState LAVA = Blocks.LAVA.defaultBlockState();

    public ForTheWorthy(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "fortheworthy".equals(seed) || "for the worthy".equals(seed);
    }

    public static Component getDifficultyName(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> Component.translatable("options.difficulty.easy");
            case EASY -> Component.translatable("options.difficulty.normal");
            case NORMAL -> Component.translatable("options.difficulty.hard");
            case HARD -> Component.translatable("options.difficulty.legendary");
        };
    }

    public static boolean summonPoweredCreeper(ServerLevel level, BlockPos pos) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match(level) && level.random.nextFloat() < 0.25F) {
            return EntityType.CREEPER.spawn(level, null, LibEntityUtils::poweringCreeper, pos, MobSpawnType.TRIGGERED, true, false) != null;
        }
        return false;
    }

    public static void splitNormalBoulder(BoulderEntity entity, ServerLevel level) {
        if (entity.generation < 1 && entity.getType() == ModEntities.BOULDER.get() && ModSecretSeeds.FOR_THE_WORTHY.match(level)) {
            float newRadius = entity.radius * 0.5F;
            int newGeneration = entity.generation + 1;

            // 计算分裂方向（左右各偏移一定角度）
            Vec3 currentMotion = entity.getDeltaMovement();
            double currentYaw = -Math.toDegrees(Math.atan2(currentMotion.x, currentMotion.z));

            for (int i = 0; i < 2; i++) {
                double offsetYaw = (i == 0 ? 60 : -60); // 左右偏移60度
                double newYaw = currentYaw + offsetYaw;
                BoulderEntity splitBoulder = new BoulderEntity(level, entity.position(), entity.getBlockState());
                splitBoulder.radius = newRadius;
                splitBoulder.generation = newGeneration;
                splitBoulder.speed = entity.speed * 0.9;
                splitBoulder.setPos(entity.position());
                splitBoulder.setDeltaMovement(new Vec3(
                        Math.sin(newYaw) * entity.speed * 0.8,
                        1,
                        Math.cos(newYaw) * entity.speed * 0.8
                ));
                level.addFreshEntity(splitBoulder);
            }
        }
    }
}
