package org.confluence.mod.api.event;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.combat.gun.ShotContext;
import org.confluence.mod.common.item.gun.BaseGun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GunEvent extends Event {
    private final Player player;
    private final BaseGun gun;

    public GunEvent(Player player, BaseGun gun) {
        this.player = player;
        this.gun = gun;
    }

    public BaseGun getGun() {
        return gun;
    }

    public Player getPlayer() {
        return player;
    }

    /// 初始化开火事件
    @Cancelable
    public static class UseGunEvent extends GunEvent {
        private int cooldowns;

        public UseGunEvent(Player player, BaseGun gun, int cooldowns) {
            super(player, gun);
            this.cooldowns = cooldowns;
        }

        public int getCooldowns() {
            return cooldowns;
        }

        public void setCooldowns(int cooldowns) {
            this.cooldowns = cooldowns;
        }
    }

    /// Posted on the client after the server has accepted and spawned a shot.
    public static class ShotConfirmedEvent extends Event {
        private final Player player;

        public ShotConfirmedEvent(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }
    }

    /// 开火事件
    public static class GunFireEvent extends GunEvent {
        private ItemStack bullet;
        private boolean fire;

        public GunFireEvent(Player player, BaseGun gun, ItemStack bullet, boolean fire) {
            super(player, gun);
            this.bullet = bullet;
            this.fire = fire;
        }

        public ItemStack getAmmo() {
            return bullet;
        }

        public void setAmmo(ItemStack bullet) {
            this.bullet = bullet;
        }

        public boolean isFire() {
            return fire;
        }

        public void setFire(boolean fire) {
            this.fire = fire;
        }
    }

    /// 初始化开火事件
    public static class AmmoSelectionEvent extends GunEvent {
        private final ItemStack ammo;
        private boolean selected;

        public AmmoSelectionEvent(Player player, BaseGun gun, ItemStack ammo, boolean selected) {
            super(player, gun);
            this.ammo = ammo;
            this.selected = selected;
        }

        public ItemStack getAmmo() {
            return ammo;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    /// 初始化开火事件
    public static class InventoryExtraEvent extends GunEvent {
        private final List<ItemStack> ammoList;

        public InventoryExtraEvent(Player player, BaseGun gun, List<ItemStack> ammoList) {
            super(player, gun);
            this.ammoList = ammoList;
        }

        public List<ItemStack> getAmmoList() {
            return ammoList;
        }

        public void addBulletFirst(ItemStack bullet) {
            PortListExtension.addFirst(ammoList, bullet);
        }

        public void addBulletLast(ItemStack bullet) {
            PortListExtension.addLast(ammoList, bullet);
        }

        public void addAmmoFirst(List<ItemStack> ammo) {
            this.ammoList.addAll(0, ammo);
        }

        public void addAmmoLast(List<ItemStack> ammo) {
            this.ammoList.addAll(ammo);
        }
    }

    /// 射击时，子弹数据计算事件
    public static class AmmoDataEvent extends GunEvent {
        private float damage;
        private float critical;
        private float knockback;
        private float velocity;
        private int penetrate;
        private float inaccuracy;
        private final ItemStack gunStack;

        public AmmoDataEvent(Player player, BaseGun gun, ItemStack gunStack, float damage, float critical, float knockback, float velocity, int penetrate, float inaccuracy) {
            super(player, gun);
            this.gunStack = gunStack;
            this.critical = critical;
            this.damage = damage;
            this.knockback = knockback;
            this.velocity = velocity;
            this.penetrate = penetrate;
            this.inaccuracy = inaccuracy;
        }

        public ItemStack getGunStack() {
            return gunStack;
        }

        public float getDamage() {
            return damage;
        }

        public float getCritical() {
            return critical;
        }

        public float getKnockback() {
            return knockback;
        }

        public float getVelocity() {
            return velocity;
        }

        public int getPenetrate() {
            return penetrate;
        }

        public void setDamage(float damage) {
            this.damage = damage;
        }

        public void setCritical(float critical) {
            this.critical = critical;
        }

        public void setKnockback(float knockback) {
            this.knockback = knockback;
        }

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        public void setPenetrate(int penetrate) {
            this.penetrate = penetrate;
        }

        public float getInaccuracy() {
            return inaccuracy;
        }

        public void setInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
        }
    }

    /// Posted after the default projectiles are selected and before their
    /// common launch data is applied.
    ///
    /// Listeners may replace the list with arbitrary projectile entities.
    /// This supports guns whose projectiles are entities rather than registered
    /// {@code BaseBullet} items while keeping spawning server-authoritative.
    public static class ProjectileCreationEvent extends GunEvent {
        private final ShotContext context;
        private final List<Projectile> projectiles;

        public ProjectileCreationEvent(BaseGun gun, ShotContext context, Collection<? extends Projectile> projectiles) {
            super(context.shooter(), gun);
            this.context = context;
            this.projectiles = new ArrayList<>(projectiles);
        }

        public ShotContext getContext() {
            return context;
        }

        public List<Projectile> getProjectiles() {
            return projectiles;
        }

        public void setProjectiles(Collection<? extends Projectile> projectiles) {
            List<Projectile> replacements = new ArrayList<>(Objects.requireNonNull(projectiles, "projectiles"));
            replacements.removeIf(Objects::isNull);
            this.projectiles.clear();
            this.projectiles.addAll(replacements);
        }
    }

    /// 子弹消耗事件
    @Cancelable
    public static class ShrinkBulletEvent extends GunEvent {
        private int shrink = 1;
        private boolean infinity;
        private ItemStack bullet;
        private final ItemStack gun;

        public ShrinkBulletEvent(Player player, BaseGun baseGun, ItemStack gun, ItemStack bullet, boolean infinity) {
            super(player, baseGun);
            this.gun = gun;
            this.infinity = infinity;
            this.bullet = bullet;
        }

        public void setShrink(int shrink) {
            this.shrink = shrink;
        }

        public int getShrink() {
            return shrink;
        }

        public boolean isInfinity() {
            return infinity;
        }

        public void setInfinity(boolean infinity) {
            this.infinity = infinity;
        }

        public ItemStack getBulletStack() {
            return bullet;
        }

        public void setBulletStack(ItemStack bullet) {
            this.bullet = bullet;
        }

        public ItemStack getGunStack() {
            return gun;
        }
    }
}
