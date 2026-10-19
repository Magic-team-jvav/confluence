package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.BaseBoss;

import java.util.List;

public class BossTargetSelectorItem extends Item {
    public BossTargetSelectorItem() {super(new Properties().stacksTo(1));}

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.confluence.boss_target_selector"));
    }

    // 支持直接选择本体，以及射线命中的大型 Boss 部件。
    private static BaseBoss boss(Entity entity) {
        Entity owner = entity instanceof PartHitTarget part ? part.encounterOwner() : LibEntityUtils.encounterOwner(entity);
        return owner instanceof BaseBoss boss ? boss : entity instanceof BaseBoss boss ? boss : null;
    }

    private InteractionResult select(Player player, Entity entity) {
        BaseBoss boss = boss(entity);
        if (boss == null || !player.isCreative()) return InteractionResult.PASS;
        if (!player.level().isClientSide) {
            boolean enabled = boss.toggleDeveloperTarget(player);
            // 双子魔眼共用一阶段目标，两眼同步开发者锁定，避免更新顺序导致目标被另一眼覆盖。
            if (boss instanceof AbstractTwinEye eye && eye.getPartner() != null
                    && eye.getPartner().isDeveloperTarget(player) != enabled)
                eye.getPartner().toggleDeveloperTarget(player);
            player.displayClientMessage(Component.translatable(enabled
                    ? "message.confluence.boss_target_selector.locked" : "message.confluence.boss_target_selector.cleared", boss.getDisplayName()), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        return select(player, target);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle().scale(64);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, start, start.add(direction),
                player.getBoundingBox().expandTowards(direction).inflate(1), entity -> entity.isAlive() && boss(entity) != null, 4096);
        if (hit != null && select(player, hit.getEntity()).consumesAction())
            return InteractionResultHolder.success(player.getItemInHand(hand));
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
