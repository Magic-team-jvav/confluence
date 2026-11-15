package org.confluence.mod.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;


public final class ModGunUtils {
    /// 获取玩家背包中第一个兼容该枪的子弹
    public static ItemStack getAmmo(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof BaseGun baseGun)) {
            return ItemStack.EMPTY;
        }
        Inventory inventory = player.getInventory();
        ItemStack ammo = ItemStack.EMPTY;
        NonNullList<ItemStack> stackNonNullList = inventory.items;
        List<ItemStack> copyList = new ArrayList<>(stackNonNullList);

        GunEvent.InventoryExtra inventoryExtra = new GunEvent.InventoryExtra(player, baseGun, copyList);
        NeoForge.EVENT_BUS.post(inventoryExtra);

        for (ItemStack item : inventoryExtra.getAmmoList()) {
            if (item == null || item.isEmpty() || item.is(Items.AIR)) continue;
            if (item.is(ModTags.Items.AMMO) && isCompatible(player, item, gun)) {
                ammo = item;
                break;
            }
        }
        return ammo;
    }

    /// 判断某个子弹是否与枪兼容
    public static boolean isCompatible(Player player, ItemStack ammo, ItemStack gun) {
        if (ammo.isEmpty() || !(gun.getItem() instanceof BaseGun baseGun)) {
            return false;
        }
        boolean selected = ammo.getItem() instanceof BaseBullet;
        if (gun.is(GunItems.BLOWGUN))
            selected = ammo.is(ModTags.Items.SEED_AMMO);
        if (gun.is(GunItems.SNOWBALL_CANNON))
            selected = ammo.is(ModTags.Items.SNOW_AMMO);

        GunEvent.AmmoSelection ammoSelection = new GunEvent.AmmoSelection(player, baseGun, ammo, selected);
        NeoForge.EVENT_BUS.post(ammoSelection);
        return ammoSelection.isSelected();
    }

    /// 是否可以开枪
    public static boolean canShoot(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof BaseGun baseGun)) {
            return false;
        }
        ItemStack ammo = getAmmo(player, gun);
        GunEvent.Fire fire = new GunEvent.Fire(player, baseGun, ammo, !ammo.isEmpty());
        NeoForge.EVENT_BUS.post(fire);

        return fire.isFire();
    }
}
