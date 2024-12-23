package org.example.hacaton.service;

import org.example.hacaton.dto.request.RegisterRequest;
import org.example.hacaton.model.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    UserDetails loadUserByUsername(String email);

    User create(User user);

    User findByEmail(String email);

    User getUserById(Long id);
}


