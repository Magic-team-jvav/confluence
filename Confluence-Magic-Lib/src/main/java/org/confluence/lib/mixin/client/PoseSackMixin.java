package org.confluence.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import org.confluence.lib.mixed.IPoseStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PoseStack.class)
public abstract class PoseSackMixin implements IPoseStack {}
