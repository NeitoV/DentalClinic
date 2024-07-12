package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.ChangePasswordDTO;
import com.java.dental_clinic.data.dto.JwtResponseDTO;
import com.java.dental_clinic.data.dto.LoginDTO;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.entity.Role;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.maper.UserMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ConflictException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.RoleRepository;
import com.java.dental_clinic.repostiory.StaffRepository;
import com.java.dental_clinic.repostiory.UserRepository;
import com.java.dental_clinic.service.JwtService;
import com.java.dental_clinic.service.MailService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailService mailService;
    @Autowired
    private StaffRepository staffRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;

    @Override
    public JwtResponseDTO loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByPhoneNumber(loginDTO.getUserName()).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("phone number: ", loginDTO.getUserName()))
        );
        String position = null;
        if(user.getRole().getId() == ERole.roleStaff) {
            Staff staff = staffRepository.findByUserId(user.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("message: ", "staff isn't exists"))
            );

            position = staff.getPosition().getName();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUserName());

        if (!checkValidPassword(loginDTO.getPassword(), user.getPassword()))
            throw new AccessDeniedException(Collections.singletonMap("message", "password is wrong"));

//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginDTO.getPhoneNumber(),
//                        loginDTO.getPassword()));

//        SecurityContextHolder.getContext().setAuthentication(authentication);

        Role role = user.getRole();

        String jwt = jwtService.generateToken(userDetails);

        return new JwtResponseDTO(jwt, loginDTO.getUserName(), role.getName(), position, user.getEmail());
    }

    @Override
    public User createUser(LoginDTO loginDTO, long roleId) {
        if (userRepository.existsByPhoneNumber(loginDTO.getUserName()))
            throw new ConflictException(Collections.singletonMap("phone number: ", loginDTO.getUserName()));

        User user = userMapper.toEntity(loginDTO);

        user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        user.setRole(roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("Role id", roleId))
        ));

        return userRepository.save(user);
    }

    @Override
    public User getUserByToken() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new AccessDeniedException(Collections.singletonMap("message: ", "Not authentication"))
        );

        return user;
    }

    @Override
    public MessageResponse sendMailActiveUser(String email) throws MessagingException {
        if(userRepository.existsByEmail(email)) {
            throw new ConflictException(Collections.singletonMap("email: ", email));
        }

        User user = getUserByToken();
        String otp = generateOTP(OTP_LENGTH);

        user.setOtp(otp);
        userRepository.save(user);

        String subject = "CONFIRM EMAIL";
        String text = "Your otp: " + otp;

        mailService.send(email, subject, text);

        return new MessageResponse(HttpServletResponse.SC_OK, "check your email");
    }

    @Override
    public MessageResponse activeEmail(String email, String otp) {
        User user = getUserByToken();
        if(user.getOtp() == null) {
            throw new AccessDeniedException(Collections.singletonMap("message: ", "user didn't send mail active"));
        }

        if(!user.getOtp().equals(otp)) {
            throw new AccessDeniedException(Collections.singletonMap("message: ", "otp is wrong"));
        }

        user.setEmail(email);
        userRepository.save(user);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponse changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = getUserByToken();

        if(!checkValidPassword(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException(Collections.singletonMap("message: ", "password isn't correct"));
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public void checkStaff(Long staffId) {
        User user = getUserByToken();
        if(user.getRole().getId() == ERole.roleStaff) {
            Staff staff = staffRepository.findByUserId(user.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("message: ", "staff isn't exists"))
            );

            if(staff.getId() != staffId) {
                throw new AccessDeniedException(Collections.singletonMap("message: ", "this is not yours"));
            }
        }
    }

    private Boolean checkValidPassword(String password, String passwordEncoded) {

        return passwordEncoder.matches(password, passwordEncoded);
    }

    public static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }

        return otp.toString();
    }
}
