package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.entity.projectile.BeeProjectile;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_guns.api.IAmmo;
import org.confluence.terra_guns.api.IGun;

public class BeeGunItem extends ManaGunItem<BeeProjectile> {
    public BeeGunItem() {
        super(ModRarity.GREEN, 1.8F, 0, 0.25F, 1.5F, 5);
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("bee_gun"), 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public BeeProjectile createAmmo(Level level, Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serverShoot(ServerLevel level, Player player, ItemStack gunStack, ItemStack ammoStack, IAmmo<BeeProjectile> ammo, IGun<BeeProjectile> gun, boolean infiniteAmmo) {
        boolean hasHivePack = TCUtils.hasAccessoriesType(player, TCItems.HIVE$PACK);
        RandomSource random = player.getRandom();
        int amount = hasHivePack ? random.nextInt(1, 7) : random.nextInt(1, 4);
        if (random.nextFloat() < 0.1667F) amount++;
        if (random.nextFloat() < 0.1667F) amount++;
        if (hasHivePack && random.nextFloat() < 0.3333F) amount++;

        for (int i = 0; i < amount; i++) {
            BeeProjectile projectile = new BeeProjectile(level, player, hasHivePack && random.nextBoolean());
            projectile.setPos(player.getX(), player.getEyeY(), player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, getRealAmmoSpeed(player, projectile, gunStack, ammoStack), getInaccuracy(player, projectile, gunStack, ammoStack));

            ammo.beforeAmmoShoot(player, projectile, gunStack, ammoStack);
            if (level.addFreshEntity(projectile)) {
                gun.afterGunShoot(gunStack, player);
                if (!infiniteAmmo) {
                    ammo.afterAmmoShoot(ammoStack, player);
                }
            }
        }
        player.getCooldowns().addCooldown(this, getUseDelay(player, gunStack, ammoStack));
    }

    @Override
    public float getRealAmmoSpeed(Player player, BeeProjectile projectile, ItemStack gunStack, ItemStack ammoStack) {
        return 0.4F;
    }

    @Override
    protected int getUseDelay(Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        return 12;
    }
}
