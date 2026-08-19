package com.chunkdevastator.client;

import java.util.Comparator;
import java.util.List;

import com.chunkdevastator.ChunkDevastatorConstants;
import com.chunkdevastator.client.render.DoomRenderer;
import com.chunkdevastator.net.DoomPayloads;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/**
 * Client entrypoint: receives doom packets, ticks the local registry, renders
 * the warning boxes (world space) and the countdown numbers (HUD).
 */
public class ChunkDevastatorClient implements ClientModInitializer {
    private final ClientDoomRegistry doomRegistry = new ClientDoomRegistry();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DoomPayloads.DoomStartPayload.TYPE, (payload, context) ->
                context.client().execute(() ->
                        doomRegistry.add(payload.dimension(), payload.chunkX(), payload.chunkZ(), payload.remainingTicks())));

        ClientPlayNetworking.registerGlobalReceiver(DoomPayloads.DoomClearedPayload.TYPE, (payload, context) ->
                context.client().execute(() ->
                        doomRegistry.remove(payload.dimension(), payload.chunkX(), payload.chunkZ())));

        ClientPlayNetworking.registerGlobalReceiver(DoomPayloads.DoomSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> doomRegistry.replaceAll(payload.dooms())));

        ClientTickEvents.END_CLIENT_TICK.register(client -> doomRegistry.tick());

        DoomRenderer renderer = new DoomRenderer(doomRegistry);
        LevelExtractionEvents.END_EXTRACTION.register(renderer::onExtract);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(renderer::onRender);

        // Countdown numbers as a HUD element (top-left), most urgent first.
        // (26.2 removed the old world-space Font#drawInBatch path; see DoomRenderer javadoc.)
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ChunkDevastatorConstants.MOD_ID, "doom_countdown"),
                (extractor, deltaTracker) -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.level == null) {
                        return;
                    }
                    List<ClientDoomRegistry.Entry> dooms = new java.util.ArrayList<>(doomRegistry.visible(mc.level));
                    if (dooms.isEmpty()) {
                        return;
                    }
                    dooms.sort(Comparator.comparingDouble(ClientDoomRegistry.Entry::remainingSeconds));
                    int y = 10;
                    for (int i = 0; i < Math.min(dooms.size(), 5); i++) {
                        ClientDoomRegistry.Entry doom = dooms.get(i);
                        int color = DoomRenderer.warningColor(doom.remainingSeconds());
                        String text = "Chunk (" + doom.chunkX() + ", " + doom.chunkZ() + ") doomed: "
                                + (int) Math.ceil(doom.remainingSeconds()) + "s";
                        extractor.text(mc.font, text, 10, y, color);
                        y += 12;
                    }
                });
    }
}
