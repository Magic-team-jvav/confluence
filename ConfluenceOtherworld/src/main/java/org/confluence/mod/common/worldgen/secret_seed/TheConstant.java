package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

public class TheConstant extends SecretSeed {
    public static final ResourceLocation POST_EFFECT = Confluence.asResource("shaders/post/the_constant.json");

    public TheConstant(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "constant".equals(seed) || "theconstant".equals(seed) || "the constant".equals(seed) || "eye4aneye".equals(seed) || "eyeforaneye".equals(seed);
    }

    public static float applyAttackDamage(@Nullable Entity causer, float amount) {
        if (causer instanceof ServerPlayer serverPlayer && serverPlayer.getFoodData().needsFood() && ModSecretSeeds.THE_CONSTANT.match(serverPlayer.server)) {
            return amount * 0.8F;
        }
        return amount;
    }

    public static void applyDarkness(ServerPlayer player, ServerLevel level) {
        if (player.gameMode.getGameModeForPlayer().isSurvival() && level.getGameTime() % 20 == 0 && ModSecretSeeds.THE_CONSTANT.match(level)) {
            if (player.hasEffect(ModEffects.SHINE) || player.hasEffect(MobEffects.GLOWING)) return;
            if (LibUtils.anyHandHasItem(player, itemStack -> itemStack.is(ModTags.Items.PROVIDE_LIGHT))) return;
            CompoundTag data = player.getPersistentData();
            int tick = data.getInt("confluence:in_darkness_tick");
            BlockPos eyePos = BlockPos.containing(player.getEyePosition());
            int brightness = level.getLevel().isThundering()
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

    @OnlyIn(Dist.CLIENT)
    public static void postEffect(boolean post) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        PostChain postChain = gameRenderer.currentEffect();
        if (post) {
            if (postChain == null || !POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.loadEffect(POST_EFFECT);
            }
            gameRenderer.effectActive = true;
        } else {
            if (postChain != null && POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.effectActive = false;
            }
        }
    }
}
