import Link from "next/link";
import { BookOpen, Disc3, FileCode2, GitBranch, Headphones, Mail } from "lucide-react";
import styles from "./Footer.module.css";

export default function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={`container ${styles.grid}`}>
        <section className={styles.about}>
          <h2 className={styles.brand}>
            <Disc3 size={20} aria-hidden /> RecordingLogs
          </h2>
          <p>
            Rate albums and songs, write reviews, build lists and follow what your friends are
            listening to.
          </p>
        </section>

        <section>
          <h3 className={styles.heading}>Explore</h3>
          <ul className={styles.links}>
            <li>
              <Link href="/artists">Artists</Link>
            </li>
            <li>
              <Link href="/genres">Genres</Link>
            </li>
            <li>
              <Link href="/reviews">Latest reviews</Link>
            </li>
          </ul>
        </section>

        <section>
          <h3 className={styles.heading}>Project</h3>
          <ul className={styles.links}>
            <li>
              <a href="/swagger-ui/index.html" target="_blank" rel="noreferrer">
                <FileCode2 size={14} aria-hidden /> API docs
              </a>
            </li>
            <li>
              <a href="https://github.com/cliffs1/RecordingLogs" target="_blank" rel="noreferrer">
                <GitBranch size={14} aria-hidden /> Source code
              </a>
            </li>
            <li>
              <a href="mailto:info@recordinglogs.example">
                <Mail size={14} aria-hidden /> Contact
              </a>
            </li>
          </ul>
        </section>

        <section className={styles.badgeBox}>
          <Headphones size={28} aria-hidden />
          <p>
            <strong>KTU · Saityno taikomųjų programų projektavimas</strong>
          </p>
          <p className={styles.fine}>
            <BookOpen size={12} aria-hidden /> Course project, {new Date().getFullYear()}
          </p>
        </section>
      </div>
      <div className={styles.bottom}>
        <div className="container">© {new Date().getFullYear()} RecordingLogs</div>
      </div>
    </footer>
  );
}
