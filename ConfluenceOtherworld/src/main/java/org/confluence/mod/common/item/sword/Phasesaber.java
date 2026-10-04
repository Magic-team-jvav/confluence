package org.confluence.mod.common.item.sword;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;

public final class Phasesaber extends BasePhasebladeItem {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/item/phasesaber.geo.json");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/item/phasesaber.animation.json");
    private static final ProjectileGeometry PROJECTILE_GEOMETRY = new ProjectileGeometry(
            -3.0F / 16.0F, 4.0F / 16.0F, -1.0F / 16.0F, 32.0F / 16.0F,
            -2.0F / 16.0F, 2.0F / 16.0F, 22.0F / 16.0F);

    public Phasesaber(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, PhaseColor color) {
        super(tier, rarity, rawDamage, rawSpeed, color);
    }

    @Override
    public ResourceLocation modelResource() {return MODEL;}

    @Override
    public ResourceLocation animationResource() {return ANIMATION;}

    @Override
    public ProjectileGeometry projectileGeometry() {return PROJECTILE_GEOMETRY;}

    @Override
    protected String texturePrefix() {return "phasesaber";}
}
