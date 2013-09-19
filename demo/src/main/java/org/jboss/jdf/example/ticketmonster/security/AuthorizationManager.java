/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.jdf.example.ticketmonster.security;

import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.apache.deltaspike.security.api.authorization.Secures;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Role;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>
 *  Provides authorization services for the application.
 * </p>
 * 
 * @author Pedro Silva
 * 
 */
@Named
@ApplicationScoped
public class AuthorizationManager {

    private static final String ANY_RESOURCE_PATTERN = "*";

    private Map<String, String[]> roleProtectedResources = new HashMap<String, String[]>();

    @Inject
    private Instance<Identity> identity;

    @Inject
    private Instance<IdentityManager> identityManager;

    @Inject
    private Instance<RelationshipManager> relationshipManager;

    @PostConstruct
    public void init() {
        // let's configure which URIs should be protected
        this.roleProtectedResources.put("/admin/*", new String[] { "Administrator" });
    }

    /**
     * <p>
     * Check if a method or type annotated with the {@link UserLoggedIn} is being access by an authenticated user. This method
     * is called before the annotated method is called.
     * </p>
     * 
     * @param identity
     * @return
     */
    @Secures
    @UserLoggedIn
    public boolean isUserLoggedIn(Identity identity) {
        return identity.isLoggedIn();
    }

    public boolean isAdmin() {
        Identity identity = getIdentity();

        if (isUserLoggedIn(identity)) {
            IdentityManager identityManager = getIdentityManager();
            RelationshipManager relationshipManager = getRelationshipManager();

            return BasicModel.hasRole(relationshipManager, identity.getAccount(), BasicModel.getRole(identityManager, "Administrator"));
        }

        return false;
    }

    /**
     * <p>
     * Check if the current user is allowed to access the requested resource.
     * </p>
     * 
     * @param httpRequest
     * @throws UserNotLoggedInException If the request requires authentication and the user is not authenticated
     * @throws AccessDeniedException If the request is not allowed considering the resource permissions.
     */
    public boolean isAllowed(HttpServletRequest httpRequest) throws AccessDeniedException {
        final String requestURI = httpRequest.getRequestURI();

        Set<Entry<String, String[]>> entrySet = this.roleProtectedResources.entrySet();

        for (Entry<String, String[]> entry : entrySet) {
            if (matches(entry.getKey(), requestURI)) {
                Identity identity = getIdentity();

                if (!identity.isLoggedIn()) {
                    return false;
                } else {
                    String[] roles = entry.getValue();

                    for (String roleName : roles) {
                        IdentityManager identityManager = getIdentityManager();

                        Role role = BasicModel.getRole(identityManager, roleName.trim());

                        if (role == null) {
                            throw new IllegalStateException("The specified role does not exists [" + role
                                    + "]. Check your configuration.");
                        }

                        if (!BasicModel.hasRole(getRelationshipManager(), identity.getAccount(), role)) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    private RelationshipManager getRelationshipManager() {
        return this.relationshipManager.get();
    }

    /**
     * <p>
     * Checks if the provided URI matches the specified pattern.
     * </p>
     * 
     * @param uri
     * @param pattern
     * @return
     */
    private boolean matches(String pattern, String uri) {
        if (pattern.equals(ANY_RESOURCE_PATTERN)) {
            return true;
        }

        if (pattern.equals(uri)) {
            return true;
        }

        if (pattern.endsWith(ANY_RESOURCE_PATTERN)) {
            String formattedPattern = pattern.replaceAll("/[*]", "/");

            if (uri.contains(formattedPattern)) {
                return true;
            }
        }

        if (pattern.equals("*")) {
            return true;
        } else {
            return (pattern.startsWith(ANY_RESOURCE_PATTERN) && uri.endsWith(pattern.substring(
                    ANY_RESOURCE_PATTERN.length() + 1, pattern.length())));
        }
    }

    private IdentityManager getIdentityManager() {
        return this.identityManager.get();
    }

    private Identity getIdentity() {
        return this.identity.get();
    }

}