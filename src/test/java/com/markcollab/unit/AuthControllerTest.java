//package com.markcollab.unit;
//
//import com.markcollab.controller.AuthController;
//import com.markcollab.service.AuthService;
//import com.markcollab.service.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import org.springframework.http.MediaType;
//
//public class AuthControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private AuthService authService;
//
//    @Mock
//    private JwtService jwtService;
//
//    private AuthController authController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        authController = new AuthController(authService, jwtService);
//        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//    }
//
//    @Test
//    void testRegisterEmployerSuccess() throws Exception {
//        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678900\", \"email\": \"test@example.com\", \"name\": \"John Doe\", \"password\": \"Valid1234\", \"companyName\": \"Test Company\" }";
//
//        doNothing().when(authService).registerEmployer(anyMap());
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Empregador registrado com sucesso!"));
//
//        verify(authService, times(1)).registerEmployer(anyMap());
//    }
//
//    @Test
//    void testRegisterFreelancerSuccess() throws Exception {
//        String jsonBody = "{ \"role\": \"FREELANCER\", \"cpf\": \"12345678901\", \"email\": \"freelancer@example.com\", \"name\": \"Jane Doe\", \"password\": \"Valid1234\", \"portfolioLink\": \"http://portfolio.com\" }";
//
//        doNothing().when(authService).registerFreelancer(anyMap());
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Freelancer registrado com sucesso!"));
//
//        verify(authService, times(1)).registerFreelancer(anyMap());
//    }
//
//    @Test
//    void testRegisterWithInvalidRole() throws Exception {
//        String jsonBody = "{ \"role\": \"INVALID_ROLE\", \"cpf\": \"12345678901\", \"email\": \"test@example.com\", \"name\": \"John Doe\", \"password\": \"Valid1234\" }";
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Role inválido."));
//
//        verify(authService, times(0)).registerEmployer(anyMap());
//        verify(authService, times(0)).registerFreelancer(anyMap());
//    }
//
//    @Test
//    void testRegisterWithDuplicateEmail() throws Exception {
//        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678901\", \"email\": \"duplicate@example.com\", \"name\": \"John Doe\", \"password\": \"Valid1234\", \"companyName\": \"Company\" }";
//
//        doThrow(new RuntimeException("E-mail já está em uso")).when(authService).registerEmployer(anyMap());
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("E-mail já está em uso"));
//
//        verify(authService, times(1)).registerEmployer(anyMap());
//    }
//
//    @Test
//    void testRegisterWithDuplicateCpf() throws Exception {
//        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678900\", \"email\": \"test@example.com\", \"name\": \"John Doe\", \"password\": \"Valid1234\", \"companyName\": \"Test Company\" }";
//
//        doThrow(new RuntimeException("CPF já está em uso")).when(authService).registerEmployer(anyMap());
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("CPF já está em uso"));
//
//        verify(authService, times(1)).registerEmployer(anyMap());
//    }
//
//}
