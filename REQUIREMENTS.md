# StonX — System Requirements & Dependencies

## System Requirements

| Requirement | Version |
|-------------|---------|
| Java JDK    | 17 or higher |
| Apache Maven| 3.6 or higher |
| Node.js     | 18 or higher |
| npm         | 9 or higher |
| Git         | 2.x |

## Java Backend Dependencies
> Managed automatically by Maven (`pom.xml`)

| Library | Version | Purpose |
|---------|---------|---------|
| [FlatLaf](https://www.formdev.com/flatlaf/) | 3.4.1 | Modern dark/light Swing Look & Feel |
| [JFreeChart](https://www.jfree.org/jfreechart/) | 1.5.4 | Stock price charts and graphs |

## React Frontend Dependencies
> Managed automatically by npm (`StockVaultSimulator/frontend/package.json`)

### Core
| Package | Purpose |
|---------|---------|
| react + react-dom | UI rendering |
| react-router-dom | Client-side routing |
| vite | Dev server & bundler |

### Styling
| Package | Purpose |
|---------|---------|
| tailwindcss v4 | Utility-first CSS |
| @tailwindcss/postcss | PostCSS integration |
| autoprefixer | CSS vendor prefixes |

### UI & Animations
| Package | Purpose |
|---------|---------|
| framer-motion | Smooth animations & transitions |
| lucide-react | Premium icon library |

### Data Visualisation
| Package | Purpose |
|---------|---------|
| recharts | Interactive stock charts (Area, Pie, Bar) |

### TypeScript Support
| Package | Purpose |
|---------|---------|
| typescript | Type checking |
| @types/react | React type definitions |

---

## Installation

### Backend (Java Swing)
```bash
# Install dependencies and compile
mvn clean install

# Run the application
mvn exec:java

# Or run the packaged JAR
java -jar target/StonX-1.0-SNAPSHOT.jar
```

### Frontend (React + Vite)
```bash
cd StockVaultSimulator/frontend

# Install all npm dependencies
npm install

# Start development server (http://localhost:5173)
npm run dev

# Build for production
npm run build
```

---

## Default Login Credentials

| Username   | Password |
|------------|----------|
| `java_demo`| `1234`   |
| `demo`     | `demo123`|
| `admin`    | `admin123`|
