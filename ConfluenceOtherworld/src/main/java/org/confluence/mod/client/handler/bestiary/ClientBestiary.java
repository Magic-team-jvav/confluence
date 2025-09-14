package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.BestiaryEntry;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientBestiary extends SingleJsonFileReloadListener {
    private static ClientBestiary INSTANCE;

    private LinkedHashMap<EntityType<?>, ClientBestiaryEntry> entries = Maps.newLinkedHashMap();

    private ClientBestiary() {}

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
        List<ClientBestiaryEntry> list = Lists.newArrayList();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ClientBestiaryEntry.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).ifSuccess(result -> {
                result.type = BuiltInRegistries.ENTITY_TYPE.get(entry.getKey());
                list.add(result);
            });
        }
        LinkedHashMap<EntityType<?>, ClientBestiaryEntry> map = Maps.newLinkedHashMap();
        list.stream().sorted(Comparator.comparingInt(ClientBestiaryEntry::getOrder))
                .forEachOrdered(entry -> map.put(entry.type, entry));
        this.entries = map;
    }

    @Override
    protected ResourceLocation resourcePath() {
        return Confluence.asResource("bestiary");
    }

    @Override
    protected String identifier() {
        return "bestiary";
    }

    public LinkedHashMap<EntityType<?>, ClientBestiaryEntry> getEntries() {
        return entries;
    }

    public static ClientBestiary getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientBestiary();
        }
        return INSTANCE;
    }

    // 玩家进入存档统一同步
    // 随后只需更新部分实体
    public static void handle(Either<Map<EntityType<?>, BestiaryEntry>, EntityType<?>> either) {
        either.ifLeft(map -> {
            LinkedHashMap<EntityType<?>, ClientBestiaryEntry> entries = getInstance().getEntries();
            for (Map.Entry<EntityType<?>, BestiaryEntry> entry : map.entrySet()) {
                BestiaryEntry be = entry.getValue();
                ClientBestiaryEntry cbe = entries.computeIfAbsent(entry.getKey(), type -> {
                    ClientBestiaryEntry unknown = new ClientBestiaryEntry();
                    unknown.type = type;
                    return unknown;
                });
                cbe.killedByCount = be.killedByCount;
                cbe.maxHealth = be.maxHealth;
                cbe.knockbackResistance = be.knockbackResistance;
                cbe.attackDamage = be.attackDamage;
                cbe.armor = be.armor;
                cbe.drops = be.drops;
            }
        }).ifRight(type -> {
            ClientBestiaryEntry entry = getInstance().getEntries().get(type);
            if (entry != null) entry.killedByCount++;
        });
    }
}
