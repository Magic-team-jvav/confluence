package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.PlayerAboutToEmptyTargetSweepEvent;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.mixin.ServerPlayerAccessor;
import org.confluence.mod.util.PlayerUtils;

public final class EmptyTargetSweepPacketC2S implements IPacketC2S {
    private static final EmptyTargetSweepPacketC2S INSTANCE = new EmptyTargetSweepPacketC2S();
    public static final Type<EmptyTargetSweepPacketC2S> TYPE = Confluence.createType("empty_target_sweep");
    public static final StreamCodec<ByteBuf, EmptyTargetSweepPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private EmptyTargetSweepPacketC2S() {}

    @Override
    public Type<EmptyTargetSweepPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (player.getAttackStrengthScale(0.5F) < 1.0F - Mth.EPSILON) return;
            float baseDamage = 1.0F + (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * damage;
            PlayerAboutToEmptyTargetSweepEvent event = NeoForge.EVENT_BUS.post(new PlayerAboutToEmptyTargetSweepEvent(player, baseDamage));
            if (event.isCanceled()) return;
            float attackDamage = event.getAttackDamage();
            DamageSource source = player.damageSources().playerAttack(player);
            for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, BaseSwordItem.getSpecialSweepArea(player))) {
                double entityReachSq = Mth.square(player.entityInteractionRange());
                if (target != player &&
                        !player.isAlliedTo(target) &&
                        (!(target instanceof ArmorStand) || !((ArmorStand) target).isMarker()) &&
                        player.distanceToSqr(target) < entityReachSq
                ) {
                    target.knockback(0.4F, Mth.sin(player.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD));
                    float amount = ((ServerPlayerAccessor) player).callGetEnchantedDamage(target, attackDamage, source);
                    target.hurt(source, amount);
                    EnchantmentHelper.doPostAttackEffects(player.serverLevel(), target, source);
                }
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
            player.sweepAttack();
            player.swing(InteractionHand.MAIN_HAND, false);
        }
    }

    public static void send2Server() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
