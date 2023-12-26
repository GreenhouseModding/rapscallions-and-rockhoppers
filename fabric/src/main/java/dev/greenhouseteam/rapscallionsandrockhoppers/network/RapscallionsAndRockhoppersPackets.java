package dev.greenhouseteam.rapscallionsandrockhoppers.network;

import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.RapscallionsAndRockhoppersPacketS2C;
import dev.greenhouseteam.rapscallionsandrockhoppers.network.s2c.SyncBlockPosLookPacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;

public class RapscallionsAndRockhoppersPackets {
    public static void registerS2C() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            ClientPlayNetworking.registerReceiver(SyncBlockPosLookPacketS2C.ID, createS2CHandler(SyncBlockPosLookPacketS2C::decode, SyncBlockPosLookPacketS2C::handle));
        });
    }

    public static void sendS2C(RapscallionsAndRockhoppersPacketS2C packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet.getFabricId(), packet.toBuf());
    }

    public static void sendS2CTracking(RapscallionsAndRockhoppersPacketS2C packet, Entity entity) {
        for (ServerPlayer player : PlayerLookup.tracking(entity))
            sendS2C(packet, player);
    }

    private static <T extends RapscallionsAndRockhoppersPacketS2C> ClientPlayNetworking.PlayChannelHandler createS2CHandler(Function<FriendlyByteBuf, T> decode, Consumer<T> handler) {
        return (client, _handler, buf, responseSender) -> handler.accept(decode.apply(buf));
    }
}
