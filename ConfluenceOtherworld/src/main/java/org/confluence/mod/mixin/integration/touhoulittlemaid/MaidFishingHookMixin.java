package org.confluence.mod.mixin.integration.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.MaidFishingHook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.item.fishing.AbstractFishingPole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Pseudo
@Mixin(targets = "com/github/tartaricacid/touhoulittlemaid/entity/projectile/MaidFishingHook", remap = false)
public abstract class MaidFishingHookMixin implements SelfGetter<MaidFishingHook> {
    @Shadow
    @Nullable
    public abstract EntityMaid getMaidOwner();

    @Inject(method = "getLoot", at = @At("RETURN"), cancellable = true)
    private void getLootMixin(MinecraftServer server, LootParams lootParams, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (this.getMaidOwner() != null && getMaidOwner().getMainHandItem().getItem() instanceof AbstractFishingPole) {
            cir.setReturnValue(server.reloadableRegistries().getLootTable(ModLootTables.FISHING).getRandomItems(lootParams));
        }
    }

    @Inject(method = "addExtraLoot", at = @At("RETURN"))
    private void addExtraLootMixin(List<ItemStack> randomItems, CallbackInfo ci) {
        if (getMaidOwner() != null) {
            float chance = getMaidOwner().hasEffect(ModEffects.CRATE) ? 0.25F : 0.1F;
            if (getMaidOwner().level() instanceof ServerLevel level) {
                if (level.random.nextFloat() < chance) {
                    randomItems.addAll(level.getServer().reloadableRegistries().getLootTable(ModLootTables.CRATE)
                            .getRandomItems(new LootParams.Builder(level)
                                    .withParameter(LootContextParams.ORIGIN, confluence$self().position())
                                    .withParameter(LootContextParams.THIS_ENTITY, confluence$self())
                                    .create(LootContextParamSets.GIFT)));
                }
            }
        }
    }
}
