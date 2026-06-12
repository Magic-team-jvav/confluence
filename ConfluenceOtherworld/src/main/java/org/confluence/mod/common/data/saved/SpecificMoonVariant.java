package org.confluence.mod.common.data.saved;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.*;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public enum SpecificMoonVariant implements IExtensibleEnum {
    MC_BLOOD("mc_blood", BloodMoonGameEvent.KEY),
    MC_BRIGHT_BLUE("mc_bright_blue", SpecificMoonGameEvent.KEY),
    MC_FROST("mc_frost", FrostMoonGameEvent.KEY),
    MC_GREEN("mc_green", SpecificMoonGameEvent.KEY),
    MC_MYTHRIL("mc_mythril", SpecificMoonGameEvent.KEY),
    MC_ORANGE("mc_orange", SpecificMoonGameEvent.KEY),
    MC_PINK("mc_pink", SpecificMoonGameEvent.KEY),
    MC_PUMPKIN("mc_pumpkin", PumpkinMoonGameEvent.KEY),
    MC_PURPLE("mc_purple", SpecificMoonGameEvent.KEY),
    MC_RINGED("mc_ringed", SpecificMoonGameEvent.KEY),
    MC_SMILEY("mc_smiley", GameEventSystem.ALL_EVENT_KEY),
    MC_YELLOW("mc_yellow", SpecificMoonGameEvent.KEY),

    TR_COMMON("tr_common", SpecificMoonGameEvent.KEY),
    TR_BLOOD("tr_blood", BloodMoonGameEvent.KEY),
    TR_BRIGHT_BLUE("tr_bright_blue", SpecificMoonGameEvent.KEY),
    TR_FROST("tr_frost", FrostMoonGameEvent.KEY),
    TR_GREEN("tr_green", SpecificMoonGameEvent.KEY),
    TR_MYTHRIL("tr_mythril", SpecificMoonGameEvent.KEY),
    TR_ORANGE("tr_orange", SpecificMoonGameEvent.KEY),
    TR_PINK("tr_pink", SpecificMoonGameEvent.KEY),
    TR_PUMPKIN("tr_pumpkin", PumpkinMoonGameEvent.KEY),
    TR_PURPLE("tr_purple", SpecificMoonGameEvent.KEY),
    TR_RINGED("tr_ringed", SpecificMoonGameEvent.KEY),
    TR_SMILEY("tr_smiley", GameEventSystem.ALL_EVENT_KEY),
    TR_YELLOW("tr_yellow", SpecificMoonGameEvent.KEY);

    public final ResourceLocation texture;
    public final ResourceKey<? extends GameEvent> associatedEventKey;
    private static Map<ResourceKey<? extends GameEvent>, List<SpecificMoonVariant>> resourceKeyListMap;

    SpecificMoonVariant(String name, ResourceKey<? extends GameEvent> associatedEventKey) {
        this.texture = Confluence.asResource("textures/environment/specific_moon/" + name + ".png");
        this.associatedEventKey = associatedEventKey;
    }

    public static List<SpecificMoonVariant> getByGameEvent(ResourceKey<? extends GameEvent> key) {
        if (resourceKeyListMap == null) {
            resourceKeyListMap = new IdentityHashMap<>();
            Map<ResourceKey<? extends GameEvent>, ImmutableList.Builder<SpecificMoonVariant>> map = new IdentityHashMap<>();
            for (SpecificMoonVariant variant : values()) {
                map.computeIfAbsent(variant.associatedEventKey, m -> ImmutableList.builder()).add(variant);
            }
            for (Map.Entry<ResourceKey<? extends GameEvent>, ImmutableList.Builder<SpecificMoonVariant>> entry : map.entrySet()) {
                resourceKeyListMap.put(entry.getKey(), entry.getValue().build());
            }
        }
        return resourceKeyListMap.getOrDefault(key, List.of());
    }

    public static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(SpecificMoonVariant.class);
    }
}
