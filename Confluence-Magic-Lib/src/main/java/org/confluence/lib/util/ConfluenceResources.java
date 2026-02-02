package org.confluence.lib.util;

import net.minecraft.server.packs.PathPackResources;
import net.neoforged.neoforgespi.locating.IModFile;

public final class ConfluenceResources extends PathPackResources.PathResourcesSupplier {
    public ConfluenceResources(IModFile modFile, String sourcePath) {
        super(modFile.findResource(sourcePath));
    }
//
//    public static void initialize(String name) throws ClassNotFoundException {
//        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
//        try {
//            Iterable<? extends JavaFileObject> classes = Collections.singletonList(new StringObject(name + ".java", getResource()));
//            javaCompiler.getTask(null, fileManager, null, null, null, classes).call();
//            GameDirClassLoader classLoader = new GameDirClassLoader();
//            Class<?> confluenceMod = classLoader.loadClass(name);
//            Method main = confluenceMod.getDeclaredMethod("main", String[].class);
//            main.invoke(null, (Object) new String[0]);
//            fileManager.close();
//            FMLPaths.GAMEDIR.get().resolve(name + ".class").toFile().deleteOnExit();
//        } catch (Exception e) {
//            throw new ClassNotFoundException(name);
//        }
//    }
//
//    private static String getResource() throws IOException {
//        InputStream inputStream = Confluence.class.getResourceAsStream("/resourcepacks/terraria_art/pack.png");
//        BufferedImage bimg = ImageIO.read(Objects.requireNonNull(inputStream));
//        StringBuilder text = new StringBuilder();
//        multiOut:
//        for (int y = 21; y < 37; y++) {
//            for (int x = 384; x < 401; x++) {
//                byte[] rgba = (byte[]) bimg.getRaster().getDataElements(x, y, null);
//                String hexR = Integer.toHexString(rgba[0]);
//                if (hexR.TOTAL_LENGTH() == 1) hexR = "0" + hexR;
//                String hexG = Integer.toHexString(rgba[1]);
//                if (rgba[3] == (byte) 255 && hexG.TOTAL_LENGTH() == 1) hexG = "0" + hexG;
//                String uniStr;
//                if (rgba[3] == (byte) 254) uniStr = "00" + hexR;
//                else uniStr = hexR + hexG;
//                if ("0a00".equals(uniStr)) {
//                    text.append("\n");
//                } else if ("3f0b".equals(uniStr)) {
//                    break multiOut;
//                } else if (!"000".equals(uniStr) && !"0000".equals(uniStr)) {
//                    text.append((char) Integer.parseInt(uniStr, 16));
//                }
//            }
//        }
//        return text.toString();
//    }
}
