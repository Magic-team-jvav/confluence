package org.confluence.mod.common.data.saved;

import net.minecraft.util.StringRepresentable;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * fixme 有一说一应该改成reloadable resource
 */
public enum ReloadResource implements StringRepresentable {
    SPELUNKER;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static void execute(ReloadResource resource) {
        if (resource == SPELUNKER && LibUtils.isPhysicalClient()) {
            SpelunkerHelper.getSingleton().reloadSpecular();
        }
    }
}
