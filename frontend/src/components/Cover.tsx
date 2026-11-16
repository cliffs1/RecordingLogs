"use client";

import { useState } from "react";
import { Disc3 } from "lucide-react";
import styles from "./Cover.module.css";

/**
 * Album cover. Uses a plain <img> because covers are arbitrary external URLs.
 * The image is fluid (max-width: 100%) and keeps a square aspect ratio,
 * so it shrinks with its container. Falls back to an SVG placeholder.
 */
export default function Cover({
  src,
  alt,
  className,
}: {
  src?: string | null;
  alt: string;
  className?: string;
}) {
  const [failed, setFailed] = useState(false);
  const showImage = src && !failed;

  return (
    <div className={`${styles.cover} ${className ?? ""}`}>
      {showImage ? (
        // eslint-disable-next-line @next/next/no-img-element
        <img src={src} alt={alt} loading="lazy" onError={() => setFailed(true)} />
      ) : (
        <div className={styles.placeholder} role="img" aria-label={alt}>
          <Disc3 aria-hidden />
        </div>
      )}
    </div>
  );
}
