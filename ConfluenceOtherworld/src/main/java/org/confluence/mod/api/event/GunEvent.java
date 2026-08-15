package org.confluence.mod.api.event;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.item.gun.BaseGun;

import java.util.List;

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

    /**
     * 在服务端准备一次射击动作时发布。
     *
     * <p>取消事件会阻止本次射击；监听器也可以调整成功射击后使用的冷却时间。</p>
     */
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

    /**
     * 在完成默认弹药选择后发布。
     *
     * <p>监听器可以替换本次弹药，或通过 {@link #setFire(boolean)} 决定是否允许继续射击。
     * 该事件本身不可取消，避免同时存在两套含义相同的控制方式。</p>
     */
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

    /**
     * 检查一个物品栈是否可作为当前枪械的弹药时发布。
     *
     * <p>默认判定已经写入 {@link #isSelected()}，监听器可以补充或覆盖该结果。</p>
     */
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

    /**
     * 在枪械扫描玩家弹药前发布，用于向默认物品栏列表补充其他弹药来源。
     *
     * <p>列表中的元素仍然指向真实物品栈，因此只应调整搜索顺序或增删候选项，
     * 不应复制候选物品后再期望消耗原库存。</p>
     */
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

    /**
     * 枪械与弹药数值合并完成后、生成弹丸前发布。
     *
     * <p>监听器可调整本次射击的伤害、暴击率、击退、速度、穿透与散布；修改仅作用于
     * 当前射击，不会反写枪械物品的定义或组件。</p>
     */
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

    /**
     * 弹丸成功生成后、提交弹药消耗前发布。
     *
     * <p>取消事件或将无限弹药设为 {@code true} 都会跳过消耗；监听器还可以替换实际
     * 被消耗的物品栈并调整消耗数量。</p>
     */
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
