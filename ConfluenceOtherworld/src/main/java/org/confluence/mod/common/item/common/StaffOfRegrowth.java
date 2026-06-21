package org.confluence.mod.common.item.common;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StaffOfRegrowth extends CustomRarityItem {
    protected final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private @Nullable TooltipComponent component;

    public StaffOfRegrowth() {
        super(new PortItem.PortProperties().stacksTo(1), ModRarity.GREEN);
        this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(LibAttributes.getAttackDamage().value(), new AttributeModifier(
                        PortAttributeModifier.rl2uuid(ModItems.BASE_ATTACK_DAMAGE_ID),
                        ModItems.BASE_ATTACK_DAMAGE_ID.getPath(),
                        ModItems.getAttackDamage(ModTiers.PLATINUM, 3),
                        AttributeModifier.Operation.ADDITION
                ))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(
                        PortAttributeModifier.rl2uuid(ModItems.BASE_ATTACK_SPEED_ID),
                        ModItems.BASE_ATTACK_SPEED_ID.getPath(),
                        ModItems.getAttackSpeed(1),
                        AttributeModifier.Operation.ADDITION
                ))
                .build();
    }

    @Diff
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return defaultModifiers;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(component);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return useOnAction(context);
    }

    public static InteractionResult useOnAction(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player != null && !level.isClientSide && state.is(Blocks.DIRT)) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static void dropAndPlaceOnRightClick(Player player, ItemStack stack, BlockPos pos) {
        ServerLevel level = (ServerLevel) player.level();
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.CROPS)) return;
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty && "age".equals(integerProperty.getName())) {
                int age = state.getValue(integerProperty);
                if (property.getPossibleValues().size() == age + 1) { // 其他模组可能不兼容
                    ItemStack consumed = ItemStack.EMPTY;
                    List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, stack);
                    increaseDrops(player, stack, drops.stream());
                    for (ItemStack drop : drops) {
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
                    List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, stack);
                    increaseDrops(player, stack, drops.stream());
                    for (ItemStack drop : drops) {
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

    // 再生法杖/再生之斧 时运
    public static void increaseDrops(Entity breaker, ItemStack stack, Stream<ItemStack> drops) {
        int l = stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        RandomSource random = breaker.getRandom();
        drops.forEach(drop -> {
            int increase = random.nextIntBetweenInclusive(0, 2);
            drop.grow(increase);
            for (int i = 0; i < l; i++) {
                if (random.nextBoolean()) {
                    drop.grow(1);
                }
            }
        });
    }
}
