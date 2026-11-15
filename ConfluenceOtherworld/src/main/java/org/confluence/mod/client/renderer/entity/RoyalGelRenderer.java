package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.client.model.entity.QueenSlimePartModel;
import org.confluence.mod.common.entity.boss.RoyalGel;

// 皇家凝胶与弹力凝胶使用各自的模型和贴图。
public class RoyalGelRenderer extends GeoNormalRenderer<RoyalGel> {
    public RoyalGelRenderer(EntityRendererProvider.Context context, String name) {
        super(context, new QueenSlimePartModel<>(name), false, 1.0F, 0.0F);
    }
}
