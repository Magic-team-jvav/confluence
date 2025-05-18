package org.confluence.mod.common.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.mixed.IDamageSource;
import org.jetbrains.annotations.NotNull;

// 除了显示数字还能显示“美味...” “致命失误！”
public record DamageIndicatorOptions(Component text, boolean big, Type type) implements ParticleOptions {
    @Override
    @NotNull
    public ParticleType<?> getType() {
        return ModParticleTypes.DAMAGE_INDICATOR.get();
    }

    public static final MapCodec<DamageIndicatorOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((thisOptions) -> thisOptions.text),
            Codec.BOOL.fieldOf("big").forGetter(options -> options.big),
            Type.CODEC.fieldOf("type").forGetter(options -> options.type)
    ).apply(instance, DamageIndicatorOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DamageIndicatorOptions> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, opt -> opt.text,
            ByteBufCodecs.BOOL, opt -> opt.big,
            Type.STREAM_CODEC, opt -> opt.type,
            DamageIndicatorOptions::new
    );

    public static void sendDamageParticle(ServerLevel serverLevel, DamageSource damageSource, float amount, LivingEntity victim) {
        if (damageSource.is(DamageTypes.GENERIC_KILL)) return;
        float roundedAmount = Math.round(amount * 10) / 10f;
        int intAmount = (int) roundedAmount;
        if (roundedAmount == 0F) return;
        String text = roundedAmount % 1 == 0 ? String.valueOf(intAmount) : String.valueOf(roundedAmount);
        Vec3 pos = victim.position();
        boolean crit = ((IDamageSource) damageSource).confluence$isCritical();
        Component component = Component.literal(text).withStyle(crit ? ChatFormatting.DARK_RED : ChatFormatting.GOLD, ChatFormatting.BOLD);
        serverLevel.sendParticles(new DamageIndicatorOptions(component, crit, Type.DAMAGE), pos.x, victim.getBoundingBoxForCulling().maxY, pos.z, 1, 0.1, 0.1, 0.1, 0);
    }

    public static void sendHealParticle(float amount, ServerLevel level, LivingEntity living) {
        if (living.getHealth() < living.getMaxHealth()) {
            double y = living.getBoundingBoxForCulling().maxY;
            Vec3 pos = living.position();
            amount = Math.round(amount * 10.0F) / 10.0F;
            String text = amount % 1 == 0 ? Integer.toString((int) amount) : Float.toString(amount);
            Component component = Component.literal(text).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            level.sendParticles(new DamageIndicatorOptions(component, false, Type.HEAL), pos.x, y, pos.z, 1, 0.1, 0.1, 0.1, 0.0);
        }
    }

    public enum Type {
        DAMAGE,
        HEAL,
        OTHER;

        public static final Codec<Type> CODEC = Codec.BYTE.xmap(Type::byId, t -> (byte) t.ordinal());

        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.BYTE.map(Type::byId, t -> (byte) t.ordinal());

        public static Type byId(byte i) {
            return i == 0 ? DAMAGE : i == 1 ? HEAL : OTHER;
        }
    }
}
