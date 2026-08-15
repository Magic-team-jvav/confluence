package org.confluence.mod.util;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.ArrayList;
import java.util.List;

public final class ModGunUtils {
    public static void stopAndPlayAnim(GeoItem geoItem, ItemStack itemStack, ServerPlayer serverPlayer, @Nullable String controllerName, @Nullable String animName) {
        if (controllerName == null || animName == null) return;
        long orAssignId = GeoItem.getOrAssignId(itemStack, serverPlayer.serverLevel());
        AnimatableManager<GeoAnimatable> animatableManager = geoItem.getAnimatableInstanceCache().getManagerForId(orAssignId);
        AnimationController<GeoAnimatable> gunController = animatableManager.getAnimationControllers().get(controllerName);
        if (gunController == null) return;

        if (gunController.isPlayingTriggeredAnimation()) {
            geoItem.stopTriggeredAnim(serverPlayer, orAssignId, controllerName, animName);
        }
        geoItem.triggerAnim(serverPlayer, orAssignId, controllerName, animName);
    }

    /// 获取玩家背包中第一个兼容该枪的子弹
    public static ItemStack getAmmo(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof BaseGun baseGun)) {
            return ItemStack.EMPTY;
        }
        Inventory inventory = player.getInventory();
        ItemStack ammo = ItemStack.EMPTY;
        NonNullList<ItemStack> stackNonNullList = inventory.items;
        List<ItemStack> copyList = new ArrayList<>(stackNonNullList);

        GunEvent.InventoryExtraEvent inventoryExtraEvent = new GunEvent.InventoryExtraEvent(player, baseGun, copyList);
        PortEventHandler.postEvent(inventoryExtraEvent);

        for (ItemStack item : inventoryExtraEvent.getAmmoList()) {
            if (item == null || item.is(Items.AIR)) continue;
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

        GunEvent.AmmoSelectionEvent ammoSelectionEvent = new GunEvent.AmmoSelectionEvent(player, baseGun, ammo, selected);
        PortEventHandler.postEvent(ammoSelectionEvent);
        return ammoSelectionEvent.isSelected();
    }

    /// 是否可以开枪
    public static boolean canShoot(Player player, ItemStack gun) {
        if (!(gun.getItem() instanceof BaseGun baseGun)) {
            return false;
        }
        ItemStack ammo = getAmmo(player, gun);
        GunEvent.GunFireEvent gunFireEvent = new GunEvent.GunFireEvent(player, baseGun, ammo, !ammo.isEmpty());
        PortEventHandler.postEvent(gunFireEvent);

        return gunFireEvent.isFire();
    }
}
