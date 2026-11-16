"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import Field from "@/components/Field";
import FormActions from "@/components/forms/FormActions";
import { useAuth } from "@/context/AuthContext";
import { useSubmit } from "@/hooks/useSubmit";
import styles from "../auth.module.css";

export default function RegisterPage() {
  const router = useRouter();
  const { register } = useAuth();
  const [form, setForm] = useState({
    username: "",
    displayName: "",
    email: "",
    password: "",
    confirm: "",
  });
  const [terms, setTerms] = useState(false);
  const { busy, errors, setErrors, formError, submit } = useSubmit();

  const set = (key: keyof typeof form) => (event: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [key]: event.target.value });

  return (
    <div className={styles.wrap}>
      <aside className={styles.art}>
        <h2>Start your music log.</h2>
        <p>Rate every album you hear, write reviews, and follow friends to see what they love.</p>
      </aside>

      <section className={styles.panel}>
        <div>
          <p className="eyebrow">New account</p>
          <h1>Sign up</h1>
        </div>

        <form
          className="form"
          onSubmit={(event) => {
            event.preventDefault();
            if (form.password !== form.confirm) {
              setErrors({ confirm: "Passwords do not match" });
              return;
            }
            submit(
              () =>
                register({
                  username: form.username.trim(),
                  email: form.email.trim(),
                  password: form.password,
                  displayName: form.displayName.trim() || undefined,
                }),
              { success: "Account created — you are now logged in", onSuccess: () => router.push("/") },
            );
          }}
        >
          <div className="form-row">
            <Field label="Username" htmlFor="reg-username" required error={errors.username} hint="3–50 characters">
              <input
                id="reg-username"
                className="input"
                value={form.username}
                onChange={set("username")}
                required
                minLength={3}
                maxLength={50}
                autoFocus
                autoComplete="username"
              />
            </Field>
            <Field label="Display name" htmlFor="reg-display" error={errors.displayName}>
              <input
                id="reg-display"
                className="input"
                value={form.displayName}
                onChange={set("displayName")}
                maxLength={100}
                autoComplete="nickname"
              />
            </Field>
          </div>
          <Field label="Email" htmlFor="reg-email" required error={errors.email}>
            <input
              id="reg-email"
              type="email"
              className="input"
              value={form.email}
              onChange={set("email")}
              required
              autoComplete="email"
            />
          </Field>
          <div className="form-row">
            <Field label="Password" htmlFor="reg-password" required error={errors.password} hint="8–72 characters">
              <input
                id="reg-password"
                type="password"
                className="input"
                value={form.password}
                onChange={set("password")}
                required
                minLength={8}
                maxLength={72}
                autoComplete="new-password"
              />
            </Field>
            <Field label="Repeat password" htmlFor="reg-confirm" required error={errors.confirm}>
              <input
                id="reg-confirm"
                type="password"
                className="input"
                value={form.confirm}
                onChange={set("confirm")}
                required
                autoComplete="new-password"
              />
            </Field>
          </div>
          <label className="row small" style={{ gap: 8, cursor: "pointer" }}>
            <input
              type="checkbox"
              checked={terms}
              onChange={(event) => setTerms(event.target.checked)}
              required
              style={{ accentColor: "var(--violet-600)" }}
            />
            I agree to keep my reviews respectful
          </label>
          <FormActions busy={busy} formError={formError} submitLabel="Create account" />
        </form>

        <p className={styles.switch}>
          Already have an account? <Link href="/login">Log in</Link>
        </p>
      </section>
    </div>
  );
}
