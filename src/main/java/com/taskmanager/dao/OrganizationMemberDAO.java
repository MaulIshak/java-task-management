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
     * Catatan: Karena role memiliki default 'MEMBER' di SQL, kita dapat membiarkan
     * SQL
     * tanpa memasukkan role, kecuali jika kita ingin secara eksplisit menentukan
     * 'OWNER'.
     *
     * @param organizationId ID Organisasi
     * @param userId         ID User
     * @param role           Peran anggota ('OWNER', 'MEMBER', dll).
     */
    public void addMember(int organizationId, int userId, String role) throws SQLException {
        String sql = "INSERT INTO organization_members (organization_id, user_id, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            stmt.setInt(2, userId);
            stmt.setString(3, role);
            stmt.executeUpdate();
        }
    }

    public void addMember(int organizationId, int userId) throws SQLException {
        // Kita panggil versi 3 parameter dengan default 'MEMBER'
        addMember(organizationId, userId, "MEMBER");
    }

    /**
     * Menghapus anggota dari organisasi.
     * 
     * @param organizationId ID Organisasi
     * @param userId         ID User
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
     * 
     * @param organizationId ID Organisasi
     * @return List User yang merupakan anggota.
     */
    public List<User> findMembersByOrganizationId(int organizationId) {
        String sql = "SELECT user_id FROM organization_members WHERE organization_id = ?";
        List<User> members = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    userDAO.findById(userId).ifPresent(members::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    // --- Custom Method Baru ---

    /**
     * Memeriksa apakah seorang pengguna sudah menjadi anggota dari organisasi
     * tertentu.
     * Dibutuhkan untuk validasi sebelum Join Organization.
     *
     * @param organizationId ID Organisasi
     * @param userId         ID User
     * @return true jika pengguna adalah anggota, false sebaliknya.
     */
    public boolean isMember(int organizationId, int userId) {
        String sql = "SELECT 1 FROM organization_members WHERE organization_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengambil semua organisasi yang diikuti oleh seorang user.
     * Dibutuhkan untuk fitur Organization List.
     *
     * @param userId ID User
     * @return List ID Organisasi yang diikuti.
     */
    public List<Integer> findOrganizationIdsByUserId(int userId) {
        String sql = "SELECT organization_id FROM organization_members WHERE user_id = ?";
        List<Integer> organizationIds = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    organizationIds.add(rs.getInt("organization_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return organizationIds;
    }

    /**
     * Memeriksa apakah user adalah OWNER dari organisasi.
     * 
     * @param organizationId ID Organisasi
     * @param userId         ID User
     * @return true jika user adalah OWNER.
     */
    public boolean isOwner(int organizationId, int userId) {
        String sql = "SELECT 1 FROM organization_members WHERE organization_id = ? AND user_id = ? AND role = 'OWNER'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizationId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}