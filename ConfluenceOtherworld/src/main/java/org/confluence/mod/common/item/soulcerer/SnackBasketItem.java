package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/// 零食箩筐：随机获得面包 / 熟牛排 / 熟猪排 / 苹果 / 熟鱼之一，固定消耗 2 魂，冷却 35 秒。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SnackBasketItem extends SoulArtifactItem {
    public SnackBasketItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.SNACK_BASKET_SOUL_COST;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.SNACK_BASKET_COOLDOWN_TICKS;
    }

    @Override
    public void useArtifact(ServerPlayer player, ItemStack stack) {
        List<Item> foods = foods();
        if (foods.isEmpty()) {
            return;
        }
        ItemStack food = new ItemStack(foods.get(player.getRandom().nextInt(foods.size())));
        if (!player.addItem(food)) {
            player.drop(food, false);
        }
    }

    /// 固定五种食物；模组食物要延迟取，注册完成前不能碰。
    private static List<Item> foods() {
        List<Item> foods = new ArrayList<>(5);
        foods.add(Items.BREAD);
        foods.add(Items.COOKED_BEEF);
        foods.add(Items.COOKED_PORKCHOP);
        foods.add(Items.APPLE);
        Item cookedFish = FoodItems.COOK_FISH.get();
        if (cookedFish != Items.AIR) {
            foods.add(cookedFish);
        }
        return foods;
    }
}
