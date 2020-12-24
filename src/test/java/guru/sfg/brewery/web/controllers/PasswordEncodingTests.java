package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

public class PasswordEncodingTests {

    static final String PASSWORD = "password";

    @Test
    void hashingExample() {
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD + "this isMySALTVALUE";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

}
