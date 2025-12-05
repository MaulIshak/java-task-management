import com.taskmanager.dao.TaskDAO;
import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.model.enums.TaskStatus;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private UserDAO userDAO;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // Inject kedua mock DAO ke service
        taskService = new TaskService(taskDAO, userDAO);
    }

    // ==========================================
    // CREATE TASK TESTS
    // ==========================================

    @Test
    @DisplayName("Create Task: Sukses membuat task baru")
    void testCreateTaskSuccess() {
        // Arrange
        int projectId = 1;
        String title = "Implementasi Login";
        String description = "Buat login screen";
        LocalDate dueDate = LocalDate.now().plusDays(3);
        User assignee = new User(1, "Budi", "budi@test.com", "hash");

        // Mock behavior: Return task yang sama tapi dengan ID baru
        when(taskDAO.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(100); // Simulasi auto-increment ID
            return t;
        });

        // Act
        Task createdTask = taskService.createTask(projectId, title, description, dueDate, assignee);

        // Assert
        assertNotNull(createdTask);
        assertEquals(100, createdTask.getId());
        assertEquals(title, createdTask.getTitle());
        assertEquals(TaskStatus.TODO, createdTask.getStatus()); // Default status check
        assertEquals(assignee, createdTask.getAssignee());

        verify(taskDAO).save(any(Task.class));
    }

    @Test
    @DisplayName("Create Task: Gagal jika judul kosong")
    void testCreateTaskValidation() {
        // Act & Assert
        Exception e = assertThrows(Exception.class, () ->
                taskService.createTask(1, "", "Desc", LocalDate.now(), null)
        );
        assertEquals("Task title cannot be empty", e.getMessage());

        verify(taskDAO, never()).save(any());
    }

    // ==========================================
    // UPDATE STATUS TESTS
    // ==========================================

    @Test
    @DisplayName("Update Status: Status berubah dan disimpan")
    void testUpdateTaskStatus() {
        // Arrange
        Task task = new Task();
        task.setId(1);
        task.setTitle("Old Task");
        task.setStatus(TaskStatus.TODO);

        // Act
        taskService.updateTaskStatus(task, TaskStatus.ON_PROGRESS);

        // Assert
        assertEquals(TaskStatus.ON_PROGRESS, task.getStatus());
        verify(taskDAO).save(task); // Pastikan DAO dipanggil untuk update
    }

    // ==========================================
    // GET TASKS BY PROJECT TESTS
    // ==========================================

    @Test
    @DisplayName("Get Tasks: Mengambil list dan mengisi detail Assignee")
    void testGetTasksByProject() {
        // Arrange
        int projectId = 5;

        // Setup Dummy User (Full Data dari UserDAO)
        User fullUser = new User(10, "Budi Santoso", "budi@gmail.com", "hash123");

        // Setup Dummy Task dari TaskDAO (Assignee cuma punya ID)
        User dummyUserRef = new User();
        dummyUserRef.setId(10); // ID Cocok dengan fullUser

        Task taskFromDB = new Task();
        taskFromDB.setId(50);
        taskFromDB.setProjectId(projectId);
        taskFromDB.setTitle("Database Task");
        taskFromDB.setAssignee(dummyUserRef); // Assignee belum lengkap, cuma ID

        List<Task> mockTaskList = new ArrayList<>();
        mockTaskList.add(taskFromDB);

        // Mocking DAO calls
        when(taskDAO.findByProjectId(projectId)).thenReturn(mockTaskList);
        when(userDAO.findById(10)).thenReturn(Optional.of(fullUser));

        // Act
        List<Task> result = taskService.getTasksByProject(projectId);

        // Assert
        assertFalse(result.isEmpty());
        Task t = result.get(0);

        // Verifikasi bahwa data assignee sudah "di-hydrate" (dilengkapi)
        assertNotNull(t.getAssignee());
        assertEquals("Budi Santoso", t.getAssignee().getName()); // Name harus ada sekarang
        assertEquals("budi@gmail.com", t.getAssignee().getEmail());

        verify(taskDAO).findByProjectId(projectId);
        verify(userDAO).findById(10);
    }

    @Test
    @DisplayName("Get Tasks: Task tanpa assignee tidak memanggil UserDAO")
    void testGetTasksNoAssignee() {
        // Arrange
        Task taskNoAssignee = new Task();
        taskNoAssignee.setId(1);
        taskNoAssignee.setAssignee(null); // Tidak ada assignee

        when(taskDAO.findByProjectId(1)).thenReturn(List.of(taskNoAssignee));

        // Act
        taskService.getTasksByProject(1);

        // Assert
        verify(userDAO, never()).findById(anyInt()); // UserDAO jangan dipanggil
    }
}