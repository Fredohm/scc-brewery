package guru.sfg.brewery.config;

import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
                })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                    .withUser("spring")
                    .password("{bcrypt}$2a$12$Em8Q7/tAsOn/G4xon.qK0u9he8ulmKBawS2ARA50iKWJmcPLAPkKu")
                    .roles("ADMIN")
                .and()
                    .withUser("user")
                    .password("{sha256}e5de757bfe18e6945f52ced22b3010eb32de5d05722707bfa2f4bce839381be3f4fd86be4d115d3f")
                    .roles("USER");

        auth.inMemoryAuthentication()
                    .withUser("scott")
                    .password("{bcrypt15}$2a$15$AOZZNaePF/IpfUiRdcVJYebPnDfO0WvQdhdgbbkkQZ5IYJ/9qJfBu")
                    .roles("CUSTOMER");
    }

/*
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("spring")
                .password("guru")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
*/
}
