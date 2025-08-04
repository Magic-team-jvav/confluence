package org.confluence.mod.integration.sodium.dynamiclights;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ToolItems;

public class SodiumDynamicLightsHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("sodiumdynamiclights");

    public static void registerDynamicLight() {
        if (IS_LOADED) {
            DynamicLightHandlers.registerDynamicLightHandler(ModEntities.ARROW_PROJECTILE.get(), entity -> entity.modify.getLuminance());
        }
    }

    /**
     * 拓宽了负数域的发光
     *
     * @param returnValue 原发光强度（可能为负数）
     * @return 目标发光强度，值域在[-15, 15]。负数代表仅水下光照
     */
    public static int getLuminance(Entity entity, int returnValue) {
        int luminance = 0;
        if (entity instanceof LivingEntity living) {
            if (living.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.Items.PROVIDE_LIGHT)) {
                luminance += 10;
            }
            if (living.hasEffect(ModEffects.SHINE)) {
                luminance += 10;
            }
            if (LibUtils.anyHandHasItem(living, ToolItems.BOTTOMLESS_LAVA_BUCKET.get())) {
                luminance += 10;
            }
        }
        return returnValue >= 0 ? Math.min(returnValue + luminance, 15) : Math.max(returnValue - luminance, -15);
    }
}
