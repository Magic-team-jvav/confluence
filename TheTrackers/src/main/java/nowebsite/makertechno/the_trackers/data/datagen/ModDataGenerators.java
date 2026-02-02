package nowebsite.makertechno.the_trackers.data.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public class ModDataGenerators {
    public static void gatherData(@NotNull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(true, new ModEnglishLanguageProvider(packOutput, "en_us"));
        generator.addProvider(true, new ModChineseLanguageProvider(packOutput, "zh_cn"));
    }
}
