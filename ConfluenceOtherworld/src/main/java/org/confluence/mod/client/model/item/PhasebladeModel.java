package org.confluence.mod.client.model.item;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.item.sword.Phasesaber;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class PhasebladeModel extends GeoModel<BasePhasebladeItem> {
    private final boolean forceBladeOn;

    public PhasebladeModel(boolean forceBladeOn) {
        this.forceBladeOn = forceBladeOn;
    }

    @Override
    public ResourceLocation getModelResource(BasePhasebladeItem item) {
        return item.modelResource();
    }

    @Override
    public ResourceLocation getTextureResource(BasePhasebladeItem item) {
        return item.textureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(BasePhasebladeItem item) {
        return item.animationResource();
    }

    @Override
    public void setCustomAnimations(BasePhasebladeItem item, long instanceId, AnimationState<BasePhasebladeItem> state) {
        super.setCustomAnimations(item, instanceId, state);
        String prefix = item instanceof Phasesaber ? "phasesaber" : "phaseblade";
        GeoBone blade = getAnimationProcessor().getBone(prefix);
        GeoBone outer = getAnimationProcessor().getBone(prefix + "_outer");
        GeoBone middle = getAnimationProcessor().getBone(prefix + "_middle");
        GeoBone core = getAnimationProcessor().getBone(prefix + "_core");
        if (blade == null || outer == null || middle == null || core == null) return;

        if (forceBladeOn) {
            blade.setScaleY(1.0F);
            outer.setScaleY(1.0F);
            middle.setScaleY(1.0F);
            core.setScaleY(1.0F);
        }

        double time = state.getAnimationTick();
        applyVibration(outer, instanceId, time, 0, 0.10F, 0.025F);
        applyVibration(middle, instanceId, time, 1, 0.075F, 0.018F);
        applyVibration(core, instanceId, time, 2, 0.045F, 0.012F);
    }

    private static void applyVibration(GeoBone bone, long instanceId, double time, int layer, float horizontalAmplitude, float verticalAmplitude) {
        double basePhase = phase(instanceId, layer);
        bone.setPosX((float) Math.sin(time * 5.7D + basePhase) * horizontalAmplitude);
        bone.setPosY((float) Math.sin(time * 7.9D + basePhase * 1.37D) * verticalAmplitude);
        bone.setPosZ((float) Math.sin(time * 6.8D + basePhase * 1.91D) * horizontalAmplitude);
    }

    private static double phase(long instanceId, int layer) {
        long value = instanceId ^ layer * 0xD1B54A32D192ED03L;
        value = (value ^ value >>> 30) * 0xBF58476D1CE4E5B9L;
        value = (value ^ value >>> 27) * 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return ((value >>> 40) & 0xFFFFFFL) / 16777216.0D * Math.PI * 2.0D;
    }
}
