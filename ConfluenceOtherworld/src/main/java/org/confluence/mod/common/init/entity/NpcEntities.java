package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.*;

public class NpcEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Confluence.MODID);

    private static RegistryObject<EntityType<SimpleNPC>> register(String name) {
        return PortDeferredRegisterExtension.register(ENTITIES, name,
                id -> EntityType.Builder.of(SimpleNPC::new, MobCategory.MISC)
                        .sized(0.6F, 1.85F)
                        .clientTrackingRange(10)
                        .build(id.toString()));
    }

    // 默认 NPC
    public static final RegistryObject<EntityType<SimpleNPC>> MERCHANT = register("merchant");
    public static final RegistryObject<EntityType<SimpleNPC>> DYE_TRADER = register("dye_trader");
    public static final RegistryObject<EntityType<SimpleNPC>> PAINTER = register("painter");
    public static final RegistryObject<EntityType<SimpleNPC>> DRYAD = register("dryad");
    public static final RegistryObject<EntityType<SimpleNPC>> WITCH_DOCTOR = register("witch_doctor");
    public static final RegistryObject<EntityType<SimpleNPC>> CLOTHIER = register("clothier");
    public static final RegistryObject<EntityType<SimpleNPC>> MECHANIC = register("mechanic");
    public static final RegistryObject<EntityType<SimpleNPC>> PARTY_GIRL = register("party_girl");
    public static final RegistryObject<EntityType<SimpleNPC>> STYLIST = register("stylist");
    public static final RegistryObject<EntityType<SimpleNPC>> TAX_COLLECTOR = register("tax_collector");
    public static final RegistryObject<EntityType<SimpleNPC>> TRUFFLE = register("truffle");
    public static final RegistryObject<EntityType<SimpleNPC>> WIZARD = register("wizard");
    public static final RegistryObject<EntityType<SimpleNPC>> ZOOLOGIST = register("zoologist");

    // 有特殊行为的 NPC 子类
    public static final RegistryObject<EntityType<GuideNPC>> GUIDE = PortDeferredRegisterExtension.register(ENTITIES, "guide",
            id -> EntityType.Builder.of(GuideNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<NurseNPC>> NURSE = PortDeferredRegisterExtension.register(ENTITIES, "nurse",
            id -> EntityType.Builder.of(NurseNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<DemolitionistNPC>> DEMOLITIONIST = PortDeferredRegisterExtension.register(ENTITIES, "demolitionist",
            id -> EntityType.Builder.of(DemolitionistNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<ArmsDealerNPC>> ARMS_DEALER = PortDeferredRegisterExtension.register(ENTITIES, "arms_dealer",
            id -> EntityType.Builder.of(ArmsDealerNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<GoblinTinkererNPC>> GOBLIN_TINKERER = PortDeferredRegisterExtension.register(ENTITIES, "goblin_tinkerer",
            id -> EntityType.Builder.of(GoblinTinkererNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));

    public static final RegistryObject<EntityType<AnglerNPC>> ANGLER = PortDeferredRegisterExtension.register(ENTITIES, "angler",
            id -> EntityType.Builder.of(AnglerNPC::new, MobCategory.MISC).sized(0.6F, 1.4F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<TravelingMerchantNPC>> TRAVELING_MERCHANT = PortDeferredRegisterExtension.register(ENTITIES, "traveling_merchant",
            id -> EntityType.Builder.of(TravelingMerchantNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<OldManNPC>> OLD_MAN = PortDeferredRegisterExtension.register(ENTITIES, "old_man",
            id -> EntityType.Builder.of(OldManNPC::new, MobCategory.MISC).sized(0.6F, 1.85F).clientTrackingRange(10).build(id.toString()));
}
