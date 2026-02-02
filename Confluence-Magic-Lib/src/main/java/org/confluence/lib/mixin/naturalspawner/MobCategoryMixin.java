package org.confluence.lib.mixin.naturalspawner;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.MobCategory;
import org.confluence.lib.mixed.IChunkSpawnDataAccess;
import org.confluence.lib.util.NaturalSpawnerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin implements IChunkSpawnDataAccess {
    @Unique
    private NaturalSpawnerUtil.ChunkSpawnData confluence$data = NaturalSpawnerUtil.ChunkSpawnData.DEFAULT;

    @Override
    public void confluence$setData(NaturalSpawnerUtil.ChunkSpawnData data) {
        this.confluence$data = data;
    }

    @Override
    public NaturalSpawnerUtil.ChunkSpawnData confluence$getData() {
        return confluence$data;
    }

    @ModifyReturnValue(method = "getMaxInstancesPerChunk", at = @At("RETURN"))
    private int mul(int original) {
        return confluence$getData().getCount(original);
    }
}
