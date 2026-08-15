package org.confluence.mod.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ProjectileWeaponAction;
import org.confluence.mod.network.c2s.ProjectileFireIntentPacketC2S;

import java.util.Objects;

/**
 * 客户端主动弹幕触发门面。
 *
 * <p>客户端只判断手持物是否声明了公共动作能力，并发送固定意图。
 * 弹药、魔力、伤害、暴击、冷却和实际弹幕数量都必须由服务端事务重新读取与裁定。</p>
 */
public final class ProjectileFireIntentClient {
    private ProjectileFireIntentClient() {}

    /**
     * 当前手持物支持公共动作时发送意图。
     *
     * @return 是否发送了统一意图包
     */
    public static boolean sendIfSupported(
            LocalPlayer player,
            InteractionHand hand,
            ProjectileFireTrigger trigger
    ) {
        Objects.requireNonNull(player, "Client player must not be null");
        Objects.requireNonNull(hand, "Interaction hand must not be null");
        Objects.requireNonNull(trigger, "Projectile fire trigger must not be null");
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof ProjectileWeaponAction)) return false;
        ProjectileFireIntentPacketC2S.sendToServer(hand, trigger);
        return true;
    }
}
