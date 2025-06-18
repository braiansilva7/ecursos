package com.ecursos.myapp.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String CCABR_ELO_EXECUTIVO_AVANCADO = "ROLE_CCABR_ELO_EXECUTIVO_AVANCADO";
    public static final String CCABR_ELO_EXECUTIVO_BASICO = "ROLE_CCABR_ELO_EXECUTIVO_BASICO";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
