package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }
    }

    private void loadSecurityData() {

        Authority createBeer = authorityRepository.save(Authority.builder().role("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().role("beer.read").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().role("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().role("beer.delete").build());

        Authority createCustomer = authorityRepository.save(Authority.builder().role("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().role("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().role("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().role("customer.delete").build());

        Authority createBrewery = authorityRepository.save(Authority.builder().role("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().role("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().role("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().role("brewery.delete").build());

        Authority createOrder = authorityRepository.save(Authority.builder().role("order.create").build());
        Authority readOrder = authorityRepository.save(Authority.builder().role("order.read").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().role("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().role("order.delete").build());

        Authority createOrderCustomer = authorityRepository.save(Authority.builder().role("customer.order.create").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().role("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().role("customer.order.update").build());
        Authority deleteOrderCustomer  = authorityRepository.save(Authority.builder().role("customer.order.delete").build());


        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(
                createBeer, readBeer, updateBeer, deleteBeer,
                createCustomer, readCustomer, updateCustomer, deleteCustomer,
                createBrewery, readBrewery, updateBrewery, deleteBrewery
        )));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery)));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeer)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build()
        );

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build()
        );

        User user = userRepository.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build()
        );

        log.debug("Users Loaded: " + userRepository.count());

        user.getAuthorities().forEach(authority -> {
            System.out.println(authority.getRoles());
        });
    }
}
