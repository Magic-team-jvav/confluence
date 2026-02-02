package org.confluence.terraentity.entity.npc.chat;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.ai.ISkill;
import org.confluence.terraentity.entity.ai.goal.skill.SkillCooldownManager;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * NPC 对话管理器
 */
public class ChatManager extends SkillCooldownManager {
    public static final Codec<ChatManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ChatHolder.CODEC).fieldOf("chatHolders").forGetter(ChatManager::getChatHolders),
            ToTypeChat.CODEC.lenientOptionalFieldOf("toOtherChat", null).forGetter(i -> i.toOtherChat)
    ).apply(instance, ChatManager::new));

    private final List<ChatHolder> chatHolders;
    private final ToTypeChat toOtherChat;

    private AbstractTerraNPC owner;
    private int forceCooldown = 50;

    public ChatManager(List<ChatHolder> chatHolders) {
        this(chatHolders, null);
    }

    public ChatManager(List<ChatHolder> chatHolders, ToTypeChat toOtherChat) {
        this.chatHolders = chatHolders;
        this.toOtherChat = toOtherChat;
        chatHolders.forEach(this::addSkill);
    }

    private List<ChatHolder> getChatHolders() {
        return chatHolders;
    }

    public Optional<ToTypeChat> getToOtherChat() {
        return Optional.ofNullable(toOtherChat);
    }

    public void setOwner(AbstractTerraNPC owner) {
        this.owner = owner;
        this.chatHolders.forEach(chatHolder -> chatHolder.setOwner(owner));
        if (this.toOtherChat != null) {
            toOtherChat.chatMap.values().forEach(e -> e.forEach(i -> i.setOwner(owner)));
        }
    }

    @Override
    public void update(int deltaTime) {
        --this.forceCooldown;
        if (this.owner == null) {
            return;
        }
        super.update(deltaTime);
        for (ChatHolder chatHolder : chatHolders) {
            if (chatHolder.canChat(owner, chatHolder)) {
                this.triggerSkill(chatHolder);
            }
        }
    }

    @Override
    public boolean canTriggerSkill(ISkill skill) {
        if (this.forceCooldown > 0) {
            return false;
        }
        if (super.canTriggerSkill(skill)) {
            return true;
        }
        this.exchangeQueue();
        return false;
    }

    @Override
    public boolean triggerSkill(ISkill skill) {
        if (super.triggerSkill(skill)) {
            this.forceCooldown = 50;
            this.owner.setChat(((ChatHolder) skill).getChat());
            return true;
        }
        return false;
    }

    public static @Nullable ChatManager get(EntityType<?> type) {
        return Loader.getInstance().chatManagers.getOrDefault(type, null);
    }

    public static class Loader extends SimpleJsonResourceReloadListener {
        public static final String KEY = "npc/chat";
        private static Loader INSTANCE;
        private Map<EntityType<?>, ChatManager> chatManagers = ImmutableMap.of();

        public Loader() {
            super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), KEY);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            ImmutableMap.Builder<EntityType<?>, ChatManager> map1 = ImmutableMap.builder();

            ConditionalOps<JsonElement> ops = makeConditionalOps();
            map.forEach((k, v) -> CODEC.parse(ops, v).ifSuccess(result -> {
                if (TerraEntity.MODID.equals(k.getNamespace())) {
                    map1.put(BuiltInRegistries.ENTITY_TYPE.get(k), result);
                } else {
                    TerraEntity.LOGGER.warn("Unable to load chat for non-terra_entity npc: {}", k);
                }
            }).ifError(err -> TerraEntity.LOGGER.error("Failed to parse chat list {} : {}", k, err.message())));

            this.chatManagers = map1.build();
        }

        public Map<EntityType<?>, ChatManager> getChatManagers() {
            return chatManagers;
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }
    }
}
