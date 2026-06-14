package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.network.s2c.BestiarySyncPacketS2C;

public class BestiaryItem extends CustomRarityItem {
    public BestiaryItem() {
        super(new Properties().stacksTo(1), ModRarity.MASTER);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            double reach = Math.max(PortPlayerExtension.entityInteractionRange(player), PortPlayerExtension.blockInteractionRange(player));
            double squared = Mth.square(reach);
            Vec3 from = player.getEyePosition(1.0F);
            HitResult hitResult = player.pick(reach, 1.0F, false);
            double sqr = hitResult.getLocation().distanceToSqr(from);
            if (hitResult.getType() != HitResult.Type.MISS) {
                squared = sqr;
                reach = Math.sqrt(sqr);
            }
            Vec3 viewVector = player.getViewVector(1.0F);
            Vec3 to = from.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
            AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                    player, from, to, aabb, entity -> !entity.isSpectator() && entity.isPickable(), squared
            );
            if (entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr && entityHitResult.getEntity() instanceof LivingEntity living) {
                if (Bestiary.canBeSeenAsBestiaryEntry(living)) {
                    BestiaryEntry entry = Bestiary.INSTANCE.getOrCreateEntry(living);
                    entry.killedByCount += 100;
                    BestiarySyncPacketS2C.syncEntry(living, entry);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }
}
