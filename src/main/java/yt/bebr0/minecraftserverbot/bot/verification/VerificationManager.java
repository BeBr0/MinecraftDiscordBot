package yt.bebr0.minecraftserverbot.bot.verification;

import org.jetbrains.annotations.Nullable;
import yt.bebr0.minecraftserverbot.database.Database;

import java.util.UUID;

public class VerificationManager {

    @Nullable
    public static Request createRequest(UUID requester, String requestedDiscordId) {
        if (Database.getInstance().getUserByMinecraftID(requester) != null ||
                Database.getInstance().getUserByDiscordID(requestedDiscordId) != null) {

            return null;
        }

        return new Request(requester, requestedDiscordId);
    }


    public static class Request {
        private final UUID requester;
        private final String requestedDiscordId;

        private Request(UUID requester, String requestedDiscordId) {
            this.requester = requester;
            this.requestedDiscordId = requestedDiscordId;
        }

        public UUID getRequester() {
            return requester;
        }

        public String getRequestedDiscordId() {
            return requestedDiscordId;
        }
    }

    public static enum RequestResponse {

        SUCCESS,
        REJECTED
    }
}
