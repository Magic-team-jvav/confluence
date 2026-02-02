package org.confluence.terraentity.registries.chat.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.Font;
import org.confluence.terraentity.api.npc.chat.IChatElement;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.registries.chat.ChatElementProvider;
import org.confluence.terraentity.registries.chat.ChatProviderTypes;

public class SeparatorElement implements IChatElement<Void> {

    public static SeparatorElement INSTANCE = new SeparatorElement();

    public static MapCodec<SeparatorElement> CODEC = Codec.unit(() -> INSTANCE).fieldOf("content");

    @Override
    public Void getContent() {
        return null;
    }

    @Override
    public ChatElementProvider getProvider() {
        return ChatProviderTypes.SEPARATOR.get();
    }

    @Override
    public IChatRenderer getRenderer() {
        return null;
    }

    @Override
    public int wrapWidth(int input, Font font) {
        return 0;
    }

    @Override
    public int warpHeight(int input, Font font) {
        return input + 12;
    }
}
