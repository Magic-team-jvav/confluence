package org.confluence.mod.mixin.integration.magiclib;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LibEntityUtils.class, remap = false)
public abstract class LibEntityUtilsMixin {
    @WrapMethod(method = "getTeam")
    private static Object getTeam(Player player, Operation<Object> original) {
        return PlayerSpecialData.of(player).getTeam();
    }
}
