// ============================================================
// Ticker.jsx — Continuous Market Ticker Bar
// Designed with a premium horizontal layout, marquee scrolling,
// and glowing green/red indicators.
// ============================================================

import { useApp } from "../context/AppContext";
import { ArrowUpRight, ArrowDownRight } from "lucide-react";

export default function Ticker() {
  const { state } = useApp();
  const { stocks } = state;

  // Double the array for infinite horizontal scroll effect
  const doubledStocks = [...stocks, ...stocks];

  return (
    <div className="relative z-10 w-full h-11 bg-[#090c15]/60 border-b border-white/[0.04] backdrop-blur-md overflow-hidden flex items-center shrink-0">
      
      {/* "Live Ticker" Indicator label pinned to left */}
      <div className="absolute left-0 top-0 bottom-0 px-3.5 bg-slate-950 border-r border-white/[0.05] z-20 flex items-center gap-1.5 shadow-md">
        <span className="relative flex h-1.5 w-1.5">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-emerald-500"></span>
        </span>
        <span className="text-[10px] font-black uppercase tracking-wider text-slate-400">Live Prices</span>
      </div>

      {/* Marquee Track */}
      <div className="flex whitespace-nowrap pl-24 select-none animate-marquee py-1.5">
        {doubledStocks.map((stock, idx) => {
          const isUp = stock.changePercent >= 0;
          return (
            <div 
              key={`${stock.ticker}-${idx}`} 
              className="inline-flex items-center gap-2 px-5 border-r border-white/[0.03] last:border-r-0 cursor-pointer hover:bg-white/[0.02] transition-colors h-full"
            >
              <span className="text-xs font-black text-white tracking-tight">{stock.ticker}</span>
              <span className="text-xs font-mono font-bold text-slate-300">${stock.price.toFixed(2)}</span>
              <span 
                className={`text-[10px] font-bold inline-flex items-center gap-0.5 px-1 py-0.5 rounded ${
                  isUp 
                    ? "bg-emerald-500/10 text-emerald-400" 
                    : "bg-red-500/10 text-red-400"
                }`}
              >
                {isUp ? <ArrowUpRight size={10} /> : <ArrowDownRight size={10} />}
                {Math.abs(stock.changePercent).toFixed(2)}%
              </span>
            </div>
          );
        })}
      </div>

      {/* Fade indicators on right to smooth marquee end */}
      <div className="absolute right-0 top-0 bottom-0 w-12 bg-gradient-to-l from-[#090c15] to-transparent pointer-events-none z-10" />
    </div>
  );
}
