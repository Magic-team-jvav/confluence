package org.confluence.mod.common.block.functional.enemybanner;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.List;

public class AbstractEnemyBannerBlock extends Block implements EntityBlock {
    public static final String TAG_ENTRY_KEY = "entry_key";
    public static final String DEFAULT_ENTRY_KEY = EntityType.ZOMBIE.getDescriptionId();

    public AbstractEnemyBannerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return level.getBlockEntity(pos) instanceof AbstractEnemyBannerBlock.BEntity entity
                ? entity.getItem() : super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : LibUtils.getTicker(blockEntityType, ModBlocks.ENEMY_BANNER_ENTITY.get(), BEntity::serverTick);
    }

    public static float processAttacker(ServerPlayer attacker, LivingEntity victim, float amount) {
        if (victim.getType() != EntityType.PLAYER &&
                attacker.hasEffect(ModEffects.ENEMY_BANNER.get()) && // 确保玩家关闭这个效果时不会应用增益
                PlayerSpecialData.of(attacker).getEnemyBannerEntries().contains(RegisterBestiaryKeyEvent.getKey(victim))
        ) {
            return amount * 1.1F;
        }
        return amount;
    }

    public static float processVictim(ServerPlayer victim, @Nullable Entity attacker, float amount) {
        if (attacker instanceof LivingEntity living &&
                living.getType() != EntityType.PLAYER &&
                victim.hasEffect(ModEffects.ENEMY_BANNER.get()) && // 确保玩家关闭这个效果时不会应用增益
                PlayerSpecialData.of(victim).getEnemyBannerEntries().contains(RegisterBestiaryKeyEvent.getKey(living))
        ) {
            return amount * 0.9167F;
        }
        return amount;
    }

    public static class BEntity extends BlockEntity {
        public String entryKey = DEFAULT_ENTRY_KEY;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(ModBlocks.ENEMY_BANNER_ENTITY.get(), pos, blockState);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            tag.putString(TAG_ENTRY_KEY, entryKey);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            this.entryKey = tag.getString(TAG_ENTRY_KEY);
        }

        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString(TAG_ENTRY_KEY, entryKey);
            return tag;
        }

        public ItemStack getItem() {
            ItemStack stack = ModItems.ENEMY_BANNER.toStack();
            LibUtils.updateItemStackNbt(stack, tag -> tag.putString(TAG_ENTRY_KEY, entryKey));
            return stack;
        }

        public static void serverTick(Level level, BlockPos pos, BlockState state, BEntity entity) {
            if (level.getGameTime() % 20 == 6) {
                Vec3 center = pos.getCenter();
                for (Player player : level.players()) {
                    if (player.distanceToSqr(center) < 100 * 100) {
                        if (!player.getActiveEffectsMap().containsKey(ModEffects.ENEMY_BANNER.get())) {
                            player.addEffect(new MobEffectInstance(ModEffects.ENEMY_BANNER.get(), -1));
                        }
                        PlayerSpecialData.of(player).updateEnemyBannerEntries(entity.entryKey, pos, true);
                    } else {
                        PlayerSpecialData.of(player).updateEnemyBannerEntries(entity.entryKey, pos, false);
                    }
                }
            }
        }
    }

    public static class BItem extends StandingAndWallBlockItem {
        public BItem() {
            super(
                    ModBlocks.ENEMY_BANNER.get(),
                    ModBlocks.WALL_ENEMY_BANNER.get(),
                    new PortItem.PortProperties()
                            .component(ConfluenceMagicLib.NBT, NbtComponent.create(tag -> tag.putString(TAG_ENTRY_KEY, DEFAULT_ENTRY_KEY)))
                            .component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
                    Direction.DOWN
            );
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            Component component = Component.translatable(getEntryKey(stack)).withStyle(ChatFormatting.GREEN);
            tooltip.add(Component.translatable("tooltip.item.confluence.enemy_banner.0", component).withStyle(ChatFormatting.GRAY));
        }

        public static String getEntryKey(ItemStack stack) {
            NbtComponent component = stack.get(ConfluenceMagicLib.NBT);
            if (component == null) return DEFAULT_ENTRY_KEY;
            return component.nbt().getString(TAG_ENTRY_KEY);
        }
    }
}
