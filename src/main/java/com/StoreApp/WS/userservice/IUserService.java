package com.StoreApp.WS.userservice;

import com.StoreApp.WS.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {

    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id, UserDto user);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
}
