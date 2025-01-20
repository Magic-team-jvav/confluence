package org.confluence.terraentity.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ModConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec.ConfigValue<Integer> BossBarStyle  = BUILDER.comment("Boss Bar Style").defineInRange("boss_bar_style", 0, 0, 2);

    public static int bossBarStyle;

    public static void load(){
        bossBarStyle = BossBarStyle.get();
    }
    public static final ModConfigSpec SPEC = BUILDER.build();

}
