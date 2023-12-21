package dev.greenhouseteam.rapscallionsandrockhoppers.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface RapscallionsAndRockhoppersPacket {

    default FriendlyByteBuf toBuf() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encode(buf);
        return buf;
    }

    void encode(FriendlyByteBuf buf);

    ResourceLocation getFabricId();

}
