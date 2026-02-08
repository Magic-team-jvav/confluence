package org.confluence.mod.common.item.gun;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.BeeGunBullet;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BeeGunItem extends ManaGunItem {
    public BeeGunItem(Properties properties) {
        super(properties, 4, 4.6f, 1, 0.01f, 0.04f, 2, 1.5F, ModRarity.GREEN, 5);
    }

    @Override
    public void shoot(ServerPlayer player, ItemStack bullet, ItemStack gunStack) {
        super.shoot(player, bullet, gunStack);
        notTheBees(player);
    }

    @Override
    protected void prepareBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        boolean hasHivePack = TCUtils.hasType(player, TCItems.HIVE$PACK);
        int times = ThreadLocalRandom.current().nextInt(1, hasHivePack ? 5 : 4);
        for (int i = 0; i < times; i++) {
            BeeGunBullet beeGunBullet = new BeeGunBullet(player.serverLevel(), player, hasHivePack && player.getRandom().nextBoolean());
            beeGunBullet.setPos(player.getX(), player.getEyeY(), player.getZ());
            beeGunBullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
            baseBulletEntities.add(beeGunBullet);
        }
    }

    private static void notTheBees(Player player) {
        CompoundTag data = LibUtils.getOrCreatePersistedData(player);
        if (!data.getBoolean("confluence:not_the_bees")) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.BEE_HELMET.get()) ||
                    player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.BEE_CHESTPLATE.get()) ||
                    player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.BEE_LEGGINGS.get()) ||
                    player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.BEE_BOOTS.get())) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(AchievementUtils.asAchievement("not_the_bees"));
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement, "never");
                }
                data.putBoolean("confluence:not_the_bees", true);
            }
        }
    }
}
