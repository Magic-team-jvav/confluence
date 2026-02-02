package org.confluence.lib.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public class DummyVertexConsumer implements VertexConsumer {
    public static final VertexConsumer INSTANCE = new DummyVertexConsumer();

    @Override
    public VertexConsumer addVertex(float x, float y, float z){
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha){
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v){
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v){
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v){
        return this;
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ){
        return this;
    }
}
