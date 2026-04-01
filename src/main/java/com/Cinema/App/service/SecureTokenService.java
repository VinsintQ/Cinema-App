package com.Cinema.App.service;


import com.Cinema.App.model.SecureToken;
public interface SecureTokenService {

    SecureToken createToken();
    void saveSecureToken(SecureToken secureToken);
    SecureToken findByToken(String token);
    void removeToken(SecureToken token);

}