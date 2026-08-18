package org.confluence.mod.common.item.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.mana.BeeGunBullet;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.wrapper.advancements.PortAdvancementHolder;

import java.util.ArrayList;
import java.util.List;

public class BeeGunItem extends ManaGunItem {
    public BeeGunItem(Properties properties) {
        super(properties, 4, 4.6f, 1, 0.01f, 0.04f, 2, 1.5F, ModRarity.GREEN, 5);
    }

    public List<Projectile> createProjectiles(ServerPlayer player) {
        boolean hivePack = TCUtils.hasType(player, TCItems.HIVE$PACK);
        int count = 1 + player.getRandom().nextInt(hivePack ? 4 : 3);
        List<Projectile> projectiles = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            projectiles.add(new BeeGunBullet(player.serverLevel(), player,
                    hivePack && player.getRandom().nextBoolean()));
        }
        notTheBees(player);
        return projectiles;
    }

    private static void notTheBees(Player player) {
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        if (!data.getBoolean("confluence:not_the_bees")) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.BEE_HELMET.get()) ||
                    player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.BEE_CHESTPLATE.get()) ||
                    player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.BEE_LEGGINGS.get()) ||
                    player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.BEE_BOOTS.get())) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                PortAdvancementHolder advancement = PortAdvancementHolder.wrap(
                        serverPlayer.server.getAdvancements().getAdvancement(
                                AchievementUtils.asAchievement("not_the_bees")));
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement.value(), "never");
                }
                data.putBoolean("confluence:not_the_bees", true);
            }
        }
    }
}
