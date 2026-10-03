package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.client.entity.model.MartianWalkerModel;
import org.confluence.mod.common.entity.monster.MartianWalker;
import org.confluence.mod.common.entity.monster.WalkerGeometry;

public final class MartianWalkerRenderer extends GeoNormalRenderer<MartianWalker> {
    public MartianWalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new MartianWalkerModel(), false, WalkerGeometry.SCALE, WalkerGeometry.OFFSET_Y);
        shadowRadius = 2.0F;
    }
    @Override public boolean shouldRender(MartianWalker walker, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        double scale = walker.getScale();
        return walker.shouldRender(cameraX,cameraY,cameraZ) && frustum.isVisible(new AABB(walker.position(),
                walker.position().add(0, 9*scale, 0)).inflate(4*scale));
    }
}
