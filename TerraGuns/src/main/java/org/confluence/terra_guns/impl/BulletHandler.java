package org.confluence.terra_guns.impl;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terra_guns.common.item.bullet.BaseBullet;
import org.confluence.terra_guns.common.item.gun.BaseGun;

import java.util.ArrayList;
import java.util.List;

public class BulletHandler {
    /**
     * 获取玩家背包中第一个兼容该枪的子弹
     */
    public static ItemStack getAmmo(Player player, ItemStack gun) {
        Inventory inventory = player.getInventory();
        ItemStack ammo = ItemStack.EMPTY;
        NonNullList<ItemStack> stackNonNullList = inventory.items;
        List<ItemStack> copyList = new ArrayList<>(stackNonNullList);

        GunEvent.InventoryExtraEvent inventoryExtraEvent = new GunEvent.InventoryExtraEvent(player, (BaseGun) gun.getItem(), copyList);
        NeoForge.EVENT_BUS.post(inventoryExtraEvent);

        for (ItemStack item : inventoryExtraEvent.getAmmoList()) {
            if (item == null || item.is(Items.AIR)) continue;
            if (item.is(TGTags.AMMO) && isCompatible(player, item, gun)) {
                ammo = item;
                break;
            }
        }
        return ammo;
    }

    /**
     * 判断某个子弹是否与枪兼容
     */
    public static boolean isCompatible(Player player, ItemStack ammo, ItemStack gun) {
        boolean selected = ammo.getItem() instanceof BaseBullet;
        if (gun.is(TGItems.BLOWGUN)) selected = ammo.is(TGTags.SEED_AMMO);
        if (gun.is(TGItems.SNOWBALL_CANNON)) selected = ammo.is(TGTags.SNOW_AMMO);

        GunEvent.AmmoSelectionEvent ammoSelectionEvent = new GunEvent.AmmoSelectionEvent(player, (BaseGun) gun.getItem(), ammo, selected);
        NeoForge.EVENT_BUS.post(ammoSelectionEvent);
        return ammoSelectionEvent.isSelected();
    }

    /**
     * 是否可以开枪
     */
    public static boolean canShoot(Player player, ItemStack gun) {
        ItemStack ammo = getAmmo(player, gun);
        GunEvent.GunFireEvent gunFireEvent = new GunEvent.GunFireEvent(player, (BaseGun) gun.getItem(), ammo, !ammo.isEmpty());
        NeoForge.EVENT_BUS.post(gunFireEvent);

        return gunFireEvent.isFire();
    }
}
