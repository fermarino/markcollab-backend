//package projects;
//
//import com.markcollab.controller.ProjectController;
//import com.markcollab.service.ProjectService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class ProjectControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private ProjectService projectService;
//
//    private ProjectController projectController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        projectController = new ProjectController(projectService); // Agora funciona
//        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
//    }
//
//
//    @Test
//    void testGetProjects() throws Exception {
//        mockMvc.perform(get("/api/projects/open"))
//                .andExpect(status().isOk());
//
//        verify(projectService, times(1)).findAll();
//    }
//}
