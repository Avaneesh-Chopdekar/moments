package com.moments.backend.services;

import com.moments.backend.entities.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUser(String username);

//    void deleteUser(String username);

//    void editUserInfo(String username);
}
