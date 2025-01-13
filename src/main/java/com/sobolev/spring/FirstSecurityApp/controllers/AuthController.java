package com.sobolev.spring.FirstSecurityApp.controllers;

import com.sobolev.spring.FirstSecurityApp.dto.AuthenticationDTO;
import com.sobolev.spring.FirstSecurityApp.dto.PersonDTO;
import com.sobolev.spring.FirstSecurityApp.models.Person;
import com.sobolev.spring.FirstSecurityApp.security.JwtUtil;
import com.sobolev.spring.FirstSecurityApp.services.RegistrationService;
import com.sobolev.spring.FirstSecurityApp.util.PersonValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(PersonValidator personValidator,
                          RegistrationService registrationService,
                          JwtUtil jwtUtil,
                          ModelMapper modelMapper,
                          AuthenticationManager authenticationManager) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

//    @GetMapping("/login")
//    public String loginPage(){
//        return "auth/login";
//    }
//
//    @GetMapping("/registration")
//    public String registrationPage(@ModelAttribute("person") Person person){
//        return "auth/registration";
//    }

    @PostMapping("/registration")
    public Map<String, String> registration(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return Map.of("message","Error!");
        }

        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                authenticationDTO.getPassword());
        try{
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("message","Authentication failed");
        }

        return Map.of("jwt-token", jwtUtil.generateToken(authenticationDTO.getUsername()));
    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO, Person.class);
    }
}
