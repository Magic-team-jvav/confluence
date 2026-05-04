package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicMissileProjectile;

import java.util.List;

public class MagicMissileItem extends ManaStaffItem<MagicMissileProjectile> {
    public static final int COOLDOWN = 2;

    public MagicMissileItem() {
        super(ModRarity.GREEN, MagicMissileProjectile::new, 35, 14, 12, COOLDOWN, 0.04);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        super.use(level, player, usedHand);
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, MagicMissileProjectile projectile) {
        Vec3 vector = player.calculateViewVector(0, player.getYRot());
        projectile.setPos(player.getX() + vector.x, player.getEyeY() - 0.1, player.getZ() + vector.z);
        projectile.setDamage(damage);
        projectile.setDefaultVelocity(velocity);
        projectile.setOwner(player);
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack itemStack, MagicMissileProjectile projectile) {
        player.level().playSound(null, player.getX(), player.getEyeY(), player.getZ(), getShootSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    protected void rayTrace(ServerPlayer player, ItemStack itemStack, MagicMissileProjectile projectile) {}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.magic_missile.0").withStyle(ChatFormatting.GRAY));
    }
}
