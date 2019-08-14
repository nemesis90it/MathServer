package com.nemesis.mathserver.security.service;

import com.nemesis.mathserver.security.adapter.UserAdapter;
import com.nemesis.mathserver.security.jpa.entity.UserEntity;
import com.nemesis.mathserver.security.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("UserEntity with %s doesn't exist!", username));
        }
        return new UserAdapter(user);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }
}