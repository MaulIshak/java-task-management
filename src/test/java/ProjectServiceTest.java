import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.dao.TaskDAO;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private TaskDAO taskDAO;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        // Inject kedua mock DAO ke service
        projectService = new ProjectService(projectDAO, taskDAO);
    }

    // ==========================================
    // CREATE PROJECT TESTS
    // ==========================================

    @Test
    @DisplayName("Create Project: Sukses membuat project baru")
    void testCreateProjectSuccess() {
        // Arrange
        int orgId = 1;
        String name = "Website Redesign";
        String desc = "Revamp UI/UX";

        // Mock behavior: Return project dengan ID baru
        when(projectDAO.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(55); // Simulasi ID dari DB
            return p;
        });

        // Act
        Project result = projectService.createProject(orgId, name, desc);

        // Assert
        assertNotNull(result);
        assertEquals(55, result.getId());
        assertEquals(name, result.getName());
        assertEquals(orgId, result.getOrganizationId());

        verify(projectDAO).save(any(Project.class));
    }

    @Test
    @DisplayName("Create Project: Gagal jika nama kosong")
    void testCreateProjectValidation() {
        // Act & Assert
        Exception e = assertThrows(Exception.class, () ->
                projectService.createProject(1, "   ", "Desc")
        );
        assertEquals("Project name cannot be empty", e.getMessage());

        verify(projectDAO, never()).save(any());
    }

    // ==========================================
    // GET PROJECT WITH TASKS TESTS
    // ==========================================

    @Test
    @DisplayName("Get Project with Tasks: Mengambil project beserta list task-nya")
    void testGetProjectWithTasksSuccess() {
        // Arrange
        int projectId = 10;
        Project mockProject = new Project(projectId, 1, "Alpha Project", "Desc");

        Task t1 = new Task(); t1.setId(101); t1.setTitle("Task A");
        Task t2 = new Task(); t2.setId(102); t2.setTitle("Task B");
        List<Task> mockTasks = List.of(t1, t2);

        // Mock DAO behaviors
        when(projectDAO.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(taskDAO.findByProjectId(projectId)).thenReturn(mockTasks);

        // Act
        Project result = projectService.getProjectWithTasks(projectId);

        // Assert
        assertNotNull(result);
        assertEquals("Alpha Project", result.getName());
        assertEquals(2, result.getTasks().size()); // Pastikan tasks terisi
        assertEquals("Task A", result.getTasks().get(0).getTitle());

        verify(projectDAO).findById(projectId);
        verify(taskDAO).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Get Project with Tasks: Gagal jika project tidak ditemukan")
    void testGetProjectWithTasksNotFound() {
        // Arrange
        int projectId = 999;
        when(projectDAO.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception e = assertThrows(Exception.class, () ->
                projectService.getProjectWithTasks(projectId)
        );
        assertEquals("Project not found with ID: "+projectId, e.getMessage());

        // Pastikan tidak mencoba mengambil task jika project saja tidak ada
        verify(taskDAO, never()).findByProjectId(anyInt());
    }

    // ==========================================
    // UPDATE PROJECT TESTS
    // ==========================================

    @Test
    @DisplayName("Update Project: Sukses update nama dan deskripsi")
    void testUpdateProjectSuccess()  {
        // Arrange
        Project existingProject = new Project(1, 1, "Old Name", "Old Desc");

        // Act
        projectService.updateProject(existingProject, "New Name", "New Desc");

        // Assert
        assertEquals("New Name", existingProject.getName());
        assertEquals("New Desc", existingProject.getDescription());

        verify(projectDAO).save(existingProject);
    }

    @Test
    @DisplayName("Update Project: Gagal jika nama baru kosong")
    void testUpdateProjectValidation() {
        Project p = new Project(1, 1, "Name", "Desc");

        assertThrows(Exception.class, () ->
                projectService.updateProject(p, "", "New Desc")
        );

        verify(projectDAO, never()).save(any());
    }

    // ==========================================
    // DELETE PROJECT TESTS
    // ==========================================

    @Test
    @DisplayName("Delete Project: Memanggil DAO delete")
    void testDeleteProject() {
        // Act
        projectService.deleteProject(123);

        // Assert
        verify(projectDAO).delete(123);
    }
}