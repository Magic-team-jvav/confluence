package org.confluence.terraentity.integration.iris;

import org.confluence.terraentity.client.event.RenderEvent;

public class IrisHelper {

    // client only
    public static boolean isIrisShader(){
        return RenderEvent.isIrisShader;
    }


}
