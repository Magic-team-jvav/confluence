package org.confluence.terraentity.mixed;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.confluence.terraentity.mixin.accessor.GameRendererAccessor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class HotSwap {
    public static void doSomething(float partialTicks, TextureTarget target){

        target.setClearColor(0, 0, 0, 0);
        target.clear(true);
        target.bindWrite(true);
        GameRenderer gr = Minecraft.getInstance().gameRenderer;
        Camera camera = gr.getMainCamera();
        Matrix4f matrix = (new Matrix4f()).rotation(camera.rotation().conjugate(new Quaternionf()));
        ((GameRendererAccessor)gr).callRenderItemInHand(camera, partialTicks, matrix);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        target.blitToScreen(Minecraft.getInstance().getMainRenderTarget().width,Minecraft.getInstance().getMainRenderTarget().height,false);

    }

    public static int  changeColor(int color){
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;
        int a = (color >> 24) & 255;

        return ColorARGB.pack(r, g, b,a);
    }
}
