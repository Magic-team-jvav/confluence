package org.confluence.terraentity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.client.ClientTerraEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.loading.FileLoader;

import java.io.InputStream;
import java.nio.charset.Charset;

@Mixin(value = FileLoader.class, remap = false)
public class FileLoaderMixin {
    @WrapOperation(method = "getFileContents", at = @At(value = "INVOKE", target = "Lorg/apache/commons/io/IOUtils;toString(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/lang/String;"))
    private static String wrapToString(InputStream sw, Charset input, Operation<String> original, @Local(argsOnly = true) ResourceLocation location) {
        String result = original.call(sw, input);
        return ClientTerraEntity.wrapFile(result, location);
    }

}
