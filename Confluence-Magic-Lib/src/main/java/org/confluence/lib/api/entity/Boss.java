package org.confluence.lib.api.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.stream.Streams;
import org.confluence.lib.color.GlobalColors;

/// All bosses should implement this interface
///
/// 所有boss都应该实现这个接口
public interface Boss extends Enemy, IDiscardWhenRespawnEntity {
    default boolean shouldShowMessage() {
        return isMainBody();
    }

    default boolean isMainBody() {
        return true;
    }

    default boolean shouldEnhanceMultiplayer() {
        return true;
    }

    interface BossPart extends Boss {
        @Override
        default boolean isMainBody() {
            return false;
        }
    }

    static void sendBossSpawnMessage(Entity entity) {
        Level level = entity.level();
        if (!level.isClientSide && entity instanceof Boss boss) {
            if (boss.shouldShowMessage()) {
                Component mes = Component.translatable("message.confluence.boss_spawn",
                        entity.getDisplayName()).withColor(GlobalColors.EVENT.get()).withStyle(ChatFormatting.BOLD);

                for (Player player : level.players()) {
                    player.sendSystemMessage(mes);
                }
            }
        }
    }

    static void sendBossDeathMessage(Entity entity) {
        Level level = entity.level();
        if (!level.isClientSide && entity instanceof Boss boss) {
            if (boss.shouldShowMessage()) {
                Component mes = Component.translatable("message.confluence.boss_leave",
                        entity.getDisplayName()).withColor(GlobalColors.EVENT.get()).withStyle(ChatFormatting.BOLD);

                for (Player player : level.players()) {
                    player.sendSystemMessage(mes);
                }
            }
        }
    }

    static boolean noBossInWorld(ServerLevel level) {
        return Streams.of(level.getAllEntities()).noneMatch(entity -> entity instanceof Boss);
    }
}
