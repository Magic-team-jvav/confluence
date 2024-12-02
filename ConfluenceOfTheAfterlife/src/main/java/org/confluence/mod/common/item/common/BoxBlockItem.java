package org.confluence.mod.common.item.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;

public class BoxBlockItem extends BlockItem {
    public BoxBlockItem(Block block, ResourceKey<LootTable> lootTable) {
        super(block, new Properties().component(ModDataComponentTypes.LOOT.get(), new LootComponent(lootTable)));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && hand == InteractionHand.MAIN_HAND && !player.isCrouching()) {
            float fishingPower = PlayerUtils.getFishingPower((ServerPlayer) player);
            LootParams lootparams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(fishingPower + player.getLuck())
                    .create(LootContextParamSets.GIFT);
            LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(itemStack.get(ModDataComponentTypes.LOOT).lootTable());
            for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                if (!player.addItem(loot)) player.drop(loot, false, false);
            }
            itemStack.shrink(1);
            serverLevel.playSound(null, player.blockPosition(), ModSoundEvents.TERRA_OPERATION.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, @NotNull BlockState pState) {
        return pContext.getPlayer() == null || pContext.getPlayer().isCrouching();
    }
}
