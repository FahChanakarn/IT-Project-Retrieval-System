package com.springmvc.controller;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Work with jBCrypt which is Java 8 compatible
    private static final int WORKLOAD = 12; // cost factor

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainPassword, salt);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        String password = "mju6504106307";
        String hashed = hashPassword(password);

        System.out.println("Plain Password: " + password);
        System.out.println("Hashed Password: " + hashed);
        System.out.println("Password Length: " + hashed.length());
        System.out.println("Verify Correct Password: " + verifyPassword(password, hashed));
        System.out.println("Verify Wrong Password: " + verifyPassword("wrongpassword", hashed));
    }
}