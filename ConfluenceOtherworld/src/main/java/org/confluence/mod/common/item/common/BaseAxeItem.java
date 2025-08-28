package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.Block.dropResources;

public class BaseAxeItem extends AxeItem {public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4)));
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier, (rawDamage - tier.getAttackDamageBonus() - 1), rawSpeed - 4, consumer)));
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    public static void dropAndPlaceOnRightClick(Player player, ItemStack stack, BlockPos pos) {
        Level level = player.level();
        BlockState block = level.getBlockState(pos);
        if (block.is(BlockTags.CROPS)) {
            for (var p : block.getProperties()) {
                if (p instanceof IntegerProperty ip) {
                    int l = block.getValue(ip);
                    if (p.getPossibleValues().size() == l + 1) {  // 其他模组可能不兼容
                        dropResources(block, level, pos, null, player, stack);
                        level.removeBlock(pos, false);
                        var i = block.getBlock().asItem();
                        if (player.getInventory().countItem(i) > 0) {
                            PlayerUtils.consumeItemCount(player.getInventory().items, i, 1);
                            BlockState newState = block.getBlock().defaultBlockState();
                            newState.setValue(ip, 0);
                            level.setBlock(pos, newState, 2);
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void increaseDropsOnBlockBreak(Entity entity, ItemStack stack, List<ItemEntity> drops) {
        // 再生法杖/再生之斧 时运
        Holder<Enchantment> enchantment = entity.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        int l = stack.getTagEnchantments().getLevel(enchantment);
        for (ItemEntity drop : drops) {
            int increase = entity.getRandom().nextIntBetweenInclusive(0, 2);
            drop.getItem().grow(increase);
            for (int i = 0; i < l; i++) {
                if (entity.getRandom().nextFloat() < 0.5f) {
                    drop.getItem().grow(1);
                }
            }
        }
    }
}

