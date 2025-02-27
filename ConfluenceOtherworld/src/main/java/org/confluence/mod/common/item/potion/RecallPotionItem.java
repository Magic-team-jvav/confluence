package org.confluence.mod.common.item.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class RecallPotionItem extends AbstractPotionItem {
    public RecallPotionItem() {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.BLUE));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 4;
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (living instanceof ServerPlayer serverPlayer) {
            BlockPos pos = serverPlayer.getRespawnPosition();
            if (pos == null) {
                ServerLevel serverLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
                if (serverLevel == null) serverLevel = (ServerLevel) level;
                LevelData data = serverLevel.getLevelData();
                serverPlayer.teleportTo(data.getSpawnPos().getX(), data.getSpawnPos().getY(), data.getSpawnPos().getZ());
            } else {
                serverPlayer.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            }
            serverPlayer.getCooldowns().addCooldown(this, 10);
            level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                ModSoundEvents.TRANSMISSION.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
        }
    }
}
