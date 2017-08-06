package ir.company.app.service.migmig;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import ir.company.app.config.Constants;
import ir.company.app.domain.Authority;
import ir.company.app.domain.entity.Game;
import ir.company.app.domain.entity.GameStatus;
import ir.company.app.domain.entity.User;
import ir.company.app.repository.AuthorityRepository;
import ir.company.app.repository.GameRepository;
import ir.company.app.repository.UserRepository;
import ir.company.app.security.AuthoritiesConstants;
import ir.company.app.security.SecurityUtils;
import ir.company.app.security.jwt.JWTConfigurer;
import ir.company.app.security.jwt.TokenProvider;
import ir.company.app.service.UserService;
import ir.company.app.service.dto.*;
import ir.company.app.service.util.CalendarUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FarzadUserService {

    @Inject
    private TokenProvider tokenProvider;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private AuthorityRepository authorityRepository;
    @Inject
    private AuthenticationManager authenticationManager;
    @Inject
    private UserRepository userRepository;
    @Inject
    private GameRepository gameRepository;
    @Inject
    private UserService userService;
    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/1/user_authenticate", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> authorize(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            if (authentication.isAuthenticated()) {
                //todo authenticate

                SecurityContextHolder.getContext().setAuthentication(authentication);
//                boolean rememberMe = (loginDTO.isRememberMe() == null) ? false : loginDTO.isRememberMe();
                String jwt = tokenProvider.createToken(authentication, true);
                response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
                User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
                user.setPushSessionKey(loginDTO.getDeviceToken());
                userRepository.save(user);
                UserLoginDTO userLoginDTO = new UserLoginDTO();
                userLoginDTO.token = jwt;
                userLoginDTO.score = user.getScore();
                userLoginDTO.gem = user.getGem();
                userLoginDTO.level = user.getLevel();
                userLoginDTO.avatar = user.getAvatar();
                userLoginDTO.rating = user.getRating();
                userLoginDTO.coins = user.getCoin();
                userLoginDTO.userid = user.getId();
                return ResponseEntity.ok(new ObjectMapper().writeValueAsString(userLoginDTO));
            }
        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException", exception.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException", e.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok("401");

    }

    @RequestMapping(value = "/1/signup", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> signUp(@Valid @RequestBody UserDTO userDTO, HttpServletResponse response) {

        User user = userRepository.findOneByGuestId(userDTO.getTempUser());

        user.setLogin(userDTO.getUsername());
        user.setActivated(true);
        user.setCreatedBy("system");
        List<Authority> authoritie = new ArrayList<>();
        authoritie.add(authorityRepository.findOne(AuthoritiesConstants.USER));
//        user.setAuthorities(authoritie);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getName());
        user.setMobile(userDTO.getMobile());
        user.setAvatar(userDTO.getAvatar());
        userRepository.save(user);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/forget", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> forget(@Valid @RequestBody String username, HttpServletResponse response) {
        //todo forget scenario send email or sms
        Optional<User> user = userRepository.findOneByLogin(username);
        if (user.isPresent()) {

            int START = 1000;
            int END = 9999;
            Random random = new Random();
            long range = END - START + 1;
            // compute a fraction of the range, 0 <= frac < range
            long fraction = (long) (range * random.nextDouble());
            int randomNumber = (int) (fraction + START);
            String s = String.valueOf(randomNumber);
            User user1 = user.get();
            user1.setResetKey(s);
            user1.setResetDate(CalendarUtil.getNowDateTimeOfIran());
            userRepository.save(user1);
            try {
                String tel = user1.getMobile();

                KavenegarApi api = new KavenegarApi("5635717141617A52534F636F49546D38454E647870773D3D");
//                api.send("10006006606600", tel, "شماره بازیابی :  " + s);

                api.verifyLookup(tel, s, "restore");

            } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                System.out.print("HttpException  : " + ex.getMessage());
                return ResponseEntity.ok("302");
            } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
                System.out.print("ApiException : " + ex.getMessage());
                return ResponseEntity.ok("302");
            }
//            MailUtils.sendEmail("farzad.sedaghatbin@gmail.com", s, "ResetPassword");
            return ResponseEntity.ok("200");
        } else {
            return ResponseEntity.ok("201");
        }
    }

    @RequestMapping(value = "/1/confirmReset", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> confirmReset(@Valid @RequestBody ForgetPasswordDTO data, HttpServletResponse response) {
        //todo forget scenario send email or sms
        Optional<User> user = userRepository.findOneByResetKey(data.getCode());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setPassword(passwordEncoder.encode(data.getPassword()));
            user1.setResetDate(ZonedDateTime.now());
            userRepository.save(user1);
            return ResponseEntity.ok("200");
        } else {
            return ResponseEntity.ok("201");
        }
    }


    @RequestMapping(value = "/1/changePassword", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> changePassword(@Valid @RequestBody String password, HttpServletResponse response) {
        User user1 = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        user1.setPassword(passwordEncoder.encode(password));
        userRepository.save(user1);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/userInfo", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")
//todo testy
    public ResponseEntity<?> deviceToken(HttpServletResponse response) {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getFirstName());
        userDTO.setAvatar("");
        userDTO.setMobile("09128626242");
        try {
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(userDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("200");

    }

    @RequestMapping(value = "/1/deviceToken", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> deviceToken(@Valid @RequestBody String token, HttpServletResponse response) {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        user.setPushSessionKey(token);
        userRepository.save(user);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/rating", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> rating(@Valid @RequestBody String param, HttpServletResponse response) {
        String[] s = param.split(",");

        return ResponseEntity.ok("200");

    }


    @RequestMapping(value = "/1/refresh", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> refreshMoney(HttpServletResponse response) throws JsonProcessingException {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        HomeDTO homeDTO = new HomeDTO();
        homeDTO.score = user.getScore();
        homeDTO.gem = user.getGem();
        homeDTO.level = user.getLevel();
        homeDTO.avatar = user.getAvatar();
        homeDTO.rating = user.getRating();
        homeDTO.coins = user.getCoin();
        homeDTO.userid = user.getId();
        List<Game> halfGame = gameRepository.findByGameStatusAndFirst(GameStatus.HALF, userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get(), new PageRequest(0, 5));
        halfGame.addAll(gameRepository.findByGameStatusAndFirst(GameStatus.HALF, userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get(), new PageRequest(0, 5)));
        List<Game> fullGame = gameRepository.findByGameStatusAndSecond(GameStatus.FULL, userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get(), new PageRequest(0, 5));
        fullGame.addAll(gameRepository.findByGameStatusAndSecond(GameStatus.FULL, userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get(), new PageRequest(0, 5)));


        homeDTO.halfGame = new ArrayList<>();
        for (Game game : halfGame) {
            GameLowDTO gameLowDTO = new GameLowDTO();
            gameLowDTO.status = GameStatus.HALF.name();
            gameLowDTO.user = game.getFirst().getLogin();
            homeDTO.halfGame.add(gameLowDTO);
        }
        homeDTO.fullGame = new ArrayList<>();
        for (Game game : fullGame) {
            GameLowDTO gameLowDTO = new GameLowDTO();
            gameLowDTO.status = GameStatus.HALF.name();
            gameLowDTO.user = game.getFirst().getLogin();
            homeDTO.halfGame.add(gameLowDTO);
        }
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(homeDTO));

    }


    @RequestMapping(value = "/1/tempUser", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> tempUser(HttpServletResponse response) throws JsonProcessingException {
        User user = new User();
        user.setLogin("DAGALA" + Constants.index.incrementAndGet());
        user.setActivated(true);
        user.setCreatedBy("system");
        List<Authority> authoritie = new ArrayList<>();
        authoritie.add(authorityRepository.findOne(AuthoritiesConstants.USER));
//        user.setAuthorities(authoritie);
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        user.setFirstName(userDTO.getFirstName());
//        user.setLastName(userDTO.getLastName());
//        user.setMobile(userDTO.getMobile());
//        user.setGender(userDTO.getGender());

        user.setAvatar("1.png");
        userRepository.save(user);
        return ResponseEntity.ok(user.getLogin());

    }

}
