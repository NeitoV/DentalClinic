package com.java.dental_clinic.service.impl.UserDetailsImpl;


import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.RoleRepository;
import com.java.dental_clinic.repostiory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByPhoneNumber(userName).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("phone number: ", userName))
        );

        return UserDetailsImpl.build(user);
    }

}