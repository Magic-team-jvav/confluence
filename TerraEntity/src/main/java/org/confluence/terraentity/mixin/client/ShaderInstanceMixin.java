package org.confluence.terraentity.mixin.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.confluence.terraentity.mixed.IShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin implements IShaderInstance {
    @Shadow @Nullable public abstract Uniform getUniform(String name);

    @Unique private  Uniform terra_entity$TEST;
    public Uniform getTerra_entity$TEST(){return terra_entity$TEST;}
    @Unique private  Uniform terra_entity$Time;
    public Uniform getTerra_entity$Time(){return terra_entity$Time;}
    @Unique private  Uniform terra_entity$Radius;
    public Uniform getTerra_entity$Radius(){return terra_entity$Radius;}
    @Unique private  Uniform terra_entity$Progress;
    public Uniform getTerra_entity$Progress(){return terra_entity$Progress;}
    @Unique private  Uniform terra_entity$Distance;
    public Uniform getTerra_entity$Distance(){return terra_entity$Distance;}
    @Unique private  Uniform terra_entity$PixelSize;
    public Uniform getTerra_entity$PixelSize(){return terra_entity$PixelSize;}
    @Inject(method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V", at = @At("RETURN"))
    public void ShaderInstance(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_, CallbackInfo ci) {

        terra_entity$TEST = this.getUniform("TEST");
        terra_entity$Time = this.getUniform("Time");
        terra_entity$Radius = this.getUniform("Radius");
        terra_entity$Progress = this.getUniform("Progress");
        terra_entity$Distance = this.getUniform("Distance");
        terra_entity$PixelSize = this.getUniform("PixelSize");
    }


}
