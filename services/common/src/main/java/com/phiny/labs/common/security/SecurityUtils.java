package com.phiny.labs.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Get the current authenticated user's email/username from SecurityContext
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof UserPrincipal) {
                return ((UserPrincipal) authentication.getPrincipal()).getUsername();
            }
            return authentication.getPrincipal().toString();
        }
        return null;
    }

    /**
     * Get the current authenticated user's ID from SecurityContext
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUserId();
        }
        return null;
    }

    /**
     * Get the current authenticated user's tenant ID from SecurityContext
     */
    public static Long getCurrentTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getTenantId();
        }
        return null;
    }

    /**
     * Get the current authenticated user's role from SecurityContext
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getRole();
        }
        return null;
    }

    /**
     * Get the current UserPrincipal
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Check if current user has a specific authority/role
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(authority));
        }
        return false;
    }

    /**
     * Check if current user is ADMIN
     */
    public static boolean isAdmin() {
        return hasAuthority("ADMIN");
    }

    /**
     * Check if current user is the owner of the resource (by user ID)
     */
    public static boolean isOwner(Long resourceUserId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null || resourceUserId == null) {
            return false;
        }
        return currentUserId.equals(resourceUserId);
    }

    /**
     * Check if current user can access resource (owner or admin)
     */
    public static boolean canAccess(Long resourceUserId) {
        return isAdmin() || isOwner(resourceUserId);
    }

    /**
     * Require that the current user is the owner or admin, throw exception if not
     */
    public static void requireOwnerOrAdmin(Long resourceUserId) {
        if (!canAccess(resourceUserId)) {
            throw new SecurityException("Access denied: You can only access your own resources");
        }
    }
}

