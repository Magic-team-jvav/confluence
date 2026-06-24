package org.confluence.mod.common.item.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.renderer.item.NormalArmorItemRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FamiliarVanityArmorItem extends BaseVanityArmorItem {
    public FamiliarVanityArmorItem(Type type) {
        super(DEFAULT_NAME, type, ModRarity.WHITE);
    }

    public FamiliarVanityArmorItem(ArmorMaterial material, Type type) {
        super(DEFAULT_NAME, material, type, ModRarity.WHITE);
    }

    public FamiliarVanityArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(DEFAULT_NAME, material, type, properties, ModRarity.WHITE);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private NormalArmorItemRenderer<BaseVanityArmorItem> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null) {
                    this.renderer = new NormalArmorItemRenderer<>(name) {
                        @Override
                        public void defaultRender(PoseStack poseStack, BaseVanityArmorItem animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @org.jetbrains.annotations.Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {}
                    };
                }
                return renderer;
            }
        });
    }
}
