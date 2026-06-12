package org.confluence.mod.common.item.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 占卜球
 */
// TODO: lore
public class ScryingOrb extends Item {
    public static Player spectatingPlayer;

    public ScryingOrb() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        changeTarget(level, player);
        return super.use(level, player, usedHand);
    }

    public static void changeTarget(@Nullable Level level, @Nullable Player player) {
        if (level == null) return;
        if (!level.isClientSide || !(level instanceof ClientLevel clientLevel)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSingleplayer()) {
            mc.getChatListener().handleSystemMessage(Component.translatable("message.confluence.scrying_orb.singleplayer"), false);
            return;
        }
        List<? extends Player> players = clientLevel.players().stream().filter(p -> p != player && p.isAlive()).toList();
        if (players.isEmpty()) {
            stopSpectating();
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
    }

    @OnlyIn(Dist.CLIENT)
    public static void stopSpectating() {
        Minecraft mc = Minecraft.getInstance();
        spectatingPlayer = null;
        mc.setCameraEntity(mc.player);
    }
}
