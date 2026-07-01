package org.confluence.mod.common.entity.npc.dialog;

import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.util.List;

public record NPCDialog(List<String> keys) {
    public String randomKey(RandomSource random) {
        return Util.getRandom(keys, random);
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }
}
