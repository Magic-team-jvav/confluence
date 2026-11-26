package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.item.sword.SwordProjectileAppearance;

/// 神圣之星的渲染：与星怒/星泣的坠星完全同一套参数（缩放 2.0、自转 18°/刻、方块光 10），只换贴图。
public class HallowStarRenderer<T extends HomingFallingStarProjectile> extends FallingStarCrossRenderer<T> {
    public static final SwordProjectileAppearance.Cross APPEARANCE = new SwordProjectileAppearance.Cross(
            Confluence.asResource("textures/entity/projectile/hallow_star_projectile.png"),
            0xFFFFFFFF, 2.0F, 18.0F, 10);

    public HallowStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SwordProjectileAppearance.Cross appearance() {
        return APPEARANCE;
    }
}
