package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.Nymph;
import software.bernie.geckolib.core.animation.AnimationState;

/// 根据宁芙的伪装、显形和受伤状态切换对应纹理。
public final class NymphModel extends GeoNormalModel<Nymph> {
    private static final ResourceLocation NORMAL = Confluence.asResource("textures/entity/nymph.png");
    private static final ResourceLocation REVEALED = Confluence.asResource("textures/entity/nymph_dark.png");
    private static final ResourceLocation WOUNDED = Confluence.asResource("textures/entity/nymph_dark_blood.png");

    public NymphModel(ResourceLocation path) {
        super(path);
    }

    @Override
    public ResourceLocation getTextureResource(Nymph nymph) {
        if (!nymph.isTriggered()) return NORMAL;
        return nymph.getHealth() > nymph.getMaxHealth() * 0.5F ? REVEALED : WOUNDED;
    }

    @Override
    public void setCustomAnimations(Nymph nymph, long instanceId, AnimationState<Nymph> state) {
        super.setCustomAnimations(nymph, instanceId, state);
        if (nymph.isTamed() && head != null) head.setRotZ(0.0F);
    }
}
