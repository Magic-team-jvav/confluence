package org.confluence.terraentity.client;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.data.security.SecurityFace;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.confluence.terraentity.TerraEntity.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
public class ClientTerraEntity {

    public ClientTerraEntity(IEventBus modEventBus, ModContainer container) {

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.init());
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static Supplier<String> seKey;
    public static boolean shouldSe(ResourceLocation location, String result){
        String namespace = location.getNamespace();
        if(!namespace.equals(MODID)){
            return false;
        }
//        String path = location.getPath();
//        if(path.startsWith("geo/entity/boss")){
//            return true;
//        }
        if(!result.startsWith("{")){
            return true;
        }
        return false;
    }

    public static String wrapFile(String result, ResourceLocation location) {
        // Please respect the copyright of the model and do not attempt to publicly disseminate encrypted files.
        // 请尊重模型的著作权，加密文件请不要试图公开传播。
        if(ClientTerraEntity.shouldSe(location, result)){
            if(ClientTerraEntity.seKey == null){
                ClientTerraEntity.seKey = Suppliers.memoize(()->{
                    try {
                        ResourceLocation location1 = TerraEntity.space("license.bin");
                        InputStream inputStream = Minecraft.getInstance().getResourceManager().open(location1);
                        return SecurityFace.readKey(inputStream, i-> location1.hashCode());
                    } catch (IOException e){
                        throw new RuntimeException("License Key Error");
                    }
                });
            }
            return SecurityFace.S3.decrypt(result, ClientTerraEntity.seKey.get());
        }
        return result;
    }
}
