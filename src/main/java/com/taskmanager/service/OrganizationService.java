package com.taskmanager.service;

import com.taskmanager.dao.OrganizationDAO;
import com.taskmanager.dao.OrganizationMemberDAO;
import com.taskmanager.dao.ProjectDAO;
import com.taskmanager.model.Organization;
import com.taskmanager.model.User;
import com.taskmanager.util.OrganizationUtil;
import com.taskmanager.util.UserSession;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrganizationService implements com.taskmanager.model.interfaces.Subject {

    private static OrganizationService instance;

    private final OrganizationDAO organizationDAO;
    private final OrganizationMemberDAO memberDAO;
    private final ProjectDAO projectDAO;
    private final java.util.List<com.taskmanager.model.interfaces.Observer> observers = new java.util.ArrayList<>();

    private OrganizationService() {
        this.organizationDAO = new OrganizationDAO();
        this.memberDAO = new OrganizationMemberDAO();
        this.projectDAO = new ProjectDAO();
    }

    public static synchronized OrganizationService getInstance() {
        if (instance == null) {
            instance = new OrganizationService();
        }
        return instance;
    }

    // Constructor for dependency injection (optional, can be kept or removed if not
    // used)
    public OrganizationService(OrganizationDAO organizationDAO, OrganizationMemberDAO memberDAO,
            ProjectDAO projectDAO) {
        this.organizationDAO = organizationDAO;
        this.memberDAO = memberDAO;
        this.projectDAO = projectDAO;
    }

    // ===========================================
    // 1. CREATE ORGANIZATION
    // ===========================================

    /**
     * Membuat organisasi baru dan menetapkan pembuatnya sebagai OWNER.
     * 
     * @param name Nama organisasi.
     * @return Objek Organization yang baru dibuat.
     * @throws Exception Jika validasi gagal atau operasi DB error.
     */
    public Organization createOrganization(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name cannot be empty.");
        }

        if (!UserSession.getInstance().isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create an organization.");
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
            throw new RuntimeException("Failed to add user as organization owner. Transaction aborted.", e);
        }

        notifyObservers();
        return newOrganization;
    }

    // ===========================================
    // 2. JOIN ORGANIZATION
    // ===========================================

    /**
     * Bergabung ke organisasi menggunakan kodenya.
     * 
     * @param organizationCode Kode unik organisasi.
     * @return Objek Organization yang berhasil digabungi.
     * @throws Exception Jika kode tidak valid, atau user sudah menjadi anggota.
     */
    public Organization joinOrganization(String organizationCode) {
        if (!UserSession.getInstance().isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to join an organization.");
        }
        User currentUser = UserSession.getInstance().getCurrentUser();
        Optional<Organization> orgOpt = organizationDAO.findByCode(organizationCode.toUpperCase().trim());

        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found with code: " + organizationCode);
        }
        Organization organization = orgOpt.get();

        if (memberDAO.isOwner(organization.getId(), currentUser.getId())) {
            throw new IllegalStateException("You are the owner of this organization.");
        }

        if (memberDAO.isMember(organization.getId(), currentUser.getId())) {
            throw new IllegalStateException("You are already a member of " + organization.getOrgName());
        }
        try {
            memberDAO.addMember(organization.getId(), currentUser.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to join organization due to database error.", e);
        }

        notifyObservers();
        return organization;
    }

    // ===========================================
    // 3. VIEW DETAIL ORGANIZATION
    // ===========================================

    /**
     * Mengambil detail lengkap Organization (dengan Projects dan Members).
     * 
     * @param organizationId ID Organisasi.
     * @return Objek Organization yang sudah di-hydrate.
     * @throws Exception Jika Organization tidak ditemukan.
     */
    public Organization getOrganizationDetails(int organizationId) {
        Optional<Organization> orgOpt = organizationDAO.findById(organizationId);

        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found.");
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
     * 
     * @return List Organization.
     * @throws Exception Jika tidak ada user yang login.
     */
    public List<Organization> getOrganizationsByCurrentUser() {
        if (!UserSession.getInstance().isLoggedIn()) {
            throw new IllegalStateException("User must be logged in.");
        }
        User currentUser = UserSession.getInstance().getCurrentUser();

        List<Integer> orgIds = memberDAO.findOrganizationIdsByUserId(currentUser.getId());

        return orgIds.stream()
                .map(organizationDAO::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Memeriksa apakah user yang sedang login adalah pemilik organisasi.
     * 
     * @param organizationId ID Organisasi
     * @return true jika owner.
     */
    public boolean isCurrentUserOwner(int organizationId) {
        if (!UserSession.getInstance().isLoggedIn()) {
            return false;
        }
        return memberDAO.isOwner(organizationId, UserSession.getInstance().getCurrentUser().getId());
    }

    @Override
    public void registerObserver(com.taskmanager.model.interfaces.Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(com.taskmanager.model.interfaces.Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (com.taskmanager.model.interfaces.Observer observer : observers) {
            observer.update();
        }
    }
}