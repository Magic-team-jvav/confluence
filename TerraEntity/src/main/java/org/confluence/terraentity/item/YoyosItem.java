package org.confluence.terraentity.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.event.YoyosThrowingEvent;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.api.item.IProjectileModifier;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.entity.proj.BaseProj;
import org.confluence.terraentity.entity.proj.YoyosEntity;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.utils.AdapterUtils;

import java.util.List;

public class YoyosItem<T extends BaseProj<?>> extends CustomRarityItem implements ILeftClickStateItem, IProjectileModifier<T> {
    final int stringColor;
    final float attackDamage;
    final float maxRange;
    final float existTime;
    final ResourceLocation texture;
    IEffectStrategy effectStrategy;

    public YoyosItem(Properties properties, ModRarity rarity, float attackDamage, int maxRange, int stringColor, float existTime, String suffix) {
        super(properties, rarity);
        this.attackDamage = attackDamage;
        this.stringColor = stringColor;
        this.texture = TerraEntity.space("textures/entity/yoyos/" + suffix + ".png");
        this.maxRange = maxRange;
        this.existTime = existTime;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public int getStringColor() {
        return stringColor;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public float getMaxRange() {
        return maxRange;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("attribute.name.generic.attack_damage").append(Component.literal(" " + attackDamage)).withStyle(s -> s.withColor(0xbbffaa)));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.yoyo.max_range").append(Component.literal(" " + maxRange)).withStyle(s -> s.withColor(0xbbffaa)));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.yoyo.exist_time").append(Component.literal(" " + existTime)).withStyle(s -> s.withColor(0xbbffaa)));
        if (effectStrategy != null) {
            IEffectStrategy.appendDescription(tooltipComponents, List.of(effectStrategy), Component.translatable("tooltip.terra_entity.yoyo.hit_effect").withStyle(s -> s.withColor(0xffbbaa)));
        }
    }


    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        WeaponStorage weaponStorage = WeaponStorage.of(player);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveLeftClick(player, itemStack);
            return;
        }
        Level level = player.level();
        YoyosEntity proj = TEProjectileEntities.YOYO_PROJ.get().create(level);
        if (proj != null) {
            player.getCooldowns().addCooldown(itemStack.getItem(), (int) (this.existTime * 20));
            weaponStorage.yoyosEntity = proj;
            proj.setPos(player.getX(), player.getY(0.5f), player.getZ());
            proj.setOwner(player);
            proj.setWeaponItem(itemStack);
            AdapterUtils.postGameEvent(new YoyosThrowingEvent(player, itemStack, proj));
            level.addFreshEntity(proj);
        }
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {
        WeaponStorage weaponStorage = WeaponStorage.of(player);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveLeftRelease(player, itemStack);
        }

    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    @Override
    public void onWhellScroll(Player player, ItemStack itemStack, int scrollAmount) {
        WeaponStorage weaponStorage = WeaponStorage.of(player);
        if (weaponStorage.yoyosEntity != null && weaponStorage.yoyosEntity.isAlive()) {
            weaponStorage.yoyosEntity.onReceiveWhellScroll(player, itemStack, scrollAmount);
        }
    }

    public YoyosItem setEffectStrategy(IEffectStrategy effectStrategy) {
        this.effectStrategy = effectStrategy;
        return this;
    }

    public IEffectStrategy getEffectStrategy() {
        return effectStrategy;
    }

    public float getExistTime() {
        return existTime;
    }


    @Override
    public void modifyProjectile(Level level, LivingEntity shooter, T projectile) {
        projectile.setDamage(attackDamage * 0.5f);
    }
}
