package org.confluence.mod.common.item.common;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.attachment.WeaponStorage;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class LanceItem extends TooltipItem implements ILeftClickStateItem, GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int attackInterval;
    private final double attackDistance;
    private final double baseAttackDamage;
    private final double baseKnockback;

    public LanceItem(Properties properties, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback, List<Component> tooltips) {
        super(properties.stacksTo(1), rarity, tooltips);
        this.attackInterval = attackInterval;
        this.attackDistance = attackDistance;
        this.baseAttackDamage = baseAttackDamage;
        this.baseKnockback = baseKnockback;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        if (!player.level().isClientSide) {
            triggerAnim(player, GeoItem.getOrAssignId(itemStack, (ServerLevel) player.level()), "lance", "use");
        }
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {
        if (!player.level().isClientSide) {
            stopTriggeredAnim(player, GeoItem.getOrAssignId(itemStack, (ServerLevel) player.level()), "lance", "use");
        }
    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity instanceof ServerPlayer owner) {
            long gameTime = owner.level().getGameTime();
            if (WeaponStorage.of(owner).leftClicking && (attackInterval <= 1 || gameTime % attackInterval == 0)) {
                Vec3 viewVector = owner.getViewVector(1.0F);
                Vec3 startVec = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
                Vec3 endVec = startVec.add(viewVector.scale(attackDistance));
                AABB boundingBox = new AABB(startVec, endVec);

                for (Entity victim : level.getEntities(owner, boundingBox, target -> ModUtils.canHitEntity(target, owner))) {
                    AABB aabb = victim.getBoundingBox().inflate(0.3);
                    if (aabb.clip(startVec, endVec).isPresent()) {
                        owner.setLastHurtMob(victim);
                        if (victim instanceof PartEntity<?> partEntity) {
                            victim = partEntity.getParent();
                        }
                        DamageSource damageSource = ModDamageTypes.of(level, DamageTypes.STING, owner);
                        double v = IServerPlayer.of(owner).confluence$getMovementSpeed() * 1.5;
                        victim.hurt(damageSource, Mth.floor(baseAttackDamage * (v * 6 / 175 + 0.1F)));
                        double kb = v * baseKnockback * 4 / 105;
                        VectorUtils.knockBackA2B(owner, victim, kb, kb * 0.3);
                        EnchantmentHelper.doPostAttackEffects((ServerLevel) level, victim, damageSource);
                    }
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lance", state -> PlayState.STOP)
                .triggerableAnim("use", RawAnimation.begin().thenPlayAndHold("use")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        if (true) return; // todo
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<LanceItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("lance/" + BuiltInRegistries.ITEM.getKey(LanceItem.this).getPath())));
                }
                return renderer;
            }
        });
    }
}
