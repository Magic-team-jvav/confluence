package org.confluence.terraentity.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.IOriented;
import org.confluence.terraentity.mixin.accessor.LevelRendererAccessor;
import org.confluence.terraentity.utils.OBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox",at = @At("RETURN"))
    private static void renderObb(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha, CallbackInfo ci){
        if(entity instanceof IOriented oe){
            OBB obb = oe.getOrientedBoundingBox();
            Vec3 pos = entity.position();
            AABB border = obb.getBorder().move(pos.scale(-1));
            LevelRendererAccessor.callRenderShape(poseStack, buffer, obb, -pos.x, -pos.y, -pos.z, 1, 1, 0, 1);
            LevelRenderer.renderLineBox(poseStack, buffer, border, 0, 1, 1, 1);
        }
    }
}
