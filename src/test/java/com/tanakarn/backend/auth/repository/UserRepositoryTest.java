package com.tanakarn.backend.auth.repository;

import com.tanakarn.backend.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername(){
        User user = new User();
        user.setUsername("naphop");
        user.setPassword("1234");

        userRepository.save(user);

        User foundUser = userRepository.findByUsername(user.getUsername());

        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }
}
