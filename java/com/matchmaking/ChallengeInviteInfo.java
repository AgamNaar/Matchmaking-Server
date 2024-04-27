package com.matchmaking;

/**
 * Represents information about a challenge invitation.
 */
public class ChallengeInviteInfo {
    // Information about the player who created the challenge
    private final String userNameOfPlayerWhoCreated; // Username of the player who created the challenge
    private final String ipOfUserWhoCreated; // IP address of the player who created the challenge
    private final String gameCodeID; // Unique ID of the game/challenge
    private final String tokenOfPLayerWhoCreated; // Token of the player who created the challenge

    /**
     * Constructs a new ChallengeInviteInfo object with the specified details.
     *
     * @param userNameOfPlayerWhoCreated The username of the player who created the challenge.
     * @param ipOfUserWhoCreated         The IP address of the player who created the challenge.
     * @param gameCodeID                 The unique ID of the game/challenge.
     * @param tokenOfPLayerWhoCreated    The token of the player who created the challenge.
     */
    public ChallengeInviteInfo(String userNameOfPlayerWhoCreated, String ipOfUserWhoCreated,
                               String gameCodeID, String tokenOfPLayerWhoCreated) {
        this.userNameOfPlayerWhoCreated = userNameOfPlayerWhoCreated;
        this.ipOfUserWhoCreated = ipOfUserWhoCreated;
        this.gameCodeID = gameCodeID;
        this.tokenOfPLayerWhoCreated = tokenOfPLayerWhoCreated;
    }

    /**
     * Gets the username of the player who created the challenge.
     *
     * @return The username of the player who created the challenge.
     */
    public String getUserNameOfPlayerWhoCreated() {
        return userNameOfPlayerWhoCreated;
    }

    /**
     * Gets the IP address of the player who created the challenge.
     *
     * @return The IP address of the player who created the challenge.
     */
    public String getIpOfUserWhoCreated() {
        return ipOfUserWhoCreated;
    }

    /**
     * Gets the unique ID of the game/challenge.
     *
     * @return The unique ID of the game/challenge.
     */
    public String getGameCodeID() {
        return gameCodeID;
    }

    /**
     * Gets the token of the player who created the challenge.
     *
     * @return The token of the player who created the challenge.
     */
    public String getTokenOfPLayerWhoCreated() {
        return tokenOfPLayerWhoCreated;
    }
}
