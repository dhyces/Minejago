package dev.thomasglasser.minejago.network;

import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.world.level.storage.PowerCapabilityAttacher;
import dev.thomasglasser.minejago.world.level.storage.ActivatedSpinjitzuCapabilityAttacher;
import dev.thomasglasser.minejago.world.level.storage.UnlockedSpinjitzuCapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MinejagoMainChannel
{

    private static SimpleChannel INSTANCE;
    private static int packetID = 0;

    private static int id() { return packetID++;}

    public static void register()
    {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Minejago.MOD_ID, "main_channel"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        SimpleEntityCapabilityStatusPacket.register(net, id());
        SimpleEntityCapabilityStatusPacket.registerRetriever(PowerCapabilityAttacher.POWER_CAPABILITY_RL, PowerCapabilityAttacher::getPowerCapabilityUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(ActivatedSpinjitzuCapabilityAttacher.ACTIVATED_SPINJITZU_CAPABILITY_RL, ActivatedSpinjitzuCapabilityAttacher::getActivatedSpinjitzuCapabilityUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(UnlockedSpinjitzuCapabilityAttacher.UNLOCKED_SPINJITZU_CAPABILITY_RL, UnlockedSpinjitzuCapabilityAttacher::getUnlockedSpinjitzuCapabilityUnwrap);

        net.messageBuilder(ActivateSpinjitzuPacket.class, id())
                .decoder(ActivateSpinjitzuPacket::new)
                .encoder(ActivateSpinjitzuPacket::toBytes)
                .consumerMainThread(ActivateSpinjitzuPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG msg)
    {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayer player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static SimpleChannel getChannel() {
        return INSTANCE;
    }
}
