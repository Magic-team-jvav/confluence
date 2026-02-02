package org.confluence.terraentity.mixin;


import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ChunkMap.class})
public abstract class ChunkMapMixin {

    public ChunkMapMixin() {
    }
    //控制肉山的渲染距离
    @ModifyVariable(
            method = "addEntity",
            at = @At("STORE"),
            ordinal = 0
    )
    private int replaceDistance(int distance, Entity entity) {
        if (entity instanceof WallOfFlesh wallOfFlesh && entity.isAlive()) {
            double width = wallOfFlesh.getGridSizeX() * wallOfFlesh.gridSpacing;
            double height = wallOfFlesh.getGridSizeY() * wallOfFlesh.gridSpacing;
            double diagonalDistance = Math.sqrt(width * width + height * height);
            int bonusDistance = 2000;

            return Math.min((int) (diagonalDistance + bonusDistance), 10000);
        }
        return distance;
    }

}
