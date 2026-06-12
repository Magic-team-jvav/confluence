package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import PortLib.extensions.net.minecraft.world.item.enchantment.PortEnchantmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.PlayerAboutToEmptyTargetSweepEvent;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.mixin.server.level.ServerPlayerAccessor;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.PortLib;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum EmptyTargetSweepPacketC2S implements IPortPacket.C2S {
    INSTANCE;
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, EmptyTargetSweepPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);
    public static final ResourceLocation ID = Confluence.asResource("empty_target_sweep");

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            float damage = (float) player.getAttributeValue(LibAttributes.getAttackDamage());
            if (player.getAttackStrengthScale(0.5F) < 1.0F - Mth.EPSILON) return;
            float baseDamage = 1.0F + (float) player.getAttributeValue(PortLib.SWEEPING_DAMAGE_RATIO) * damage;
            PlayerAboutToEmptyTargetSweepEvent event = PortEventHandler.postEventWithReturn(new PlayerAboutToEmptyTargetSweepEvent(player, baseDamage));
            if (event.isCanceled()) return;
            float attackDamage = event.getAttackDamage();
            DamageSource source = player.damageSources().playerAttack(player);
            for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, BaseSwordItem.getSpecialSweepArea(player))) {
                double entityReachSq = Mth.square(PortPlayerExtension.entityInteractionRange(player));
                if (target != player &&
                        !player.isAlliedTo(target) &&
                        (!(target instanceof ArmorStand) || !((ArmorStand) target).isMarker()) &&
                        player.distanceToSqr(target) < entityReachSq
                ) {
                    target.knockback(0.4F, Mth.sin(player.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD));
                    float amount = ((ServerPlayerAccessor) player).callGetEnchantedDamage(target, attackDamage, source);
                    target.hurt(source, amount);
                    PortEnchantmentHelper.doPostAttackEffects(player.serverLevel(), target, source);
                }
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
            player.sweepAttack();
            player.swing(InteractionHand.MAIN_HAND, false);
        }
    }

    public static void send2Server() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
