package com.sobolev.spring.FirstSecurityApp.util;

import com.sobolev.spring.FirstSecurityApp.models.Person;
import com.sobolev.spring.FirstSecurityApp.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonValidator(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        try{
            personDetailsService.loadUserByUsername(person.getUsername());
        }catch(UsernameNotFoundException ignored){
            return; //
        }

        errors.rejectValue("username", "", "Username already exist");
    }
}
