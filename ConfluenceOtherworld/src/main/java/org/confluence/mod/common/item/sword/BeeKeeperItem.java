package org.confluence.mod.common.item.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.sword.BeeKeeperProjectile;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.CuriosUtils;

public class BeeKeeperItem extends EffectSwordItem {
    public BeeKeeperItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.GREEN, 18, 1.6F, SwordDefinition.builder()
                .tooltips(2)
                .tooltipImage()
                .specialSweep(0.8F), LibEffects.CONFUSED, 40, 1, 1.0F);
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        for (int index = 0; index < 3; index++) {
            BeeKeeperProjectile projectile = ModEntities.BEE.get().create(attacker.level());
            if (projectile == null) continue;
            boolean giant = CuriosUtils.hasCurio(attacker, TCItems.HIVE_PACK.get()) && attacker.getRandom().nextBoolean();
            projectile.configure(attacker, 2.0F, (float) attacker.getAttributeValue(LibAttributes.getCriticalChance()),
                    (float) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK), giant);
            projectile.setPos(victim.position().add(victim.getRandom1211().nextFloat() * 0.2F,
                    victim.getEyeHeight() * 0.5F, victim.getRandom1211().nextFloat() * 0.2F));
            attacker.level().addFreshEntity(projectile);
        }
        super.onDamage(weapon, attacker, victim, source);
    }
}
