package org.confluence.mod.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.EnvironmentAmountRecipe;
import org.confluence.lib.common.recipe.EnvironmentEitherAmountRecipe4x;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.block.functional.crafting.*;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.terra_curio.common.init.TCBlocks;
import org.confluence.terra_curio.common.init.TCRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 泰拉式合成：附近（半径 {@link #RADIUS} 格）所有制作站的配方都出现在背包的合成列表里，
/// 网格配方按“材料 × 数量”处理（不看摆放形状）。熔炉类（需要时间/燃料）与祭坛（放物品的仪式）不参与。
public final class StationCrafting {
    public static final int RADIUS = 4;

    public record Requirement(Ingredient ingredient, int count) {}

    /// 参与泰拉式合成的配方类型
    public static List<RecipeType<?>> supportedTypes() {
        return List.of(
                RecipeType.CRAFTING,
                ModRecipes.HEAVY_WORK_BENCH_TYPE.get(), ModRecipes.HARDMODE_ANVIL_TYPE.get(),
                ModRecipes.SAWMILL_TYPE.get(), ModRecipes.LOOM_TYPE.get(), ModRecipes.SOLIDIFIER_TYPE.get(),
                ModRecipes.SKY_MILL_TYPE.get(), ModRecipes.CRYSTAL_BALL_TYPE.get(), ModRecipes.ALCHEMY_TABLE_TYPE.get(),
                ModRecipes.DYE_VAT_TYPE.get(), ModRecipes.FLETCHING_TABLE_TYPE.get(), TCRecipes.WORKSHOP_TYPE.get()
        );
    }

    /// 方块提供的配方类型（不是制作站返回 null）
    public static @Nullable RecipeType<?> stationType(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.CRAFTING_TABLE) return RecipeType.CRAFTING;
        if (block == Blocks.FLETCHING_TABLE) return ModRecipes.FLETCHING_TABLE_TYPE.get();
        if (block instanceof HeavyWorkBenchBlock) return ModRecipes.HEAVY_WORK_BENCH_TYPE.get();
        if (block instanceof HardmodeAnvilBlock) return ModRecipes.HARDMODE_ANVIL_TYPE.get();
        if (block instanceof SawmillBlock) return ModRecipes.SAWMILL_TYPE.get();
        if (block instanceof LoomBlock) return ModRecipes.LOOM_TYPE.get();
        if (block instanceof SolidifierBlock) return ModRecipes.SOLIDIFIER_TYPE.get();
        if (block instanceof SkyMillBlock) return ModRecipes.SKY_MILL_TYPE.get();
        if (block instanceof CrystalBallBlock) return ModRecipes.CRYSTAL_BALL_TYPE.get();
        if (block instanceof AlchemyTableBlock) return ModRecipes.ALCHEMY_TABLE_TYPE.get();
        if (block instanceof DyeVatBlock) return ModRecipes.DYE_VAT_TYPE.get();
        if (block == TCBlocks.WORKSHOP.get()) return TCRecipes.WORKSHOP_TYPE.get();
        return null;
    }

    public static boolean isStation(BlockState state) {
        return stationType(state) != null;
    }

    /// 玩家周围半径内的制作站
    public static Set<RecipeType<?>> nearbyStations(Player player) {
        Set<RecipeType<?>> types = new HashSet<>();
        Level level = player.level();
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -RADIUS, -RADIUS), center.offset(RADIUS, RADIUS, RADIUS))) {
            RecipeType<?> type = stationType(level.getBlockState(pos));
            if (type != null) types.add(type);
        }
        return types;
    }

    /// 所有可参与的配方（不含特殊配方与没有材料/产物的配方）
    public static List<RecipeHolder<?>> candidates(Level level) {
        List<RecipeHolder<?>> list = new ArrayList<>();
        RecipeManager manager = level.getRecipeManager();
        for (RecipeType<?> type : supportedTypes()) {
            for (Object entry : manager.getAllRecipesFor(castType(type))) {
                RecipeHolder<?> holder = (RecipeHolder<?>) entry;
                Recipe<?> recipe = holder.value();
                if (recipe.isSpecial() || recipe.isIncomplete()) continue;
                if (recipe.getResultItem(level.registryAccess()).isEmpty()) continue;
                if (requirements(recipe).isEmpty()) continue;
                list.add(holder);
            }
        }
        list.sort(Comparator.comparing(holder -> holder.value().getResultItem(level.registryAccess()).getItem().builtInRegistryHolder().key().location().toString()));
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static RecipeType castType(RecipeType<?> type) {
        return type;
    }

    /// 手工（无工作台）可做 2×2 以内的原版配方，与泰拉“徒手合成”相同
    public static boolean isAvailable(Player player, Recipe<?> recipe, Set<RecipeType<?>> stations) {
        RecipeType<?> type = recipe.getType();
        boolean station = stations.contains(type) || (type == RecipeType.CRAFTING && recipe.canCraftInDimensions(2, 2));
        if (!station) return false;
        EnvironmentLevelAccess.Matcher environment = null;
        if (recipe instanceof EnvironmentAmountRecipe environmentRecipe) environment = environmentRecipe.getEnvironment();
        if (recipe instanceof EnvironmentEitherAmountRecipe4x environmentRecipe) environment = environmentRecipe.getEnvironment();
        if (environment == null) return true;
        EnvironmentLevelAccess access = new EnvironmentLevelAccess(player.level(), player.blockPosition());
        access.initializeIfNeeded(player);
        return environment.matches(access);
    }

    /// 网格配方 → 材料清单（相同材料合并数量）
    public static List<Requirement> requirements(Recipe<?> recipe) {
        List<Requirement> list = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient == null || ingredient.getItems().length == 0) continue;
            Ingredient base = ingredient.getCustomIngredient() instanceof AmountIngredient amountIngredient ? amountIngredient.ingredient() : ingredient;
            if (base.getItems().length == 0) continue;
            int amount = Math.max(1, AmountIngredient.getAmount(ingredient));
            boolean merged = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).ingredient().equals(base)) {
                    list.set(i, new Requirement(base, list.get(i).count() + amount));
                    merged = true;
                    break;
                }
            }
            if (!merged) list.add(new Requirement(base, amount));
        }
        return list;
    }

    /// 背包（含快捷栏）里是否有足够的材料
    public static boolean hasMaterials(Inventory inventory, List<Requirement> requirements) {
        int[] available = new int[Inventory.INVENTORY_SIZE];
        for (int i = 0; i < available.length; i++) available[i] = inventory.items.get(i).getCount();
        for (Requirement requirement : requirements) {
            int need = requirement.count();
            for (int i = 0; i < available.length && need > 0; i++) {
                if (available[i] > 0 && requirement.ingredient().test(inventory.items.get(i))) {
                    int take = Math.min(need, available[i]);
                    available[i] -= take;
                    need -= take;
                }
            }
            if (need > 0) return false;
        }
        return true;
    }

    /// 服务端合成：检查附近制作站、环境与材料，扣除材料（含容器返还，如桶）。
    /// 像泰拉一样：普通点击产物放到鼠标上（同种物品可叠加到满组）；Shift+点击尽量多做，产物进背包，放不下的上鼠标。
    public static void craft(ServerPlayer player, ResourceLocation id, boolean max) {
        Level level = player.level();
        Optional<RecipeHolder<?>> optional = level.getRecipeManager().byKey(id);
        if (optional.isEmpty()) return;
        Recipe<?> recipe = optional.get().value();
        if (!supportedTypes().contains(recipe.getType()) || recipe.isSpecial()) return;
        if (!isAvailable(player, recipe, nearbyStations(player))) return;
        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (result.isEmpty()) return;
        List<Requirement> requirements = requirements(recipe);
        Inventory inventory = player.getInventory();
        int times = max ? Math.max(1, result.getMaxStackSize() / result.getCount()) : 1;
        for (int t = 0; t < times; t++) {
            if (!hasMaterials(inventory, requirements)) break;
            if (!max && !fitsOnCursor(player, result)) break; // 鼠标上是别的东西或已满：不做
            List<ItemStack> remainders = new ArrayList<>();
            for (Requirement requirement : requirements) {
                int need = requirement.count();
                for (int i = 0; i < Inventory.INVENTORY_SIZE && need > 0; i++) {
                    ItemStack stack = inventory.items.get(i);
                    if (stack.isEmpty() || !requirement.ingredient().test(stack)) continue;
                    int take = Math.min(need, stack.getCount());
                    if (stack.hasCraftingRemainingItem()) {
                        ItemStack remainder = stack.getCraftingRemainingItem();
                        remainder.setCount(remainder.getCount() * take);
                        remainders.add(remainder);
                    }
                    stack.shrink(take);
                    need -= take;
                }
            }
            ItemStack crafted = result.copy();
            crafted.onCraftedBy(level, player, crafted.getCount());
            if (max) {
                give(player, crafted);
            } else {
                putOnCursor(player, crafted);
            }
            remainders.forEach(remainder -> give(player, remainder));
        }
        inventory.setChanged();
        player.containerMenu.broadcastChanges();
    }

    /// 鼠标为空，或拿着同种物品且叠加后不超过一组
    public static boolean fitsOnCursor(Player player, ItemStack result) {
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(carried, result) && carried.getCount() + result.getCount() <= carried.getMaxStackSize();
    }

    private static void putOnCursor(ServerPlayer player, ItemStack stack) {
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            player.containerMenu.setCarried(stack);
        } else {
            carried.grow(stack.getCount());
        }
    }

    /// 先进背包；背包满时放到鼠标上（若能叠加），实在放不下才丢出
    private static void give(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (player.getInventory().add(stack) || stack.isEmpty()) return;
        if (fitsOnCursor(player, stack)) {
            putOnCursor(player, stack);
        } else {
            player.drop(stack, false);
        }
    }

    private StationCrafting() {}
}
