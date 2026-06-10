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
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.GunItems;
import org.confluence.mod.common.init.item.LightPetItems;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.init.item.SpearItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.entity.boss.BrainOfCthulhu;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.jetbrains.annotations.Nullable;

public class CrimsonHeartBlock extends Block {
    protected static final VoxelShape SHAPE = box(3, 3, 3, 13, 13, 13);

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

            if (count == 0 || level.random.nextFloat() < 0.2F) {
                LibEntityUtils.createItemEntity(GunItems.THE_UNDERTAKER.toStack(), center.x, center.y, center.z, level, 0);
                LibEntityUtils.createItemEntity(GunItems.MUSKET_BULLET.toStack(100), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                LibEntityUtils.createItemEntity(LightPetItems.CRIMSON_HEART.toStack(), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                LibEntityUtils.createItemEntity(TCItems.PANIC_NECKLACE.toStack(), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                LibEntityUtils.createItemEntity(ManaWeaponItems.CRIMSON_ROD.toStack(), center.x, center.y, center.z, level, 0);
            }
            if (level.random.nextFloat() < 0.2F) {
                LibEntityUtils.createItemEntity(SpearItems.THE_ROTTED_FORK.toStack(), center.x, center.y, center.z, level, 0);
            }

            for (ServerPlayer player : serverLevel.getPlayers(serverPlayer -> serverPlayer.distanceToSqr(center) <= 32 * 32)) {
                AchievementUtils.awardAchievement(player, "smashing_poppet");
            }

            if (count != 2) {
                Component component = Component.translatable("event.confluence.crimson_heart_broken." + count).withColor(GlobalColors.MESSAGE.get());
                serverLevel.getServer().getPlayerList().broadcastSystemMessage(component, false);
            }

            if (data.updateEvilBrokenCount()) {
                ModUtils.summonBoss(serverLevel, pos, new BrainOfCthulhu(TEBossEntities.BRAIN_OF_CTHULHU.get(), level), false);
            }
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (player instanceof ServerPlayer serverPlayer) {
            AchievementUtils.awardAchievement(serverPlayer, "smashing_poppet");
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
