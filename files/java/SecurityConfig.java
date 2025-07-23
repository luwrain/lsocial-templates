@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/register", "/login", "/oauth/**", 
                             "/forgot-password", "/reset-password").permitAll()
                .antMatchers("/static/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
            .and()
            .logout()
                .logoutSuccessUrl("/login")
                .permitAll()
            .and()
            .csrf().disable(); // Для упрощения на этапе разработки
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}