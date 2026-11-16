"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Info } from "lucide-react";
import Field from "@/components/Field";
import FormActions from "@/components/forms/FormActions";
import { useAuth } from "@/context/AuthContext";
import { useSubmit } from "@/hooks/useSubmit";
import styles from "../auth.module.css";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [form, setForm] = useState({ login: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const { busy, errors, formError, submit } = useSubmit();

  return (
    <div className={styles.wrap}>
      <aside className={styles.art}>
        <h2>Welcome back.</h2>
        <p>Pick up where you left off: your ratings, reviews and lists are waiting.</p>
      </aside>

      <section className={styles.panel}>
        <div>
          <p className="eyebrow">Account</p>
          <h1>Log in</h1>
        </div>

        <form
          className="form"
          onSubmit={(event) => {
            event.preventDefault();
            submit(() => login(form), {
              success: "Logged in — welcome back!",
              onSuccess: () => router.push("/"),
            });
          }}
        >
          <Field label="Username or email" htmlFor="login" required error={errors.login}>
            <input
              id="login"
              className="input"
              value={form.login}
              onChange={(event) => setForm({ ...form, login: event.target.value })}
              required
              autoFocus
              autoComplete="username"
            />
          </Field>
          <Field label="Password" htmlFor="password" required error={errors.password}>
            <input
              id="password"
              type={showPassword ? "text" : "password"}
              className="input"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              required
              autoComplete="current-password"
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
          <FormActions busy={busy} formError={formError} submitLabel="Log in" />
        </form>

        <p className={styles.demo}>
          <Info size={14} aria-hidden /> Demo accounts: <strong>rokas</strong>,{" "}
          <strong>moderator</strong> or <strong>admin</strong> — password <strong>password123</strong>
        </p>

        <p className={styles.switch}>
          No account yet? <Link href="/register">Create one</Link>
        </p>
      </section>
    </div>
  );
}
