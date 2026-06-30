package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.SimpleNPC;

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

    public static final RegistryObject<EntityType<SimpleNPC>> GUIDE = register("guide");
    public static final RegistryObject<EntityType<SimpleNPC>> MERCHANT = register("merchant");
    public static final RegistryObject<EntityType<SimpleNPC>> NURSE = register("nurse");
    public static final RegistryObject<EntityType<SimpleNPC>> DEMOLITIONIST = register("demolitionist");
    public static final RegistryObject<EntityType<SimpleNPC>> DYE_TRADER = register("dye_trader");
    public static final RegistryObject<EntityType<SimpleNPC>> PAINTER = register("painter");
    public static final RegistryObject<EntityType<SimpleNPC>> DRYAD = register("dryad");
    public static final RegistryObject<EntityType<SimpleNPC>> ARMS_DEALER = register("arms_dealer");
    public static final RegistryObject<EntityType<SimpleNPC>> GOBLIN_TINKERER = register("goblin_tinkerer");
    public static final RegistryObject<EntityType<SimpleNPC>> WITCH_DOCTOR = register("witch_doctor");
    public static final RegistryObject<EntityType<SimpleNPC>> CLOTHIER = register("clothier");
    public static final RegistryObject<EntityType<SimpleNPC>> MECHANIC = register("mechanic");
    public static final RegistryObject<EntityType<SimpleNPC>> PARTY_GIRL = register("party_girl");
    public static final RegistryObject<EntityType<SimpleNPC>> STYLIST = register("stylist");
    public static final RegistryObject<EntityType<SimpleNPC>> TAX_COLLECTOR = register("tax_collector");
    public static final RegistryObject<EntityType<SimpleNPC>> TRUFFLE = register("truffle");
    public static final RegistryObject<EntityType<SimpleNPC>> WIZARD = register("wizard");
    public static final RegistryObject<EntityType<SimpleNPC>> ZOOLOGIST = register("zoologist");
    // Special NPCs with custom subclasses
    // public static final RegistryObject<EntityType<AnglerNPC>> ANGLER = ...;
    // public static final RegistryObject<EntityType<TravelingMerchantNPC>> TRAVELING_MERCHANT = ...;
}
