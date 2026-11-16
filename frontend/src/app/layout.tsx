import type { Metadata, Viewport } from "next";
import { Inter, Space_Grotesk } from "next/font/google";
import Providers from "./providers";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import "./globals.css";

const display = Space_Grotesk({
  variable: "--font-display",
  subsets: ["latin", "latin-ext"],
  weight: ["400", "500", "600", "700"],
});

const body = Inter({
  variable: "--font-body",
  subsets: ["latin", "latin-ext"],
});

export const metadata: Metadata = {
  title: {
    default: "RecordingLogs",
    template: "%s · RecordingLogs",
  },
  description: "Rate and review albums and songs, build lists and follow other listeners.",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  themeColor: "#17143a",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${display.variable} ${body.variable}`}>
      <body>
        <Providers>
          <Header />
          <main className="main">
            <div className="container">{children}</div>
          </main>
          <Footer />
        </Providers>
      </body>
    </html>
  );
}
