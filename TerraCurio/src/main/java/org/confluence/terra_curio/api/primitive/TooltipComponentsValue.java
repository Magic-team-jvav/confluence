package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.confluence.terra_curio.TerraCurio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TooltipComponentsValue implements PrimitiveValue<List<TooltipComponentsValue.Storage>> {
    public static final Codec<TooltipComponentsValue> CODEC = Storage.CODEC.listOf().xmap(TooltipComponentsValue::new, TooltipComponentsValue::get);
    public static final CombineRule<List<Storage>, TooltipComponentsValue> EXPANSION = CombineRule.register((a, b) -> {
        ArrayList<Storage> list = new ArrayList<>(a);
        a.addAll(b);
        return list;
    }, "tooltip_components_expansion");

    private final List<Storage> storages;
    private final Set<Storage> set;

    public TooltipComponentsValue(List<Storage> storages) {
        this.storages = storages;
        this.set = new HashSet<>(storages);
    }

    @Override
    public List<Storage> get() {
        return storages;
    }

    @Override
    public Codec<TooltipComponentsValue> codec() {
        return CODEC;
    }

    public Set<Storage> getSet() {
        return set;
    }

    public static Storage create(ResourceLocation texture, Component text) {
        return new Storage(texture, text);
    }

    public static Storage create(String texture, Component text) {
        return new Storage(TerraCurio.asResource(texture), text);
    }

    public static Storage create(ResourceLocation texture, String text) {
        return new Storage(texture, Component.literal(text));
    }

    public static Storage create(String texture, String text) {
        return new Storage(TerraCurio.asResource(texture), Component.literal(text));
    }

    public static Storage create(String path) {
        return new Storage(TerraCurio.asResource("textures/gui/information/" + path + ".png"), Component.translatable("tooltip.terra_curio." + path));
    }

    public record Storage(ResourceLocation texture, Component text) {
        public static final Codec<Storage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Storage::texture),
                ComponentSerialization.CODEC.fieldOf("text").forGetter(Storage::text)
        ).apply(instance, Storage::new));
    }

    public record Multi(List<Storage> storages) implements TooltipComponent {}
}
