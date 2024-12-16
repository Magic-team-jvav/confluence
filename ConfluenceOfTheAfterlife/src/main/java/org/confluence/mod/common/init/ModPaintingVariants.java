package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;

import java.util.function.Supplier;

public final class ModPaintingVariants {
    public static final DeferredRegister<PaintingVariant> VARIANTS = DeferredRegister.create(Registries.PAINTING_VARIANT, Confluence.MODID);

    public static final Supplier<PaintingVariant> MAGIC_HARP = register("magic_harp");
    public static final Supplier<PaintingVariant> WESTERNAT = register("westernat");
    public static final Supplier<PaintingVariant> COOOBRID = register("cooobrid");
    public static final Supplier<PaintingVariant> NAKINOSI = register("nakinosi");
    public static final Supplier<PaintingVariant> MAKER = register("maker");
    public static final Supplier<PaintingVariant> MUSTARD_OASIS = register("mustard_oasis");
    public static final Supplier<PaintingVariant> A_PIGEON_DELIGHT = register("a_pigeon_delight");
    public static final Supplier<PaintingVariant> SHEEP_MINK = register("sheep_mink");
    public static final Supplier<PaintingVariant> VOILA = register("voila");
    public static final Supplier<PaintingVariant> XUANYU_1725 = register("xuanyu_1725");
    public static final Supplier<PaintingVariant> SHADOW_END = register("shadow_end");
    public static final Supplier<PaintingVariant> HUNAO = register("hunao");
    public static final Supplier<PaintingVariant> KL_JIANA = register("kl_jiana");
    public static final Supplier<PaintingVariant> SIHUAI_2412 = register("sihuai_2412");
    public static final Supplier<PaintingVariant> OLD_SHEEP = register("old_sheep");
    public static final Supplier<PaintingVariant> SLIME_DRAGON = register("slime_dragon");
    public static final Supplier<PaintingVariant> KHAKI_COFFEE_BEANS = register("khaki_coffee_beans");
    public static final Supplier<PaintingVariant> UQTQU_DAY = register("uqtqu_day");
    public static final Supplier<PaintingVariant> EMERALD_SHENYI = register("emerald_shenyi");
    public static final Supplier<PaintingVariant> CHROMATIC = register("chromatic");

    private static Supplier<PaintingVariant> register(String path) {
        return VARIANTS.register(path, () -> new PaintingVariant(2, 2, Confluence.asResource(path)));
    }
}
