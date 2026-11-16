"use client";

import { useState } from "react";
import { Shield, ShieldCheck, User } from "lucide-react";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { userApi } from "@/lib/endpoints";
import type { Role, UserResponse } from "@/types/api";

const ROLES: { value: Role; label: string; description: string; icon: typeof User }[] = [
  { value: "USER", label: "User", description: "Rates, reviews and makes lists", icon: User },
  { value: "MODERATOR", label: "Moderator", description: "Also edits the catalog", icon: Shield },
  { value: "ADMIN", label: "Admin", description: "Full access, manages users", icon: ShieldCheck },
];

/** Change a user's role (PATCH /api/users/{id}/role) with a radio group. */
export default function RoleForm({
  user,
  onDone,
  onCancel,
}: {
  user: UserResponse;
  onDone: (user: UserResponse) => void;
  onCancel: () => void;
}) {
  const [role, setRole] = useState<Role>(user.role);
  const { busy, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        submit(() => userApi.updateRole(user.id, role), {
          success: `${user.username} is now ${role.toLowerCase()}`,
          onSuccess: onDone,
        });
      }}
    >
      <fieldset className="choice-group" style={{ flexDirection: "column" }}>
        <legend className="label" style={{ marginBottom: 8 }}>
          Role for {user.username}
        </legend>
        {ROLES.map(({ value, label, description, icon: Icon }) => (
          <label key={value} className="choice">
            <input
              type="radio"
              name="role"
              value={value}
              checked={role === value}
              onChange={() => setRole(value)}
            />
            <Icon size={18} aria-hidden />
            <span>
              <strong>{label}</strong>
              <span className="muted small"> — {description}</span>
            </span>
          </label>
        ))}
      </fieldset>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} submitLabel="Change role" />
    </form>
  );
}
