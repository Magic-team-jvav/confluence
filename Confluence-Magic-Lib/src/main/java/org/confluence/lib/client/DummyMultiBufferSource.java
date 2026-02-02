package org.confluence.lib.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class DummyMultiBufferSource implements MultiBufferSource {
    public static final MultiBufferSource INSTANCE = new DummyMultiBufferSource();
    @Override
    @NotNull
    public VertexConsumer getBuffer(@NotNull RenderType renderType){
        return DummyVertexConsumer.INSTANCE;
    }
}
