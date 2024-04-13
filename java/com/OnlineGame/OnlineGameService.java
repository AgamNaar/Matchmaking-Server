package com.OnlineGame;

import com.ChessGame.ChessMove;
import com.GameUser.GameUser;
import com.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OnlineGameService {

    public ServerResponse submitPlayerMove(GameUser gameUser, ChessMove playerMove, long gameID) {
        // TODO: check if its a valid move in the game
        return new ServerResponse(HttpStatus.OK);

    }
}
