package org.confluence.mod.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
    @Invoker
    @Nullable RenderType callGetRenderType(LivingEntity livingEntity, boolean bodyVisible, boolean translucent, boolean glowing);

    @Invoker
    boolean callIsBodyVisible(LivingEntity livingEntity);

    @Invoker
    void callSetupRotations(LivingEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale);

    @Invoker
    void callScale(LivingEntity livingEntity, PoseStack poseStack, float partialTickTime);

    @Invoker
    float callGetBob(LivingEntity livingBase, float partialTick);
}
