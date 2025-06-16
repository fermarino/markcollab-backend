//package com.markcollab.integration;
//
//import com.github.javafaker.Faker;
//import com.markcollab.controller.AuthController;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class AuthIntegrationTest {
//
//    @Autowired
//    private AuthController authController;
//
//    private Faker faker;
//
//    @BeforeEach
//    public void setUp() {
//        faker = new Faker();
//    }
//
//    private Map<String, Object> generateEmployerData() {
//        return Map.of(
//                "cpf", faker.number().digits(11),
//                "name", faker.name().fullName(),
//                "username", faker.name().username(),
//                "email", faker.internet().emailAddress(),
//                "password", "Valid1234",
//                "role", "EMPLOYER",
//                "companyName", faker.company().name()
//        );
//    }
//
//    private Map<String, Object> generateFreelancerData() {
//        return Map.of(
//                "cpf", faker.number().digits(11),
//                "name", faker.name().fullName(),
//                "username", faker.name().username(),
//                "email", faker.internet().emailAddress(),
//                "password", "Valid1234",
//                "role", "FREELANCER",
//                "portfolioLink", faker.internet().url()
//        );
//    }
//
//    @Test
//    public void shouldRegisterEmployerSuccessfully_whenAllFieldsAreValid() {
//        Map<String, Object> body = generateEmployerData();
//        ResponseEntity<String> response = authController.register(body);
//        assertEquals("Empregador registrado com sucesso!", response.getBody());
//    }
//
//    @Test
//    public void shouldRegisterFreelancerSuccessfully_whenAllFieldsAreValid() {
//        Map<String, Object> body = generateFreelancerData();
//        ResponseEntity<String> response = authController.register(body);
//        assertEquals("Freelancer registrado com sucesso!", response.getBody());
//    }
//
//    @Test
//    public void shouldReturnBadRequest_whenEmailIsDuplicate() {
//        Map<String, Object> body = generateEmployerData();
//        authController.register(body);
//
//        Map<String, Object> duplicateEmailData = Map.of(
//                "cpf", faker.number().digits(11),
//                "name", faker.name().fullName(),
//                "username", faker.name().username(),
//                "email", body.get("email"),
//                "password", "Valid1234",
//                "role", "EMPLOYER",
//                "companyName", faker.company().name()
//        );
//
//        ResponseEntity<String> response = authController.register(duplicateEmailData);
//        assertTrue(response.getBody().contains("E-mail já está em uso"));
//    }
//
//    @Test
//    public void shouldReturnBadRequest_whenCpfIsDuplicate() {
//        Map<String, Object> body = generateFreelancerData();
//        authController.register(body);
//
//        Map<String, Object> duplicateCpfData = Map.of(
//                "cpf", body.get("cpf"),
//                "name", faker.name().fullName(),
//                "username", faker.name().username(),
//                "email", faker.internet().emailAddress(),
//                "password", "Valid1234",
//                "role", "FREELANCER",
//                "portfolioLink", faker.internet().url()
//        );
//
//        ResponseEntity<String> response = authController.register(duplicateCpfData);
//        assertTrue(response.getBody().contains("CPF já em uso"));
//    }
//
//
//    @Test
//    public void shouldReturnBadRequest_whenRoleIsInvalid() {
//        Map<String, Object> body = Map.of(
//                "cpf", faker.number().digits(11),
//                "name", faker.name().fullName(),
//                "username", faker.name().username(),
//                "email", faker.internet().emailAddress(),
//                "password", "Valid1234",
//                "role", "INVALID" // Role inválido
//        );
//        ResponseEntity<String> response = authController.register(body);
//        assertTrue(response.getBody().contains("Role inválido"));
//    }
//
//
//    @Test
//    public void shouldReturnBadRequest_whenUsernameIsDuplicate() {
//        Map<String, Object> body = generateEmployerData();
//        authController.register(body);
//
//        Map<String, Object> duplicateUsernameData = Map.of(
//                "cpf", faker.number().digits(11),
//                "name", faker.name().fullName(),
//                "username", body.get("username"), // Mesma username
//                "email", faker.internet().emailAddress(),
//                "password", "Valid1234",
//                "role", "EMPLOYER",
//                "companyName", faker.company().name()
//        );
//
//        ResponseEntity<String> response = authController.register(duplicateUsernameData);
//        assertTrue(response.getBody().contains("Username já está em uso"));
//    }
//}
