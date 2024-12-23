package org.example.hacaton.service;

import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.User;

public interface AdminService {

    Admin createAdmin(User user);

    void deleteHackathon(Long id);

}
