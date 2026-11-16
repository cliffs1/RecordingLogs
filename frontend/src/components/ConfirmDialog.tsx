"use client";

import { useState } from "react";
import { Trash2 } from "lucide-react";
import Modal from "./Modal";
import Spinner from "./Spinner";
import { useToast } from "./Toast";
import { errorMessage } from "@/lib/format";

interface ConfirmDialogProps {
  open: boolean;
  onClose: () => void;
  title: string;
  message: React.ReactNode;
  confirmLabel?: string;
  successMessage?: string;
  onConfirm: () => Promise<unknown>;
  onDone?: () => void;
}

/** Delete confirmation in a modal, with a loading state and toast feedback. */
export default function ConfirmDialog({
  open,
  onClose,
  title,
  message,
  confirmLabel = "Delete",
  successMessage = "Deleted",
  onConfirm,
  onDone,
}: ConfirmDialogProps) {
  const toast = useToast();
  const [busy, setBusy] = useState(false);

  async function confirm() {
    setBusy(true);
    try {
      await onConfirm();
      toast.success(successMessage);
      onClose();
      onDone?.();
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal open={open} onClose={onClose} title={title} size="sm" icon={<Trash2 size={20} />}>
      <div className="stack">
        <p className="muted">{message}</p>
        <div className="form-actions">
          <button type="button" className="btn btn-ghost" onClick={onClose} disabled={busy}>
            Cancel
          </button>
          <button type="button" className="btn btn-danger" onClick={confirm} disabled={busy}>
            {busy ? <Spinner size={16} /> : <Trash2 size={16} />}
            {confirmLabel}
          </button>
        </div>
      </div>
    </Modal>
  );
}
