package org.confluence.terraentity.client.util;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.trail.ITrail;
import org.joml.Matrix4f;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class ShaderUtil {


    public static void shaderBlit(Matrix4f matrix4f, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight) {
        shaderBlit(matrix4f, x , x+width, y, y+height , 0, width, height,uOffset, vOffset, textureWidth, textureHeight);
    }

    static void shaderBlit(Matrix4f matrix4f, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        innerBlit(matrix4f, x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float)textureWidth, (uOffset + (float)uWidth) / (float)textureWidth, (vOffset + 0.0F) / (float)textureHeight, (vOffset + (float)vHeight) / (float)textureHeight);
    }

    static void innerBlit(Matrix4f matrix4f, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x1, (float)y1, (float)blitOffset).setUv(minU, minV);
        bufferbuilder.addVertex(matrix4f, (float)x1, (float)y2, (float)blitOffset).setUv(minU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, (float)blitOffset).setUv(maxU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y1, (float)blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }


    public static void blitScreen(ShaderInstance shader, Consumer<ShaderInstance> setupShader){


        RenderSystem.assertOnRenderThread();
        GlStateManager._disableDepthTest();
        GlStateManager._viewport(0, 0, Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height);

//        ShaderInstance shader = ModRenderTypes.Shaders.colorBlitShader;
//        ShaderInstance shaderinstance = Objects.requireNonNull(shader, "Blit shader not loaded");
//        shader.COLOR_MODULATOR.set(1f, 1f, 1f, 0.2f);
//        shaderinstance.setSampler("Sampler0", Minecraft.getInstance().getMainRenderTarget());
//        shaderinstance.setSampler("Sampler1", BrainOfCthulhuRenderer.target);

        setupShader.accept(shader);

        shader.apply();
        BufferBuilder bufferbuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferbuilder.addVertex(0.0F, 0.0F, 0F);
        bufferbuilder.addVertex(1.0F, 0.0F, 0F);
        bufferbuilder.addVertex(1.0F, 1.0F, 0F);
        bufferbuilder.addVertex(0.0F, 1.0F, 0F);
        BufferUploader.draw(bufferbuilder.buildOrThrow());

        shader.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        RenderSystem.disableBlend();
    }

    public static void renderDebugBlock(BufferBuilder buffer, BlockPos pos, float size, int r, int g, int b, int a, boolean up, boolean down, boolean north, boolean south, boolean east, boolean west){
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        //0
        if (!((!up) || (!north))) {
            buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
            buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
        }

        //1
        if (!((!up) || (!east))) {
            buffer.addVertex(x + size, y + size, z).setColor(r, g, b, a);
            buffer.addVertex(x + size, y + size, z + size).setColor(r, g, b, a);
        }

        //2
        if (!((!up) || (!south))) {
            buffer.addVertex(x + size, y + size, z + size).setColor(r, g, b, a);
            buffer.addVertex(x, y + size, z + size).setColor(r, g, b, a);
        }

        //3
        if (!((!up) || (!west))) {
            buffer.addVertex(x, y + size, z + size).setColor(r, g, b, a);
            buffer.addVertex(x, y + size, z).setColor(r, g, b, a);
        }

        // BOTTaddVertex()
        //4
        if (!((!down) || (!east))) {
            buffer.addVertex(x + size, y, z).setColor(r, g, b, a);
            buffer.addVertex(x + size, y, z + size).setColor(r, g, b, a);
        }

        //5
        if (!((!down) || (!south))) {
            buffer.addVertex(x + size, y, z + size).setColor(r, g, b, a);
            buffer.addVertex(x, y, z + size).setColor(r, g, b, a);
        }

        //6
        if (!((!down) || (!west))) {
            buffer.addVertex(x, y, z + size).setColor(r, g, b, a);
            buffer.addVertex(x, y, z).setColor(r, g, b, a);
        }

        //7
        if (!((!down) || (!north))) {
            buffer.addVertex(x, y, z).setColor(r, g, b, a);
            buffer.addVertex(x + size, y, z).setColor(r, g, b, a);
        }

        // EdgeaddVertex()
        //8
        if (!((!east) || (!south))) {
            buffer.addVertex(x + size, y, z + size).setColor(r, g, b, a);
            buffer.addVertex(x + size, y + size, z + size).setColor(r, g, b, a);
        }

        // EdgeaddVertex()
        //9
        if (!((!east) || (!north))) {
            buffer.addVertex(x + size, y, z).setColor(r, g, b, a);
            buffer.addVertex(x + size, y + size, z).setColor(r, g, b, a);
        }

        // EdgeaddVertex()
        //10
        if (!((!west) || (!south))) {
            buffer.addVertex(x, y, z + size).setColor(r, g, b, a);
            buffer.addVertex(x, y + size, z + size).setColor(r, g, b, a);
        }

        // EdgeaddVertex()
        //11
        if (!((!west) || (!north))) {
            buffer.addVertex(x, y, z).setColor(r, g, b, a);
            buffer.addVertex(x, y + size, z).setColor(r, g, b, a);
        }
    }

    public static void renderDebugBlock(BufferBuilder buffer, BlockPos pos, float size, int r, int g, int b, int a){
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        //0
        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);

        //1
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);

        //2
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);

        //3
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);

        // BOTTaddVertex()
        //4
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);

        //5
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);

        //6
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y, z).setColor(r,g,b,a);

        //7
        buffer.addVertex(x, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);

        // EdgeaddVertex()
        //8
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);

        // EdgeaddVertex()
        //9
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);

        // EdgeaddVertex()
        //10
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);

        // EdgeaddVertex()
        //11
        buffer.addVertex(x, y, z).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
    }


    public static void renderLightBoundle(MultiBufferSource buffer, Matrix4f matrix4f, float size,float height, int color){
        VertexConsumer consumer = buffer.getBuffer(LIGHT_BOUNDLE_RENDER_TYPE.get());

        ITrail.addVertex(consumer, matrix4f, new Vec3(0,0,0), color).setNormal(0,0,-1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(0,height,0), 0x00000000).setNormal(0,0,-1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,height,0), 0x00000000).setNormal(0,0,-1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,0,0), color).setNormal(0, 0,-1);

        ITrail.addVertex(consumer, matrix4f, new Vec3(0,0,0), color).setNormal(1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(0,height,0), 0x00000000).setNormal(1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(0,height,size), 0x00000000).setNormal(1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(0,0,size), color).setNormal(1,0,0);

        ITrail.addVertex(consumer, matrix4f, new Vec3(0,0,size), color).setNormal(0, 0,1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(0,height,size), 0x00000000).setNormal(0, 0,1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,height,size), 0x00000000).setNormal(0, 0,1);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,0,size), color).setNormal(0, 0,1);

        ITrail.addVertex(consumer, matrix4f, new Vec3(size,0,size), color).setNormal(-1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,height,size), 0x00000000).setNormal(-1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,height,0), 0x00000000).setNormal(-1,0,0);
        ITrail.addVertex(consumer, matrix4f, new Vec3(size,0,0), color).setNormal(-1,0,0);

    }

    public static Supplier<RenderType> TRAIL_RENDER_TYPE = Suppliers.memoize(() -> RenderType.create(
            "trail_render_type",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
                    1536,
                    false,
                    true,
                    RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                                .setWriteMaskState(COLOR_DEPTH_WRITE)
                                .setTransparencyState(LIGHTNING_TRANSPARENCY)
                                .setOutputState(TRANSLUCENT_TARGET)
                                .setCullState(NO_CULL)
                                .createCompositeState(true)
    ));

    public static Supplier<RenderType> LIGHT_BOUNDLE_RENDER_TYPE = Suppliers.memoize(() -> RenderType.create(
            "trail_render_type",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                        .setWriteMaskState(COLOR_WRITE)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(TRANSLUCENT_TARGET)
                        .setCullState(NO_CULL)
                        .createCompositeState(true)
        ));
}
