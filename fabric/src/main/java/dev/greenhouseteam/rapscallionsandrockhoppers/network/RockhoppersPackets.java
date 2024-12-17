package dev.greenhouseteam.rapscallionsandrockhoppers.network;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.InvalidateCachedPenguinTypePacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBlockPosLookPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBoatLinksAttachmentPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncPlayerLinksAttachmentPacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class RockhoppersPackets {
    public static void registerS2C() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            ClientPlayNetworking.registerReceiver(SyncBlockPosLookPacketS2C.TYPE, (payload, context) -> payload.handle());
            ClientPlayNetworking.registerReceiver(InvalidateCachedPenguinTypePacketS2C.TYPE, (payload, context) -> payload.handle());
            ClientPlayNetworking.registerReceiver(SyncBoatLinksAttachmentPacketS2C.TYPE, (payload, context) -> payload.handle());
            ClientPlayNetworking.registerReceiver(SyncPlayerLinksAttachmentPacketS2C.TYPE, (payload, context) -> payload.handle());
        });
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playS2C().register(SyncBlockPosLookPacketS2C.TYPE, SyncBlockPosLookPacketS2C.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(InvalidateCachedPenguinTypePacketS2C.TYPE, InvalidateCachedPenguinTypePacketS2C.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBoatLinksAttachmentPacketS2C.TYPE, SyncBoatLinksAttachmentPacketS2C.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPlayerLinksAttachmentPacketS2C.TYPE, SyncPlayerLinksAttachmentPacketS2C.STREAM_CODEC);
    }

    public static void sendS2C(CustomPacketPayload packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendS2CTracking(CustomPacketPayload packet, Entity entity) {
        for (ServerPlayer player : PlayerLookup.tracking(entity))
            sendS2C(packet, player);
    }
}
