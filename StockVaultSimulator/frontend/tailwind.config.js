/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#080b0e", // Deep warm dark base
        surface: {
          DEFAULT: "#111418", // Warm charcoal card background
          hover: "#1a1f26",   // Card hover background
          border: "#242b36",  // Faint card border
        },
        accent: {
          amber: "#FDA481",   // StockVault Warm Salmon/Amber
          orange: "#FF8C42",  // StockVault Orange
          gold: "#FFB347",    // StockVault Gold
          rust: "#E8613C",    // StockVault Deep Orange
        },
        text: {
          primary: "#f8fafc",
          secondary: "#94a3b8",
          muted: "#475569",
        }
      },
      fontFamily: {
        sans: ["Plus Jakarta Sans", "Inter", "sans-serif"],
        mono: ["JetBrains Mono", "monospace"],
      },
      boxShadow: {
        glow: "0 0 30px rgba(253, 164, 129, 0.18)",
        card: "0 8px 30px rgba(0, 0, 0, 0.5)",
      }
    },
  },
  plugins: [],
}
