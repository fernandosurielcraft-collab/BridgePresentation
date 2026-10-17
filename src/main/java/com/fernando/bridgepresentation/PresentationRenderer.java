package com.fernando.bridgepresentation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public class PresentationRenderer {
    @SubscribeEvent
    public void renderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        int slide = AnnotationData.activeSlide();
        if (slide < 1) return;
        AnnotationData.load();
        PoseStack pose = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = pose.last().pose();
        for (AnnotationData.Annotation a : AnnotationData.ITEMS) {
            if (a.slide != slide) continue;
            float r=((a.color >> 16)&255)/255f, g=((a.color >> 8)&255)/255f, b=(a.color&255)/255f;
            Vec3 s=a.start(), e=a.end();
            if (a.type.equals("line") || a.type.equals("arrow")) {
                vertex(buffer, matrix, s, r,g,b);
                vertex(buffer, matrix, e, r,g,b);
                if (a.type.equals("arrow")) {
                    Vec3 d=e.subtract(s).normalize();
                    Vec3 side=new Vec3(-d.z,0,d.x);
                    if (side.lengthSqr() < 0.001) side=new Vec3(1,0,0);
                    Vec3 back=e.subtract(d.scale(0.22));
                    Vec3 p1=back.add(side.normalize().scale(0.12));
                    Vec3 p2=back.subtract(side.normalize().scale(0.12));
                    vertex(buffer,matrix,e,r,g,b); vertex(buffer,matrix,p1,r,g,b);
                    vertex(buffer,matrix,e,r,g,b); vertex(buffer,matrix,p2,r,g,b);
                }
            } else if (a.type.equals("box")) {
                double minX=Math.min(s.x,e.x), minY=Math.min(s.y,e.y), minZ=Math.min(s.z,e.z);
                double maxX=Math.max(s.x,e.x)+0.6, maxY=Math.max(s.y,e.y)+0.6, maxZ=Math.max(s.z,e.z)+0.6;
                Vec3[] p={new Vec3(minX,minY,minZ),new Vec3(maxX,minY,minZ),new Vec3(maxX,maxY,minZ),new Vec3(minX,maxY,minZ),
                          new Vec3(minX,minY,maxZ),new Vec3(maxX,minY,maxZ),new Vec3(maxX,maxY,maxZ),new Vec3(minX,maxY,maxZ)};
                int[][] edges={{0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},{0,4},{1,5},{2,6},{3,7}};
                for(int[] edge:edges){vertex(buffer,matrix,p[edge[0]],r,g,b);vertex(buffer,matrix,p[edge[1]],r,g,b);}
            }
        }
        BufferUploader.drawWithShader(buffer.end());
        pose.popPose();
        // Text is rendered separately in camera-facing billboards.
        for (AnnotationData.Annotation a : AnnotationData.ITEMS) {
            if (a.slide == slide && a.type.equals("text")) renderText(mc, pose, cam, a);
        }
        RenderSystem.enableCull();
    }

    private static void vertex(BufferBuilder b, Matrix4f m, Vec3 p, float r,float g,float bl) {
        b.vertex(m, (float)p.x, (float)p.y, (float)p.z).color(r,g,bl,1f).endVertex();
    }

    private static void renderText(Minecraft mc, PoseStack unused, Vec3 cam, AnnotationData.Annotation a) {
        // Billboard text in world space; a small readable label above the anchor.
        PoseStack ps = new PoseStack();
        ps.translate(a.x1-cam.x, a.y1-cam.y+0.25, a.z1-cam.z);
        ps.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        ps.scale(-0.025f, -0.025f, 0.025f);
        var matrices = ps.last().pose();
        float dist = (float)Math.sqrt(cam.distanceToSqr(a.start()));
        if (dist > 48) return;
        mc.font.drawInBatch(a.text, -mc.font.width(a.text)/2f, 0, a.color, false, matrices,
                mc.renderBuffers().bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880);
    }
}
