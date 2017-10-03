package ir.company.app.config;

import ir.company.app.security.AuthoritiesConstants;
import ir.company.app.security.Http401UnauthorizedEntryPoint;
import ir.company.app.security.jwt.JWTConfigurer;
import ir.company.app.security.jwt.TokenProvider;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        try {
            auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**")
            .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/1/calculate").permitAll()
            .antMatchers("/api/1/arrived").permitAll()
            .antMatchers("/api/1/detail").permitAll()
            .antMatchers("/api/1/user_authenticate").permitAll()
            .antMatchers("/api/1/signup").permitAll()
            .antMatchers("/api/1/forget").permitAll()
            .antMatchers("/api/1/confirmReset").permitAll()
            .antMatchers("/api/1/changePassword").permitAll()
            .antMatchers("/api/1/confirmRequest").permitAll()
            .antMatchers("/api/1/confirmReserve").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/1/tempUser").permitAll()
            .antMatchers("/api/1/factor").permitAll()
            .antMatchers("/api/1/inventory").permitAll()
            .antMatchers("/api/1/games").permitAll()
            .antMatchers("/api/1/marketObject").permitAll()
            .antMatchers("/api/1/**").permitAll()
            .antMatchers("/api/1/videoWatch").permitAll()
            .antMatchers("/api/1/expandMenu").permitAll()
            .antMatchers("/api/1/detailGame").permitAll()
            .antMatchers("/api/1/detailLeague").permitAll()
            .antMatchers("/api/1/stopGame").permitAll()
            .antMatchers("/api/1/endGame").permitAll()
            .antMatchers("/api/1/submitRecord").permitAll()
            .antMatchers("/api/1/timeOut").permitAll()
            .antMatchers("/api/1/refreshPolicy").permitAll()
            .antMatchers("/api/account/reset_password/init").permitAll()
            .antMatchers("/api/account/reset_password/finish").permitAll()
            .antMatchers("/api/profile-info").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/swagger-resources/configuration/ui").permitAll()
            .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
            .and()
            .apply(securityConfigurerAdapter());

    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
