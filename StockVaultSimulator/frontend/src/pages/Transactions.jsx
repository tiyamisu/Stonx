// ============================================================
// Transactions.jsx — Premium Transaction History Log
// Redesigned with custom filter tabs, valuation trackers,
// and hoverable transaction details logs.
// ============================================================

import { useState } from "react";
import { useApp } from "../context/AppContext";
import { formatCurrency } from "../utils/stockSimulator";
import { 
  ArrowUpCircle, ArrowDownCircle, ClipboardList, TrendingUp, 
  TrendingDown, Filter, Calendar, ShieldCheck, DollarSign
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

export default function Transactions() {
  const { state } = useApp();
  const { transactions, stocks } = state;
  const navigate = useNavigate();
  const [filter, setFilter] = useState("ALL");

  const filtered = transactions.filter((t) => filter === "ALL" || t.type === filter);

  const totalBuys = transactions.filter((t) => t.type === "BUY").reduce((s, t) => s + t.total, 0);
  const totalSells = transactions.filter((t) => t.type === "SELL").reduce((s, t) => s + t.total, 0);

  function formatDate(iso) {
    const d = new Date(iso);
    return d.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" }) +
      " · " + d.toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" });
  }

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.05
      }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 12 },
    show: { opacity: 1, y: 0, transition: { type: "spring", stiffness: 120 } }
  };

  return (
    <motion.div 
      variants={containerVariants}
      initial="hidden"
      animate="show"
      className="flex flex-col gap-6"
    >
      
      {/* ── Page Header ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 border-b border-white/[0.04] pb-5 shrink-0">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-white">Execution Logs</h2>
          <p className="text-xs text-slate-400 mt-1">Review historic trade records and capital allocations.</p>
        </div>
        <button 
          onClick={() => navigate("/market")}
          className="self-start px-4.5 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all cursor-pointer shadow-lg shadow-amber-900/10"
        >
          + Execute Trade
        </button>
      </div>

      {/* ── Metric valuation stats cards ── */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <motion.div variants={itemVariants} className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Execution Frequency</span>
          <span className="text-xl font-mono font-black text-white mt-1.5 block">{transactions.length} Trades</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">
            {transactions.filter((t) => t.type === "BUY").length} buys · {transactions.filter((t) => t.type === "SELL").length} sells
          </span>
        </motion.div>

        <motion.div variants={itemVariants} className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Capital Invested</span>
          <span className="text-xl font-mono font-black text-red-400 mt-1.5 block">{formatCurrency(totalBuys)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">Cash deployed to buy</span>
        </motion.div>

        <motion.div variants={itemVariants} className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Settlement Proceeds</span>
          <span className="text-xl font-mono font-black text-emerald-400 mt-1.5 block">{formatCurrency(totalSells)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">Cash retrieved from sells</span>
        </motion.div>
      </div>

      {/* ── Filter Selection Controls ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 bg-slate-950/20 p-2.5 rounded-2xl border border-white/[0.03]">
        <div className="flex items-center gap-2 flex-wrap">
          <div className="flex items-center gap-1.5 text-[10px] text-slate-500 font-bold uppercase tracking-wider px-2">
            <Filter size={12} className="text-amber-400" />
            <span>Filter List:</span>
          </div>
          
          {["ALL", "BUY", "SELL"].map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-4 py-2 rounded-xl text-xs font-bold cursor-pointer transition-all ${
                filter === f
                  ? "bg-amber-500/10 text-amber-400 border border-amber-500/20"
                  : "text-slate-400 hover:text-slate-200 hover:bg-white/[0.01]"
              }`}
            >
              {f === "ALL" ? "All Logs" : f === "BUY" ? "Purchases Only" : "Settlements Only"}
            </button>
          ))}
        </div>

        <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest px-2">
          {filtered.length} entry record{filtered.length !== 1 ? "s" : ""}
        </span>
      </div>

      {/* ── Transaction list table logs ── */}
      {filtered.length === 0 ? (
        <div className="p-8 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col items-center justify-center text-center py-20">
          <div className="p-4 rounded-3xl bg-white/[0.02] border border-white/[0.04] text-slate-500 mb-4 animate-float">
            <ClipboardList size={36} className="text-amber-400" />
          </div>
          <h3 className="text-lg font-black text-slate-200 uppercase tracking-wider">No execution logs</h3>
          <p className="text-xs text-slate-500 max-w-[280px] mt-2 leading-relaxed">
            No execution files found matching sector filter categories.
          </p>
          <button 
            onClick={() => navigate("/market")}
            className="mt-6 px-5 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all inline-flex items-center gap-1.5 cursor-pointer shadow-lg"
          >
            <span>Browse Markets</span>
            <ArrowUpCircle size={13} />
          </button>
        </div>
      ) : (
        <div className="p-4 bg-slate-950/40 border border-white/[0.04] rounded-3xl overflow-hidden shadow-xl">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-white/[0.04] text-[10px] text-slate-500 font-bold uppercase tracking-wider">
                  <th className="py-4 px-4">Action</th>
                  <th className="py-4 px-4">Asset</th>
                  <th className="py-4 px-4">Shares</th>
                  <th className="py-4 px-4">Share Price</th>
                  <th className="py-4 px-4">Total Value</th>
                  <th className="py-4 px-4">Timestamp</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((txn) => {
                  const stock = stocks.find((s) => s.ticker === txn.ticker);
                  const isBuy = txn.type === "BUY";
                  return (
                    <tr key={txn.id} className="border-b border-white/[0.02] last:border-b-0 hover:bg-white/[0.02] transition-colors">
                      <td className="py-4 px-4">
                        <span className={`text-[10px] font-black uppercase tracking-wider px-2.5 py-1 rounded-md border ${
                          isBuy 
                            ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' 
                            : 'bg-red-500/10 text-red-400 border-red-500/20'
                        }`}>
                          {txn.type}
                        </span>
                      </td>

                      <td className="py-4 px-4">
                        <span className="text-sm font-black text-white tracking-tight block">{txn.ticker}</span>
                        <span className="text-[11px] text-slate-500 font-semibold max-w-[130px] block truncate mt-0.5">{stock?.name || txn.ticker}</span>
                      </td>

                      <td className="py-4 px-4 font-mono font-semibold text-xs text-slate-200">
                        {txn.shares}
                      </td>

                      <td className="py-4 px-4 font-mono text-xs text-slate-400">
                        ${txn.price.toFixed(2)}
                      </td>

                      <td className={`py-4 px-4 font-mono font-bold text-xs ${isBuy ? 'text-red-400' : 'text-emerald-400'}`}>
                        {isBuy ? "−" : "+"}{formatCurrency(txn.total)}
                      </td>

                      <td className="py-4 px-4 text-xs font-semibold text-slate-500">
                        <div className="flex items-center gap-1.5">
                          <Calendar size={11} className="text-slate-650" />
                          <span>{formatDate(txn.date)}</span>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

    </motion.div>
  );
}
