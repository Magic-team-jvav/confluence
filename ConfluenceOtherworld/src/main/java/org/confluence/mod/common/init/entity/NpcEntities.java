package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.*;
import org.confluence.mod.common.entity.npc.ai.NPCCombatActions;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.projectile.NPCProjectileEffects;
import org.confluence.mod.common.init.item.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NpcEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            net.minecraft.core.registries.Registries.ENTITY_TYPE, Confluence.MODID);

    public static final RegistryObject<EntityType<SimpleNPC>> GUIDE = register("guide", () -> Items.BOW,
            NPCCombatActions.ARROW,
            builder -> builder.damage(8).defense(30).projectileSpeed(1.5).healthRegeneration(2));
    public static final RegistryObject<EntityType<SimpleNPC>> MERCHANT = register("merchant",
            ConsumableItems.THROWING_KNIVE,
            NPCCombatActions.thrown(ConsumableItems.THROWING_KNIVE::toStack, NPCProjectileEffects.NONE),
            builder -> builder.damage(12).projectileSpeed(1.2));
    public static final RegistryObject<EntityType<NurseNPC>> NURSE = register("nurse", NurseNPC::new,
            () -> Items.SPLASH_POTION,
            NPCCombatActions.thrown(() -> new ItemStack(Items.SPLASH_POTION), NPCProjectileEffects.POISON),
            builder -> builder.damage(8).attackRange(5).attackInterval(35).projectileSpeed(1.1), 0.6F, 1.85F);
    public static final RegistryObject<EntityType<SimpleNPC>> DEMOLITIONIST = register("demolitionist",
            ConsumableItems.GRENADE,
            NPCCombatActions.thrown(ConsumableItems.GRENADE::toStack, NPCProjectileEffects.EXPLOSIVE),
            builder -> builder.damage(20).attackRange(8).retreatRange(5).attackInterval(45)
                    .projectileSpeed(0.8));
    public static final RegistryObject<EntityType<SimpleNPC>> DYE_TRADER = register("dye_trader",
            SwordItems.EXOTIC_SCIMITAR, NPCCombatActions.MELEE,
            builder -> builder.damage(11).attackRange(3).retreatRange(3).attackInterval(25));
    public static final RegistryObject<EntityType<AnglerNPC>> ANGLER = register("angler", AnglerNPC::new,
            ConsumableItems.FROST_DAGGERFISH,
            NPCCombatActions.thrown(ConsumableItems.FROST_DAGGERFISH::toStack, NPCProjectileEffects.NONE),
            builder -> builder.projectileSpeed(1.3), 0.6F, 1.4F);
    public static final RegistryObject<EntityType<AnglerNPC>> FEMALE_ANGLER = register("female_angler", AnglerNPC::new,
            ConsumableItems.FROST_DAGGERFISH,
            NPCCombatActions.thrown(ConsumableItems.FROST_DAGGERFISH::toStack, NPCProjectileEffects.NONE),
            builder -> builder.projectileSpeed(1.3), 0.45F, 1.45F);
    public static final RegistryObject<EntityType<SimpleNPC>> ZOOLOGIST = register("zoologist", () -> Items.AIR,
            NPCCombatActions.MELEE,
            builder -> builder.damage(15).attackRange(3).retreatRange(3).attackInterval(20));
    public static final RegistryObject<EntityType<DryadNPC>> DRYAD = register("dryad", DryadNPC::new, () -> Items.AIR,
            NPCCombatActions.DRYAD_WARD,
            builder -> builder.damage(4).attackRange(18.75).retreatRange(4).attackInterval(200), 0.6F, 1.85F);
    public static final RegistryObject<EntityType<SimpleNPC>> PAINTER = register("painter", PaintItems.PAINTBRUSH,
            NPCCombatActions.thrown(PaintItems.PAINT::toStack, NPCProjectileEffects.NONE),
            builder -> builder.damage(8).attackInterval(20).projectileSpeed(1.5));
    public static final RegistryObject<EntityType<SimpleNPC>> ARMS_DEALER = register("arms_dealer",
            npc -> NPCCombatActions.isHardmode() ? GunItems.MINISHARK.get() : GunItems.FLINTLOCK_PISTOL.get(),
            NPCCombatActions.ARMS_DEALER,
            builder -> builder.damage(24).attackRange(13).attackInterval(30).projectileSpeed(2));
    public static final RegistryObject<EntityType<SimpleNPC>> STYLIST = register("stylist",
            SwordItems.STYLISH_SCISSORS, NPCCombatActions.MELEE,
            builder -> builder.attackRange(3).retreatRange(3).attackInterval(20));
    public static final RegistryObject<EntityType<SimpleNPC>> GOBLIN_TINKERER = register("goblin_tinkerer",
            ConsumableItems.SPIKY_BALL, NPCCombatActions.SPIKY_BALL,
            builder -> builder.damage(15).attackRange(9).projectileSpeed(0.75));
    public static final RegistryObject<EntityType<SimpleNPC>> WITCH_DOCTOR = register("witch_doctor",
            GunItems.BLOWGUN,
            NPCCombatActions.thrown(() -> new ItemStack(Items.ARROW), NPCProjectileEffects.POISON),
            builder -> builder.damage(20).attackRange(12).attackInterval(25).projectileSpeed(1.6));
    public static final RegistryObject<EntityType<SimpleNPC>> CLOTHIER = register("clothier",
            ManaWeaponItems.BOOK_OF_SKULLS, NPCCombatActions.SHADOWFLAME_SKULL,
            builder -> builder.damage(16).attackRange(12).attackInterval(35).projectileSpeed(0.7));
    public static final RegistryObject<EntityType<MechanicNPC>> MECHANIC = register("mechanic", MechanicNPC::new,
            BoomerangItems.COMBAT_WRENCH, NPCCombatActions.COMBAT_WRENCH,
            builder -> builder.damage(11).projectileSpeed(1.4), 0.6F, 1.85F);
    public static final RegistryObject<EntityType<SimpleNPC>> PARTY_GIRL = register("party_girl",
            ConsumableItems.GRENADE,
            NPCCombatActions.thrown(ConsumableItems.GRENADE::toStack, NPCProjectileEffects.EXPLOSIVE),
            builder -> builder.damage(30).attackRange(9).retreatRange(5).attackInterval(50)
                    .projectileSpeed(0.9));
    public static final RegistryObject<EntityType<SimpleNPC>> WIZARD = register("wizard",
            ManaWeaponItems.FLOWER_OF_FIRE, NPCCombatActions.FIREBALL,
            builder -> builder.damage(18).attackRange(12).attackInterval(35).projectileSpeed(0.75));
    public static final RegistryObject<EntityType<SimpleNPC>> TAX_COLLECTOR = register("tax_collector",
            () -> Items.STICK, NPCCombatActions.MELEE,
            builder -> builder.attackRange(3).retreatRange(3));
    public static final RegistryObject<EntityType<SimpleNPC>> TRUFFLE = register("truffle", () -> Items.AIR,
            NPCCombatActions.TRUFFLE_SPORES,
            builder -> builder.attackRange(8).attackInterval(25).projectileSpeed(0.8));
    public static final RegistryObject<EntityType<TravelingMerchantNPC>> TRAVELING_MERCHANT = register(
            "traveling_merchant", TravelingMerchantNPC::new,
            npc -> NPCCombatActions.isHardmode() ? Items.BOW : GunItems.FLINTLOCK_PISTOL.get(),
            NPCCombatActions.TRAVELING_MERCHANT,
            builder -> builder.attackRange(12).attackInterval(25).projectileSpeed(2), 0.6F, 1.85F);
    public static final RegistryObject<EntityType<OldManNPC>> OLD_MAN = register("old_man", OldManNPC::new,
            () -> Items.AIR, (npc, target, values) -> {},
            builder -> builder.damage(0).attackRange(0).retreatRange(6).attackInterval(Integer.MAX_VALUE)
                    .healthRegeneration(0),
            0.6F, 1.85F);

    /// 注册没有额外交互或生命周期逻辑、使用固定武器的普通城镇 NPC。
    private static RegistryObject<EntityType<SimpleNPC>> register(String name, Supplier<? extends Item> weapon,
                                                                  NPCCombatProfile.Attack attack,
                                                                  Consumer<NPCCombatProfile.Builder> settings) {
        return register(name, SimpleNPC::new, ignored -> weapon.get(), attack, settings, 0.6F, 1.85F);
    }

    /// 注册没有额外交互或生命周期逻辑、可按当前状态切换武器的普通城镇 NPC。
    private static RegistryObject<EntityType<SimpleNPC>> register(String name, Function<BaseNPC, Item> weapon,
                                                                  NPCCombatProfile.Attack attack,
                                                                  Consumer<NPCCombatProfile.Builder> settings) {
        return register(name, SimpleNPC::new, weapon, attack, settings, 0.6F, 1.85F);
    }

    /// 注册具有专用实体类、碰撞尺寸和固定武器的城镇 NPC。
    private static <T extends BaseNPC> RegistryObject<EntityType<T>> register(String name, NPCFactory<T> factory,
                                                                              Supplier<? extends Item> weapon,
                                                                              NPCCombatProfile.Attack attack,
                                                                              Consumer<NPCCombatProfile.Builder> settings,
                                                                              float width, float height) {
        return register(name, factory, ignored -> weapon.get(), attack, settings, width, height);
    }

    /// 构建不可变战斗定义，并把它交给实体工厂；注册类只负责声明与 Forge 注册。
    private static <T extends BaseNPC> RegistryObject<EntityType<T>> register(String name, NPCFactory<T> factory,
                                                                              Function<BaseNPC, Item> weapon,
                                                                              NPCCombatProfile.Attack attack,
                                                                              Consumer<NPCCombatProfile.Builder> settings,
                                                                              float width, float height) {
        NPCCombatProfile.Builder builder = NPCCombatProfile.builder(weapon, attack);
        settings.accept(builder);
        NPCCombatProfile profile = builder.build();
        return PortDeferredRegisterExtension.register(ENTITIES, name, id -> EntityType.Builder.<T>of(
                        (type, level) -> factory.create(type, level, profile), MobCategory.CREATURE)
                .sized(width, height).clientTrackingRange(10).build(id.toString()));
    }

    @FunctionalInterface
    private interface NPCFactory<T extends BaseNPC> {
        /// 使用注册类型、世界和已经校验的不可变战斗配置创建 NPC。
        T create(EntityType<T> type, Level level, NPCCombatProfile profile);
    }
}
