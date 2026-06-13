package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public class TheConstant extends SecretSeed {
    public static final ResourceLocation POST_EFFECT = Confluence.asResource("shaders/post/the_constant.json");

    public TheConstant(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "constant".equals(seed) || "theconstant".equals(seed) || "the constant".equals(seed) || "eye4aneye".equals(seed) || "eyeforaneye".equals(seed);
    }

    public static float applyAttackDamage(ServerPlayer attacker, float amount) {
        if (attacker.getFoodData().needsFood() && ModSecretSeeds.THE_CONSTANT.match(attacker.server)) {
            return amount * 0.8F;
        }
        return amount;
    }

    public static void applyDarkness(ServerPlayer player, ServerLevel level, long gameTime) {
        if (player.gameMode.getGameModeForPlayer().isSurvival() && gameTime % 20 == 0 && ModSecretSeeds.THE_CONSTANT.match(player.server)) {
            if (player.hasEffect(ModEffects.SHINE.get()) || player.hasEffect(MobEffects.GLOWING)) return;
            if (TCUtils.getValue(player, TCItems.LUMINANCE) > 0) return;
            if (LibEntityUtils.anyHandHasItem(player, ModTags.Items.PROVIDE_LIGHT)) return;
            CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
            int tick = data.getInt("confluence:in_darkness_tick");
            BlockPos eyePos = BlockPos.containing(player.getEyePosition());
            int brightness = level.isThundering()
                    ? level.getMaxLocalRawBrightness(eyePos, 10)
                    : level.getMaxLocalRawBrightness(eyePos);
            if (brightness <= 5) {
                if (tick < 5) {
                    if (++tick == 3) {
                        player.sendSystemMessage(Component.translatable("secret_seed.the_constant.in_darkness_for_3_second"), false);
                    }
                    data.putInt("confluence:in_darkness_tick", tick);
                } else {
                    player.hurt(ModDamageTypes.of(level, ModDamageTypes.DARKNESS), 50);
                }
            } else if (tick != 0) {
                data.putInt("confluence:in_darkness_tick", 0);
            }
        }
    }

    public static void instantlyDieWhenHasNoFoodLevel(ServerPlayer player) {
        if (player.getFoodData().getFoodLevel() <= 0 && ModSecretSeeds.THE_CONSTANT.match(player.server)) {
            player.hurt(player.damageSources().starve(), Float.MAX_VALUE);
        }
    }
}
