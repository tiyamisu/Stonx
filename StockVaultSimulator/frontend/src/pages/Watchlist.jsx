// ============================================================
// Watchlist.jsx — Premium Watchlist Stock Grid
// Redesigned with individual interactive cards, live sparklines,
// 52-week slider visualizers, and quick action panels.
// ============================================================

import { useApp } from "../context/AppContext";
import BuyModal from "../components/BuyModal";
import { useState } from "react";
import { Star, Trash2, TrendingUp, TrendingDown, ShoppingCart, Search, AlertCircle, Plus } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { AreaChart, Area, ResponsiveContainer } from "recharts";
import { motion, AnimatePresence } from "framer-motion";

export default function Watchlist() {
  const { state, dispatch } = useApp();
  const { watchlist, stocks } = state;
  const navigate = useNavigate();
  const [buyModal, setBuyModal] = useState(null);
  const [addSearch, setAddSearch] = useState("");
  const [showAddPanel, setShowAddPanel] = useState(false);

  const watchedStocks = watchlist
    .map((ticker) => stocks.find((s) => s.ticker === ticker))
    .filter(Boolean);

  const availableToAdd = stocks.filter(
    (s) =>
      !watchlist.includes(s.ticker) &&
      (addSearch === "" || s.ticker.toLowerCase().includes(addSearch.toLowerCase()) || s.name.toLowerCase().includes(addSearch.toLowerCase()))
  );

  function removeFromWatchlist(ticker) {
    dispatch({ type: "REMOVE_WATCHLIST", payload: ticker });
  }

  function addToWatchlist(ticker) {
    dispatch({ type: "ADD_WATCHLIST", payload: ticker });
    setAddSearch("");
  }

  // Animation variants
  const gridVariants = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.05
      }
    }
  };

  const cardVariants = {
    hidden: { opacity: 0, scale: 0.96, y: 10 },
    show: { opacity: 1, scale: 1, y: 0, transition: { type: "spring", stiffness: 120 } }
  };

  return (
    <div className="flex flex-col gap-6">
      
      {/* ── Page Header ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 border-b border-white/[0.04] pb-5">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-white">Watchlist</h2>
          <p className="text-xs text-slate-400 mt-1">
            Track saved assets and monitor live market performance.
          </p>
        </div>
        <button 
          onClick={() => setShowAddPanel(!showAddPanel)}
          className="self-start px-4.5 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all cursor-pointer shadow-lg shadow-amber-900/10 flex items-center gap-1.5"
        >
          <Plus size={14} />
          <span>Add Stocks</span>
        </button>
      </div>

      {/* ── Search & Add Panel ── */}
      <AnimatePresence>
        {showAddPanel && (
          <motion.div 
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl"
          >
            <div className="flex items-center gap-2 mb-3 px-1 text-slate-400 text-xs font-bold uppercase tracking-widest">
              <Search size={14} className="text-amber-400" />
              <span>Add Tickers to Watchlist</span>
            </div>
            
            <input
              type="text"
              placeholder="Search ticker or company..."
              value={addSearch}
              onChange={(e) => setAddSearch(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl bg-slate-900/60 border border-white/[0.06] text-white text-xs placeholder:text-slate-650 focus:outline-none focus:border-amber-500 mb-3.5 transition-all"
            />

            <div className="flex flex-wrap gap-2 max-h-32 overflow-y-auto pr-1">
              {availableToAdd.slice(0, 12).map((s) => (
                <button 
                  key={s.ticker} 
                  onClick={() => addToWatchlist(s.ticker)}
                  className="px-3.5 py-2 rounded-xl bg-white/[0.01] hover:bg-white/[0.03] border border-white/[0.04] hover:border-amber-500/35 text-xs font-semibold text-slate-300 hover:text-white flex items-center gap-1.5 transition-all cursor-pointer"
                >
                  <Star size={11} className="text-slate-500 hover:text-yellow-400" />
                  <span className="font-bold text-white tracking-tight">{s.ticker}</span>
                  <span className="text-slate-500 text-[10px] truncate max-w-[80px]">{s.name}</span>
                </button>
              ))}
              {availableToAdd.length === 0 && (
                <div className="flex items-center gap-2 text-xs text-slate-500 py-2 px-1">
                  <AlertCircle size={13} />
                  <span>All available stocks are added!</span>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Watchlist Cards Grid ── */}
      {watchedStocks.length === 0 ? (
        <div className="p-8 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col items-center justify-center text-center py-20 animate-fade-in">
          <div className="p-4 rounded-3xl bg-white/[0.02] border border-white/[0.04] text-slate-500 mb-4 animate-float">
            <Star size={36} className="text-yellow-500 fill-yellow-500" />
          </div>
          <h3 className="text-lg font-black text-slate-200 uppercase tracking-wider">No Tickers Tracked</h3>
          <p className="text-xs text-slate-500 max-w-[280px] mt-2 leading-relaxed">
            Click the star icon in the stock directory to add items here.
          </p>
          <button 
            onClick={() => navigate("/market")}
            className="mt-6 px-5 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all inline-flex items-center gap-1.5 cursor-pointer shadow-lg shadow-amber-900/20"
          >
            <span>Browse Market Directory</span>
            <ArrowRight size={13} />
          </button>
        </div>
      ) : (
        <motion.div 
          variants={gridVariants}
          initial="hidden"
          animate="show"
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5"
        >
          {watchedStocks.map((stock) => {
            const isUp = stock.changePercent >= 0;
            const rangePct = Math.min(100, Math.max(0,
              ((stock.price - stock.low52) / (stock.high52 - stock.low52)) * 100
            )).toFixed(1);

            return (
              <motion.div 
                key={stock.ticker}
                variants={cardVariants}
                className={`p-5 rounded-3xl bg-slate-950/40 border backdrop-blur-md flex flex-col gap-4 relative overflow-hidden group shadow-lg hover:shadow-2xl transition-all duration-300 ${
                  isUp 
                    ? 'border-white/[0.04] hover:border-emerald-500/30' 
                    : 'border-white/[0.04] hover:border-red-500/30'
                }`}
              >
                {/* Background lighting glow on card hover */}
                <div className={`absolute -inset-px opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none rounded-3xl bg-gradient-to-tr ${
                  isUp ? 'from-transparent via-emerald-500/5 to-transparent' : 'from-transparent via-red-500/5 to-transparent'
                }`} />

                {/* Card Top Title Banner */}
                <div className="flex justify-between items-start relative z-10">
                  <div className="min-w-0">
                    <span className="text-base font-black text-white tracking-tight block">{stock.ticker}</span>
                    <span className="text-[11px] text-slate-500 font-semibold block truncate mt-0.5">{stock.name}</span>
                  </div>
                  <button 
                    onClick={() => removeFromWatchlist(stock.ticker)}
                    className="p-1 text-yellow-400 hover:text-slate-600 transition-colors cursor-pointer shrink-0"
                  >
                    <Star size={16} className="fill-yellow-400" />
                  </button>
                </div>

                {/* Live Sparkline Area Graph */}
                <div className="w-full h-12 relative z-10">
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={stock.history.slice(-20)} margin={{ top: 2, right: 0, left: 0, bottom: 0 }}>
                      <defs>
                        <linearGradient id={`wl-${stock.ticker}`} x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={isUp ? "#2dd4a0" : "#ef4444"} stopOpacity={0.2} />
                          <stop offset="100%" stopColor={isUp ? "#2dd4a0" : "#ef4444"} stopOpacity={0.01} />
                        </linearGradient>
                      </defs>
                      <Area 
                        type="monotone" 
                        dataKey="price" 
                        stroke={isUp ? "#2dd4a0" : "#ef4444"} 
                        strokeWidth={1.5} 
                        fill={`url(#wl-${stock.ticker})`} 
                        dot={false} 
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>

                {/* Value & returns display row */}
                <div className="flex justify-between items-center relative z-10 mt-1">
                  <div>
                    <span className="text-base font-mono font-black text-white">${stock.price.toFixed(2)}</span>
                    <span className={`text-[10px] font-bold inline-flex items-center gap-0.5 px-1.5 py-0.5 rounded mt-1.5 block w-max ${
                      isUp ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
                    }`}>
                      {isUp ? <TrendingUp size={10} /> : <TrendingDown size={10} />}
                      {isUp ? "+" : ""}{stock.changePercent.toFixed(2)}%
                    </span>
                  </div>

                  <div className="text-right flex flex-col text-[10px] text-slate-500 font-bold uppercase tracking-wider gap-0.5">
                    <span>Vol: {stock.volume}</span>
                    <span>P/E: {stock.pe}</span>
                  </div>
                </div>

                {/* 52-Week Progress Slider Slider visualizer */}
                <div className="flex flex-col gap-1.5 mt-1 relative z-10">
                  <div className="flex justify-between text-[9px] text-slate-500 font-bold">
                    <span>52W L: ${stock.low52}</span>
                    <span>52W H: ${stock.high52}</span>
                  </div>
                  <div className="relative h-1 bg-slate-900 rounded-full overflow-hidden">
                    <div 
                      className={`absolute top-0 bottom-0 left-0 rounded-full ${
                        isUp ? 'bg-emerald-500' : 'bg-red-500'
                      }`}
                      style={{ width: `${rangePct}%` }}
                    />
                  </div>
                </div>

                {/* Card execution triggers drawer */}
                <div className="flex gap-2 mt-2 relative z-10">
                  <button 
                    onClick={() => setBuyModal({ stock, mode: "BUY" })}
                    className="flex-1 py-2 rounded-xl bg-amber-500/15 border border-amber-500/25 hover:bg-amber-500/25 text-amber-400 text-xs font-bold transition-all inline-flex items-center justify-center gap-1 cursor-pointer"
                  >
                    <ShoppingCart size={11} />
                    <span>Buy</span>
                  </button>
                  <button 
                    onClick={() => setBuyModal({ stock, mode: "SELL" })}
                    className="flex-1 py-2 rounded-xl bg-slate-900 border border-white/[0.08] hover:bg-slate-800 text-slate-300 hover:text-white text-xs font-bold transition-all cursor-pointer"
                  >
                    <span>Sell</span>
                  </button>
                  <button 
                    onClick={() => removeFromWatchlist(stock.ticker)}
                    className="px-2.5 py-2 rounded-xl bg-[#FDA481]/5 border border-[#FDA481]/15 hover:bg-[#FDA481]/15 text-[#FDA481] transition-all cursor-pointer"
                    title="Remove Tracker"
                  >
                    <Trash2 size={12} />
                  </button>
                </div>

              </motion.div>
            );
          })}
        </motion.div>
      )}

      {/* Execution confirmation modal */}
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
