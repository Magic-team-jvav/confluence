package org.confluence.mod.common.item.sponsor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.entity.projectile.IceTofuBrickProjectile;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortProjectileItem;

import java.util.List;

public class IceTofuBrickItem extends CustomRarityItem implements PortProjectileItem {
    public IceTofuBrickItem() {
        super(new Properties(), ModRarity.MASTER);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            IceTofuBrickProjectile brick = new IceTofuBrickProjectile(player, level);
            brick.setItem(itemstack);
            brick.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 0.0F);
            level.addFreshEntity(brick);
            player.getCooldowns().addCooldown(this, 6);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack itemStack, Direction direction) {
        IceTofuBrickProjectile brick = new IceTofuBrickProjectile(pos.x(), pos.y(), pos.z(), level);
        brick.setItem(itemStack);
        return brick;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(TooltipItem.getTooltipsFromString("ice_tofu_brick", 1, ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
