package com.chunkdevastator.net;

import java.util.List;

import com.chunkdevastator.ChunkDevastatorConstants;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Server-to-client payloads (Fabric Networking API v1 custom payloads).
 * Client never sends anything.
 *
 * VERIFIED against 26.2: CustomPacketPayload / PayloadTypeRegistry.clientboundPlay() /
 * StreamCodec / ByteBufCodecs (compiled successfully with MC 26.2 + Fabric API 0.158.0).
 */
public final class DoomPayloads {
    private DoomPayloads() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(DoomStartPayload.TYPE, DoomStartPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(DoomSyncPayload.TYPE, DoomSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(DoomClearedPayload.TYPE, DoomClearedPayload.STREAM_CODEC);
    }

    /** A new doom armed somewhere: ALL clients render the warning box. */
    public record DoomStartPayload(ResourceKey<Level> dimension, int chunkX, int chunkZ, long remainingTicks) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<DoomStartPayload> TYPE =
                new CustomPacketPayload.Type<>(ChunkDevastatorConstants.DOOM_START_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, DoomStartPayload> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION), DoomStartPayload::dimension,
                ByteBufCodecs.VAR_INT, DoomStartPayload::chunkX,
                ByteBufCodecs.VAR_INT, DoomStartPayload::chunkZ,
                ByteBufCodecs.VAR_LONG, DoomStartPayload::remainingTicks,
                DoomStartPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Full list of active dooms, sent on player join so the client re-learns mid-countdown state. */
    public record DoomSyncEntry(ResourceKey<Level> dimension, int chunkX, int chunkZ, long remainingTicks) {
        public static final StreamCodec<RegistryFriendlyByteBuf, DoomSyncEntry> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION), DoomSyncEntry::dimension,
                ByteBufCodecs.VAR_INT, DoomSyncEntry::chunkX,
                ByteBufCodecs.VAR_INT, DoomSyncEntry::chunkZ,
                ByteBufCodecs.VAR_LONG, DoomSyncEntry::remainingTicks,
                DoomSyncEntry::new);
    }

    public record DoomSyncPayload(List<DoomSyncEntry> dooms) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<DoomSyncPayload> TYPE =
                new CustomPacketPayload.Type<>(ChunkDevastatorConstants.DOOM_SYNC_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, DoomSyncPayload> STREAM_CODEC =
                DoomSyncEntry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(DoomSyncPayload::new, DoomSyncPayload::dooms);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** A doom fired (chunk deleted): clients drop the warning box immediately. */
    public record DoomClearedPayload(ResourceKey<Level> dimension, int chunkX, int chunkZ) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<DoomClearedPayload> TYPE =
                new CustomPacketPayload.Type<>(ChunkDevastatorConstants.DOOM_CLEARED_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, DoomClearedPayload> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION), DoomClearedPayload::dimension,
                ByteBufCodecs.VAR_INT, DoomClearedPayload::chunkX,
                ByteBufCodecs.VAR_INT, DoomClearedPayload::chunkZ,
                DoomClearedPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
