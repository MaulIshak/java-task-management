package com.taskmanager.service;

import com.taskmanager.dao.OrganizationDAO;
import com.taskmanager.dao.OrganizationMemberDAO;
import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.model.Organization;
import com.taskmanager.model.User;
import com.taskmanager.util.OrganizationUtil;
import com.taskmanager.util.UserSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrganizationService {

    private final OrganizationDAO organizationDAO;
    private final OrganizationMemberDAO memberDAO;
    private final ProjectDAO projectDAO; 

    public OrganizationService() {
        this.organizationDAO = new OrganizationDAO();
        this.memberDAO = new OrganizationMemberDAO();
        this.projectDAO = new ProjectDAO();
    }

    public OrganizationService(OrganizationDAO organizationDAO, OrganizationMemberDAO memberDAO, ProjectDAO projectDAO) {
        this.organizationDAO = organizationDAO;
        this.memberDAO = memberDAO;
        this.projectDAO = projectDAO;
    }

    // ===========================================
    // 1. CREATE ORGANIZATION
    // ===========================================

    /**
     * Membuat organisasi baru dan menetapkan pembuatnya sebagai OWNER.
     * @param name Nama organisasi.
     * @return Objek Organization yang baru dibuat.
     * @throws Exception Jika validasi gagal atau operasi DB error.
     */
    public Organization createOrganization(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Organization name cannot be empty.");
        }

        if (!UserSession.getInstance().isLoggedIn()) {
            throw new Exception("User must be logged in to create an organization.");
        }
        User currentUser = UserSession.getInstance().getCurrentUser();

        String uniqueCode;
        do {
            uniqueCode = OrganizationUtil.generateUniqueCode();
        } while (organizationDAO.findByCode(uniqueCode).isPresent());

        Organization newOrganization = new Organization(0, name, uniqueCode);
        newOrganization = organizationDAO.save(newOrganization); 

        try {
            memberDAO.addMember(newOrganization.getId(), currentUser.getId(), "OWNER");
        } catch (SQLException e) {
            organizationDAO.delete(newOrganization.getId());
            throw new Exception("Failed to add user as organization owner. Transaction aborted.");
        }

        return newOrganization;
    }

    // ===========================================
    // 2. JOIN ORGANIZATION
    // ===========================================

    /**
     * Bergabung ke organisasi menggunakan kodenya.
     * @param organizationCode Kode unik organisasi.
     * @return Objek Organization yang berhasil digabungi.
     * @throws Exception Jika kode tidak valid, atau user sudah menjadi anggota.
     */
    public Organization joinOrganization(String organizationCode) throws Exception {
        if (!UserSession.getInstance().isLoggedIn()) {
            throw new Exception("User must be logged in to join an organization.");
        }
        User currentUser = UserSession.getInstance().getCurrentUser();
        Optional<Organization> orgOpt = organizationDAO.findByCode(organizationCode.toUpperCase().trim());

        if (orgOpt.isEmpty()) {
            throw new Exception("Organization not found with code: " + organizationCode);
        }
        Organization organization = orgOpt.get();

        if (memberDAO.isMember(organization.getId(), currentUser.getId())) {
            throw new Exception("You are already a member of " + organization.getOrgName());
        }
        try {
            memberDAO.addMember(organization.getId(), currentUser.getId()); 
        } catch (SQLException e) {
            throw new Exception("Failed to join organization due to database error.");
        }

        return organization;
    }

    // ===========================================
    // 3. VIEW DETAIL ORGANIZATION
    // ===========================================

    /**
     * Mengambil detail lengkap Organization (dengan Projects dan Members).
     * @param organizationId ID Organisasi.
     * @return Objek Organization yang sudah di-hydrate.
     * @throws Exception Jika Organization tidak ditemukan.
     */
    public Organization getOrganizationDetails(int organizationId) throws Exception {
        Optional<Organization> orgOpt = organizationDAO.findById(organizationId);

        if (orgOpt.isEmpty()) {
            throw new Exception("Organization not found.");
        }

        Organization organization = orgOpt.get();
        organization.setProjects(projectDAO.findByOrganizationId(organizationId));
        organization.setMembers(memberDAO.findMembersByOrganizationId(organizationId));
        
        return organization;
    }


    // ===========================================
    // 4. GET ORGANIZATION LIST FOR CURRENT USER (Utility untuk View)
    // ===========================================
    
    /**
     * Mengambil semua Organization yang diikuti oleh user yang sedang login.
     * @return List Organization.
     * @throws Exception Jika tidak ada user yang login.
     */
    public List<Organization> getOrganizationsByCurrentUser() throws Exception {
        if (!UserSession.getInstance().isLoggedIn()) {
            throw new Exception("User must be logged in.");
        }
        User currentUser = UserSession.getInstance().getCurrentUser();
        
        List<Integer> orgIds = memberDAO.findOrganizationIdsByUserId(currentUser.getId());
        
        return orgIds.stream()
                .map(organizationDAO::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}