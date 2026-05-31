# Contributing to StonX

Thank you for your interest in contributing to **StonX** — the Virtual Stock Market Simulator!

## Getting Started

1. **Fork** the repository on GitHub
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Stonx.git
   cd Stonx
   ```
3. Create a **feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Project Structure

```
StonX/
├── src/                          # Java Swing backend (MVC)
│   └── main/java/com/stonx/
│       ├── model/                # Data models (Stock, Portfolio, User…)
│       ├── controller/           # Business logic controllers
│       ├── service/              # Services (Market, User, Chatbot)
│       ├── ui/                   # Swing UI panels
│       └── utils/                # Utilities (FileHandler, Observers)
├── StockVaultSimulator/
│   └── frontend/                 # React + Vite + TailwindCSS frontend
│       ├── src/
│       │   ├── components/       # Reusable UI components
│       │   ├── pages/            # Route-level page components
│       │   ├── context/          # Global state (AppContext)
│       │   ├── data/             # Mock stock data & constants
│       │   └── utils/            # Simulators, formatters, TradeBot
│       └── package.json
├── pom.xml                       # Maven build config
├── LICENSE
└── README.md
```

## Development Setup

### Java Backend
```bash
# Requirements: Java 17+, Maven 3.6+
mvn clean install
mvn exec:java
```

### React Frontend
```bash
cd StockVaultSimulator/frontend
npm install
npm run dev          # Dev server at http://localhost:5173
npm run build        # Production build
```

## Code Style

- **Java**: Follow standard Oracle Java conventions; use meaningful method/variable names
- **React/JSX**: Functional components with hooks; TailwindCSS for all styling
- Keep components **focused and reusable**
- Add **JSDoc** comments to public methods

## Pull Request Guidelines

- Keep PRs **focused** — one feature or fix per PR
- Update the `README.md` if you add a new feature
- Ensure `mvn clean install` passes before submitting
- Ensure `npm run build` passes for frontend changes

## Reporting Issues

Please use [GitHub Issues](https://github.com/tiyamisu/Stonx/issues) and include:
- Steps to reproduce
- Expected vs actual behaviour
- OS and Java/Node version

## License

By contributing, you agree that your contributions will be licensed under the **MIT License**.
