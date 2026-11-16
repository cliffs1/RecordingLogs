import Link from "next/link";
import { Disc3 } from "lucide-react";

export default function NotFound() {
  return (
    <div className="empty" style={{ marginTop: 32 }}>
      <Disc3 size={48} aria-hidden />
      <h1>Page not found</h1>
      <p>This track seems to be missing from the record.</p>
      <Link href="/" className="btn btn-primary">
        Back to home
      </Link>
    </div>
  );
}
