package org.confluence.mod.common.data.saved;

import net.minecraft.util.StringRepresentable;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ReloadResource implements StringRepresentable {

    SPELUNKER;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static void execute(ReloadResource resource){
        if(resource == SPELUNKER){
            SpelunkerHelper.getSingleton().reloadSpecular();
        }
    }

}
