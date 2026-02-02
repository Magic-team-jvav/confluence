package org.confluence.terraentity.integration;

import net.neoforged.neoforge.common.NeoForge;
import org.confluence.terraentity.integration.curios.CuriosEvents;
import org.confluence.terraentity.integration.iron_spell.IronSpellEvents;

public class ModChecker {
    public static final ModLoadPair confluence = create("confluence");
    public static final ModLoadPair iris = create("iris");
    public static final ModLoadPair irons_spellbooks = create("irons_spellbooks");
    public static final ModLoadPair curios = create("curios");
    public static final ModLoadPair terraCurio = create("terra_curio");
    public static final ModLoadPair veil = create("veil");
    public static final ModLoadPair sodiumdynamiclights = create("sodiumdynamiclights");

    public static void registerEvents() {
        if (irons_spellbooks.isLoaded()) {
            NeoForge.EVENT_BUS.register(IronSpellEvents.class);
        }
        if (curios.isLoaded()) {
            NeoForge.EVENT_BUS.register(CuriosEvents.class);
        }
    }

    private static ModLoadPair create(String key) {
        return new ModLoadPair(key);
    }
}
