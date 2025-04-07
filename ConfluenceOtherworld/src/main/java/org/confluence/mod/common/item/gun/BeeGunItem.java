package org.confluence.mod.common.item.gun;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terra_curio.common.entity.projectile.BeeProjectile;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_guns.api.IAmmo;
import org.confluence.terra_guns.api.IGun;

public class BeeGunItem extends ManaGunItem<BeeProjectile> {
    public BeeGunItem() {
        super(ModRarity.GREEN, 1.8F, 32, 12, 0, 0.04F, 1.5F, 5);
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(Confluence.asResource("bee_gun"), 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public BeeProjectile createAmmo(Level level, Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getVelocityMultiplier(Player shooter, BeeProjectile ammoEntity, ItemStack gunStack) {
        return 1;
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

        notTheBees(player);
    }

    private static void notTheBees(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean("confluence:not_the_bees")) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.BEE_HELMET.get()) ||
                    player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.BEE_CHESTPLATE.get()) ||
                    player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.BEE_LEGGINGS.get()) ||
                    player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.BEE_BOOTS.get())) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(Confluence.asResource("achievements/not_the_bees"));
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement, "never");
                }
                data.putBoolean("confluence:not_the_bees", true);
            }
        }
    }

    @Override
    public float getRealAmmoSpeed(Player player, BeeProjectile projectile, ItemStack gunStack, ItemStack ammoStack) {
        return 0.4F;
    }

}
