import com.taskmanager.dao.UserDAO;
import com.taskmanager.model.User;
import com.taskmanager.service.AuthService;
import com.taskmanager.util.PasswordUtils;
import com.taskmanager.util.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthService authService;
    
    @BeforeEach
    void setUp() {
        // Inject mock UserDAO
        authService = new AuthService(userDAO);
}

    @AfterEach
    void tearDown() {
        // Reset session
        if (UserSession.getInstance().isLoggedIn()) {
            UserSession.getInstance().endSession();
        }
    }

    // ==========================================
    // REGISTER TESTS
    // ==========================================

    @Test
    @DisplayName("Register: Sukses membuat user baru")
    void testRegisterSuccess() {
        // Arrange
        String name = "Budi";
        String email = "budi@test.com";
        String password = "password123";

        // Mock behavior: Email belum terdaftar
        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());

        // Mock behavior: Saat save dipanggil, kembalikan user yang sama (seolah-olah sukses dari DB)
        when(userDAO.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1); // Simulasi ID dari database
            return u;
        });

        // Act
        User result = authService.register(name, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertNotEquals(password, result.getPasswordHash()); // Password harus di-hash!

        // Verifikasi DAO dipanggil
        verify(userDAO).findByEmail(email);
        verify(userDAO).save(any(User.class));
    }

    @Test
    @DisplayName("Register: Gagal jika email sudah ada")
    void testRegisterFailEmailExists() {
        // Arrange
        String email = "ada@test.com";
        when(userDAO.findByEmail(email)).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.register("New User", email, "pass123");
        });

        assertEquals("Email already registered!", exception.getMessage());
        verify(userDAO, never()).save(any()); // Pastikan tidak ada simpan data
    }

    @Test
    @DisplayName("Register: Validasi input kosong atau password pendek")
    void testRegisterValidation() {
        // Name empty
        assertThrows(Exception.class, () -> authService.register("", "a@b.com", "123456"));

        // Email empty
        assertThrows(Exception.class, () -> authService.register("Budi", "", "123456"));

        // Password too short
        assertThrows(Exception.class, () -> authService.register("Budi", "a@b.com", "123"));
    }

    // ==========================================
    // LOGIN TESTS
    // ==========================================

    @Test
    @DisplayName("Login: Sukses login dengan kredensial valid")
    void testLoginSuccess() {
        // Arrange
        String email = "valid@test.com";
        String rawPassword = "secretPassword";
        String hashedPassword = PasswordUtils.hashPassword(rawPassword); // Gunakan util asli utk konsistensi

        User mockUser = new User(10, "Valid User", email, hashedPassword);

        when(userDAO.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        User result = authService.login(email, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertTrue(UserSession.getInstance().isLoggedIn(), "Session harus aktif setelah login");
        assertEquals(mockUser, UserSession.getInstance().getCurrentUser());
    }

    @Test
    @DisplayName("Login: Gagal jika email tidak ditemukan")
    void testLoginUserNotFound() {
        // Arrange
        String email = "unknown@test.com";
        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception e = assertThrows(Exception.class, () -> authService.login(email, "pass"));
        assertEquals("Invalid credentials", e.getMessage());
    }

    @Test
    @DisplayName("Login: Gagal jika password salah")
    void testLoginWrongPassword() {
        // Arrange
        String email = "user@test.com";
        String correctHash = PasswordUtils.hashPassword("correctPass");
        User mockUser = new User(1, "User", email, correctHash);

        when(userDAO.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        Exception e = assertThrows(Exception.class, () -> authService.login(email, "wrongPass"));
        assertEquals("Invalid credentials", e.getMessage());

        // Pastikan session tidak terbentuk
        assertFalse(UserSession.getInstance().isLoggedIn());
    }
}