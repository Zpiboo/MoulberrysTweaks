package com.moulberry.moulberrystweaks.packet;

import com.moulberry.moulberrystweaks.DebugMovementData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DebugMovementDataPacket(DebugMovementData debugMovementData) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath("moulberrystweaks", "debug_movement_data");
    public static final Type<DebugMovementDataPacket> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<FriendlyByteBuf, DebugMovementDataPacket> STREAM_CODEC = new DebugMovementDataPacketStreamCodec();

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class DebugMovementDataPacketStreamCodec implements StreamCodec<FriendlyByteBuf, DebugMovementDataPacket> {
        @Override
        public DebugMovementDataPacket decode(FriendlyByteBuf friendlyByteBuf) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void encode(FriendlyByteBuf friendlyByteBuf, DebugMovementDataPacket debugMovementDataPacket) {
            DebugMovementData data = debugMovementDataPacket.debugMovementData();
            friendlyByteBuf.writeVector3f(data.baseTickVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.localPlayerAiStepVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.livingAiStepVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.travelVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.moveRelativeVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.moveVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.afterTravelVelocity.toVector3f());
            friendlyByteBuf.writeVector3f(data.moveInput.toVector3f());
            friendlyByteBuf.writeFloat(data.moveRelativeSpeed);
            friendlyByteBuf.writeBoolean(data.isInWater);
            friendlyByteBuf.writeBoolean(data.isInLava);
            friendlyByteBuf.writeBoolean(data.isSwimming);
            friendlyByteBuf.writeBoolean(data.isSprinting);
            friendlyByteBuf.writeBoolean(data.hasSprintSpeedModifier);
        }
    }

}
