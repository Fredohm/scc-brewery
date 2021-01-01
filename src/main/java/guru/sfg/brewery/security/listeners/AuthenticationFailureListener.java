package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {

        log.debug("User Logged In failure");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof String) {
                builder.username((String) token.getPrincipal());
                userRepository.findByUsername((String) token.getPrincipal()).ifPresent(builder::user);

                log.debug("Attempted user: " + token.getPrincipal());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                builder.sourceIP(details.getRemoteAddress());

                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginFailure loginFailure = loginFailureRepository.save(builder.build());
            log.debug("Failure Event: " + loginFailure.getId());
        }
    }
}
