package org.example.userservice.validation;

import org.example.userservice.exception.*;

public class UserValidation {
    public static void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new NameEmptyOrNullException(name);
        }
        String regexEng = "^[A-Z][a-z]+$";
        String regexRu = "^[А-Я][а-я]+$";

        if (!name.matches(regexRu) && !name.matches(regexEng)) {
            throw new InvalidNameException(name);
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new EmailEmptyOrNullException(email);
        }

        String regexEmail = "^[A-Za-z0-9+_.-]+@([A-Za-z]+)[.][A-Za-z]+$";
        if (!email.matches(regexEmail)) {

            throw new InvalidEmailException(email);
        }
    }

    public static void validateAge(Integer age) {
        if (age == null) {
            throw new AgeNullException(age);
        }
        if ((age < 1) || (age > 100)) {
            throw new InvalidAgeException(age);
        }
    }

    public static void validateId(String id) {
        if (id == null) {
            throw new IdNullException(null);
        }

        String regexId = "^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$";
        if (!id.matches(regexId)) {
            throw new InvalidIdException(id);
        }
    }
}