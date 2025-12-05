import com.taskmanager.dao.OrganizationDAO;
import com.taskmanager.dao.OrganizationMemberDAO;
import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.model.Organization;
import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import com.taskmanager.service.OrganizationService;
import com.taskmanager.util.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationDAO organizationDAO;

    @Mock
    private OrganizationMemberDAO memberDAO;

    @Mock
    private ProjectDAO projectDAO;

    @InjectMocks
    private OrganizationService organizationService;
    
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Setup user dummy yang sedang login (ID 1)
        mockUser = new User(1, "Test User", "test@user.com", "hash");
        
        // Memastikan UserSession aktif untuk test yang membutuhkan login
        if (UserSession.getInstance().isLoggedIn()) {
             UserSession.getInstance().endSession();
        }
        UserSession.getInstance().startSession(mockUser);
        
        // Note: @InjectMocks akan menginisialisasi organizationService
    }

    @AfterEach
    void tearDown() {
        UserSession.getInstance().endSession();
    }

    // ==========================================
    // CREATE ORGANIZATION TESTS
    // ==========================================

    @Test
    @DisplayName("Create Org: Sukses membuat organisasi dan menetapkan OWNER")
    void testCreateOrganizationSuccess() throws Exception { // Menambahkan throws Exception
        // Arrange
        String orgName = "New Test Org";
        
        // 1. Mock DAO: findByCode(uniqueCode) return empty (unik)
        when(organizationDAO.findByCode(anyString())).thenReturn(Optional.empty());

        // 2. Mock DAO: save() (Insert) mengembalikan Organization dengan ID baru
        when(organizationDAO.save(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(10); // ID yang dihasilkan DB
            return org;
        });

        // Act
        Organization result = organizationService.createOrganization(orgName);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getId());
        
        // Verifikasi langkah-langkah dipanggil
        verify(organizationDAO).save(any(Organization.class));
        // Verifikasi Owner ditambahkan
        verify(memberDAO).addMember(eq(10), eq(mockUser.getId()), eq("OWNER")); 
    }

    @Test
    @DisplayName("Create Org: Gagal jika nama kosong/null")
    void testCreateOrganizationValidation() throws Exception { // Menambahkan throws Exception
        // Act & Assert
        assertThrows(Exception.class, () ->
                organizationService.createOrganization("")
        );
        assertThrows(Exception.class, () ->
                organizationService.createOrganization(null)
        );

        verify(organizationDAO, never()).save(any());
        verify(memberDAO, never()).addMember(anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Create Org: Rollback jika gagal menambahkan owner")
    void testCreateOrganizationMemberAddFails() throws Exception { // Menambahkan throws Exception
        // Arrange
        String orgName = "Rollback Test";
        
        when(organizationDAO.findByCode(anyString())).thenReturn(Optional.empty());
        when(organizationDAO.save(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(11);
            return org;
        });
        
        // Mock kegagalan saat menambahkan member/owner
        doThrow(new SQLException("DB Error")).when(memberDAO).addMember(anyInt(), anyInt(), eq("OWNER"));

        // Act & Assert
        Exception e = assertThrows(Exception.class, () ->
            organizationService.createOrganization(orgName)
        );
        
        assertTrue(e.getMessage().contains("Transaction aborted"));
        
        // VERIFIKASI ROLLBACK MANUAL:
        verify(organizationDAO).save(any(Organization.class));
        verify(organizationDAO).delete(11); // Pastikan organisasi yang baru dibuat dihapus
    }
    
    // ==========================================
    // JOIN ORGANIZATION TESTS
    // ==========================================
    
    @Test
    @DisplayName("Join Org: Sukses bergabung dengan kode valid")
    void testJoinOrganizationSuccess() throws Exception { // Menambahkan throws Exception
        // Arrange
        String code = "ABCDEF";
        Organization mockOrg = new Organization(20, "Joinable Org", code);
        
        when(organizationDAO.findByCode(code)).thenReturn(Optional.of(mockOrg));
        when(memberDAO.isMember(mockOrg.getId(), mockUser.getId())).thenReturn(false); // Belum jadi member

        // Act
        Organization result = organizationService.joinOrganization(code);

        // Assert
        assertNotNull(result);
        
        // Verifikasi ditambahkan sebagai MEMBER
        verify(memberDAO).addMember(eq(20), eq(mockUser.getId()));
    }

    @Test
    @DisplayName("Join Org: Gagal jika kode tidak ditemukan")
    void testJoinOrganizationNotFound() throws Exception { // Menambahkan throws Exception
        // Arrange
        String code = "INVALID";
        when(organizationDAO.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () ->
                organizationService.joinOrganization(code)
        );

        verify(memberDAO, never()).addMember(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Join Org: Gagal jika sudah menjadi anggota")
    void testJoinOrganizationAlreadyMember() throws Exception { // Menambahkan throws Exception
        // Arrange
        String code = "EXIST";
        Organization mockOrg = new Organization(21, "Existing Org", code);

        when(organizationDAO.findByCode(code)).thenReturn(Optional.of(mockOrg));
        when(memberDAO.isMember(mockOrg.getId(), mockUser.getId())).thenReturn(true); // Sudah jadi member

        // Act & Assert
        Exception e = assertThrows(Exception.class, () ->
                organizationService.joinOrganization(code)
        );
        
        assertTrue(e.getMessage().contains("You are already a member"));
        // Memperbaiki error: Memanggil method addMember yang melempar checked exception
        verify(memberDAO, never()).addMember(anyInt(), anyInt()); 
    }

    // ==========================================
    // GET ORGANIZATION DETAILS TESTS
    // ==========================================

    @Test
    @DisplayName("Get Details: Sukses mengambil detail lengkap (Project dan Member)")
    void testGetOrganizationDetailsSuccess() throws Exception { // Menambahkan throws Exception
        // Arrange
        int orgId = 30;
        Organization mockOrg = new Organization(orgId, "Full Org", "CODE");
        List<Project> mockProjects = List.of(new Project(1, orgId, "P1", "D1"));
        List<User> mockMembers = List.of(mockUser);

        when(organizationDAO.findById(orgId)).thenReturn(Optional.of(mockOrg));
        when(projectDAO.findByOrganizationId(orgId)).thenReturn(mockProjects);
        when(memberDAO.findMembersByOrganizationId(orgId)).thenReturn(mockMembers);

        // Act
        Organization result = organizationService.getOrganizationDetails(orgId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProjects().size());
        assertEquals(1, result.getMembers().size());
        
        verify(organizationDAO).findById(orgId);
        verify(projectDAO).findByOrganizationId(orgId);
        verify(memberDAO).findMembersByOrganizationId(orgId);
    }
    
    // ==========================================
    // GET ORGANIZATION LIST FOR CURRENT USER
    // ==========================================

    @Test
    @DisplayName("Get List: Sukses mengambil semua organisasi milik user yang login")
    void testGetOrganizationsByCurrentUserSuccess() throws Exception { // Menambahkan throws Exception
        // Arrange
        int orgId1 = 40;
        int orgId2 = 41;
        Organization org1 = new Organization(orgId1, "Org A", "A");
        Organization org2 = new Organization(orgId2, "Org B", "B");
        
        List<Integer> mockOrgIds = List.of(orgId1, orgId2);

        when(memberDAO.findOrganizationIdsByUserId(mockUser.getId())).thenReturn(mockOrgIds);
        when(organizationDAO.findById(orgId1)).thenReturn(Optional.of(org1));
        when(organizationDAO.findById(orgId2)).thenReturn(Optional.of(org2));

        // Act
        List<Organization> result = organizationService.getOrganizationsByCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(memberDAO).findOrganizationIdsByUserId(mockUser.getId());
        verify(organizationDAO, times(2)).findById(anyInt());
    }
}