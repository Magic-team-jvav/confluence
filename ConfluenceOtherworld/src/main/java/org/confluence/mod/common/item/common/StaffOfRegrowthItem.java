package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


public class StaffOfRegrowthItem extends Item {
    private @Nullable TooltipComponent component;
    private static final Map<Block, Block> BLOCK_MAPPING = Map.of(
            Blocks.DIRT, Blocks.GRASS_BLOCK
    );

    public StaffOfRegrowthItem() {
        this(
                ModTiers.PLATINUM,
                3.0F,
                1.0F,
                new Item.Properties(),
                builder -> {
                },
                ModRarity.GREEN
        );
    }

    public StaffOfRegrowthItem(Tier tier, float rawDamage, float rawSpeed, Properties properties,
                               Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(properties
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ModItems.createAttributes(tier,
                        rawDamage - tier.getAttackDamageBonus() - 1,
                        rawSpeed - 4, consumer))
                .stacksTo(1));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player != null && !level.isClientSide) {
            Block targetBlock = BLOCK_MAPPING.get(state.getBlock());
            if (targetBlock != null) {
                level.setBlockAndUpdate(pos, targetBlock.defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

}
