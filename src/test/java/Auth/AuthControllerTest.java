package Auth;

import com.markcollab.controller.AuthController;
import com.markcollab.service.AuthService;
import com.markcollab.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.http.MediaType;


public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);
        // Inicializa o controlador com os mocks via construtor
        authController = new AuthController(authService, jwtService);
        // Configura o MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegister() throws Exception {
        // Simula o comportamento do service
        doNothing().when(authService).registerEmployer(anyMap());

        // Cria um corpo de requisição para o cadastro
        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678900\", \"email\": \"test@example.com\", \"name\": \"John Doe\", \"password\": \"password\" }";

        // Faz a requisição POST
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk());

        // Verifica se o serviço foi chamado corretamente
        verify(authService, times(1)).registerEmployer(anyMap());
    }

    @Test
    void testRegisterWithInvalidCpf() throws Exception {
        // Criando o corpo da requisição com um CPF inválido
        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"123\", \"email\": \"test@example.com\", \"name\": \"John Doe\", \"password\": \"password\" }";

        // Fazendo a requisição POST
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())  // Espera um status HTTP 400 de erro
                .andExpect(content().string("CPF inválido"));  // Verifica se a mensagem "CPF inválido" foi retornada

        // Verifica que o método do service não foi chamado
        verify(authService, times(0)).registerEmployer(anyMap());
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        // Criando o corpo da requisição com um e-mail inválido (por exemplo, 'usuario@com')
        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678901\", \"email\": \"usuario@com\", \"name\": \"John Doe\", \"password\": \"password\" }";

        // Fazendo a requisição POST
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())  // Espera um status HTTP 400 de erro
                .andExpect(content().string("E-mail inválido"));  // Verifica se a mensagem "E-mail inválido" foi retornada

        // Verifica que o método do service não foi chamado
        verify(authService, times(0)).registerEmployer(anyMap());
    }

    @Test
    void testRegisterWithInvalidName() throws Exception {
        // Corpo da requisição com um nome contendo caracteres inválidos
        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"12345678900\", \"email\": \"test@example.com\", \"name\": \"Invalid<Name\" }";

        // Fazendo a requisição POST
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())  // Espera um status HTTP 400 de erro
                .andExpect(content().string("O nome não pode conter caracteres inválidos como <, >, {, }.' "));  // Verifica se a mensagem correta foi retornada

        // Verifica que o método do service não foi chamado
        verify(authService, times(0)).registerEmployer(anyMap());
    }
    @Test
    void testRegisterWithEmptyRequiredFields() throws Exception {
        // Teste com E-mail vazio
        String jsonBody = "{ \"role\": \"EMPLOYER\", \"cpf\": \"71715507452\", \"email\": \"\", \"name\": \"Valid Name\" }";  // CPF vazio
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())  // Espera 400 (Bad Request)
                .andExpect(content().string("Preenchimento obrigatório"));  // Mensagem de campo obrigatório


    }
    @Test
    public void testRegisterWithShortPassword() throws Exception {
        // Dados de entrada para o teste, senha com menos de 6 caracteres
        String requestBody = "{\n" +
                "  \"cpf\": \"12345678900\",\n" +
                "  \"email\": \"valid@example.com\",\n" +
                "  \"name\": \"Valid User\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"role\": \"EMPLOYER\"\n" +
                "}";

        // Simula a requisição POST
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())  // Espera um erro de requisição
                .andExpect(content().string("Senha deve conter pelo menos 6 caracteres"));  // Mensagem de erro
    }

    @Test
    public void testRegisterWithValidPassword() throws Exception {
        // Dados de entrada com senha válida (mais de 6 caracteres)
        String requestBody = "{\n" +
                "  \"cpf\": \"12345678900\",\n" +
                "  \"email\": \"valid@example.com\",\n" +
                "  \"name\": \"Valid User\",\n" +
                "  \"password\": \"validPassword123\",\n" +
                "  \"role\": \"EMPLOYER\"\n" +
                "}";

        // Simula o registro do employer
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())  // Espera um OK
                .andExpect(content().string("Employer registered successfully!"));  // Mensagem de sucesso
    }
    @Test
    public void testRegisterWithLongPassword() throws Exception {
        // Dados de entrada com senha com mais de 40 caracteres
        String requestBody = "{\n" +
                "  \"cpf\": \"12345678900\",\n" +
                "  \"email\": \"valid@example.com\",\n" +
                "  \"name\": \"Valid User\",\n" +
                "  \"password\": \"ThisPasswordIsWayTooLongAndShouldFailTheTest12345\",\n" +  // Senha com mais de 40 caracteres
                "  \"role\": \"EMPLOYER\"\n" +
                "}";

        // Simula a requisição POST com a URL correta
        mockMvc.perform(post("/api/auth/register")  // Corrigido para o endpoint correto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())  // Espera um erro de requisição
                .andExpect(content().string("Senha deve conter no máximo 40 caracteres"));  // Mensagem de erro
    }

}



