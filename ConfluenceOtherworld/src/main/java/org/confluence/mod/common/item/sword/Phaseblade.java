package org.confluence.mod.common.item.sword;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;

public final class Phaseblade extends BasePhasebladeItem {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/item/phaseblade.geo.json");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/item/phaseblade.animation.json");
    private static final ProjectileGeometry PROJECTILE_GEOMETRY = new ProjectileGeometry(
            -2.0F / 16.0F, 1.0F / 16.0F, 0.0F, 35.0F / 16.0F,
            -2.0F / 16.0F, 1.0F / 16.0F, 24.0F / 16.0F);

    public Phaseblade(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, PhaseColor color) {
        super(tier, rarity, rawDamage, rawSpeed, color);
    }

    @Override
    public ResourceLocation modelResource() {return MODEL;}

    @Override
    public ResourceLocation animationResource() {return ANIMATION;}

    @Override
    public ProjectileGeometry projectileGeometry() {return PROJECTILE_GEOMETRY;}

    @Override
    protected String texturePrefix() {return "phaseblade";}
}
