package org.confluence.mod.client.renderer;

import net.minecraft.client.renderer.RenderStateShard;

public class ModRenderStateShards {
    public static final RenderStateShard.ShaderStateShard VOID_SEA_SHADER = new RenderStateShard.ShaderStateShard(
            ModRenderer::getVoidSeaShader);
}
