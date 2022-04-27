package am.epam.rest.endpoint;

import am.epam.common.dto.UserAuthDTO;
import am.epam.common.dto.UserAuthResponseDTO;
import am.epam.common.dto.UserDTO;
import am.epam.common.dto.UserSaveDTO;
import am.epam.common.model.User;
import am.epam.rest.util.JWTTokenUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import am.epam.common.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class UserEndpoint {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenUtil token;
    Logger logger= LoggerFactory.getLogger(UserEndpoint.class);

    public UserEndpoint(UserRepository userRepository, ModelMapper modelMapper, JWTTokenUtil token, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.token = token;

    }

    @GetMapping("/users")
    public List<UserDTO> users() {
        List<User> users = userRepository.findAll();
        List<UserDTO> allUser = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            allUser.add(userDTO);

        }
        logger.info("user opened users page user count={}",users.size());
        return allUser;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapper.map(byId.get(), UserDTO.class));
    }

    @PostMapping("/users")
    public UserDTO savedUser(@RequestBody UserSaveDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return modelMapper.map(userRepository.save(modelMapper.map(user, User.class)), UserDTO.class);

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable("id") int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") int id, @RequestBody UserSaveDTO user) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            ResponseEntity.notFound().build();
        }
        User userFromDB = byId.get();
        userFromDB.setName(user.getName());
        userFromDB.setPassword(user.getPassword());
        userFromDB.setSurname(user.getSurname());
        return ResponseEntity.ok().body(modelMapper.map(userRepository.save(userFromDB), UserDTO.class));
    }

    @PostMapping("/user/auth")
    private ResponseEntity authUser(@RequestBody UserAuthDTO userAuthDTO) {
        Optional<User> getEmail = userRepository.findByEmail(userAuthDTO.getEmail());
        if (!getEmail.isEmpty()) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = getEmail.get();
        if (passwordEncoder.matches(userAuthDTO.getPassword(),user.getPassword())) {
            UserAuthResponseDTO userAuthResponseDTO = new UserAuthResponseDTO();
            userAuthResponseDTO.setUserDTO(modelMapper.map(user, UserDTO.class));
            userAuthResponseDTO.setToken(token.generateToken(user.getEmail()));
            return ResponseEntity.ok(userAuthResponseDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}
