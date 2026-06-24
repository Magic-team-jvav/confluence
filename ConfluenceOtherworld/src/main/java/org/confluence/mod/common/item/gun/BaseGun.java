package org.confluence.mod.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.CustomBulletEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.util.AmmoDataContext;
import org.confluence.mod.util.ModGeckoLibUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class BaseGun extends Item implements GeoItem {
    public interface BulletEntityFactory {
        BaseBulletEntity create(ServerPlayer player, ItemStack bullet);
    }

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final GunPropertyComponent component;
    protected final ArrayList<Projectile> baseBulletEntities = new ArrayList<>();
    protected final float inaccuracy;
    protected final float gravity;
    protected final int minBullets;
    protected final int maxBullets;
    protected final int manaCost;
    protected final BulletEntityFactory bulletEntityFactory;

    public BaseGun(Builder builder) {
        super(builder.buildProperties());
        this.component = new GunPropertyComponent(builder.cooldown, builder.damage, builder.velocity, builder.knockback, builder.critical, builder.penetrate, builder.rarity);
        this.inaccuracy = builder.inaccuracy;
        this.gravity = builder.gravity;
        this.minBullets = builder.minBullets;
        this.maxBullets = builder.maxBullets;
        this.manaCost = builder.manaCost;
        this.bulletEntityFactory = builder.bulletEntityFactory;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public void shoot(ServerPlayer player, ItemStack bullet, ItemStack gun) {
        ServerLevel serverLevel = player.serverLevel();
        BulletPropertyComponent bulletComponent = bullet.get(ModDataComponentTypes.BULLET_PROPERTY);
        if (bulletComponent == null) bulletComponent = BulletPropertyComponent.EMPTY;

        AmmoDataContext ammoDataContext = new AmmoDataContext(this.component, bulletComponent, inaccuracy);
        GunEvent.AmmoDataEvent ammoDataEvent = new GunEvent.AmmoDataEvent(player, this, gun, ammoDataContext.getDamage(), ammoDataContext.getCritical(), ammoDataContext.getKnockback(), ammoDataContext.getVelocity(), ammoDataContext.getPenetrate(), ammoDataContext.getInaccuracy());
        PortEventHandler.postEvent(ammoDataEvent);

        prepareBulletEntity(baseBulletEntities, player, bullet, gun, ammoDataEvent.getDamage(), ammoDataEvent.getKnockback(), ammoDataEvent.getVelocity(), ammoDataEvent.getPenetrate(), ammoDataEvent.getInaccuracy());
        baseBulletEntities.forEach(serverLevel::addFreshEntity);
        baseBulletEntities.clear();
    }

    protected BaseBulletEntity createBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        if (bulletEntityFactory != null) {
            return bulletEntityFactory.create(player, bullet);
        }
        if (gravity != 0) {
            return new CustomBulletEntity(player, gravity, bullet);
        }
        return new BaseBulletEntity(player, bullet);
    }

    protected void prepareBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        int times = maxBullets > 1 ? ThreadLocalRandom.current().nextInt(minBullets, maxBullets + 1) : 1;

        IntStream.range(0, times).forEach(i -> {
            BaseBulletEntity baseBulletEntity = createBulletEntity(baseBulletEntities, player, bullet, gun, damage, knockback, velocity, penetrate, inaccuracy);

            baseBulletEntity.colorID(((BaseGun) gun.getItem()).getColorID());
            baseBulletEntity.damage = damage;
            baseBulletEntity.knockback = knockback;
            baseBulletEntity.penetrate = penetrate;
            baseBulletEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, velocity, inaccuracy);

            baseBulletEntities.add(baseBulletEntity);
        });
    }

    public int getManaCost() {
        return manaCost;
    }

    public String getColorID() {
        return "";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
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
        ModGeckoLibUtils.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_fire");
    }

    public void pickAnimator(ItemStack itemStack, ServerPlayer serverPlayer) {
        ModGeckoLibUtils.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_pick");
    }

    public void reloadAnimator(ItemStack itemStack, ServerPlayer serverPlayer) {
        ModGeckoLibUtils.stopAndPlayAnim(this, itemStack, serverPlayer, "gun", "gun_reload");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    public static class Builder {
        private final int cooldown;
        private final float damage;
        private final float velocity;
        private float knockback = 0;
        private float critical = 0;
        private int penetrate = 0;
        private float inaccuracy = 0;
        private ModRarity rarity = ModRarity.WHITE;
        private float gravity = 0;
        private int minBullets = 1;
        private int maxBullets = 1;
        private int manaCost = 0;
        private BulletEntityFactory bulletEntityFactory = null;
        private Item.Properties properties = new Properties();

        public Builder(int cooldown, float damage, float velocity) {
            this.cooldown = cooldown;
            this.damage = damage;
            this.velocity = velocity;
        }

        public Builder knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public Builder critical(float critical) {
            this.critical = critical;
            return this;
        }

        public Builder penetrate(int penetrate) {
            this.penetrate = penetrate;
            return this;
        }

        public Builder inaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
            return this;
        }

        public Builder rarity(ModRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder bullets(int min, int max) {
            this.minBullets = min;
            this.maxBullets = max;
            return this;
        }

        public Builder manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public Builder bulletFactory(BulletEntityFactory factory) {
            this.bulletEntityFactory = factory;
            return this;
        }

        public Builder properties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        private Item.Properties buildProperties() {
            GunPropertyComponent component = new GunPropertyComponent(cooldown, damage, velocity, knockback, critical, penetrate, rarity);
            properties.component(ModDataComponentTypes.GUN_PROPERTY, component);
            return properties.stacksTo(1);
        }

        public BaseGun build() {
            return new BaseGun(this);
        }
    }
}
