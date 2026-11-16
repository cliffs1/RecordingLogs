"use client";

import { useState } from "react";
import Field from "../Field";
import FormActions from "./FormActions";
import { useSubmit } from "@/hooks/useSubmit";
import { userApi } from "@/lib/endpoints";
import type { UserResponse } from "@/types/api";

/** Edit profile (PUT /api/users/{id}). Leaving the password empty keeps it unchanged. */
export default function UserForm({
  user,
  onDone,
  onCancel,
}: {
  user: UserResponse;
  onDone: (user: UserResponse) => void;
  onCancel: () => void;
}) {
  const [username, setUsername] = useState(user.username);
  const [email, setEmail] = useState(user.email);
  const [displayName, setDisplayName] = useState(user.displayName ?? "");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <form
      className="form"
      onSubmit={(event) => {
        event.preventDefault();
        submit(
          () =>
            userApi.update(user.id, {
              username: username.trim(),
              email: email.trim(),
              displayName: displayName.trim() || undefined,
              password: password || undefined,
            }),
          { success: "Profile updated", onSuccess: onDone },
        );
      }}
    >
      <div className="form-row">
        <Field label="Username" htmlFor="user-username" required error={errors.username}>
          <input
            id="user-username"
            className="input"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
            minLength={3}
            maxLength={50}
            autoComplete="username"
          />
        </Field>
        <Field label="Display name" htmlFor="user-display" error={errors.displayName}>
          <input
            id="user-display"
            className="input"
            value={displayName}
            onChange={(event) => setDisplayName(event.target.value)}
            maxLength={100}
          />
        </Field>
      </div>
      <Field label="Email" htmlFor="user-email" required error={errors.email}>
        <input
          id="user-email"
          type="email"
          className="input"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
          autoComplete="email"
        />
      </Field>
      <Field
        label="New password"
        htmlFor="user-password"
        error={errors.password}
        hint="Leave empty to keep the current password (8–72 characters)"
      >
        <input
          id="user-password"
          type={showPassword ? "text" : "password"}
          className="input"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          minLength={8}
          maxLength={72}
          autoComplete="new-password"
        />
      </Field>
      <label className="row small" style={{ gap: 8, cursor: "pointer" }}>
        <input
          type="checkbox"
          checked={showPassword}
          onChange={(event) => setShowPassword(event.target.checked)}
          style={{ accentColor: "var(--violet-600)" }}
        />
        Show password
      </label>
      <FormActions busy={busy} onCancel={onCancel} formError={formError} />
    </form>
  );
}
