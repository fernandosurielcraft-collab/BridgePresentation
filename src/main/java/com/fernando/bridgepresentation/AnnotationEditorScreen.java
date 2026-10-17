package com.fernando.bridgepresentation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AnnotationEditorScreen extends Screen {
    private int slide = 1;
    private int color = 0xFF3333;
    private String label = "Tração";
    protected AnnotationEditorScreen() {
        super(Component.literal("Editor da apresentação da ponte"));
    }

    @Override protected void init() {
        slide = Math.max(1, AnnotationData.activeSlide());
        int cx = width / 2;
        addRenderableWidget(Button.builder(Component.literal("Slide -"), b -> {
            slide = Math.max(1, slide - 1);
            updateStatus();
        }).bounds(cx - 150, 48, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Slide +"), b -> {
            slide = Math.min(9, slide + 1);
            updateStatus();
        }).bounds(cx + 80, 48, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Seta vermelha"), b -> {
            setSlideOnRemote(); AnnotationData.add("arrow", "Força: " + label, 0xFF3333); updateStatus();
        }).bounds(cx - 150, 82, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Linha azul"), b -> {
            setSlideOnRemote(); AnnotationData.add("line", label, 0x3388FF); updateStatus();
        }).bounds(cx + 5, 82, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Texto"), b -> {
            setSlideOnRemote(); AnnotationData.add("text", label, 0xFFFFFF); updateStatus();
        }).bounds(cx - 150, 110, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Caixa verde"), b -> {
            setSlideOnRemote(); AnnotationData.add("box", label, 0x33DD77); updateStatus();
        }).bounds(cx + 5, 110, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Alternar cor"), b -> {
            color = color == 0xFF3333 ? 0x3388FF : color == 0x3388FF ? 0x33DD77 : 0xFF3333;
            updateStatus();
        }).bounds(cx - 150, 138, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Apagar desenhos do slide"), b -> {
            AnnotationData.load();
            AnnotationData.ITEMS.removeIf(a -> a.slide == slide);
            AnnotationData.save(); updateStatus();
        }).bounds(cx + 5, 138, 145, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Fechar"), b -> onClose())
                .bounds(cx - 50, height - 32, 100, 20).build());
    }

    private void setSlideOnRemote() {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && mc.player.getMainHandItem().is(BridgePresentation.SLIDE_REMOTE.get())) {
            mc.player.getMainHandItem().getOrCreateTag().putInt("Slide", slide);
        }
    }
    private void updateStatus() {
        if (minecraft != null) minecraft.player.displayClientMessage(
                Component.literal("Editor: slide " + slide + " | mire para um bloco e adicione um elemento."), true);
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 18, 0xFFFFFF);
        g.drawCenteredString(font, "Slide selecionado: " + slide + " (1–9)", width / 2, 34, 0xCCCCCC);
        g.drawCenteredString(font, "Mire para um bloco antes de adicionar. O desenho fica ancorado no mundo.", width / 2, 170, 0xFFFFFF);
        g.drawCenteredString(font, "Tecla O abre o editor. Segure o controle de slide para exibir anotações.", width / 2, 184, 0xAAAAAA);
        g.drawCenteredString(font, "Nota: esta primeira versão usa textos padrão; edição livre de texto é etapa futura.", width / 2, 198, 0xFFCC66);
        super.render(g, mouseX, mouseY, partialTick);
    }
    @Override public boolean isPauseScreen() { return false; }
}
