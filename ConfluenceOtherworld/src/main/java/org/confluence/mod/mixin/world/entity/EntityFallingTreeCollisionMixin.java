package org.confluence.mod.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.entity.FallingTreeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/// 倒下中的树（FallingTreeEntity）参与实体移动的碰撞：每格一个方块大小的碰撞箱
@Mixin(Entity.class)
public abstract class EntityFallingTreeCollisionMixin {
    @ModifyExpressionValue(method = "collide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<VoxelShape> confluence$fallingTreeCollisions(List<VoxelShape> original, @Local(argsOnly = true) Vec3 movement) {
        Entity self = (Entity) (Object) this;
        return FallingTreeEntity.addCollisions(self, self.getBoundingBox().expandTowards(movement), original);
    }
}
