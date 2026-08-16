package org.confluence.mod.common.data.gen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.chat.ChatLine;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.confluence.mod.common.entity.npc.trade.conditions.AttackTargetCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.NPCItemInHandCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.WeatherCondition;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/// 生成内置 NPC 的默认聊天规则。
public final class NPCChatProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public NPCChatProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "npc/chat");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, List<ChatLine>> chats = new LinkedHashMap<>();
        chats.put(Confluence.asResource("guide"), List.of(
                new ChatLine(NPCChat.emoji("confluence:textures/gui/sprites/random_gift.png"), AttackTargetCondition.INSTANCE, 400),
                new ChatLine(NPCChat.emoji("confluence:textures/gui/sprites/unknown.png"), new WeatherCondition(Optional.of(true), Optional.of(false)), 1000),
                new ChatLine(new NPCChat(Optional.empty(), Optional.empty(), Optional.of(new ItemStack(Items.BOW))), new NPCItemInHandCondition(Items.BOW).not(), 600)
        ));
        return CompletableFuture.allOf(chats.entrySet().stream().map(entry -> save(output, entry.getKey(), entry.getValue())).toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> save(CachedOutput output, ResourceLocation npcId, List<ChatLine> lines) {
        DataResult<JsonElement> encoded = ChatLine.CODEC.listOf().encodeStart(JsonOps.INSTANCE, lines);
        JsonElement json = encoded.result().orElseThrow(() -> new IllegalStateException("Unable to encode NPC chat " + npcId + ": " + encoded.error().map(DataResult.PartialResult::message).orElse("unknown error")));
        Path path = pathProvider.json(npcId);
        return DataProvider.saveStable(output, json, path);
    }

    @Override
    public String getName() {
        return "Confluence NPC Chats";
    }
}
