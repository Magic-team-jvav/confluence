package org.confluence.terraentity.registries.chat.variant;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import org.confluence.terraentity.api.npc.chat.IChatElement;
import org.confluence.terraentity.api.npc.chat.IComplexChatElement;
import org.confluence.terraentity.registries.chat.ChatElementProvider;
import org.confluence.terraentity.registries.chat.ChatProviderTypes;

import java.util.List;

public record RandomElement(List<IChatElement> elements) implements IComplexChatElement {

    public static final MapCodec<RandomElement> MAPCODEC = IChatElement.TYPED_CODEC.listOf().xmap(RandomElement::new, RandomElement::elements).fieldOf("elements").codec().fieldOf("content");

    @Override
    public List<IChatElement> generate(RandomSource random){
        int size = elements.size();
        if(size == 0){
            return null;
        }
        int index = random.nextInt(size);
        return List.of(elements.get(index));
    }

    @Override
    public ChatElementProvider getProvider() {
        return ChatProviderTypes.RANDOM.get();
    }

}
