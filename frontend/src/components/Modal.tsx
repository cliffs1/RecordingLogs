"use client";

import { useEffect, useRef } from "react";
import { X } from "lucide-react";
import styles from "./Modal.module.css";

interface ModalProps {
  open: boolean;
  onClose: () => void;
  title: string;
  description?: string;
  icon?: React.ReactNode;
  size?: "sm" | "md" | "lg";
  children: React.ReactNode;
}

/**
 * Accessible modal built on the native <dialog> element:
 * focus is trapped by the browser, Esc closes it, clicking the backdrop closes it.
 */
export default function Modal({
  open,
  onClose,
  title,
  description,
  icon,
  size = "md",
  children,
}: ModalProps) {
  const ref = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = ref.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={ref}
      className={`${styles.dialog} ${styles[size]}`}
      aria-labelledby="modal-title"
      onCancel={(event) => {
        event.preventDefault();
        onClose();
      }}
      onClick={(event) => {
        if (event.target === ref.current) onClose();
      }}
    >
      {open && (
        <div className={styles.panel}>
          <header className={styles.head}>
            {icon && <span className={styles.icon}>{icon}</span>}
            <div className={styles.titles}>
              <h2 id="modal-title" className={styles.title}>
                {title}
              </h2>
              {description && <p className={styles.description}>{description}</p>}
            </div>
            <button type="button" className={styles.close} onClick={onClose} aria-label="Close">
              <X size={20} />
            </button>
          </header>
          <div className={styles.body}>{children}</div>
        </div>
      )}
    </dialog>
  );
}
