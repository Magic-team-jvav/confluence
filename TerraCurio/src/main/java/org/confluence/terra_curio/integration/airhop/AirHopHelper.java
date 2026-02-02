package org.confluence.terra_curio.integration.airhop;

import fuzs.airhop.init.ModRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AirHopHelper {
    public static final boolean LOADED = ModList.get().isLoaded("airhop");
    private static Method canJump;
    private static Method isSaturated;
    private static Method getHighestLevel;
    private static Method getOrDefault;
    private static Object AIR_HOPS_ATTACHMENT;

    public static boolean notFinishJump(Player player) {
        try {
            if (canJump == null) {
                ClassLoader classLoader = AirHopHelper.class.getClassLoader();
                Class<?> AirHopClientHandler = classLoader.loadClass("fuzs.airhop.client.handler.AirHopClientHandler");
                canJump = AirHopClientHandler.getDeclaredMethod("canJump", Player.class);
                canJump.setAccessible(true);
                isSaturated = AirHopClientHandler.getDeclaredMethod("isSaturated", Player.class);
                isSaturated.setAccessible(true);
                getHighestLevel = AirHopClientHandler.getDeclaredMethod("getHighestLevel", LivingEntity.class, DataComponentType.class);
                getHighestLevel.setAccessible(true);
                Class<?> DataAttachmentType = classLoader.loadClass("fuzs.puzzleslib.api.attachment.v4.DataAttachmentType");
                getOrDefault = DataAttachmentType.getDeclaredMethod("getOrDefault", Object.class, Object.class);
                getOrDefault.setAccessible(true);
                Field airHopsAttachmentType = ModRegistry.class.getField("AIR_HOPS_ATTACHMENT_TYPE");
                airHopsAttachmentType.setAccessible(true);
                AIR_HOPS_ATTACHMENT = airHopsAttachmentType.get(null);
            }
            if (((boolean) canJump.invoke(null, player)) && ((boolean) isSaturated.invoke(null, player))) {
                byte airHops = (Byte) getOrDefault.invoke(AIR_HOPS_ATTACHMENT, player, (byte) 0);
                int allEnchantmentLevels = (int) getHighestLevel.invoke(null, player, ModRegistry.AIR_HOP_ENCHANTMENT_EFFECT_COMPONENT_TYPE.value());
                return airHops < allEnchantmentLevels;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
