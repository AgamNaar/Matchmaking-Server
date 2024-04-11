package com;

import com.GameUser.GameUser;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    public boolean isValidToken(GameUser gameUser) {
        return true;
    }
}
