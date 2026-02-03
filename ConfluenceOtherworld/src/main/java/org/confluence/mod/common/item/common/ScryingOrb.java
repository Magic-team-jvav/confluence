package org.confluence.mod.common.item.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.mixed.ILocalPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 占卜球
 */
// TODO: lore
public class ScryingOrb extends Item {
    public static AbstractClientPlayer spectatingPlayer;
    public static boolean spectating = false;

    public ScryingOrb() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        changeTarget(level, player);
        return super.use(level, player, usedHand);
    }

    /***/
    public static void changeTarget(@Nullable Level level, @Nullable Player player) {
        if (!(player instanceof LocalPlayer localPlayer) || !(level instanceof ClientLevel clientLevel)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSingleplayer()) {
            mc.getChatListener().handleSystemMessage(Component.translatable("message.confluence.scrying_orb.singleplayer"), false);
            return;
        }
        List<AbstractClientPlayer> players = clientLevel.players().stream().filter(p -> p != player).toList();
        if (players.isEmpty()) {
            spectating = false;
            ILocalPlayer.of(localPlayer).confluence$setCanMove(false);
            mc.setCameraEntity(mc.player);
            mc.getChatListener().handleSystemMessage(Component.translatable("message.confluence.scrying_orb.alone"), false);
            return;
        }
        int index = players.indexOf(spectatingPlayer);
        if (index == -1 || index == players.size() - 1) {
            index = 0;
        } else {
            index++;
        }
        spectatingPlayer = players.get(index);
        mc.setCameraEntity(spectatingPlayer);
        spectating = true;
        ILocalPlayer.of(localPlayer).confluence$setCanMove(true);
    }
}
