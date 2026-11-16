"use client";

import Link from "next/link";
import { Lock } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { PageLoader } from "./Spinner";
import { EmptyState } from "./States";
import type { Role } from "@/types/api";

/** Renders children only for logged-in users (optionally with one of `roles`). */
export default function RequireAuth({
  children,
  roles,
}: {
  children: React.ReactNode;
  roles?: Role[];
}) {
  const { user, loading } = useAuth();

  if (loading) return <PageLoader label="Checking your session…" />;

  if (!user) {
    return (
      <EmptyState title="Log in required" icon={<Lock size={32} aria-hidden />}>
        <p>You need an account to see this page.</p>
        <div className="row">
          <Link href="/login" className="btn btn-primary">
            Log in
          </Link>
          <Link href="/register" className="btn btn-secondary">
            Create account
          </Link>
        </div>
      </EmptyState>
    );
  }

  if (roles && !roles.includes(user.role)) {
    return (
      <EmptyState title="Access denied" icon={<Lock size={32} aria-hidden />}>
        <p>This page is only available to: {roles.join(", ").toLowerCase()}.</p>
      </EmptyState>
    );
  }

  return <>{children}</>;
}
