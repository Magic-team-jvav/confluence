package org.confluence.mod.integration.sodium.dynamiclights;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terra_curio.api.primitive.IntegerValue;

public class SodiumDynamicLightsHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("sodiumdynamiclights");

    public static void registerDynamicLight() {
        if (IS_LOADED) {
            DynamicLightHandlers.registerDynamicLightHandler(ModEntities.BASE_ARROW.get(), entity -> entity.getLuminance());
            DynamicLightHandlers.registerDynamicLightHandler(ModEntities.CURSED_FLAMES.get(), entity -> 15);
            DynamicLightHandlers.registerDynamicLightHandler(ModEntities.STAR_CANNON_BULLET.get(), entity -> 10);
        }
    }

    /// 拓宽了负数域的发光
    ///
    /// @param returnValue 原发光强度（可能为负数）
    /// @return 目标发光强度，值域在[-15, 15]。负数代表仅水下光照
    public static int getLuminance(Entity entity, int returnValue) {
        int luminance = 0;
        if (entity instanceof LivingEntity living) {
            if (living instanceof Player player) {
                luminance = IntegerValue.absMax(luminance, ClientPacketHandler.getLuminance(player));
            }
            if (living.hasEffect(ModEffects.SHINE) || living.hasEffect(MobEffects.GLOWING)) {
                luminance = IntegerValue.absMax(luminance, 10);
            }
            if (LibEntityUtils.anyHandHasItem(living, ModTags.Items.PROVIDE_LIGHT)) {
                luminance = IntegerValue.absMax(luminance, 10);
            }
        }
        return IntegerValue.absMax(luminance, returnValue);
    }
}
