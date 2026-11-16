"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import {
  Disc3,
  Home,
  ListMusic,
  LogIn,
  LogOut,
  Menu,
  MessageSquareText,
  Mic2,
  ShieldCheck,
  Tags,
  UserPlus,
  UserRound,
  X,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { useToast } from "./Toast";
import styles from "./Header.module.css";

const NAV = [
  { href: "/", label: "Home", icon: Home },
  { href: "/artists", label: "Artists", icon: Mic2 },
  { href: "/genres", label: "Genres", icon: Tags },
  { href: "/reviews", label: "Reviews", icon: MessageSquareText },
  { href: "/lists", label: "Lists", icon: ListMusic, auth: true },
];

export default function Header() {
  const pathname = usePathname();
  const router = useRouter();
  const toast = useToast();
  const { user, loading, logout, isAdmin } = useAuth();
  const [open, setOpen] = useState(false);
  const [lastPath, setLastPath] = useState(pathname);

  // Close the mobile menu after navigating (state adjusted during render, no effect needed).
  if (pathname !== lastPath) {
    setLastPath(pathname);
    setOpen(false);
  }

  // Lock page scroll while the mobile menu is open.
  useEffect(() => {
    document.body.style.overflow = open ? "hidden" : "";
    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

  const isActive = (href: string) => (href === "/" ? pathname === "/" : pathname.startsWith(href));

  async function handleLogout() {
    await logout();
    toast.info("You have been logged out");
    router.push("/");
  }

  const items = NAV.filter((item) => !item.auth || user);
  if (isAdmin) items.push({ href: "/admin/users", label: "Admin", icon: ShieldCheck });

  return (
    <header className={styles.header}>
      <div className={`container ${styles.bar}`}>
        <Link href="/" className={styles.brand} aria-label="RecordingLogs home">
          <span className={styles.logo}>
            <Disc3 size={22} />
          </span>
          <span>
            Recording<strong>Logs</strong>
          </span>
        </Link>

        <button
          type="button"
          className={styles.burger}
          aria-label={open ? "Close menu" : "Open menu"}
          aria-expanded={open}
          aria-controls="main-nav"
          onClick={() => setOpen((value) => !value)}
        >
          {open ? <X size={24} /> : <Menu size={24} />}
        </button>

        <nav id="main-nav" className={`${styles.nav} ${open ? styles.open : ""}`} aria-label="Main">
          <ul className={styles.links}>
            {items.map(({ href, label, icon: Icon }) => (
              <li key={href}>
                <Link
                  href={href}
                  className={`${styles.link} ${isActive(href) ? styles.active : ""}`}
                  aria-current={isActive(href) ? "page" : undefined}
                >
                  <Icon size={18} aria-hidden />
                  {label}
                </Link>
              </li>
            ))}
          </ul>

          <div className={styles.account}>
            {loading ? null : user ? (
              <>
                <Link href={`/users/${user.id}`} className={styles.profile}>
                  <UserRound size={18} aria-hidden />
                  <span>{user.displayName || user.username}</span>
                </Link>
                <button type="button" className={styles.action} onClick={handleLogout}>
                  <LogOut size={18} aria-hidden />
                  Log out
                </button>
              </>
            ) : (
              <>
                <Link href="/login" className={styles.action}>
                  <LogIn size={18} aria-hidden />
                  Log in
                </Link>
                <Link href="/register" className={`${styles.action} ${styles.cta}`}>
                  <UserPlus size={18} aria-hidden />
                  Sign up
                </Link>
              </>
            )}
          </div>
        </nav>
      </div>
      {open && <div className={styles.scrim} onClick={() => setOpen(false)} aria-hidden />}
    </header>
  );
}
