package org.confluence.mod.common.item.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.consumer.Consumer3;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.FoodItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BaseFoodItem extends Item {
    protected final Builder builder;

    public BaseFoodItem(Builder builder) {
        super(builder.properties);
        this.builder = builder;
    }

    public Properties getProperties() {
        return this.builder.properties;
    }

    public static Builder builder() {
        return new Builder(new PortItem.PortProperties());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return builder.duration.apply(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return builder.useAnim.apply(stack);
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return builder.drinkingSoundType.apply(null);
    }

    @Override
    public SoundEvent getEatingSound() {
        return builder.eatingSoundType.apply(null);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (stack.is(FoodItems.CHERRY) && level instanceof ServerLevel serverLevel && ModSecretSeeds.NO_TRAPS.match(serverLevel) && level.random.nextFloat() < 0.7F) {
            level.explode(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 2.5F, false, Level.ExplosionInteraction.MOB);
        }
        if (builder.finishUsingCallback != null) {
            builder.finishUsingCallback.accept(stack, level, livingEntity);
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(builder.tooltips);
    }

    public static class Builder {
        private final PortItem.PortProperties properties;
        private Function<ItemStack, Integer> duration = duration -> 0;
        private Function<Void, SoundEvent> drinkingSoundType = sound -> SoundEvents.EMPTY;
        private Function<Void, SoundEvent> eatingSoundType = sound -> SoundEvents.EMPTY;
        private Function<ItemStack, UseAnim> useAnim = useAnim -> UseAnim.NONE;
        private final List<Component> tooltips = new ArrayList<>();
        private Consumer3<ItemStack, Level, LivingEntity> finishUsingCallback;

        public Builder() {
            this.properties = new PortItem.PortProperties();
        }

        public Builder(PortItem.PortProperties properties) {
            this.properties = properties;
        }

        public void isFireResistant() {
            this.properties.fireResistant();
        }

        public Builder rarity(ModRarity rarity) {
            properties.component(ConfluenceMagicLib.MOD_RARITY, rarity);
            return this;
        }

        public Builder food(FoodProperties foodProperties) {
            properties.food(foodProperties);
            return this;
        }

        public Builder duration(Function<ItemStack, Integer> duration) {
            this.duration = duration;
            return this;
        }

        public Builder useAnim(Function<ItemStack, UseAnim> useAnim) {
            this.useAnim = useAnim;
            return this;
        }

        public Builder drinkingSound(Function<Void, SoundEvent> drinkingSoundType) {
            this.drinkingSoundType = drinkingSoundType;
            return this;
        }

        public Builder eatingSound(Function<Void, SoundEvent> eatingSoundType) {
            this.eatingSoundType = eatingSoundType;
            return this;
        }

        public Builder tooltip(String id, int lineCount, ChatFormatting chatFormatting) {
            this.tooltips.addAll(TooltipItem.getTooltipsFromString(id, lineCount, chatFormatting));
            return this;
        }

        public void setFinishUsingCallback(Consumer3<ItemStack, Level, LivingEntity> finishUsingCallback) {
            this.finishUsingCallback = finishUsingCallback;
        }

        public Properties getProperties() {
            return properties;
        }

        public Builder stackTo(int stack) {
            properties.stacksTo(stack);
            return this;
        }

        public BaseFoodItem build() {
            return new BaseFoodItem(this);
        }
    }

    public static class BItem extends net.minecraft.world.item.BlockItem {
        public BItem(Block block, Properties properties) {
            super(block, properties);
        }
    }
}
