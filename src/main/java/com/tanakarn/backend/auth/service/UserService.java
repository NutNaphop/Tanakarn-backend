package com.tanakarn.backend.auth.service;

import com.tanakarn.backend.auth.dto.response.LoginResponse;
import com.tanakarn.backend.account.entity.Account;
import com.tanakarn.backend.auth.entity.User;
import com.tanakarn.backend.account.repository.AccountRepository;
import com.tanakarn.backend.auth.repository.UserRepository;
import com.tanakarn.backend.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, AccountRepository accountRepository , PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    public void registerUser(String username, String rawPassword) {
        try{
            String hashedBtn = passwordEncoder.encode(rawPassword);

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(hashedBtn);

            userRepository.save(newUser);

            Account newAccount = new Account();
            newAccount.setBalance(1000);
            newAccount.setAccountNumber(String.format("ACC%010d", System.currentTimeMillis() % 10000000000L));
            newAccount.setUser(newUser);

            accountRepository.save(newAccount);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public LoginResponse loginUser(String username, String rawPassword){
        try{
            User user = userRepository.findByUsername(username);

            if(user == null){
                throw new RuntimeException("User not found");
            }

            if(!passwordEncoder.matches(rawPassword, user.getPassword())){
                throw new RuntimeException("Invalid password");
            }

            String token = jwtService.generateToken(user.getId(), user.getUsername());
            LoginResponse authResponse = new LoginResponse();
            authResponse.setToken(token);
            authResponse.setUsername(user.getUsername());
            authResponse.setAccountId(user.getId());

            return authResponse;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
