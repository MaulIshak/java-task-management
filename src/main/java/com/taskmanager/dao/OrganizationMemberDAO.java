package com.taskmanager.dao;

import com.taskmanager.model.User;
import com.taskmanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganizationMemberDAO {

    private final Connection connection;

    // Untuk hidrasi data User secara penuh (sama seperti di TaskService)
    private final UserDAO userDAO; 

    public OrganizationMemberDAO() {
        this.connection = DBConnection.getInstance().getConnection();
        this.userDAO = new UserDAO();
    }
    
    // Constructor untuk testing (dependency injection)
    public OrganizationMemberDAO(Connection connection, UserDAO userDAO) {
        this.connection = connection;
        this.userDAO = userDAO;
    }

    /**
     * Menambahkan anggota ke organisasi dengan peran (role) default 'MEMBER'.
     * @param organizationId ID Organisasi
     * @param userId ID User
     */
    public void addMember(int organizationId, int userId) throws SQLException {
        // Tabel organization_members memiliki field: organization_id, user_id, role
        String sql = "INSERT INTO organization_members (organization_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Menghapus anggota dari organisasi.
     * @param organizationId ID Organisasi
     * @param userId ID User
     * @return true jika berhasil dihapus, false sebaliknya.
     */
    public boolean removeMember(int organizationId, int userId) {
        String sql = "DELETE FROM organization_members WHERE organization_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengambil semua user yang menjadi anggota dari suatu organisasi.
     * @param organizationId ID Organisasi
     * @return List User yang merupakan anggota.
     */
    public List<User> findMembersByOrganizationId(int organizationId) {
        // Kita hanya mengambil user_id dari tabel junction
        String sql = "SELECT user_id FROM organization_members WHERE organization_id = ?";
        List<User> members = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    
                    // Gunakan UserDAO untuk mendapatkan data User secara lengkap (Hydration)
                    userDAO.findById(userId).ifPresent(members::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    // Catatan: Jika Anda perlu mendapatkan peran (role) anggota, 
    // Anda harus membuat model OrganizationMember atau map hasilnya di sini.
    // Saat ini, metode ini hanya mengembalikan objek User.
}