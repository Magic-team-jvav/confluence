package org.confluence.mod.client.summoner.renderer.projectile;

import net.minecraft.util.FastColor;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.client.summoner.SimpleRenderer;
import org.confluence.mod.common.summoner.projectile.EyeFireball;

public class EyeFireballRenderer extends SimpleRenderer<EyeFireball> {

    public EyeFireballRenderer() {
        super((fireball, poseStack, bufferSource, visualNode, context, partialTick, alpha) -> RenderUtil.renderImage(
                Confluence.asResource("textures/entity/summon/eye_laser_turret_fireball.png"),
                poseStack,
                0.5F,
                0.5F,
                bufferSource,
                true,
                FastColor.ARGB32.color((int) (alpha * 255.0F), 255, 255, 255)
        ));
    }
}
