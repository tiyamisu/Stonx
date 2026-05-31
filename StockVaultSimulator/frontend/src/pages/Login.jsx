// ============================================================
// Login.jsx — Premium Landing Page & Login Interface
// Redesigned from first principles with a luxury fintech look.
// Features dynamic live prices, visual indicators, and smooth animations.
// ============================================================

import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useApp } from "../context/AppContext";
import { formatCurrency } from "../utils/stockSimulator";
import { 
  TrendingUp, TrendingDown, Shield, Cpu, BarChart3, 
  ArrowUpRight, Landmark, ArrowRight, UserCheck, HelpCircle
} from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

// Demo credentials
const ALLOWED_USERS = [
  { username: "demo", password: "demo123" },
  { username: "java_demo", password: "1234" },
  { username: "admin", password: "admin123" }
];

export default function Login() {
  const navigate = useNavigate();
  const { state, dispatch } = useApp();
  const { stocks } = state;

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Landing page interactive sections active index
  const [activeFeature, setActiveFeature] = useState(0);

  // Find a few notable stocks for display cards (e.g. NVDA, TSLA, AAPL)
  const previewTickers = ["NVDA", "TSLA", "AAPL", "GOOGL"];
  const previewStocks = previewTickers
    .map(ticker => stocks.find(s => s.ticker === ticker))
    .filter(Boolean);

  // Market indices mock/real values for hero ticker
  const mockIndices = [
    { name: "S&P 500", value: 5218.50, change: 0.58, color: "accent-cyan" },
    { name: "NASDAQ", value: 16420.00, change: 1.03, color: "accent-emerald" },
    { name: "SENSEX", value: 74215.10, change: 0.84, color: "accent-blue" },
    { name: "NIFTY 50", value: 22513.70, change: 0.79, color: "accent-violet" }
  ];

  // Rotate features preview every 4 seconds
  useEffect(() => {
    const timer = setInterval(() => {
      setActiveFeature((prev) => (prev + 1) % 3);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  function handleLogin(e) {
    e.preventDefault();
    setError("");

    const matchedUser = ALLOWED_USERS.find(
      (u) => u.username.toLowerCase() === username.trim().toLowerCase() && u.password === password
    );

    if (!matchedUser) {
      setError("Invalid credentials. Try java_demo / 1234 or demo / demo123.");
      return;
    }

    setLoading(true);
    setTimeout(() => {
      dispatch({ type: "LOGIN", payload: { username: matchedUser.username } });
      navigate("/dashboard");
    }, 1000);
  }

  const features = [
    {
      icon: Cpu,
      title: "TradeBot AI Counsel",
      description: "Get smart stock recommendations, risk alerts, and deep insights from our keyword-based financial assistant.",
      color: "from-cyan-500/20 to-blue-500/20"
    },
    {
      icon: BarChart3,
      title: "Advanced Terminal Charts",
      description: "Interactive TradingView-style charts with customizable metrics, volume sliders, and gradient area layouts.",
      color: "from-emerald-500/20 to-cyan-500/20"
    },
    {
      icon: Shield,
      title: "Secure Paper Trading",
      description: "Simulate high-stakes portfolio holdings using mock algorithms with ₹100,000 baseline cash capital.",
      color: "from-violet-500/20 to-fuchsia-500/20"
    }
  ];

  return (
    <div className="relative min-h-screen bg-[#07090d] text-slate-100 flex flex-col justify-between overflow-x-hidden font-sans">
      
      {/* ── Background Aesthetics ── */}
      <div className="absolute inset-0 z-0 overflow-hidden pointer-events-none">
        {/* Warm ambient light gradients */}
        <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-orange-950/20 blur-[120px] animate-pulse-soft" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-amber-950/15 blur-[120px] animate-pulse-soft" />
        <div className="absolute top-[40%] left-[30%] w-[35%] h-[35%] rounded-full bg-[#FDA481]/5 blur-[120px] animate-pulse-soft" />
        
        {/* Subtle dot matrix grid overlay */}
        <div className="absolute inset-0 opacity-[0.03] bg-[radial-gradient(#ffffff_1px,transparent_1px)] [background-size:24px_24px]" />
      </div>

      {/* ── Header / Navigation Ribbon ── */}
      <header className="relative z-10 w-full px-6 py-5 md:px-12 flex justify-between items-center border-b border-white/[0.03] bg-[#07090d]/50 backdrop-blur-md">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-[#FDA481] to-[#E8613C] flex items-center justify-center shadow-lg shadow-amber-500/10">
            <TrendingUp size={18} className="text-white" />
          </div>
          <div>
            <h1 className="text-lg font-black tracking-tight bg-gradient-to-r from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">StonX</h1>
            <p className="text-[10px] text-slate-500 font-bold tracking-widest uppercase">Series A Terminal</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <span className="hidden sm:inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping" />
            Market Simulator Active
          </span>
        </div>
      </header>

      {/* ── Landing Page Main Container ── */}
      <main className="relative z-10 flex-1 grid grid-cols-1 lg:grid-cols-12 gap-8 px-6 py-8 md:px-12 items-center max-w-7xl mx-auto w-full">
        
        {/* Left Column: Vision, Dynamic Charts & Feature Previews */}
        <div className="lg:col-span-7 flex flex-col gap-8 justify-center text-left">
          
          {/* Brand Announcement Pill */}
          <motion.div 
            initial={{ opacity: 0, y: 15 }} 
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="self-start inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/[0.03] border border-white/[0.06] text-xs font-medium text-slate-300"
          >
            <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />
            V2.0 Fully Redesigned Fintech UI
          </motion.div>

          {/* Large Hero Title */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
            className="flex flex-col gap-3"
          >
            <h2 className="text-4xl md:text-5xl lg:text-6xl font-black tracking-tight leading-[1.08] text-white">
              The Next Gen <br />
              <span className="bg-gradient-to-r from-amber-400 via-orange-400 to-red-400 bg-clip-text text-transparent">
                Trading Workspace.
              </span>
            </h2>
            <p className="text-slate-400 text-sm md:text-base max-w-lg mt-2 leading-relaxed">
              Experience dynamic virtual financial simulation with visual real-time pricing ticks, deep portfolio analysis, and custom-counseled AI automation support.
            </p>
          </motion.div>

          {/* Dynamic Mock Ticker Ribbons (Indices status) */}
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.2 }}
            className="grid grid-cols-2 md:grid-cols-4 gap-3 bg-white/[0.01] border border-white/[0.03] p-3 rounded-2xl backdrop-blur-sm"
          >
            {mockIndices.map((idx, i) => (
              <div key={i} className="flex flex-col gap-0.5 p-2 rounded-xl hover:bg-white/[0.02] transition-all">
                <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">{idx.name}</span>
                <span className="text-xs font-mono font-bold text-slate-200">{idx.value.toLocaleString()}</span>
                <span className="text-[10px] text-emerald-400 flex items-center gap-0.5 font-bold">
                  <ArrowUpRight size={10} /> +{idx.change}%
                </span>
              </div>
            ))}
          </motion.div>

          {/* Floating Stock Cards Showcase */}
          <div className="flex flex-col gap-3">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-widest">Active Stock Indexes</span>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {previewStocks.map((stock) => {
                const isUp = stock.changePercent >= 0;
                return (
                  <motion.div 
                    key={stock.ticker}
                    whileHover={{ y: -4 }}
                    className="p-3 bg-slate-950/60 border border-white/[0.04] rounded-xl backdrop-blur-md flex flex-col justify-between h-24 hover:border-cyan-500/30 transition-all cursor-pointer shadow-lg hover:shadow-cyan-900/10"
                  >
                    <div className="flex justify-between items-start">
                      <span className="text-xs font-bold text-white tracking-tight">{stock.ticker}</span>
                      <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${isUp ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'}`}>
                        {isUp ? "+" : ""}{stock.changePercent}%
                      </span>
                    </div>
                    <div>
                      <span className="text-xs text-slate-400 block truncate">{stock.name}</span>
                      <span className="text-sm font-mono font-bold text-white mt-0.5 block">${stock.price.toFixed(2)}</span>
                    </div>
                  </motion.div>
                );
              })}
            </div>
          </div>

          {/* Interactive Feature Rotating Preview */}
          <div className="relative h-20 overflow-hidden border-l-2 border-amber-500/40 pl-4 mt-2">
            <AnimatePresence mode="wait">
              <motion.div
                key={activeFeature}
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
                className="flex items-start gap-3 h-full"
              >
                {(() => {
                  const f = features[activeFeature];
                  const Icon = f.icon;
                  return (
                    <>
                      <div className="p-2 rounded-lg bg-amber-500/10 text-amber-400 shrink-0">
                        <Icon size={18} />
                      </div>
                      <div>
                        <h4 className="text-xs font-bold text-white uppercase tracking-wider">{f.title}</h4>
                        <p className="text-xs text-slate-400 mt-1 leading-relaxed max-w-md">{f.description}</p>
                      </div>
                    </>
                  );
                })()}
              </motion.div>
            </AnimatePresence>
          </div>
          
        </div>

        {/* Right Column: Premium Login Terminal */}
        <div className="lg:col-span-5 flex justify-center w-full">
          <motion.div 
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5, delay: 0.15 }}
            className="w-full max-w-[420px] p-8 rounded-3xl bg-[#111418]/80 border border-white/[0.06] backdrop-blur-2xl shadow-[0_24px_80px_rgba(0,0,0,0.8)] relative overflow-hidden group"
          >
            {/* Ambient card edge light effect */}
            <div className="absolute -inset-px bg-gradient-to-tr from-transparent via-[#FDA481]/10 to-transparent rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />

            <div className="relative z-10 flex flex-col gap-6">
              
              {/* Header */}
              <div className="flex flex-col gap-1.5">
                <h3 className="text-xl font-bold tracking-tight text-white">Enter the Terminal</h3>
                <p className="text-xs text-slate-400">Authenticating into StonX Portfolio Workspace</p>
              </div>

              {/* Login Form */}
              <form onSubmit={handleLogin} className="flex flex-col gap-4">
                <div className="flex flex-col gap-1.5">
                  <label className="text-[11px] font-bold text-slate-400 uppercase tracking-widest">Username</label>
                  <input
                    id="login-username"
                    type="text"
                    required
                    placeholder="e.g. java_demo"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[#0d1016]/60 border border-white/[0.08] text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-amber-500 focus:bg-[#0d1016] transition-all font-sans"
                  />
                </div>

                <div className="flex flex-col gap-1.5">
                  <label className="text-[11px] font-bold text-slate-400 uppercase tracking-widest">Password</label>
                  <input
                    id="login-password"
                    type="password"
                    required
                    placeholder="Enter security pin..."
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[#0d1016]/60 border border-white/[0.08] text-white text-sm placeholder:text-slate-600 focus:outline-none focus:border-amber-500 focus:bg-[#0d1016] transition-all font-sans"
                  />
                </div>

                {error && (
                  <motion.div 
                    initial={{ opacity: 0, y: -4 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-xs text-red-400 bg-red-950/20 border border-red-900/35 p-3 rounded-xl flex items-start gap-2"
                  >
                    <div className="w-1.5 h-1.5 rounded-full bg-red-400 shrink-0 mt-1.5" />
                    <span>{error}</span>
                  </motion.div>
                )}

                <button
                  id="login-submit-btn"
                  type="submit"
                  disabled={loading}
                  className="w-full py-3.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white font-bold text-sm tracking-wide shadow-lg shadow-amber-900/20 active:scale-[0.98] transition-all disabled:opacity-50 disabled:pointer-events-none flex items-center justify-center gap-2 cursor-pointer mt-2"
                >
                  {loading ? (
                    <span className="w-5 h-5 rounded-full border-2 border-white/30 border-t-white animate-spin" />
                  ) : (
                    <>
                      <span>Launch Workspace</span>
                      <ArrowRight size={14} />
                    </>
                  )}
                </button>
              </form>

              {/* Demo accounts picker block */}
              <div className="bg-white/[0.02] border border-white/[0.04] p-3 rounded-xl flex flex-col gap-2">
                <div className="flex items-center gap-1.5 text-slate-400 text-[11px] font-bold">
                  <UserCheck size={12} className="text-amber-400" />
                  <span>PRE-SET CRITICAL CREDENTIALS</span>
                </div>
                <div className="flex flex-col gap-1.5">
                  <button 
                    onClick={() => { setUsername("java_demo"); setPassword("1234"); }}
                    className="flex justify-between items-center text-xs py-1.5 px-2.5 rounded bg-slate-900/40 hover:bg-slate-900/80 border border-white/[0.04] text-slate-300 text-left transition-all"
                  >
                    <span>User: <strong className="text-white">java_demo</strong></span>
                    <span className="text-slate-500 font-mono text-[10px]">Pass: 1234</span>
                  </button>
                  <button 
                    onClick={() => { setUsername("demo"); setPassword("demo123"); }}
                    className="flex justify-between items-center text-xs py-1.5 px-2.5 rounded bg-slate-900/40 hover:bg-slate-900/80 border border-white/[0.04] text-slate-300 text-left transition-all"
                  >
                    <span>User: <strong className="text-white">demo</strong></span>
                    <span className="text-slate-500 font-mono text-[10px]">Pass: demo123</span>
                  </button>
                </div>
              </div>

              {/* Stat footer inside card */}
              <div className="grid grid-cols-3 gap-2 border-t border-white/[0.05] pt-4 mt-1 text-center">
                <div className="flex flex-col">
                  <span className="text-sm font-bold font-mono text-white">20+</span>
                  <span className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">Tickers</span>
                </div>
                <div className="flex flex-col border-x border-white/[0.05]">
                  <span className="text-sm font-bold font-mono text-amber-400">$10,000</span>
                  <span className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">Starting Wallet</span>
                </div>
                <div className="flex flex-col">
                  <span className="text-sm font-bold font-mono text-orange-400">Live AI</span>
                  <span className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">TradeBot</span>
                </div>
              </div>

            </div>
          </motion.div>
        </div>

      </main>

      {/* ── Footer ── */}
      <footer className="relative z-10 w-full py-6 text-center border-t border-white/[0.03] bg-black/10 mt-auto">
        <p className="text-slate-600 text-xs">
          &copy; {new Date().getFullYear()} StonX Financial Technologies Inc. All simulated transactions are strictly pedagogical.
        </p>
      </footer>

    </div>
  );
}
