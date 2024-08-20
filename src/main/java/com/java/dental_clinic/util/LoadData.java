package com.java.dental_clinic.util;

import com.java.dental_clinic.data.entity.Period;
import com.java.dental_clinic.data.entity.Position;
import com.java.dental_clinic.data.entity.Role;
import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.data.enumeration.EPeriod;
import com.java.dental_clinic.data.enumeration.EPosition;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.repostiory.PeriodRepository;
import com.java.dental_clinic.repostiory.PositionRepository;
import com.java.dental_clinic.repostiory.RoleRepository;
import com.java.dental_clinic.repostiory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class LoadData {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private PeriodRepository periodRepository;

    @PostConstruct
    void inIt() {
        addRole();
        addPosition();
        addUserAdmin();
        addPeriod();
    }

    public void addRole() {
        for (ERole eRole : ERole.values()) {

            if (!roleRepository.existsByName(eRole.toString())) {
                Role role = new Role();
                role.setName(eRole.toString());

                roleRepository.save(role);
            }
        }
    }

    public void addPosition() {
        for(EPosition ePosition: EPosition.values()) {

            if(!positionRepository.existsByName(ePosition.toString())) {
                Position position = new Position();
                position.setName(ePosition.toString());

                positionRepository.save(position);
            }
        }
    }

    private void addUserAdmin() {
        String userName = "admin";
        String password = "123456";

        if (!userRepository.existsByPhoneNumber(userName)) {
            User user = new User();

            user.setPassword(passwordEncoder.encode(password));
            user.setRole(roleRepository.findById(ERole.roleAdmin).orElse(null));
            user.setPhoneNumber(userName);

            userRepository.save(user);
        }
    }

    private void addPeriod() {
        for(EPeriod ePeriod: EPeriod.values()) {
            if(!periodRepository.existsByName(ePeriod.toString())) {
                Period period = new Period();

                period.setName(ePeriod.toString());
                periodRepository.save(period);
            }
        }
    }
}
