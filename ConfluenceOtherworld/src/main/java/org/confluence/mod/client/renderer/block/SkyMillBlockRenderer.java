package org.confluence.mod.client.renderer.block;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.client.model.block.SkyMillBlockModel;
import org.confluence.mod.common.block.functional.crafting.SkyMillBlock;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SkyMillBlockRenderer extends GeoBlockRenderer<SkyMillBlock.Entity> {
    public SkyMillBlockRenderer() {
        super(new SkyMillBlockModel());
    }

    @Override
    public AABB getRenderBoundingBox(SkyMillBlock.Entity blockEntity) {
        return AABB.INFINITE;
    }
}
