package org.confluence.mod.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ExtraCodecs;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.mod.Confluence;

import java.util.Map;

public record LucyTheAxeDialogCategory(int entries, boolean cycle) {
    public static final Codec<LucyTheAxeDialogCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("entries").forGetter(LucyTheAxeDialogCategory::entries),
            Codec.BOOL.lenientOptionalFieldOf("cycle", false).forGetter(LucyTheAxeDialogCategory::cycle)
    ).apply(instance, LucyTheAxeDialogCategory::new));
    public static final StreamCodec<ByteBuf, LucyTheAxeDialogCategory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LucyTheAxeDialogCategory::entries,
            ByteBufCodecs.BOOL, LucyTheAxeDialogCategory::cycle,
            LucyTheAxeDialogCategory::new
    );
    public static final ResourceLocation CUTTING_DOWN_A_TREE = Confluence.asResource("cutting_down_a_tree");
    public static final ResourceLocation CUTTING_DOWN_A_GEM_TREE = Confluence.asResource("cutting_down_a_gem_tree");
    public static final ResourceLocation CUTTING_DOWN_A_CACTUS = Confluence.asResource("cutting_down_a_cactus");
    public static final ResourceLocation PLACED_IN_OTHER_CONTAINER = Confluence.asResource("placed_in_other_container");
    public static final ResourceLocation PLACED_BACK_INTO_THE_INVENTORY = Confluence.asResource("placed_back_into_the_inventory");
    public static final ResourceLocation IDLE = Confluence.asResource("idle");
    public static final ResourceLocation THROWN_ON_THE_GROUND = Confluence.asResource("throw_on_the_ground");
    public static final ResourceLocation ATTACK_ENTITY = Confluence.asResource("attack_entity");
    public static final ResourceLocation KILL_ENTITY = Confluence.asResource("kill_entity");
    public static final ResourceLocation DESTROY_WRONG_BLOCK = Confluence.asResource("destroy_wrong_block");

    public static class Loader extends SingleJsonFileReloadListener {
        private static Loader INSTANCE;
        private Map<ResourceLocation, LucyTheAxeDialogCategory> categories = ImmutableMap.of();

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            ImmutableMap.Builder<ResourceLocation, LucyTheAxeDialogCategory> builder = ImmutableMap.builder();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                builder.put(entry.getKey(), LucyTheAxeDialogCategory.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow());
            }
            this.categories = builder.build();
        }

        @Override
        protected ResourceLocation resourcePath() {
            return Confluence.asResource("lucy_the_axe_dialog_categories.json");
        }

        @Override
        protected String identifier() {
            return "Lucy The Axe Dialog Categories";
        }

        @Override
        protected PackType packType() {
            return PackType.CLIENT_RESOURCES;
        }

        public Map<ResourceLocation, LucyTheAxeDialogCategory> getCategories() {
            return categories;
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }
    }
}
