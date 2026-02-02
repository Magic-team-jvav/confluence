package org.confluence.terra_guns.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.common.component.BulletPropertyComponent;
import org.confluence.terra_guns.common.component.GunPropertyComponent;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.init.TGDataComponents;
import org.confluence.terra_guns.impl.AmmoDataContext;
import org.confluence.terra_guns.util.AnimUtil;
import org.confluence.terra_guns.util.TGUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class BaseGun extends Item implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final GunPropertyComponent component;
    protected final ArrayList<Projectile> baseBulletEntities = new ArrayList<>();
    protected final float inaccuracy;

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity) {
        super(properties.stacksTo(1));
        GunPropertyComponent component = new GunPropertyComponent(cooldown, damage, velocity, knockback, critical, penetrate, rarity);
        properties.component(TGDataComponents.GUN_PROPERTY_COMPONENT.get(), component);

        this.components = Properties.COMPONENT_INTERNER.intern(properties.components.build());
        this.component = component;
        this.inaccuracy = inaccuracy;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, float inaccuracy, ModRarity rarity) {
        this(properties, cooldown, damage, velocity, knockback, critical, 0, inaccuracy, rarity);
    }

    public void shoot(ServerPlayer player, ItemStack bullet, ItemStack gun) {
        ServerLevel serverLevel = player.serverLevel();
        BulletPropertyComponent bulletComponent = bullet.get(TGDataComponents.BULLET_PROPERTY_COMPONENT);
        if (bulletComponent == null) bulletComponent = BulletPropertyComponent.EMPTY;

        AmmoDataContext ammoDataContext = new AmmoDataContext(this.component, bulletComponent, inaccuracy);
        GunEvent.AmmoDataEvent ammoDataEvent = new GunEvent.AmmoDataEvent(player, this, gun, ammoDataContext.getDamage(), ammoDataContext.getCritical(), ammoDataContext.getKnockback(), ammoDataContext.getVelocity(), ammoDataContext.getPenetrate(), ammoDataContext.getInaccuracy());
        NeoForge.EVENT_BUS.post(ammoDataEvent);

        float finalDamage = TGUtil.criticalDamageTotal(ammoDataEvent.getCritical(), ammoDataEvent.getDamage(), player.getRandom());
        prepareBulletEntity(baseBulletEntities, player, bullet, gun, finalDamage, ammoDataEvent.getKnockback(), ammoDataEvent.getVelocity(), ammoDataEvent.getPenetrate(), ammoDataEvent.getInaccuracy());
        baseBulletEntities.forEach(serverLevel::addFreshEntity);
        baseBulletEntities.clear();
    }

    protected BaseBulletEntity createBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        return new BaseBulletEntity(player, bullet);
    }

    protected void prepareBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        BaseBulletEntity baseBulletEntity = createBulletEntity(baseBulletEntities, player, bullet, gun, damage, knockback, velocity, penetrate, inaccuracy);

        baseBulletEntity.setColorID(((BaseGun) gun.getItem()).getColorID());
        baseBulletEntity.damage = damage;
        baseBulletEntity.knockback = knockback;
        baseBulletEntity.penetrate = penetrate;
        baseBulletEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, velocity, inaccuracy);

        baseBulletEntities.add(baseBulletEntity);
    }

    public String getColorID() {
        return "";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.critical", String.format("%.1f", component.critical() * 100)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
    }

    public int getCooldown() {
        return component.cooldown();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<BaseGun> gun = new AnimationController<>(this, "gun", state -> PlayState.CONTINUE);
        gun.triggerableAnim("gun_fire", RawAnimation.begin().then("fire", Animation.LoopType.DEFAULT));
        gun.triggerableAnim("gun_pick", RawAnimation.begin().then("pick up", Animation.LoopType.DEFAULT));
        gun.triggerableAnim("gun_reload", RawAnimation.begin().then("reloading", Animation.LoopType.DEFAULT));
        controllers.add(gun);
    }

    public void fireAnimator(ItemStack itemStack, ServerPlayer serverPlayer) {
        AnimUtil.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_fire");
    }

    public void pickAnimator(ItemStack itemStack, ServerPlayer serverPlayer) {
        AnimUtil.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_pick");
    }

    public void reloadAnimator(ItemStack itemStack, ServerPlayer serverPlayer) {
        AnimUtil.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_reload");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }
}
