package org.confluence.mod.common.item.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terra_curio.mixin.client.accessor.MinecraftAccessor;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BaseChainSawItem extends AxeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BaseChainSawItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, ModRarity rarity) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public BaseChainSawItem(Tier tier, float rawDamage, float rawSpeed, PortItem.PortProperties properties, Consumer<PortItemAttributeModifiers.PortBuilder> consumer, ModRarity rarity) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (level.isClientSide && LibUtils.isPhysicalClient()) {
            ((MinecraftAccessor) Minecraft.getInstance()).setMissTime(0);
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<BaseChainSawItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(BuiltInRegistries.ITEM.getKey(BaseChainSawItem.this).withPrefix("chainsaw/")));
                }
                return renderer;
            }
        });
    }
}
