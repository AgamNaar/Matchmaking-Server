package com.GameUser;

import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing game users.
 */
@RestController
@RequestMapping(path = "user")
public class GameUserController {

    private final GameUserService gameUserService;

    /**
     * Constructor to initialize GameUserController with GameUserService.
     *
     * @param gameUserService The GameUserService instance.
     */
    @Autowired
    public GameUserController(GameUserService gameUserService) {
        this.gameUserService = gameUserService;
    }

    /**
     * Endpoint to create a new game user account.
     *
     * @param gameUser The new GameUser object to create an account for.
     * @return A ServerResponse indicating the status of the operation.
     */
    @PutMapping
    public ServerResponse createAnAccount(@RequestBody GameUser gameUser) {
        return gameUserService.createAnAccount(gameUser);
    }

    /**
     * Endpoint to delete an existing game user account.
     *
     * @param gameUser The existing GameUser object to delete the account for.
     * @return A ServerResponse indicating the status of the operation.
     */
    @DeleteMapping
    public ServerResponse deleteAnAccount(@RequestBody GameUser gameUser) {
        return gameUserService.deleteAnAccount(gameUser);
    }
}
