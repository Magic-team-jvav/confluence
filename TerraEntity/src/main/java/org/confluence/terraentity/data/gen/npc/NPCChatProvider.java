package org.confluence.terraentity.data.gen.npc;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.data.gen.AbstractExistCodecProvider;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.entity.npc.chat.ChatManager;
import org.confluence.terraentity.entity.npc.chat.ToTypeChat;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.registries.chat_condition.variant.ItemInHandChatCondition;
import org.confluence.terraentity.registries.chat_condition.variant.MemoryStateCondition;
import org.confluence.terraentity.registries.chat_condition.variant.WeatherChatCondition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NPCChatProvider extends AbstractExistCodecProvider<ChatManager> {

    public NPCChatProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void run(HolderLookup.Provider provider) {
        this.gen(TENpcEntities.GUIDE, new ChatManager(List.of(

                ChatHolder.NPCEmoji(
                        List.of(TerraEntity.space("textures/gui/sprites/random_gift.png")),
                        new MemoryStateCondition(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)),
                        400
                ),
                ChatHolder.NPCEmoji(
                        List.of(TerraEntity.space("textures/gui/sprites/unknown.png")),
                        new WeatherChatCondition(Optional.of(true), Optional.of(false)),
                        1000
                ),
               ChatHolder.singleItem(Items.BOW.getDefaultInstance(), 600, IChatCondition.not(new ItemInHandChatCondition(Items.BOW)))
        ),new ToTypeChat(Map.of(
                TENpcEntities.GUIDE.get(), List.of(ChatHolder.NPCEmoji(
                        List.of(TerraEntity.space("textures/gui/sprites/random_gift.png")),
                        400
                )),
                TENpcEntities.GOBLIN_TINKERER.get(), List.of(ChatHolder.NPCEmoji(
                        List.of(TerraEntity.space("textures/gui/sprites/unknown.png")),
                        400
                )))
        )));

    }

    protected ResourceLocation getId(DeferredHolder<EntityType<?>, ? > entityType){
        return entityType.getId().withPrefix("npc/chat/");
    }

    protected void gen(DeferredHolder<EntityType<?>, ? > entityType, ChatManager data){
        super.gen(getId(entityType), data);
    }

    @Override
    protected Codec<ChatManager> getCodec() {
        return ChatManager.CODEC;
    }

    @Override
    public String getName() {
        return "NPC Chat Provider";
    }
}
