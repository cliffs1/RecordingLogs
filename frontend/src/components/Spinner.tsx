import { Loader2 } from "lucide-react";
import styles from "./Spinner.module.css";

export default function Spinner({ size = 20, label }: { size?: number; label?: string }) {
  return (
    <span className={styles.wrap} role={label ? "status" : undefined}>
      <Loader2 size={size} className={styles.spin} aria-hidden />
      {label && <span>{label}</span>}
    </span>
  );
}

/** Full-width centered loader for page sections. */
export function PageLoader({ label = "Loading…" }: { label?: string }) {
  return (
    <div className={styles.page}>
      <Spinner size={28} label={label} />
    </div>
  );
}
