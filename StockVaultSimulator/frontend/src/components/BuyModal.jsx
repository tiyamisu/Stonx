// ============================================================
// BuyModal.jsx — Premium Order Execution Modal
// Redesigned with custom transaction calculators, percentage allocation chips,
// warning details, and animated checkout components.
// ============================================================

import { useState } from "react";
import { useApp } from "../context/AppContext";
import { formatCurrency } from "../utils/stockSimulator";
import { X, TrendingUp, TrendingDown, AlertCircle, CheckCircle2, ShoppingCart, Banknote } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

/**
 * @param {Object} stock  - The stock being traded
 * @param {string} mode   - "BUY" or "SELL"
 * @param {Function} onClose - Close the modal
 */
export default function BuyModal({ stock, mode, onClose }) {
  const { state, dispatch } = useApp();
  const [quantity, setQuantity] = useState(1);
  const [success, setSuccess] = useState(false);

  if (!stock) return null;

  const holding = state.portfolio.find((p) => p.ticker === stock.ticker);
  const sharesOwned = holding?.shares || 0;

  const totalCost = quantity * stock.price;
  const canAffordBuy = totalCost <= state.walletBalance;
  const canSell = mode === "SELL" && quantity <= sharesOwned;
  const canExecute = mode === "BUY" ? canAffordBuy : canSell;

  function handleConfirm() {
    if (!canExecute) return;
    dispatch({
      type: mode === "BUY" ? "BUY_STOCK" : "SELL_STOCK",
      payload: { ticker: stock.ticker, shares: quantity, price: stock.price },
    });
    setSuccess(true);
    setTimeout(() => {
      setSuccess(false);
      onClose();
    }, 1200);
  }

  const isUp = stock.changePercent >= 0;

  // Percentage allocation helper
  function applyPercentage(pct) {
    if (mode === "BUY") {
      const maxAffordable = Math.floor(state.walletBalance / stock.price);
      const target = Math.floor(maxAffordable * pct);
      setQuantity(Math.max(1, target));
    } else {
      const target = Math.floor(sharesOwned * pct);
      setQuantity(Math.max(1, target));
    }
  }

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-md animate-fade-in"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <motion.div 
        initial={{ opacity: 0, scale: 0.95, y: 15 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 15 }}
        className="w-full max-w-[420px] p-6 rounded-3xl bg-slate-950/80 border border-white/[0.08] backdrop-blur-2xl shadow-[0_24px_80px_rgba(0,0,0,0.8)] relative overflow-hidden"
      >
        
        {/* Header */}
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-lg font-black text-white flex items-center gap-2">
              <span>{mode === "BUY" ? "Buy Order" : "Sell Order"}</span>
              <span className="px-2 py-0.5 rounded-md text-[10px] font-black uppercase bg-white/5 border border-white/10 text-slate-300">
                {stock.ticker}
              </span>
            </h3>
            <p className="text-[11px] text-slate-500 font-bold uppercase tracking-wider mt-1">{stock.name}</p>
          </div>
          <button 
            onClick={onClose}
            className="p-1.5 rounded-lg bg-white/[0.02] border border-white/[0.06] hover:border-amber-500/35 hover:bg-amber-500/5 text-slate-400 hover:text-white cursor-pointer transition-all"
          >
            <X size={15} />
          </button>
        </div>

        {/* Dynamic Success Checkbox Display */}
        {success ? (
          <motion.div 
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="flex flex-col items-center justify-center gap-3.5 py-12 text-center text-emerald-400 bg-emerald-500/[0.02] border border-emerald-500/15 p-6 rounded-2xl"
          >
            <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 flex items-center justify-center shadow-lg shadow-emerald-950/20">
              <CheckCircle2 size={24} />
            </div>
            <div>
              <span className="text-sm font-black uppercase tracking-wider block">Transaction Confirmed</span>
              <span className="text-xs text-slate-500 font-bold mt-1 block">
                {mode === "BUY" ? "Added position to" : "Settled shares in"} portfolio.
              </span>
            </div>
          </motion.div>
        ) : (
          <div className="flex flex-col gap-4">
            
            {/* Live Pricing Summary Block */}
            <div className="p-4 rounded-2xl bg-slate-900/40 border border-white/[0.04]">
              <div className="flex justify-between items-center">
                <div>
                  <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest block">Market Price</span>
                  <span className="text-xl font-mono font-black text-white mt-1 block">${stock.price.toFixed(2)}</span>
                  <span className={`text-[10px] mt-1.5 font-semibold flex items-center gap-0.5 ${
                    isUp ? 'text-emerald-400' : 'text-red-400'
                  }`}>
                    {isUp ? <TrendingUp size={11} /> : <TrendingDown size={11} />}
                    {isUp ? "+" : ""}{stock.changePercent}% today
                  </span>
                </div>

                <div className="text-right">
                  <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest block">
                    {mode === "BUY" ? "Cash Capital" : "Owned Holdings"}
                  </span>
                  <span className="text-sm font-mono font-black mt-2 block text-amber-400">
                    {mode === "BUY" ? formatCurrency(state.walletBalance) : `${sharesOwned} shares`}
                  </span>
                </div>
              </div>
            </div>

            {/* Quantity Input controls */}
            <div className="flex flex-col gap-2">
              <label className="text-[10px] text-slate-500 font-bold uppercase tracking-widest">Select Shares Quantity</label>
              <div className="flex items-center gap-3">
                <button
                  onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  className="w-12 h-11 rounded-xl bg-[#0d1016] border border-white/[0.06] hover:border-amber-500/35 hover:bg-amber-500/5 text-slate-300 font-bold flex items-center justify-center cursor-pointer transition-all active:scale-95"
                >
                  −
                </button>
                <input
                  type="number"
                  min={1}
                  max={mode === "SELL" ? sharesOwned : 9999}
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                  className="flex-1 h-11 text-center font-mono font-bold text-white bg-slate-900/60 border border-white/[0.08] focus:border-amber-500 focus:outline-none rounded-xl"
                />
                <button
                  onClick={() => setQuantity((q) => mode === "SELL" ? Math.min(sharesOwned, q + 1) : q + 1)}
                  className="w-12 h-11 rounded-xl bg-[#0d1016] border border-white/[0.06] hover:border-amber-500/35 hover:bg-amber-500/5 text-slate-300 font-bold flex items-center justify-center cursor-pointer transition-all active:scale-95"
                >
                  +
                </button>
              </div>

              {/* Percentage allocation buttons */}
              <div className="grid grid-cols-4 gap-2 mt-1">
                {[0.1, 0.25, 0.5, 1.0].map((pct) => {
                  const label = pct === 1.0 ? "MAX" : `${pct * 100}%`;
                  return (
                    <button
                      key={pct}
                      onClick={() => applyPercentage(pct)}
                      className="py-1.5 rounded-lg bg-white/[0.01] hover:bg-white/[0.03] border border-white/[0.04] text-[10px] font-bold text-slate-400 hover:text-white cursor-pointer transition-all"
                    >
                      {label}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Calculated Order Summary Card */}
            <div className="p-4 rounded-2xl bg-white/[0.01] border border-white/[0.03] flex flex-col gap-2">
              <div className="flex justify-between items-center text-xs">
                <span className="text-slate-500 font-medium">Market Execution Price</span>
                <span className="font-mono text-slate-300">${stock.price.toFixed(2)}</span>
              </div>
              <div className="flex justify-between items-center text-xs">
                <span className="text-slate-500 font-medium">Quantity shares</span>
                <span className="font-mono text-slate-300">{quantity}</span>
              </div>
              <div className="h-px bg-white/[0.04] my-1" />
              <div className="flex justify-between items-center text-sm font-black">
                <span className="text-slate-300">{mode === "BUY" ? "Estimated Cost" : "Estimated Value"}</span>
                <span className={mode === "BUY" ? "text-red-400 font-mono" : "text-emerald-400 font-mono"}>
                  {mode === "BUY" ? "−" : "+"}{formatCurrency(totalCost)}
                </span>
              </div>
            </div>

            {/* Error alerts */}
            {!canExecute && (
              <div className="p-3.5 rounded-2xl bg-red-950/20 border border-red-900/30 text-red-400 text-xs flex items-start gap-2">
                <AlertCircle size={15} className="shrink-0 mt-0.5" />
                <span>
                  {mode === "BUY" 
                    ? "Insufficient wallet funds. Choose a lower quantity or add cash." 
                    : "Insufficient holdings owned. Choose a smaller quantity."
                  }
                </span>
              </div>
            )}

            {/* Execution Buttons */}
            <button
              onClick={handleConfirm}
              disabled={!canExecute}
              className={`w-full py-3.5 rounded-xl text-white font-bold text-sm tracking-wide shadow-lg active:scale-[0.98] transition-all disabled:opacity-40 disabled:pointer-events-none flex items-center justify-center gap-2 cursor-pointer mt-2 ${
                mode === "BUY"
                  ? "bg-gradient-to-r from-red-500 to-rose-600 hover:from-red-400 hover:to-rose-500 shadow-red-950/25"
                  : "bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 shadow-emerald-950/25"
              }`}
            >
              {mode === "BUY" ? (
                <>
                  <ShoppingCart size={15} />
                  <span>Execute Buy Purchase</span>
                </>
              ) : (
                <>
                  <Banknote size={15} />
                  <span>Execute Sell Settlement</span>
                </>
              )}
            </button>

          </div>
        )}
      </motion.div>
    </div>
  );
}
