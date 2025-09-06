package org.confluence.mod.mixin.entity;

import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModAdvancements;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;
import org.confluence.mod.mixed.IEntity;
import org.confluence.mod.mixed.IItemEntity;
import org.confluence.mod.util.PrefixUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements IItemEntity {
    @Shadow
    public abstract ItemStack getItem();

    @Unique
    private static final Vec3 ANTI_GRAVITY = new Vec3(0.0, -5.0E-4F, 0.0);
    @Unique
    private int confluence$item_coolDown = 0;
    @Unique
    private int confluence$item_transforming = 0;

    @Override
    public void confluence$item_setCoolDown(int ticks) {
        this.confluence$item_coolDown = ticks;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void endTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();
        if (level.isClientSide || self.isRemoved()) return;
        if (confluence$item_coolDown < 0) this.confluence$item_coolDown = 0;

        if (confluence$item_coolDown == 0 && IEntity.of(self).confluence$isInShimmer()) {
            ShimmerItemTransmutationEvent.Pre pre = new ShimmerItemTransmutationEvent.Pre(self);
            if (NeoForge.EVENT_BUS.post(pre).isCanceled()) {
                self.getItem().shrink(pre.getShrink());
                confluence$setup(self, pre.getCoolDown(), pre.getSpeedY());
            } else if (confluence$item_transforming < pre.getTransformTime()) {
                this.confluence$item_transforming++;
                self.addDeltaMovement(ANTI_GRAVITY);
            } else {
                ShimmerItemTransmutationEvent.Post post = new ShimmerItemTransmutationEvent.Post(self);
                confluence$initTargets(post);
                NeoForge.EVENT_BUS.post(post);
                List<ItemStack> targets = post.getTargets();
                self.getItem().shrink(post.getShrink());
                if (targets == null) {
                    confluence$setup(self, post.getCoolDown(), post.getSpeedY());
                } else {
                    for (ItemStack target : targets) {
                        if (PrefixUtils.canInit(target)) PrefixUtils.unknown(target);
                        ItemEntity itemEntity = new ItemEntity(level, self.getX(), self.getY(), self.getZ(), target);
                        confluence$setup(itemEntity, post.getCoolDown(), post.getSpeedY());
                        level.addFreshEntity(itemEntity);
                    }
                    level.playSound(null, self.getX(), self.getY(), self.getZ(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
                    if (self.getOwner() instanceof ServerPlayer serverPlayer) {
                        ModAdvancements.CriterionTriggerz.SHIMMER_TRANSMUTATION.get().trigger(serverPlayer, self);
                    }
                }
            }
        } else if (confluence$item_coolDown > 0) {
            this.confluence$item_coolDown--;
        }
    }

    @Unique
    private static void confluence$setup(ItemEntity entity, int coolDown, double y) {
        entity.setNoGravity(true);
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, y, motion.z);
        IItemEntity.of(entity).confluence$item_setCoolDown(coolDown);
        entity.setGlowingTag(true);
    }

    @Unique
    private static void confluence$initTargets(ShimmerItemTransmutationEvent.Post event) {
        ItemEntity source = event.getSource();
        ItemStack sourceItem = source.getItem();

        GamePhase gamePhase = KillBoard.INSTANCE.getGamePhase();
        List<RecipeHolder<ItemTransmutationRecipe>> recipes = source.level().getRecipeManager().getRecipesFor(ModRecipes.ITEM_TRANSMUTATION_TYPE.get(), new SingleRecipeInput(sourceItem), source.level());
        for (RecipeHolder<ItemTransmutationRecipe> recipeHolder : recipes) {
            ItemTransmutationRecipe recipe = recipeHolder.value();
            if (!recipe.isValid()) return;
            int times = sourceItem.getCount() / recipe.shrink();
            List<ItemStack> results = new ArrayList<>();
            for (ItemStack result : recipe.target()) {
                int count = result.getCount() * times;
                int maxStackSize = result.getMaxStackSize();
                while (count > maxStackSize) {
                    results.add(result.copyWithCount(maxStackSize));
                    count -= maxStackSize;
                }
                results.add(result.copyWithCount(count));
            }
            event.setShrink(recipe.shrink() * times);
            event.setTargets(results);
            return;
        }

        if (!CommonConfigs.SHIMMER_DECOMPOSE.get()) return;

        if (sourceItem.getDamageValue() != 0) return;
        RegistryAccess registryAccess = source.level().registryAccess();
        boolean isHardmode = gamePhase.isHardmode();
        RandomSource random = source.level().random;
        for (RecipeHolder<?> recipeHolder : ((ServerLevel) source.level()).getServer().getRecipeManager().getRecipes()) {
            Recipe<?> recipe = recipeHolder.value();
            if (recipe.isSpecial() || recipe.isIncomplete() || recipe instanceof AbstractCookingRecipe) continue;
            ItemStack resultItem = recipe.getResultItem(registryAccess);
            if (sourceItem.getCount() >= resultItem.getCount() && ItemStack.isSameItem(sourceItem, resultItem)) {
                int times = sourceItem.getCount() / resultItem.getCount();
                List<ItemStack> results = new ArrayList<>();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ItemStack[] itemStacks = ingredient.getItems();
                    if (itemStacks.length == 0 || Arrays.stream(itemStacks).allMatch(itemStack -> itemStack.is(ModTags.Items.HARDMODE))) {
                        continue;
                    }
                    ItemStack input = Util.getRandom(itemStacks, random);
                    if (!isHardmode && input.is(ModTags.Items.HARDMODE)) {
                        for (int i = 0; i < itemStacks.length && input.is(ModTags.Items.HARDMODE); i++) {
                            input = itemStacks[i];
                        }
                        if (input.is(ModTags.Items.HARDMODE)) continue;
                    }
                    ItemStack result = input.copy();
                    if (result.getItem().hasCraftingRemainingItem(result)) continue;
                    int count = result.getCount() * times;
                    int maxStackSize = result.getMaxStackSize();
                    while (count > maxStackSize) {
                        ItemStack copy = result.copy();
                        copy.setCount(maxStackSize);
                        results.add(copy);
                        count -= maxStackSize;
                    }
                    result.setCount(count);
                    results.add(result);
                }
                if (results.isEmpty()) continue;
                event.setShrink(resultItem.getCount() * times);
                event.setTargets(results);
                return;
            }
        }
    }
}
