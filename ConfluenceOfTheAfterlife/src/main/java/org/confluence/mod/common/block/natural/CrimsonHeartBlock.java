package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.item.LightPetItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.color.IntegerRGB;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.jetbrains.annotations.Nullable;

public class CrimsonHeartBlock extends Block {
    public CrimsonHeartBlock() {
        super(Properties.ofFullCopy(Blocks.NETHER_WART_BLOCK));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            Vec3 center = pos.getCenter();
            ConfluenceData data = ConfluenceData.get(serverLevel);
            int count = data.getEvilBrokenCount() % 3;

            if (level.random.nextFloat() < 0.2F) {
                ModUtils.createItemEntity(TGItems.THE_UNDERTAKER.toStack(), center.x, center.y, center.z, level, 0);
                ModUtils.createItemEntity(TGItems.MUSKET_BULLET.get(), 99, center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                ModUtils.createItemEntity(LightPetItems.CRIMSON_HEART.get().getDefaultInstance(), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                ModUtils.createItemEntity(TCItems.PANIC_NECKLACE.get().getDefaultInstance(), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                // 猩红魔杖
            }
            if (level.random.nextFloat() < 0.2F) {
                // 腐叉
            }

            for (ServerPlayer player : serverLevel.getPlayers(serverPlayer -> serverPlayer.distanceToSqr(center) <= 32 * 32)) {
                PlayerUtils.awardAchievement(player, "smashing_poppet");
            }

            Component component = Component.translatable("event.confluence.evil_broken." + count).withColor(IntegerRGB.GREEN.get());
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(component, false);

            if (data.updateEvilBrokenCount()) {
//                EaterOfWorld eaterOfWorld = new EaterOfWorld(level, true);
//                eaterOfWorld.setPos(center.x + level.random.nextInt(-50, 51), center.y, center.z + level.random.nextInt(-50, 51));
//                level.addFreshEntity(eaterOfWorld);
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerUtils.awardAchievement(serverPlayer, "smashing_poppet");
        }
    }
}
