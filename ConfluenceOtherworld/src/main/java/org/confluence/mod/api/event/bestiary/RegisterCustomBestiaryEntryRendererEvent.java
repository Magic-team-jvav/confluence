package org.confluence.mod.api.event.bestiary;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.client.renderer.entity.bestiary.GeoWormBestiaryEntryRenderer;
import org.confluence.terraentity.entity.monster.BaseWorm;
import org.confluence.terraentity.entity.monster.BaseWormPart;
import org.confluence.terraentity.entity.monster.BoneSerpent;
import org.confluence.terraentity.entity.monster.SurefaceWorm;

import java.util.Map;

public class RegisterCustomBestiaryEntryRendererEvent extends Event implements IModBusEvent {
    private static final Map<String, EntityRenderer<LivingEntity>> RENDERERS = new Object2ObjectOpenHashMap<>();
    private final EntityRendererProvider.Context context;

    private RegisterCustomBestiaryEntryRendererEvent(EntityRendererProvider.Context context) {
        this.context = context;
    }

    public EntityRendererProvider.Context getContext() {
        return context;
    }

    public <T extends LivingEntity, R extends EntityRenderer<T>> void register(String key, R renderer) {
        RENDERERS.put(key, (EntityRenderer<LivingEntity>) renderer);
    }

    public void registerBaseWorm(RegistryObject, EntityType<BaseWorm<BaseWormPart>>>holder) {
        register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer<>(context, holder.getId()));
    }

    public void registeSurefaceWorm(RegistryObject, EntityType<SurefaceWorm<BaseWormPart>>>holder) {
        register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer<>(context, holder.getId()));
    }

    public void registerBoneSerpent(RegistryObject, EntityType<BoneSerpent<BaseWormPart>>>holder) {
        register(holder.get().getDescriptionId(), new GeoWormBestiaryEntryRenderer<>(context, holder.getId()));
    }


    public static void postEvent(EntityRendererProvider.Context context) {
        ModLoader.postEvent(new RegisterCustomBestiaryEntryRendererEvent(context));
    }

    public static EntityRenderer<LivingEntity> getRenderer(String key) {
        EntityRenderer<LivingEntity> renderer = RENDERERS.get(key);
        if (renderer == null)
            throw new NullPointerException("No renderer registered for key: " + key);
        return renderer;
    }

    public static boolean hasRenderer(String key) {
        return RENDERERS.containsKey(key);
    }
}
