package org.confluence.mod.client.connected;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.connected.behaviour.ConnectedTextureBehaviour;

public abstract class AllCTTypes implements CTType {
    public static final AllCTTypes HORIZONTAL = new AllCTTypes("horizontal", 2, ConnectedTextureBehaviour.ContextRequirement.builder().horizontal().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.right ? 1 : 0) + (context.left ? 2 : 0);
        }
    };
    public static final AllCTTypes HORIZONTAL_KRYPPERS = new AllCTTypes("horizontal_kryppers", 2, ConnectedTextureBehaviour.ContextRequirement.builder().horizontal().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return !context.right && !context.left ? 0 : !context.right ? 3 : !context.left ? 2 : 1;
        }
    };
    public static final AllCTTypes VERTICAL = new AllCTTypes("vertical", 2, ConnectedTextureBehaviour.ContextRequirement.builder().vertical().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.up ? 1 : 0) + (context.down ? 2 : 0);
        }
    };
    public static final AllCTTypes OMNIDIRECTIONAL = new AllCTTypes("omnidirectional", 8, ConnectedTextureBehaviour.ContextRequirement.builder().all().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            int tileX = 0, tileY = 0;
            int borders = (!context.up ? 1 : 0) + (!context.down ? 1 : 0) + (!context.left ? 1 : 0) + (!context.right ? 1 : 0);

            if (context.up)
                tileX++;
            if (context.down)
                tileX += 2;
            if (context.left)
                tileY++;
            if (context.right)
                tileY += 2;

            if (borders == 0) {
                if (context.topRight)
                    tileX++;
                if (context.topLeft)
                    tileX += 2;
                if (context.bottomRight)
                    tileY += 2;
                if (context.bottomLeft)
                    tileY++;
            }

            if (borders == 1) {
                if (!context.right) {
                    if (context.topLeft || context.bottomLeft) {
                        tileY = 4;
                        tileX = -1 + (context.bottomLeft ? 1 : 0) + (context.topLeft ? 1 : 0) * 2;
                    }
                }
                if (!context.left) {
                    if (context.topRight || context.bottomRight) {
                        tileY = 5;
                        tileX = -1 + (context.bottomRight ? 1 : 0) + (context.topRight ? 1 : 0) * 2;
                    }
                }
                if (!context.down) {
                    if (context.topLeft || context.topRight) {
                        tileY = 6;
                        tileX = -1 + (context.topLeft ? 1 : 0) + (context.topRight ? 1 : 0) * 2;
                    }
                }
                if (!context.up) {
                    if (context.bottomLeft || context.bottomRight) {
                        tileY = 7;
                        tileX = -1 + (context.bottomLeft ? 1 : 0) + (context.bottomRight ? 1 : 0) * 2;
                    }
                }
            }

            if (borders == 2) {
                if ((context.up && context.left && context.topLeft) || (context.down && context.left && context.bottomLeft)
                        || (context.up && context.right && context.topRight) || (context.down && context.right && context.bottomRight))
                    tileX += 3;
            }

            return tileX + 8 * tileY;
        }
    };
    public static final AllCTTypes CROSS = new AllCTTypes("cross", 4, ConnectedTextureBehaviour.ContextRequirement.builder().axisAligned().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.up ? 1 : 0) + (context.down ? 2 : 0) + (context.left ? 4 : 0) + (context.right ? 8 : 0);
        }
    };
    public static final AllCTTypes RECTANGLE = new AllCTTypes("rectangle", 4, ConnectedTextureBehaviour.ContextRequirement.builder().axisAligned().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            int x = context.left && context.right ? 2 : context.left ? 3 : context.right ? 1 : 0;
            int y = context.up && context.down ? 1 : context.up ? 2 : context.down ? 0 : 3;
            return x + y * 4;
        }
    };

    private final ResourceLocation id;
    private final int sheetSize;
    private final ConnectedTextureBehaviour.ContextRequirement contextRequirement;

    AllCTTypes(String name, int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
        this.id = Confluence.asResource(name);
        this.sheetSize = sheetSize;
        this.contextRequirement = contextRequirement;
        CTTypeRegistry.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public int getSheetSize() {
        return sheetSize;
    }

    @Override
    public ConnectedTextureBehaviour.ContextRequirement getContextRequirement() {
        return contextRequirement;
    }

    public abstract int getTextureIndex(ConnectedTextureBehaviour.CTContext context);
}
