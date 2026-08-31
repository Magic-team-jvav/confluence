package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.function.Consumer;

public class ModRenderer {
    /// 虚空海海面
    private static ShaderInstance voidSeaShader;

    public static void register(ResourceProvider resourceProvider, RegisterConsumer consumer) throws IOException {
        consumer.register(new ShaderInstance(resourceProvider, VoidSeaRenderer.SEA_SHADER_ID, DefaultVertexFormat.POSITION_TEX_COLOR),
                shader -> voidSeaShader = shader);
    }

    public static ShaderInstance getVoidSeaShader() {
        return voidSeaShader;
    }

    @FunctionalInterface
    public interface RegisterConsumer {
        void register(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded);
    }
}
