package com.matchmaking;

import com.gameuser.GameUser;

/**
 * Represents information about a challenge invitation.
 */
public class ChallengeInviteInfo {
    private final GameUser userWhoCreated; // The user who created the challenge invitation
    private final String ipUserWhoCreated; // The IP address of the user who created the challenge invitation
    private final String gameCodeID; // The ID of the game associated with the challenge invitation

    /**
     * Constructs a new ChallengeInviteInfo object.
     *
     * @param userWhoCreated   The user who created the challenge invitation
     * @param ipUserWhoCreated The IP address of the user who created the challenge invitation
     * @param gameCodeID       The ID of the game associated with the challenge invitation
     */
    public ChallengeInviteInfo(GameUser userWhoCreated, String ipUserWhoCreated, String gameCodeID) {
        this.userWhoCreated = userWhoCreated;
        this.ipUserWhoCreated = ipUserWhoCreated;
        this.gameCodeID = gameCodeID;
    }

    /**
     * Gets the user who created the challenge invitation.
     *
     * @return The user who created the challenge invitation
     */
    public GameUser getUserWhoCreated() {
        return userWhoCreated;
    }

    /**
     * Gets the IP address of the user who created the challenge invitation.
     *
     * @return The IP address of the user who created the challenge invitation
     */
    public String getIpUserWhoCreated() {
        return ipUserWhoCreated;
    }

    /**
     * Gets the ID of the game associated with the challenge invitation.
     *
     * @return The ID of the game associated with the challenge invitation
     */
    public String getGameCodeID() {
        return gameCodeID;
    }
}
