package org.confluence.mod.common.item.sword.stagedy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.c2s.SwordShootingPacketC2S;
import org.confluence.terraentity.entity.proj.generation.AboveFallenGeneration;
import org.confluence.terraentity.entity.proj.generation.ForwardGeneration;
import org.confluence.terraentity.init.TESounds;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 这里是定义弹幕策略类工厂，方便给类似的弹幕逻辑修改参数
 * @author coffee
 */
public class ProjectileStrategy {


    public static final Supplier<SwordProjectileComponent> ICE_PROJ =
            ()->new SwordProjectileComponent(2,0.6f,0.9f,40, 0, 10,
                    ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,0));

    public static final Supplier<SwordProjectileComponent> STAR_FURY_PROJ =
            ()->new SwordProjectileComponent(1.5f,1.5f,0.9f,100, 0, 10,
                    ModSoundEvents.STAR.getId(),ModEntities.STAR_FURY_PROJECTILE.getId(),
                    Optional.empty(), new AboveFallenGeneration(30,30,10,0.5f,20,5));

    public static final Supplier<SwordProjectileComponent> ENCHANTED_SWORD_PROJ =
            ()->new SwordProjectileComponent(1,0.8f,0.9f,40, 0, 10,
                    TESounds.REGULAR_STAFF_SHOOT_2.getId(),ModEntities.ENCHANTED_SWORD_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,0));




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
