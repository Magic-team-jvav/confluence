package org.confluence.mod.common.item.sword.stagedy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.stagedy.projectile.AbstractProjContainer;
import org.confluence.mod.common.item.sword.stagedy.projectile.ForwardProjContainer;
import org.confluence.mod.common.item.sword.stagedy.projectile.StarFuryProjContainer;
import org.confluence.mod.network.c2s.SwordShootingPacketC2S;
import org.confluence.terraentity.init.TESounds;

import java.util.function.Supplier;

/**
 * 这里是定义弹幕策略类工厂，方便给类似的弹幕逻辑修改参数
 * @author coffee
 */
public class ProjectileStrategy {


    public static final Supplier<AbstractProjContainer> ICE_PROJ = ()-> new ForwardProjContainer(ModEntities.ICE_BLADE_SWORD_PROJECTILE.get(),10,1,10,0.6f);

    public static final Supplier<AbstractProjContainer> STAR_FURY_PROJ = ()-> new StarFuryProjContainer(10,1,10,1.5f, ModSoundEvents.STAR.get());

    public static final Supplier<AbstractProjContainer> ENCHANTED_SWORD_PROJ = ()->new ForwardProjContainer(ModEntities.ENCHANTED_SWORD_PROJECTILE.get(),9,1,10,0.8f, TESounds.REGULAR_STAFF_SHOOT_2.get());





    public static final Supplier<AbstractProjContainer> UNDEFINED_PROJ = ()->ICE_PROJ.get().setDamage(1);

    // TODO: 这是飞龙、波涌之刃的发剑气方式，还要写附魔剑、泰拉刃的
    @OnlyIn(Dist.CLIENT)
    public static void handle(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.gameMode == null || minecraft.gameMode.isDestroying() || !minecraft.options.keyAttack.isDown()) {return;}
        Item item = player.getMainHandItem().getItem();
        if (item instanceof BaseSwordItem sword && !player.getCooldowns().isOnCooldown(item)
            && sword.modifier.proj!= null
        ) {
            PacketDistributor.sendToServer((new SwordShootingPacketC2S()));
            player.getCooldowns().addCooldown(sword,sword.modifier.proj.get().getAttackSpeed(player));
            player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
