package org.confluence.mod.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.api.IAmmo;
import org.confluence.terra_guns.api.IGun;
import org.confluence.terra_guns.common.entity.SimpleItemModelProjectile;
import org.confluence.terra_guns.common.item.gun.GeoGunItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class ManaGunItem<T extends Projectile> extends GeoGunItem<T> implements IAmmo<T> {
    private final float baseDamage;
    private final float ammoSpeed;
    private final float knockBack;
    private final float inaccuracy;
    private final int manaCost;

    public ManaGunItem(Properties properties, float baseDamage, float ammoSpeed, float knockBack, float inaccuracy, int manaCost) {
        super(properties);
        this.baseDamage = baseDamage;
        this.ammoSpeed = ammoSpeed;
        this.knockBack = knockBack;
        this.inaccuracy = Math.max(0, inaccuracy);
        this.manaCost = manaCost;
    }

    public ManaGunItem(float baseDamage, float ammoSpeed, float knockBack, float inaccuracy, int manaCost) {
        this(new Properties(), baseDamage, ammoSpeed, knockBack, inaccuracy, manaCost);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(itemInHand);
        }
        if (player instanceof ServerPlayer serverPlayer && PlayerUtils.extractMana(serverPlayer, () -> manaCost)) {
            return ItemUtils.startUsingInstantly(level, player, hand);
        }
        return InteractionResultHolder.fail(itemInHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack gunStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            ItemStack ammoStack = player.getProjectile(gunStack);
            IGun<T> gun = (IGun<T>) gunStack.getItem();
            IAmmo<T> ammo = (IAmmo<T>) ammoStack.getItem();
            if (level.isClientSide) {
                gun.clientShoot((ClientLevel) level, player, gunStack, ammoStack);
                ammo.clientShoot((ClientLevel) level, player, gunStack, ammoStack);
            } else {
                serverShoot((ServerLevel) level, player, gunStack, ammoStack, ammo, gun, true);
            }
        }
        return gunStack;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return itemStack -> itemStack.is(this);
    }

    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
        return getDefaultInstance();
    }

    @Override
    public boolean isInfinite(Player shooter, ItemStack ammoStack, ItemStack gunStack) {
        return true;
    }

    @Override
    public boolean isValidAmmo(ItemStack ammoStack) {
        return true;
    }

    @Override
    public float getAmmoSpeed(Player shooter, T projectile, ItemStack gunStack) {
        return ammoSpeed;
    }

    @Override
    public float getInaccuracy(Player shooter, T projectile, ItemStack gunStack) {
        return inaccuracy;
    }

    @Override
    public float getKnockBack() {
        return knockBack;
    }

    @Override
    public float getBaseDamage(Player shooter, T projectile, ItemStack gunStack) {
        return baseDamage;
    }

    @Override
    public float getDamageMultiplier(Player shooter, T projectile, ItemStack gunStack) {
        return 1;
    }

    @Override
    public void beforeAmmoShoot(Player shooter, T projectile, ItemStack gunStack, ItemStack ammoStack) {}

    @Override
    public void clientShoot(ClientLevel level, Player shooter, ItemStack gunStack, ItemStack ammoStack) {}

    @Override
    public void doPostHurtEffects(T projectile, Entity target) {}

    @Override
    public float getFinalDamage(float damage, Player shooter, T projectile, ItemStack gunStack, ItemStack ammoStack) {
        return damage;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!TerraGuns.IS_CONFLUENCE_LOADED) {
            tooltipComponents.add(Component.translatable("tooltip.terra_guns.mana_unsupported").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<GeoGunItem<SimpleItemModelProjectile>> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    String path = BuiltInRegistries.ITEM.getKey(ManaGunItem.this).getPath();
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("guns/" + path)));
                }
                return renderer;
            }
        });
    }
}
