package org.confluence.mod.common.data.saved;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.*;

public enum SpecificMoonVariant implements IExtensibleEnum {
    MC_BRIGHT_BLUE("mc_bright_blue", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_FROST("mc_frost", MoonPhase.FULL_MOON, FrostMoonGameEvent.KEY),
    MC_GREEN("mc_green", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_MYTHRIL("mc_mythril", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_ORANGE("mc_orange", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_PINK("mc_pink", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_PUMPKIN("mc_pumpkin", MoonPhase.FULL_MOON, PumpkinMoonGameEvent.KEY),
    MC_PURPLE("mc_purple", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_RINGED("mc_ringed", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    MC_SMILEY("mc_smiley", MoonPhase.FULL_MOON, GameEventSystem.ALL_EVENT_KEY),
    MC_YELLOW("mc_yellow", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_BLOOD_FULL_MOON("tr_blood_full_moon", MoonPhase.FULL_MOON, BloodMoonGameEvent.KEY),
    TR_BRIGHT_BLUE("tr_bright_blue", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_FIRST_QUARTER("tr_first_quarter", MoonPhase.FIRST_QUARTER, SpecificMoonGameEvent.KEY),
    TR_FROST("tr_frost", MoonPhase.FULL_MOON, FrostMoonGameEvent.KEY),
    TR_FULL_MOON("tr_full_moon", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_GREEN("tr_green", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_MYTHRIL("tr_mythril", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_NEW_MOON("tr_new_moon", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_ORANGE("tr_orange", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_PINK("tr_pink", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_PUMPKIN("tr_pumpkin", MoonPhase.FULL_MOON, PumpkinMoonGameEvent.KEY),
    TR_PURPLE("tr_purple", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_RINGED("tr_ringed", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY),
    TR_SMILEY("tr_smiley", MoonPhase.FULL_MOON, GameEventSystem.ALL_EVENT_KEY),
    TR_THIRD_QUARTER("tr_third_quarter", MoonPhase.THIRD_QUARTER, SpecificMoonGameEvent.KEY),
    TR_WANING_CRESCENT("tr_waning_crescent", MoonPhase.WANING_CRESCENT, SpecificMoonGameEvent.KEY),
    TR_WANING_GIBBOUS("tr_waning_gibbous", MoonPhase.WANING_GIBBOUS, SpecificMoonGameEvent.KEY),
    TR_WAXING_CRESCENT("tr_waxing_crescent", MoonPhase.WAXING_CRESCENT, SpecificMoonGameEvent.KEY),
    TR_WAXING_GIBBOUS("tr_waxing_gibbous", MoonPhase.WAXING_GIBBOUS, SpecificMoonGameEvent.KEY),
    TR_YELLOW("tr_yellow", MoonPhase.FULL_MOON, SpecificMoonGameEvent.KEY);

    public final ResourceLocation texture;
    public final MoonPhase moonPhase;
    public final ResourceKey<? extends GameEvent> associatedEventKey;

    SpecificMoonVariant(String name, MoonPhase moonPhase, ResourceKey<? extends GameEvent> associatedEventKey) {
        this.texture = Confluence.asResource("textures/environment/specific_moon/" + name + ".png");
        this.moonPhase = moonPhase;
        this.associatedEventKey = associatedEventKey;
    }

    public static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(SpecificMoonVariant.class);
    }
}
