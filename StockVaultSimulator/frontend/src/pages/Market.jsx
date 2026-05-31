// ============================================================
// Market.jsx — Premium Stock Market Browser
// Redesigned with custom filters, interactive search rows,
// expandable analytical worksheets, and smooth transitions.
// ============================================================

import { useState } from "react";
import { useApp } from "../context/AppContext";
import { useNavigate } from "react-router-dom";
import { formatCurrency } from "../utils/stockSimulator";
import BuyModal from "../components/BuyModal";
import {
  AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer
} from "recharts";
import { 
  Search, TrendingUp, TrendingDown, Star, X, ShoppingCart, 
  ChevronDown, ChevronUp, BarChart2, ShieldAlert
} from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

const SECTORS = ["All", "Technology", "Finance", "Energy", "Healthcare", "Consumer"];

const SECTOR_STYLES = {
  Technology:  "bg-amber-500/10 text-amber-400 border-amber-500/20",
  Finance:     "bg-orange-500/10 text-orange-400 border-orange-500/20",
  Energy:      "bg-yellow-500/10 text-yellow-400 border-yellow-500/20",
  Healthcare:  "bg-emerald-500/10 text-emerald-400 border-emerald-500/20",
  Consumer:    "bg-violet-500/10 text-violet-400 border-violet-500/20",
};

export default function Market() {
  const { state, dispatch } = useApp();
  const { stocks, watchlist } = state;
  const navigate = useNavigate();

  const [search, setSearch] = useState("");
  const [sector, setSector] = useState("All");
  const [selectedStock, setSelected] = useState(null);
  const [buyModal, setBuyModal] = useState(null);
  const [sort, setSort] = useState("ticker");

  const filtered = stocks
    .filter((s) => {
      const q = search.toLowerCase();
      return (
        (sector === "All" || s.sector === sector) &&
        (s.ticker.toLowerCase().includes(q) || s.name.toLowerCase().includes(q))
      );
    })
    .sort((a, b) => {
      if (sort === "price") return b.price - a.price;
      if (sort === "change") return b.changePercent - a.changePercent;
      return a.ticker.localeCompare(b.ticker);
    });

  function toggleWatchlist(ticker) {
    dispatch({ 
      type: watchlist.includes(ticker) ? "REMOVE_WATCHLIST" : "ADD_WATCHLIST", 
      payload: ticker 
    });
  }

  return (
    <div className="flex flex-col gap-6">
      
      {/* ── Page Header ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 border-b border-white/[0.04] pb-5">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-white">Stock Directory</h2>
          <p className="text-xs text-slate-400 mt-1">
            Browse and search listed assets. Live prices tick every 3s.
          </p>
        </div>
        <button 
          onClick={() => navigate("/watchlist")}
          className="self-start px-4 py-2 rounded-xl bg-white/[0.02] hover:bg-white/[0.05] border border-white/[0.06] text-xs font-bold text-slate-300 inline-flex items-center gap-2 cursor-pointer transition-all"
        >
          <Star size={13} className="text-yellow-400 fill-yellow-400" />
          <span>Watchlist ({watchlist.length})</span>
        </button>
      </div>

      {/* ── Search and Sorting Controls ── */}
      <div className="flex flex-col md:flex-row gap-4 items-center justify-between">
        {/* Search bar */}
        <div className="relative w-full md:max-w-md">
          <Search size={16} className="absolute left-4 top-3.5 text-slate-500" />
          <input
            id="stock-search-input"
            type="text"
            placeholder="Search ticker, company, or sector..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-slate-950/40 border border-white/[0.05] focus:border-amber-500 text-white text-sm focus:outline-none placeholder:text-slate-600 transition-all"
          />
        </div>

        {/* Sort pills */}
        <div className="flex items-center gap-2 shrink-0 self-start md:self-auto bg-slate-950/40 border border-white/[0.04] p-1 rounded-2xl">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest px-3">Sort:</span>
          {["ticker", "price", "change"].map((option) => (
            <button
              key={option}
              onClick={() => setSort(option)}
              className={`px-4 py-2 rounded-xl text-xs font-bold uppercase tracking-wide cursor-pointer transition-all ${
                sort === option
                  ? "bg-amber-500/10 text-amber-400 border border-amber-500/20"
                  : "text-slate-400 hover:text-slate-200 hover:bg-white/[0.01]"
              }`}
            >
              {option}
            </button>
          ))}
        </div>
      </div>

      {/* ── Sector Tabs ── */}
      <div className="flex flex-wrap items-center gap-2">
        {SECTORS.map((s) => (
          <button
            key={s}
            onClick={() => setSector(s)}
            className={`px-4.5 py-2.5 rounded-full text-xs font-bold transition-all cursor-pointer border ${
              sector === s
                ? "bg-amber-500/10 text-amber-400 border-amber-500/20 shadow-md shadow-amber-900/5"
                : "bg-transparent border-white/[0.05] text-slate-400 hover:text-slate-200 hover:bg-white/[0.01]"
            }`}
          >
            {s}
          </button>
        ))}
        <span className="text-[11px] text-slate-500 font-bold uppercase tracking-widest ml-auto shrink-0 px-1 py-2">
          {filtered.length} listed asset{filtered.length !== 1 ? "s" : ""} found
        </span>
      </div>

      {/* ── Stock Listing Table ── */}
      <div className="p-4 bg-slate-950/40 border border-white/[0.04] rounded-3xl overflow-hidden shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/[0.04] text-[10px] text-slate-500 font-bold uppercase tracking-wider">
                <th className="py-4 px-4">Symbol</th>
                <th className="py-4 px-4">Price</th>
                <th className="py-4 px-4">Change</th>
                <th className="py-4 px-4 hidden md:table-cell">Volume</th>
                <th className="py-4 px-4 hidden md:table-cell">Mkt Cap</th>
                <th className="py-4 px-4 hidden lg:table-cell">P/E</th>
                <th className="py-4 px-4">Sector</th>
                <th className="py-4 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((stock) => {
                const isUp = stock.changePercent >= 0;
                const inWatch = watchlist.includes(stock.ticker);
                const isSelected = selectedStock?.ticker === stock.ticker;

                return (
                  <React.Fragment key={stock.ticker}>
                    {/* Row Item */}
                    <tr 
                      onClick={() => setSelected(isSelected ? null : stock)}
                      className={`border-b border-white/[0.02] last:border-b-0 hover:bg-white/[0.02] cursor-pointer transition-all duration-150 ${
                        isSelected ? "bg-amber-500/[0.03] border-b-amber-500/10" : ""
                      }`}
                    >
                      <td className="py-4 px-4">
                        <span className="text-sm font-black text-white tracking-tight block">{stock.ticker}</span>
                        <span className="text-[11px] text-slate-500 font-semibold max-w-[130px] block truncate mt-0.5">{stock.name}</span>
                      </td>
                      
                      <td className="py-4 px-4 font-mono font-bold text-sm text-slate-200">
                        ${stock.price.toFixed(2)}
                      </td>
                      
                      <td className="py-4 px-4">
                        <span className={`text-xs font-bold inline-flex items-center gap-0.5 px-2 py-1 rounded ${
                          isUp ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
                        }`}>
                          {isUp ? <TrendingUp size={11} /> : <TrendingDown size={11} />}
                          {isUp ? "+" : ""}{stock.changePercent.toFixed(2)}%
                        </span>
                      </td>

                      <td className="py-4 px-4 text-xs font-semibold text-slate-400 hidden md:table-cell">
                        {stock.volume}
                      </td>

                      <td className="py-4 px-4 text-xs font-semibold text-slate-400 hidden md:table-cell">
                        {stock.marketCap}
                      </td>

                      <td className="py-4 px-4 font-mono text-xs text-slate-400 hidden lg:table-cell">
                        {stock.pe}
                      </td>

                      <td className="py-4 px-4">
                        <span className={`px-2.5 py-1 rounded-md text-[10px] font-bold border ${
                          SECTOR_STYLES[stock.sector] || "bg-slate-500/10 text-slate-400 border-slate-500/20"
                        }`}>
                          {stock.sector}
                        </span>
                      </td>

                      <td className="py-4 px-4 text-right" onClick={(e) => e.stopPropagation()}>
                        <div className="inline-flex items-center gap-2">
                          <button
                            onClick={() => toggleWatchlist(stock.ticker)}
                            className={`p-2 rounded-xl border transition-all cursor-pointer ${
                              inWatch
                                ? "bg-yellow-500/10 border-yellow-500/25 text-yellow-400"
                                : "bg-transparent border-white/[0.06] text-slate-500 hover:text-slate-300"
                            }`}
                          >
                            <Star size={13} fill={inWatch ? "currentColor" : "none"} />
                          </button>
                          <button
                            id={`buy-btn-${stock.ticker}`}
                            onClick={() => setBuyModal({ stock, mode: "BUY" })}
                            className="px-3.5 py-2 rounded-xl bg-amber-500/15 border border-amber-500/25 text-amber-400 hover:bg-amber-500/25 text-xs font-bold inline-flex items-center gap-1.5 transition-all cursor-pointer"
                          >
                            <ShoppingCart size={11} />
                            <span>Buy</span>
                          </button>
                        </div>
                      </td>
                    </tr>

                    {/* Expandable Chart Worksheet */}
                    <AnimatePresence>
                      {isSelected && (
                        <tr key={`${stock.ticker}-worksheet`}>
                          <td colSpan={8} className="bg-slate-950/20 border-b border-white/[0.03] p-0">
                            <motion.div
                              initial={{ opacity: 0, height: 0 }}
                              animate={{ opacity: 1, height: "auto" }}
                              exit={{ opacity: 0, height: 0 }}
                              transition={{ duration: 0.25 }}
                              className="overflow-hidden"
                            >
                              <div className="p-6 grid grid-cols-1 lg:grid-cols-12 gap-6 border-l-2 border-amber-500/40">
                                
                                {/* Info Worksheet Column */}
                                <div className="lg:col-span-4 flex flex-col justify-between gap-4">
                                  <div>
                                    <h4 className="text-sm font-black text-white">{stock.name} Detail Summary</h4>
                                    <p className="text-xs text-slate-400 mt-2 leading-relaxed">{stock.description}</p>
                                  </div>
                                  
                                  {/* Stats Grid */}
                                  <div className="grid grid-cols-2 gap-3.5">
                                    <div className="p-2.5 rounded-xl bg-white/[0.01] border border-white/[0.03]">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">52-Week High</span>
                                      <span className="text-xs font-mono font-bold text-emerald-400 block mt-1">${stock.high52}</span>
                                    </div>
                                    <div className="p-2.5 rounded-xl bg-white/[0.01] border border-white/[0.03]">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">52-Week Low</span>
                                      <span className="text-xs font-mono font-bold text-red-400 block mt-1">${stock.low52}</span>
                                    </div>
                                    <div className="p-2.5 rounded-xl bg-white/[0.01] border border-white/[0.03]">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">P/E Ratio</span>
                                      <span className="text-xs font-mono font-bold text-slate-300 block mt-1">{stock.pe}</span>
                                    </div>
                                    <div className="p-2.5 rounded-xl bg-white/[0.01] border border-white/[0.03]">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Volatility Risk</span>
                                      <span className={`text-xs font-bold uppercase mt-1 block tracking-wider ${
                                        stock.volatility === "extreme" ? "text-red-400" : stock.volatility === "high" ? "text-yellow-400" : "text-emerald-400"
                                      }`}>{stock.volatility}</span>
                                    </div>
                                  </div>

                                  {/* Action links */}
                                  <div className="flex gap-2 pt-2">
                                    <button 
                                      onClick={() => setBuyModal({ stock, mode: "BUY" })}
                                      className="flex-1 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer shadow-md shadow-amber-950/15"
                                    >
                                      <ShoppingCart size={12} />
                                      <span>Buy Shares</span>
                                    </button>
                                    <button 
                                      onClick={() => setBuyModal({ stock, mode: "SELL" })}
                                      className="flex-1 py-2.5 rounded-xl bg-slate-900 border border-white/[0.08] hover:bg-slate-800 text-slate-300 hover:text-white text-xs font-bold transition-all cursor-pointer"
                                    >
                                      <span>Sell Holdings</span>
                                    </button>
                                    <button 
                                      onClick={() => setSelected(null)}
                                      className="px-3 rounded-xl bg-[#FDA481]/5 border border-[#FDA481]/15 hover:bg-[#FDA481]/15 text-[#FDA481] transition-all cursor-pointer"
                                    >
                                      <X size={12} />
                                    </button>
                                  </div>
                                </div>

                                {/* Expanded Area Chart Column */}
                                <div className="lg:col-span-8 flex flex-col justify-between">
                                  <div className="flex justify-between items-center mb-4">
                                    <div className="flex items-center gap-1.5 text-xs text-slate-500 font-bold uppercase tracking-wider">
                                      <BarChart2 size={13} className="text-amber-400" />
                                      <span>Simulated Historical Asset Trend</span>
                                    </div>
                                    <span className="text-[10px] text-slate-500 font-bold">30D price log updates in real-time</span>
                                  </div>

                                  <div className="w-full h-48 bg-slate-950/30 border border-white/[0.02] p-2 rounded-2xl">
                                    <ResponsiveContainer width="100%" height="100%">
                                      <AreaChart data={stock.history} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
                                        <defs>
                                          <linearGradient id={`grad-${stock.ticker}`} x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="0%" stopColor={isUp ? "#2dd4a0" : "#ef4444"} stopOpacity={0.2} />
                                            <stop offset="100%" stopColor={isUp ? "#2dd4a0" : "#ef4444"} stopOpacity={0.01} />
                                          </linearGradient>
                                        </defs>
                                        <XAxis 
                                          dataKey="date" 
                                          tick={{ fill: "rgba(255,255,255,0.25)", fontSize: 9 }} 
                                          axisLine={false} 
                                          tickLine={false} 
                                        />
                                        <YAxis 
                                          domain={["auto", "auto"]} 
                                          tick={{ fill: "rgba(255,255,255,0.25)", fontSize: 9 }} 
                                          axisLine={false} 
                                          tickLine={false} 
                                        />
                                        <Tooltip 
                                          formatter={(v) => [`$${v.toFixed(2)}`, "Price"]}
                                          contentStyle={{ background: "#07090d", border: "1px solid rgba(255,255,255,0.08)", borderRadius: "10px", fontSize: 11, color: '#fff' }}
                                        />
                                        <Area 
                                          type="monotone" 
                                          dataKey="price" 
                                          stroke={isUp ? "#2dd4a0" : "#ef4444"} 
                                          strokeWidth={1.8} 
                                          fill={`url(#grad-${stock.ticker})`} 
                                          dot={false} 
                                        />
                                      </AreaChart>
                                    </ResponsiveContainer>
                                  </div>
                                </div>

                              </div>
                            </motion.div>
                          </td>
                        </tr>
                      )}
                    </AnimatePresence>
                  </React.Fragment>
                );
              })}
            </tbody>
          </table>
        </div>

        {filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center gap-3 py-16 text-center text-slate-500">
            <div className="p-4 rounded-3xl bg-white/[0.01] border border-white/[0.04]">
              <ShieldAlert size={32} className="text-slate-600" />
            </div>
            <div>
              <span className="text-sm font-black text-slate-300 block uppercase tracking-wider">No matching assets found</span>
              <span className="text-xs text-slate-600 max-w-[240px] block mt-1.5 leading-relaxed">Modify your search query or reset the sector filter tabs.</span>
            </div>
          </div>
        )}
      </div>

      {/* Trade Modal popup */}
      {buyModal && (
        <BuyModal 
          stock={buyModal.stock} 
          mode={buyModal.mode} 
          onClose={() => setBuyModal(null)} 
        />
      )}
    </div>
  );
}
import React from 'react';
