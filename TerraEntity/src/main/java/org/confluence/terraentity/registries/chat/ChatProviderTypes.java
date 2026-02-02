package org.confluence.terraentity.registries.chat;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chat.variant.*;

import java.util.function.Supplier;

public class ChatProviderTypes {
    public static final DeferredRegister<ChatElementProvider> TYPES = DeferredRegister.create(TERegistries.Keys.CHAT_ELEMENT_PROVIDER, TerraEntity.MODID);

    public static final Supplier<ChatElementProvider> COMPONENT = TYPES.register("component", ()-> new ChatElementProvider(StringChatElement.MAPCODEC));
    public static final Supplier<ChatElementProvider> ITEM = TYPES.register("item", ()-> new ChatElementProvider(ItemChatElement.MAPCODEC));
    public static final Supplier<ChatElementProvider> SEPARATOR = TYPES.register("separator", ()-> new ChatElementProvider(SeparatorElement.CODEC));
    public static final Supplier<ChatElementProvider> SPIRIT = TYPES.register("spirit", ()-> new ChatElementProvider(SpriteChatElement.MAPCODEC));
    public static final Supplier<ChatElementProvider> RANDOM = TYPES.register("random", ()-> new ChatElementProvider(RandomElement.MAPCODEC));


}
