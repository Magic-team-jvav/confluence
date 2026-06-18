package org.confluence.mod.common.item.bow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class TendonBow extends BaseTerraBowItem {
    public TendonBow() {
        super(5.2F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(ModEntities.BASE_ARROW.get(), shooter, ammo, weapon) {
            @Override
            protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
                if (!fullPull) return;
                target.addEffect(new MobEffectInstance(ModEffects.BLOOD_BUTCHERED.getDelegate(), 180, 4));
            }
        };
    }
}
