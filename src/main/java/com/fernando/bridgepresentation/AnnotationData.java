package com.fernando.bridgepresentation;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class AnnotationData {
    public static final List<Annotation> ITEMS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean loaded = false;

    public static class Annotation {
        public int slide = 1;
        public String type = "arrow"; // arrow, line, text, box
        public double x1, y1, z1, x2, y2, z2;
        public int color = 0xFF3333;
        public String text = "Tração";
        public Annotation() {}
        public Annotation(int slide, String type, Vec3 a, Vec3 b, int color, String text) {
            this.slide=slide; this.type=type;
            x1=a.x; y1=a.y; z1=a.z; x2=b.x; y2=b.y; z2=b.z;
            this.color=color; this.text=text;
        }
        public Vec3 start() { return new Vec3(x1,y1,z1); }
        public Vec3 end() { return new Vec3(x2,y2,z2); }
    }

    public static synchronized void load() {
        if (loaded) return;
        loaded = true;
        Path p = BridgePresentation.configPath();
        try {
            if (Files.exists(p)) {
                Annotation[] data = GSON.fromJson(Files.readString(p), Annotation[].class);
                if (data != null) ITEMS.addAll(Arrays.asList(data));
            } else save();
        } catch (Exception e) {
            System.err.println("[Bridge Presentation] Could not load annotations: " + e.getMessage());
        }
    }

    public static synchronized void save() {
        try {
            Path p = BridgePresentation.configPath();
            Files.createDirectories(p.getParent());
            Files.writeString(p, GSON.toJson(ITEMS), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("[Bridge Presentation] Could not save annotations: " + e.getMessage());
        }
    }

    public static int activeSlide() {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return -1;
        var stack = mc.player.getMainHandItem();
        if (!stack.is(BridgePresentation.SLIDE_REMOTE.get())) return -1;
        return stack.getOrCreateTag().getInt("Slide") <= 0 ? mc.player.getInventory().selected + 1
                : stack.getOrCreateTag().getInt("Slide");
    }

    public static Vec3 getPlacementPoint() {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return Vec3.ZERO;
        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            return ((BlockHitResult) hit).getLocation().add(0, 0.05, 0);
        }
        return mc.player.getEyePosition().add(mc.player.getLookAngle().scale(3));
    }

    public static void add(String type, String text, int color) {
        load();
        Vec3 a = getPlacementPoint();
        Vec3 b = a.add(0, type.equals("text") ? 0 : 1, 0);
        int slide = Math.max(1, activeSlide());
        ITEMS.add(new Annotation(slide, type, a, b, color, text));
        save();
    }
}
