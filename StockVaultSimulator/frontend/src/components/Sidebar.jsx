// ============================================================
// Sidebar.jsx — Premium Dashboard Sidebar
// StockVault warm amber/orange palette
// ============================================================

import { useNavigate, useLocation } from "react-router-dom";
import { useApp } from "../context/AppContext";
import { formatCurrency } from "../utils/stockSimulator";
import {
  LayoutDashboard, TrendingUp, Briefcase, Receipt,
  Star, MessageSquareText, LogOut, Activity, ChevronLeft, ChevronRight
} from "lucide-react";
import { useState } from "react";

const NAV_ITEMS = [
  { label: "Dashboard",    path: "/dashboard",    icon: LayoutDashboard },
  { label: "Market",       path: "/market",       icon: TrendingUp      },
  { label: "Portfolio",    path: "/portfolio",    icon: Briefcase       },
  { label: "Transactions", path: "/transactions", icon: Receipt         },
  { label: "Watchlist",    path: "/watchlist",    icon: Star            },
  { label: "TradeBot AI",  path: "/tradebot",     icon: MessageSquareText },
];

export default function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { state, dispatch } = useApp();
  const { user, walletBalance, portfolio } = state;

  const [collapsed, setCollapsed] = useState(false);

  const initials = user?.username
    ? user.username.slice(0, 2).toUpperCase()
    : "DU";

  function handleLogout() {
    dispatch({ type: "LOGOUT" });
    navigate("/");
  }

  return (
    <aside 
      className={`relative z-20 flex flex-col justify-between border-r border-white/[0.04] bg-[#090c10]/90 backdrop-blur-xl h-screen transition-all duration-300 ${
        collapsed ? "w-20" : "w-64"
      }`}
    >
      {/* Collapse Trigger Button */}
      <button 
        onClick={() => setCollapsed(!collapsed)}
        className="absolute top-8 -right-3 w-6 h-6 rounded-full bg-[#111418] border border-white/[0.08] hover:border-amber-500/40 text-slate-400 hover:text-white flex items-center justify-center cursor-pointer transition-all shadow-md"
      >
        {collapsed ? <ChevronRight size={12} /> : <ChevronLeft size={12} />}
      </button>

      {/* Top Brand Banner */}
      <div className="flex flex-col gap-6 p-6">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-[#FDA481] to-[#E8613C] flex items-center justify-center shrink-0 shadow-lg shadow-amber-500/10">
            <TrendingUp size={18} className="text-white" />
          </div>
          {!collapsed && (
            <div>
              <span className="text-sm font-black tracking-tight text-white block">StonX</span>
              <span className="text-[10px] text-amber-400 font-bold uppercase tracking-wider block leading-none">Simulator Terminal</span>
            </div>
          )}
        </div>

        {/* Live Status indicator */}
        <div className={`flex items-center gap-2.5 px-3 py-2 rounded-xl bg-white/[0.01] border border-white/[0.03] ${collapsed ? 'justify-center' : ''}`}>
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
          {!collapsed && (
            <span className="text-[11px] font-bold text-emerald-400 tracking-wide uppercase">Simulation Live</span>
          )}
        </div>
      </div>

      {/* Navigation List */}
      <nav className="flex-1 px-4 py-2 flex flex-col gap-1.5 overflow-y-auto">
        {!collapsed && (
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest px-3 mb-2 block">Navigation</span>
        )}
        {NAV_ITEMS.map(({ label, path, icon: Icon }) => {
          const isActive = location.pathname === path;
          return (
            <button
              key={path}
              onClick={() => navigate(path)}
              className={`w-full flex items-center gap-3 px-3 py-3 rounded-xl text-sm font-semibold transition-all group relative cursor-pointer ${
                isActive 
                  ? "bg-amber-500/10 text-amber-400 border border-amber-500/15" 
                  : "text-slate-400 hover:text-slate-200 hover:bg-white/[0.02] border border-transparent"
              } ${collapsed ? "justify-center" : ""}`}
            >
              <Icon size={18} className={isActive ? "text-amber-400" : "text-slate-400 group-hover:text-slate-200"} />
              
              {!collapsed && (
                <span className="flex-1 text-left">{label}</span>
              )}

              {/* Ticker counts indicator badges */}
              {!collapsed && path === "/watchlist" && state.watchlist.length > 0 && (
                <span className="px-1.5 py-0.5 rounded-md text-[10px] font-bold bg-white/5 border border-white/10 text-slate-300">
                  {state.watchlist.length}
                </span>
              )}
              {!collapsed && path === "/portfolio" && portfolio.length > 0 && (
                <span className="px-1.5 py-0.5 rounded-md text-[10px] font-bold bg-amber-500/15 border border-amber-500/25 text-amber-400">
                  {portfolio.length}
                </span>
              )}

              {/* Hover indicator tooltip (only for collapsed mode) */}
              {collapsed && (
                <div className="absolute left-20 bg-[#111418] border border-white/[0.08] text-white text-xs px-2.5 py-1.5 rounded-md opacity-0 group-hover:opacity-100 pointer-events-none transition-all shadow-xl font-medium tracking-wide translate-x-2 group-hover:translate-x-0">
                  {label}
                </div>
              )}
            </button>
          );
        })}
      </nav>

      {/* Footer / User Profile section */}
      <div className="p-4 border-t border-white/[0.04] bg-black/10 flex flex-col gap-4">
        <div className={`flex items-center gap-3 ${collapsed ? "justify-center" : ""}`}>
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-500 flex items-center justify-center text-white font-bold text-sm shadow-md shrink-0">
            {initials}
          </div>
          {!collapsed && (
            <div className="flex-1 min-w-0">
              <span className="text-xs font-bold text-white block truncate uppercase tracking-wide">{user?.username || "Demo User"}</span>
              <span className="text-xs font-mono font-bold text-amber-400 block mt-0.5">{formatCurrency(walletBalance)}</span>
            </div>
          )}
        </div>

        <button 
          onClick={handleLogout}
          className={`w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-xs font-bold text-red-400 hover:bg-red-500/10 transition-all cursor-pointer ${
            collapsed ? "justify-center" : ""
          }`}
        >
          <LogOut size={14} />
          {!collapsed && <span>Sign Out</span>}
        </button>
      </div>
    </aside>
  );
}
