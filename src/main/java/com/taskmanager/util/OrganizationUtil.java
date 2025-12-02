package com.taskmanager.util;

import java.security.SecureRandom;

public class OrganizationUtil {
    // Karakter yang diizinkan: huruf besar (A-Z) dan angka (0-9)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrganizationUtil() {
        // Private constructor untuk mencegah instansiasi
    }

    /**
     * Menghasilkan kode organisasi alfanumerik acak sepanjang 6 karakter.
     * Kode ini perlu diverifikasi keunikannya di OrganizationService/DAO 
     * sebelum disimpan ke database (untuk menghindari duplikasi).
     * @return Kode alfanumerik acak 6 karakter.
     */
    public static String generateUniqueCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
} 
