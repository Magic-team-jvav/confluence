package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.confluence.mod.mixed.ILivingEntityRenderer;
import org.confluence.mod.util.DeathAnimUtils;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin implements ILivingEntityRenderer, SelfGetter<LivingEntityRenderer<?,?>> {

    //    @Unique private LivingEntity confluence$rendering;
    @Unique private final List<ModelPart> confluence$partsCache = new ArrayList<>();
    @Unique private ModelPart confluence$rootModelPart;

//    @Override
//    public void confluence$setRendering(LivingEntity living){
//        confluence$rendering = living;
//    }


    @Override
    public ModelPart confluence$getRootModelPart(){
        if(confluence$rootModelPart == null){
            confluence$rootModelPart = DeathAnimUtils.findRootModelPart(self());
        }
        return confluence$rootModelPart;
    }
}
