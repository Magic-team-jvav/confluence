package org.confluence.mod.common.item.sword;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.component.SwordProjectileParticleEffect;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.confluence.mod.util.generation.variant.StillGeneration;
import org.confluence.mod.util.track.variant.SimpleTrack;

import java.util.List;
import java.util.Optional;

/// 剑气武器使用的完整不可变定义。
public final class SwordProjectileDefinitions {
    public static final SwordProjectileComponent ICE_BLADE = new SwordProjectileComponent(1.0F, 0.6F, 0.9F, 40, 0.0F, 15,
            ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
            model("ice_blade_sword_projectile", "ice_blade_sword_projectile.png", 1.0F, 0.0F, 0.0F),
            List.of(SwordProjectileParticleEffect.emitter(Confluence.asResource("ball_of_frost_trail"))));
    public static final SwordProjectileComponent STARFURY = new SwordProjectileComponent(1.5F, 1.5F, 0.9F, 100, 0.0F, 15,
            ModSoundEvents.STAR.getId(), ModEntities.STAR_FURY.getId(), Optional.empty(), new AboveFallenGeneration(30.0F, 30.0F, 10.0F, 1.0F, 20.0F, 5.0F),
            new SwordProjectileAppearance.Cross(Confluence.asResource("textures/entity/star_fury_projectile.png"), 0xFFFF9696, 2.0F, 18.0F, true));
    public static final SwordProjectileComponent ENCHANTED_SWORD = new SwordProjectileComponent(1.0F, 0.8F, 0.9F, 40, 0.0F, 10,
            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
            model("enchanted_sword_projectile", "enchanted_sword_projectile.png", 1.0F, 0.2F, 0.89F),
            List.of(SwordProjectileParticleEffect.emitter(Confluence.asResource("falling_star"))));
    public static final SwordProjectileComponent BLADE_OF_GRASS = new SwordProjectileComponent(0.25F, 0.8F, 0.9F, 20, 0.0F, 10,
            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.GRASS.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 20.0F),
            new SwordProjectileAppearance.Item(0.8F, SwordProjectileAppearance.Transform.FORWARD_SPIN, Optional.empty()),
            List.of(SwordProjectileParticleEffect.particle(SwordProjectileParticleEffect.Event.TRAIL, ModParticleTypes.LEAVES.get(), 2, 1, 0.0F, 0.0F)));
    public static final SwordProjectileComponent NIGHTS_EDGE = new SwordProjectileComponent(1.0F, 0.8F, 0.9F, 11, 0.0F, 10,
            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.NIGHTS_EDGE.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 20.0F),
            new SwordProjectileAppearance.Item(0.8F, SwordProjectileAppearance.Transform.OWNER_SWING, Optional.of(Confluence.asResource("nights_edge"))));
    public static final SwordProjectileComponent LIGHTS_BANE = new SwordProjectileComponent(1.0F, 0.8F, 0.9F, 12, 0.0F, 20,
            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.LIGHTS_BANE.getId(), Optional.empty(), StillGeneration.of(Vec3.ZERO),
            new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"), Confluence.asResource("textures/entity/lights_bane.png"),
                    1.0F, 0.125F, 0.0F, SwordProjectileAppearance.Lifecycle.GROW_FADE, SwordProjectileAppearance.Material.ENERGY_SWIRL));
    public static final SwordProjectileComponent DEVELOPER = new SwordProjectileComponent(1.0F, 0.3F, 1.0F, 50, 0.0F, 20,
            ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(),
            Optional.of(new SimpleTrack(Mth.HALF_PI, 0.8F, 0.2F, Optional.empty(), 0.1)), ForwardGeneration.of(0.0F, 0.0F),
            model("enchanted_sword_projectile", "enchanted_sword_projectile.png", 1.0F, 0.2F, 0.89F));

    private static SwordProjectileAppearance.Model model(String model, String texture, float scale, float offsetY, float rollSpeed) {
        return new SwordProjectileAppearance.Model(Confluence.asResource(model), Confluence.asResource("textures/entity/" + texture), scale, offsetY, rollSpeed,
                SwordProjectileAppearance.Lifecycle.GROW, SwordProjectileAppearance.Material.CUTOUT);
    }

    private SwordProjectileDefinitions() {}
}
