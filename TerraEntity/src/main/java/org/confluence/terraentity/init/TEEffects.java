package org.confluence.terraentity.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.effect.harmful.*;

public class TEEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TerraEntity.MODID);

    public static final DeferredHolder<MobEffect, DemonicThoughtsEffect> DEMONIC_THOUGHTS = EFFECTS.register("demonic_thoughts", DemonicThoughtsEffect::new);
    public static final DeferredHolder<MobEffect, SummonFocusEffect> SUMMON_FOCUS = EFFECTS.register("summon_mark", SummonFocusEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> FROST_BURN = EFFECTS.register("frost_burn", FrostburnEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> HELLFIRE = EFFECTS.register("hellfire", HellFireEffect::new);
    public static final DeferredHolder<MobEffect, HorrifiedEffect> HORRIFIED = EFFECTS.register("horrified", HorrifiedEffect::new);
    public static final DeferredHolder<MobEffect, TheTongueEffect> THE_TONGUE = EFFECTS.register("the_tongue", TheTongueEffect::new);
    public static final DeferredHolder<MobEffect, CrimsonStorm> CRIMSON_STORM = EFFECTS.register("crimson_storm", CrimsonStorm::new);

}
