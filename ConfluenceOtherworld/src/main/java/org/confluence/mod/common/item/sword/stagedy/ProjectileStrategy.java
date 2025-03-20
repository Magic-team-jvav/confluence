package org.confluence.mod.common.item.sword.stagedy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.c2s.SwordShootingPacketC2S;

/**
 * 弹幕处理
 * @author coffee
 */
public class ProjectileStrategy {

    // TODO: 这是飞龙、波涌之刃的发剑气方式，还要写附魔剑、泰拉刃的
    @OnlyIn(Dist.CLIENT)
    public static void handle(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.gameMode == null || minecraft.gameMode.isDestroying() || !minecraft.options.keyAttack.isDown()) {return;}

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        var data = stack.get(ModDataComponentTypes.SWORD_PROJECTILE);
        if (item instanceof BaseSwordItem sword && !player.getCooldowns().isOnCooldown(item)
            && data!= null
        ) {
            PacketDistributor.sendToServer((new SwordShootingPacketC2S()));
            player.getCooldowns().addCooldown(sword, data.getAttackSpeed(player));
            player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
