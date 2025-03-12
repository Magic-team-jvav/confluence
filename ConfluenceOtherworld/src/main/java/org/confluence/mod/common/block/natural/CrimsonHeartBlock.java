package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.item.LightPetItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.color.IntegerRGB;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.entity.boss.BrainOfCthulhu;
import org.confluence.terraentity.init.TEEntities;
import org.jetbrains.annotations.Nullable;

public class CrimsonHeartBlock extends Block {
    private static final VoxelShape SHAPE = box(3, 3, 3, 13, 13, 13);

    public CrimsonHeartBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .strength(1.5F)
                .sound(SoundType.AMETHYST)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.DESTROY));
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
                ModUtils.createItemEntity(TGItems.MUSKET_BULLET.get(), 100, center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                ModUtils.createItemEntity(LightPetItems.CRIMSON_HEART.get(), 1, center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                ModUtils.createItemEntity(TCItems.PANIC_NECKLACE.get(), 1, center.x, center.y, center.z, level, 0);
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

            if (count != 2) {
                Component component = Component.translatable("event.confluence.crimson_heart_broken." + count).withColor(IntegerRGB.GREEN.get());
                serverLevel.getServer().getPlayerList().broadcastSystemMessage(component, false);
            }

            if (data.updateEvilBrokenCount()) {
                BrainOfCthulhu brainOfCthulhu = new BrainOfCthulhu(TEEntities.BRAIN_OF_CTHULHU.get(), level);
                brainOfCthulhu.setPos(center.x + level.random.nextInt(-50, 51), center.y, center.z + level.random.nextInt(-50, 51));
                level.addFreshEntity(brainOfCthulhu);
                Player nearestPlayer = level.getNearestPlayer(brainOfCthulhu, 200);
                if (nearestPlayer != null) brainOfCthulhu.setTarget(nearestPlayer);
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

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
