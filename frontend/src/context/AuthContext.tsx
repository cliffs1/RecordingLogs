"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { onSessionChange, refreshSession, setAccessToken } from "@/lib/api";
import { authApi } from "@/lib/endpoints";
import type { LoginRequest, Role, UserRequest, UserResponse } from "@/types/api";

interface AuthState {
  user: UserResponse | null;
  /** True until the initial silent refresh on page load has finished. */
  loading: boolean;
  login: (body: LoginRequest) => Promise<UserResponse>;
  register: (body: UserRequest) => Promise<UserResponse>;
  logout: () => Promise<void>;
  /** Re-reads the profile with GET /api/auth/me (after editing it). */
  reloadMe: () => Promise<void>;
  hasRole: (...roles: Role[]) => boolean;
  /** MODERATOR or ADMIN: may edit the catalog (genres, artists, albums, songs). */
  canEditCatalog: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // Keep UI in sync with silent refreshes done by the API wrapper.
  useEffect(() => onSessionChange((session) => setUser(session?.user ?? null)), []);

  // Restore the session from the refresh cookie on first load.
  useEffect(() => {
    let active = true;
    refreshSession().finally(() => {
      if (active) setLoading(false);
    });
    return () => {
      active = false;
    };
  }, []);

  const login = useCallback(async (body: LoginRequest) => {
    const session = await authApi.login(body);
    setUser(session.user);
    return session.user;
  }, []);

  const register = useCallback(async (body: UserRequest) => {
    const created = await authApi.register(body);
    await login({ login: body.username, password: body.password });
    return created;
  }, [login]);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      setAccessToken(null);
      setUser(null);
    }
  }, []);

  const reloadMe = useCallback(async () => {
    setUser(await authApi.me());
  }, []);

  const value = useMemo<AuthState>(() => {
    const hasRole = (...roles: Role[]) => !!user && roles.includes(user.role);
    return {
      user,
      loading,
      login,
      register,
      logout,
      reloadMe,
      hasRole,
      canEditCatalog: hasRole("MODERATOR", "ADMIN"),
      isAdmin: hasRole("ADMIN"),
    };
  }, [user, loading, login, register, logout, reloadMe]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used inside <AuthProvider>");
  return context;
}
