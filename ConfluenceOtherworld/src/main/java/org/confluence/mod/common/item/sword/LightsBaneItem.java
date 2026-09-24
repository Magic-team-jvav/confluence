package org.confluence.mod.common.item.sword;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.LightBaneProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.generation.variant.ForwardGeneration;

import java.util.Optional;

public class LightsBaneItem extends BaseSwordItem {
    public LightsBaneItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.BLUE, 11, 3, SwordDefinition.builder()
                .projectile(new SwordProjectileComponent(0.5F, 0.8F, 0.9F, 100, 0.0F, 20,
                        ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.LIGHTS_BANE.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 0.0F),
                        new SwordProjectileAppearance.Model(Confluence.asResource("enchanted_sword_projectile"),
                                Confluence.asResource("textures/entity/lights_bane.png"), 1.0F, 0.125F, -0.125F, 0.0F,
                                SwordProjectileAppearance.Lifecycle.GROW_FADE, SwordProjectileAppearance.Material.ENERGY_SWIRL)))
                .tooltipImage()
                .specialSweep(0.8F));
    }

    @Override
    protected void customizeProjectile(LivingEntity owner, ItemStack weapon, SwordProjectile projectile) {
        /// 普通斩击是半伤；按近战暴击率生成的大斩击补足另一半伤害。
        if (owner.getRandom().nextFloat() < owner.getAttributeValue(LibAttributes.getCriticalChance())) {
            projectile.addAttackDamage((float) owner.getAttributeValue(LibAttributes.getAttackDamage()) * 0.5F);
            if (projectile instanceof LightBaneProjectile slash) slash.makeBigSlash();
        }
    }
}
