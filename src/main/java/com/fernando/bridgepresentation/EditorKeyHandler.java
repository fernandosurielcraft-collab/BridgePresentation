package com.fernando.bridgepresentation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EditorKeyHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || BridgePresentation.ClientEvents.EDIT_KEY == null) return;
        if (BridgePresentation.ClientEvents.EDIT_KEY.consumeClick()) {
            mc.setScreen(new AnnotationEditorScreen());
        }
    }
}
