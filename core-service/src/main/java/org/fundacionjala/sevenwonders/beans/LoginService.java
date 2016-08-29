package org.fundacionjala.sevenwonders.beans;

import org.fundacionjala.sevenwonders.core.rest.LoginModel;
import org.springframework.stereotype.Component;

/**
 * Created by Unkon on 8/29/2016.
 */
@Component
public class LoginService {

    public LoginModel isLogged(LoginModel loginModel){
        if (loginModel != null){
            return loginModel;
        }
        return null;
    }
}
