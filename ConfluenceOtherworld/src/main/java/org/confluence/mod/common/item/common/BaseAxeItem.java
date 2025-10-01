package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BaseAxeItem extends AxeItem {
    private @Nullable TooltipComponent component;
    private boolean hasImage;

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
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

    public BaseAxeItem hasImage() {
        this.hasImage = true;
        return this;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && hasImage) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    public static void dropAndPlaceOnRightClick(Player player, ItemStack stack, BlockPos pos) {
        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.CROPS)) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof IntegerProperty integerProperty && "age".equals(integerProperty.getName())) {
                    int age = state.getValue(integerProperty);
                    if (property.getPossibleValues().size() == age + 1) { // 其他模组可能不兼容
                        ItemStack consumed = ItemStack.EMPTY;
                        for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos), player, stack)) {
                            if (consumed.isEmpty() && (drop.is(Tags.Items.SEEDS) || drop.is(state.getBlock().asItem()))) {
                                consumed = drop.split(1);
                                if (drop.isEmpty()) continue;
                            }
                            Block.popResource(level, pos, drop);
                        }
                        if (!consumed.isEmpty() && consumed.getItem() instanceof BlockItem blockItem) {
                            level.setBlockAndUpdate(pos, blockItem.getBlock().defaultBlockState());
                        } else {
                            level.removeBlock(pos, false);
                        }
                    } else if (age > 0) l:{ // 收种子
                        for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos), player, stack)) {
                            if (drop.is(Tags.Items.SEEDS)) {
                                Block.popResource(level, pos, drop);
                            } else if (drop.is(state.getBlock().asItem())) {
                                break l;
                            }
                        }
                        level.setBlock(pos, state.setValue(integerProperty, 0), 2);
                    }
                    player.swing(InteractionHand.MAIN_HAND, true);
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

