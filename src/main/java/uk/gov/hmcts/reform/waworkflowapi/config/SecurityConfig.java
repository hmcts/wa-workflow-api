package uk.gov.hmcts.reform.waworkflowapi.config;

    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.builders.WebSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

    import java.util.ArrayList;
    import java.util.List;


@Configuration
@ConfigurationProperties(prefix = "security")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final List<String> anonymousPaths = new ArrayList<>();

    @Override
    public void configure(WebSecurity web) {

        web.ignoring().mvcMatchers(
            anonymousPaths
                .stream()
                .toArray(String[]::new)
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").hasRole("USER").and().formLogin();
    }

    //@Override
    //public void configure(HttpSecurity http) throws Exception {
    //
    //    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    //    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(idamAuthoritiesConverter);
    //
    //
    //    http
    //        .addFilterBefore(serviceAuthFiler, AbstractPreAuthenticatedProcessingFilter.class)
    //        .sessionManagement().sessionCreationPolicy(STATELESS)
    //        .and()
    //        .exceptionHandling()
    //        .and()
    //        .csrf().disable()
    //        .formLogin().disable()
    //        .logout().disable()
    //        .authorizeRequests().anyRequest().authenticated()
    //        .and()
    //        .oauth2ResourceServer()
    //        .jwt()
    //        .jwtAuthenticationConverter(jwtAuthenticationConverter)
    //        .and()
    //        .and()
    //        .oauth2Client();
    //}

    @Bean
    public AuthorizedRolesProvider authorizedRolesProvider() {
        return new SpringAuthorizedRolesProvider();
    }
}
