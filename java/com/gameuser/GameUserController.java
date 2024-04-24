package com.gameuser;

import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing game user operations.
 */
@RestController
@RequestMapping(path = "user")
public class GameUserController {

    private final GameUserService gameUserService;

    /**
     * Constructor for initializing GameUserController with GameUserService.
     *
     * @param gameUserService The GameUserService instance.
     */
    @Autowired
    public GameUserController(GameUserService gameUserService) {
        this.gameUserService = gameUserService;
    }

    /**
     * Endpoint for creating a new game user account.
     *
     * @param gameUser As JSON, The GameUser object representing the account to be created.
     *                 Should have userName, email, password and rating
     * @return A ServerResponse indicating the outcome of the operation, including the user's token.
     */
    @PostMapping(path = "/create")
    public ServerResponse createAnAccount(@RequestBody GameUser gameUser) {
        return gameUserService.createAnAccount(gameUser);
    }

    /**
     * Endpoint for deleting an existing game user account.
     *
     * @param gameUser As JSON, gameUser The GameUser object representing the account to be deleted.
     *                 Should have userName and password.
     * @return A ServerResponse indicating the outcome of the operation.
     */
    @PostMapping(path = "/delete")
    public ServerResponse deleteAnAccount(@RequestBody GameUser gameUser) {
        return gameUserService.deleteAnAccount(gameUser);
    }

    /**
     * Endpoint for logging into a game user account.
     *
     * @param gameUser As JSON, The GameUser object representing the account to be logged into.
     *                 Should have userName and password.
     * @return A ServerResponse indicating the outcome of the operation, including the user's token.
     */
    @PostMapping(path = "/login")
    public ServerResponse logIntoAccount(@RequestBody GameUser gameUser) {
        return gameUserService.logIntoAccount(gameUser);
    }

    /**
     * Retrieves the top four rated players.
     *
     * @return A ServerResponse containing information about the top four rated players.
     */
    @GetMapping(path = "/get-top4")
    public ServerResponse getTopFourRatedPlayer() {
        return gameUserService.getTopFourRatedPlayer();
    }
}
