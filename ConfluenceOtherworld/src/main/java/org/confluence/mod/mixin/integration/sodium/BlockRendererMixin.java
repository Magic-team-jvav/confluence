package org.confluence.mod.mixin.integration.sodium;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.effect.textures.GraySpriteShifterEntry;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.integration.sodium.IMutableQuadViewImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @see org.confluence.mod.mixin.client.renderer.ModelBlockRendererMixin
 */
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public abstract class BlockRendererMixin {
    @Unique
    private BlockPos confluence$pos;

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void cachePos(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        this.confluence$pos = pos;
    }

    @Inject(method = "processQuad", at = @At("HEAD"))
    private void cacheColor(MutableQuadViewImpl quad, CallbackInfo ci, @Share("colorData") LocalIntRef colorData) {
        colorData.set(LocalBrushData.getColor(confluence$pos, ((ModelQuadView) (Object) quad).getLightFace())); // 并非冗余
    }

    @ModifyVariable(method = "processQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;colorizeQuad(Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;I)V"))
    private boolean illuminant(boolean emissive, @Share("colorData") LocalIntRef colorData) {
        return emissive || colorData.get() == BrushData.ILLUMINANT_COLOR;
    }

    @Inject(method = "processQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;bufferQuad(Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;[FLnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;)V"))
    private void setColor(MutableQuadViewImpl quad, CallbackInfo ci, @Share("colorData") LocalIntRef colorData) {
        int color = colorData.get();
        if (color == BrushData.EMPTY_COLOR || color == BrushData.ILLUMINANT_COLOR || color == BrushData.ECHO_COLOR) return;
        IMutableQuadViewImpl view = (IMutableQuadViewImpl) (Object) quad;
        if (color == BrushData.NEGATIVE_COLOR) {
            ModelQuadView view1 = (ModelQuadView) (Object) quad;
            for (int i = 0; i < 4; ++i) {
                int color1 = view1.getColor(i); // 并非冗余
                if ((color1 & 0xFFFFFF) != 0xFFFFFF) {
                    int comp0 = 255 - (color1 & 255);
                    int comp1 = 255 - (color1 >>> 8 & 255);
                    int comp2 = 255 - (color1 >>> 16 & 255);
                    int comp3 = color1 >>> 24 & 255;
                    view.confluence$color(i, comp0 | comp1 << 8 | comp2 << 16 | comp3 << 24);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                view.confluence$color(i, color | 0xFF << 24);
            }
        }
    }

    @ModifyReceiver(method = "bufferQuad", at= @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;addSprite(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private ChunkModelBuilder addGray(ChunkModelBuilder instance, TextureAtlasSprite textureAtlasSprite, @Local(argsOnly = true) MutableQuadViewImpl quad) {
        GraySpriteShifterEntry entry = ((IMutableQuadViewImpl) (Object) quad).confluence$getEntry();
        if (entry != null) {
            instance.addSprite(entry.gray());
            instance.addSprite(entry.negative());
        }
        return instance;
    }
}
