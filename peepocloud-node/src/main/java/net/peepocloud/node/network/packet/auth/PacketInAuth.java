package net.peepocloud.node.network.packet.auth;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.auth.Auth;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.PacketHandler;
import net.peepocloud.node.network.NetworkServer;

import java.util.function.Consumer;
public class PacketInAuth extends JsonPacket {

    public PacketInAuth(int id) {
        super(id);
    }

    @AllArgsConstructor
    public static class NetworkAuthHandler implements PacketHandler<PacketInAuth> {

        private NetworkServer networkServer;

        @Override
        public int getId() {
            return -1;
        }

        @Override
        public Class<PacketInAuth> getPacketClass() {
            return PacketInAuth.class;
        }

        @Override
        public void handlePacket(NetworkParticipant networkParticipant, PacketInAuth packet, Consumer<Packet> queryResponse) {
            SimpleJsonObject jsonObject = packet.getSimpleJsonObject();
            if (jsonObject == null)
                return;
            Auth auth = SimpleJsonObject.GSON.fromJson(jsonObject.asJsonObject(), Auth.class);
            if (auth == null) {
                networkParticipant.getChannel().close();
                return;
            }
            if (auth.getAuthKey() == null || auth.getComponentName() == null || auth.getType() == null) {
                networkParticipant.getChannel().close();
                return;
            }

            this.networkServer.handleAuth(networkParticipant, auth);

        }
    }

}
