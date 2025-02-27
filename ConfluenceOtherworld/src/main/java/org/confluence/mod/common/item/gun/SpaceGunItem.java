package org.confluence.mod.common.item.gun;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_guns.api.IGun;
import org.confluence.terra_guns.common.entity.SimpleTrailProjectile;
import org.confluence.terra_guns.common.init.TGSoundEvents;

public class SpaceGunItem extends ManaGunItem<SimpleTrailProjectile> {
    public SpaceGunItem() {
        super(ModRarity.BLUE, 3.4F, 0, 0.75F, 0, 6);
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("space_gun"), 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public SimpleTrailProjectile createAmmo(Level level, Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        return new SimpleTrailProjectile(shooter, 0x99FF00);
    }

    @Override
    public float getRealAmmoSpeed(Player player, SimpleTrailProjectile projectile, ItemStack gunStack, ItemStack ammoStack) {
        return 3.75F;
    }

    @Override
    protected int getUseDelay(Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        return 8;
    }

    @Override
    public void beforeAmmoShoot(Player shooter, SimpleTrailProjectile projectile, ItemStack gunStack, ItemStack ammoStack) {
        float damage = ((IGun<SimpleTrailProjectile>) gunStack.getItem()).getGunDamage(shooter, projectile, gunStack, ammoStack);
        projectile.damageAndKnockback(getFinalDamage(damage, shooter, projectile, gunStack, ammoStack), getKnockBack());
    }

    @Override
    protected SoundEvent getShotSound() {
        return TGSoundEvents.LASER.get();
    }
}
