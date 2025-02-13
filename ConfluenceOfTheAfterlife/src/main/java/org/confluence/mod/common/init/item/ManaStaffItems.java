package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BaseManaStaffProjectileEntity;
import org.confluence.mod.common.entity.projectile.BaseManaStaffProjectileEntity.Variant;
import org.confluence.mod.common.entity.projectile.ThunderZapperProjectile;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class ManaStaffItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<ManaStaffItem> WAND_OF_SPARKING = ITEMS.register("wand_of_sparking", () -> new ManaStaffItem(ModRarity.BLUE, BaseManaStaffProjectileEntity.Spark::new, 2, 7.0F, 26, 0.14));
    public static final Supplier<ManaStaffItem> WAND_OF_FROSTING = ITEMS.register("wand_of_frosting", () -> new ManaStaffItem(ModRarity.BLUE, BaseManaStaffProjectileEntity.Frost::new, 2, 7.0F, 26, 0.14));
    public static final Supplier<ManaStaffItem> THUNDER_ZAPPER = ITEMS.register("thunder_zapper", () -> new ManaStaffItem(ModRarity.BLUE, ThunderZapperProjectile::new, 7, 16, 17, 0.04));
    public static final Supplier<ManaStaffItem> RUBY_STAFF = ITEMS.register("ruby_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.RUBY), 7, 9.0F, 28, 0.4));
    public static final Supplier<ManaStaffItem> AMBER_STAFF = ITEMS.register("amber_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.AMBER), 7, 9.0F, 28, 0.4));
    public static final Supplier<ManaStaffItem> TOPAZ_STAFF = ITEMS.register("topaz_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.TOPAZ), 5, 6.5F, 36, 0.4));
    public static final Supplier<ManaStaffItem> EMERALD_STAFF = ITEMS.register("emerald_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.EMERALD), 6, 8.0F, 32, 0.4));
    public static final Supplier<ManaStaffItem> SAPPHIRE_STAFF = ITEMS.register("sapphire_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.SAPPHIRE), 6, 7.5F, 34, 0.4));
    public static final Supplier<ManaStaffItem> AMETHYST_STAFF = ITEMS.register("amethyst_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.AMETHYST), 5, 6.0F, 37, 0.4));
    public static final Supplier<ManaStaffItem> DIAMOND_STAFF = ITEMS.register("diamond_staff", () -> new ManaStaffItem(ModRarity.BLUE, (player, level) -> new BaseManaStaffProjectileEntity(player, level, Variant.DIAMOND), 8, 9.5F, 26, 0.4));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
