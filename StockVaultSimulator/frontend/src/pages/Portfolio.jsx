// ============================================================
// Portfolio.jsx — Premium Portfolio Holdings Worksheet
// Redesigned with custom grids, sector allocation donut graphs,
// returns metrics, risk profiling, and smart AI recommendations.
// ============================================================

import { useState } from "react";
import { useApp } from "../context/AppContext";
import { formatCurrency, calcPortfolioMetrics } from "../utils/stockSimulator";
import { SECTOR_COLORS } from "../data/mockStocks";
import BuyModal from "../components/BuyModal";
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
  BarChart, Bar, XAxis, YAxis
} from "recharts";
import { 
  TrendingUp, TrendingDown, Briefcase, Lightbulb, 
  HelpCircle, ShieldCheck, ArrowRight, Activity, Wallet
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

export default function Portfolio() {
  const { state } = useApp();
  const { portfolio, stocks, walletBalance } = state;
  const navigate = useNavigate();
  const [sellModal, setSellModal] = useState(null);

  const metrics = calcPortfolioMetrics(portfolio, stocks);
  const totalNetWorth = walletBalance + metrics.totalValue;
  const isPnlPositive = metrics.totalPnL >= 0;

  // Sector Pie chart data
  const sectorData = metrics.positions.reduce((acc, pos) => {
    const existing = acc.find((a) => a.name === pos.sector);
    if (existing) existing.value += pos.currentValue;
    else acc.push({ name: pos.sector, value: parseFloat(pos.currentValue.toFixed(2)) });
    return acc;
  }, []);

  // Return per position bar chart data
  const pnlChartData = metrics.positions.map((p) => ({
    ticker: p.ticker,
    pnl: parseFloat(p.pnl.toFixed(2)),
  }));

  // Smart Advisory Evaluation logic
  function generateRecommendations() {
    const recs = [];
    
    // Suggestion 1: Excessive Cash allocation
    const cashRatio = walletBalance / totalNetWorth;
    if (cashRatio > 0.40) {
      recs.push({
        type: "allocation",
        title: "Deploy Idle Capital",
        text: `You hold ${(cashRatio * 100).toFixed(0)}% of your worth in cash. Consider averaging into long-term defensive blue-chips (e.g. MSFT, JNJ).`,
        priority: "high"
      });
    }

    // Suggestion 2: Over-concentration in one sector
    metrics.positions.forEach(pos => {
      const concentration = pos.currentValue / metrics.totalValue;
      if (concentration > 0.50) {
        recs.push({
          type: "diversification",
          title: `Over-exposure: ${pos.ticker}`,
          text: `${pos.ticker} represents ${(concentration * 100).toFixed(0)}% of your holdings. Hedging into other sectors could reduce volatility risk.`,
          priority: "medium"
        });
      }
    });

    // Suggestion 3: General positive return praise
    if (isPnlPositive && metrics.totalPnLPercent > 5) {
      recs.push({
        type: "praise",
        title: "Diversification Success",
        text: "Your current asset allocation is beating benchmark volatility. Hold positions to lock in gains.",
        priority: "low"
      });
    }

    // Fallback recommendation
    if (recs.length === 0) {
      recs.push({
        type: "general",
        title: "Balanced Portfolio",
        text: "Your capital allocation is well-balanced across multiple sectors. Use TradeBot to explore upcoming index trends.",
        priority: "low"
      });
    }

    return recs;
  }

  const recommendations = generateRecommendations();

  // If Portfolio is empty, show modern empty state
  if (portfolio.length === 0) {
    return (
      <div className="flex flex-col gap-6 animate-fade-in">
        <div className="flex justify-between items-center border-b border-white/[0.04] pb-5">
          <div>
            <h2 className="text-2xl font-black tracking-tight text-white">Asset Portfolio</h2>
            <p className="text-xs text-slate-400 mt-1">Manage positions, evaluate returns, and review risk analytics.</p>
          </div>
        </div>

        <div className="p-8 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col items-center justify-center text-center py-20">
          <div className="p-4 rounded-3xl bg-white/[0.02] border border-white/[0.04] text-slate-500 mb-4 animate-float">
            <Briefcase size={36} className="text-amber-400" />
          </div>
          <h3 className="text-lg font-black text-slate-200 uppercase tracking-wider">Your Portfolio is Empty</h3>
          <p className="text-xs text-slate-500 max-w-[280px] mt-2 leading-relaxed">
            You hold no asset positions. You have {formatCurrency(walletBalance)} available to invest.
          </p>
          <button 
            onClick={() => navigate("/market")}
            className="mt-6 px-5 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all inline-flex items-center gap-1.5 cursor-pointer shadow-lg shadow-amber-900/20"
          >
            <span>Browse Stock Market</span>
            <ArrowRight size={13} />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 animate-fade-in">
      
      {/* ── Page Header ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 border-b border-white/[0.04] pb-5">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-white">Asset Portfolio</h2>
          <p className="text-xs text-slate-400 mt-1">
            {portfolio.length} position{portfolio.length !== 1 ? "s" : ""} active · Real-time valuations.
          </p>
        </div>
        <button 
          onClick={() => navigate("/market")}
          className="self-start px-4.5 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white text-xs font-bold transition-all cursor-pointer shadow-lg shadow-amber-900/10"
        >
          + Purchase More
        </button>
      </div>

      {/* ── Metric Stats Cards ── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Net Worth Valuation</span>
          <span className="text-xl font-mono font-black text-white mt-1.5 block">{formatCurrency(totalNetWorth)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">Cash + Position Val</span>
        </div>

        <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Holdings Value</span>
          <span className="text-xl font-mono font-black text-amber-400 mt-1.5 block">{formatCurrency(metrics.totalValue)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">Market values</span>
        </div>

        <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Cash Reserve</span>
          <span className="text-xl font-mono font-black text-emerald-400 mt-1.5 block">{formatCurrency(walletBalance)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2 block">Liquid wallet balance</span>
        </div>

        <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl relative group">
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider block">Total Return</span>
          <span className={`text-xl font-mono font-black mt-1.5 block ${isPnlPositive ? 'text-emerald-400' : 'text-red-400'}`}>
            {isPnlPositive ? "+" : ""}{formatCurrency(metrics.totalPnL)}
          </span>
          <span className={`text-[10px] font-bold tracking-wide uppercase mt-2 block ${isPnlPositive ? 'text-emerald-500/80' : 'text-red-500/80'}`}>
            {isPnlPositive ? "+" : ""}{metrics.totalPnLPercent.toFixed(2)}% ROI
          </span>
        </div>
      </div>

      {/* ── Allocation Donut & Return Bar Chart ── */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        
        {/* Sector Allocation Donut Card */}
        <div className="lg:col-span-5 p-6 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col justify-between">
          <div>
            <span className="text-xs font-bold text-slate-500 uppercase tracking-widest">Sector Allocation</span>
            <h3 className="text-sm font-bold text-slate-200 mt-0.5">Asset Diversification</h3>
          </div>

          <div className="flex items-center gap-6 my-2">
            <div className="w-36 h-36 relative shrink-0 flex items-center justify-center">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={sectorData} cx="50%" cy="50%" innerRadius={42} outerRadius={62} paddingAngle={4} dataKey="value">
                    {sectorData.map((entry) => (
                      <Cell key={entry.name} fill={SECTOR_COLORS[entry.name] || "#FDA481"} stroke="#07090d" strokeWidth={2} />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(v) => formatCurrency(v)}
                    contentStyle={{ background: "#07090d", border: "1px solid rgba(255,255,255,0.08)", borderRadius: "10px", fontSize: 12, color: '#fff' }}
                  />
                </PieChart>
              </ResponsiveContainer>
              <div className="absolute flex flex-col items-center justify-center">
                <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider leading-none">Net Worth</span>
                <span className="text-xs font-mono font-bold text-white mt-1 block leading-none">100%</span>
              </div>
            </div>

            <div className="flex-1 flex flex-col gap-1.5">
              {sectorData.map((s) => {
                const pct = ((s.value / metrics.totalValue) * 100).toFixed(1);
                return (
                  <div key={s.name} className="flex justify-between items-center text-xs">
                    <div className="flex items-center gap-2">
                      <div className="w-2 h-2 rounded-full" style={{ background: SECTOR_COLORS[s.name] || "#FDA481" }} />
                      <span className="text-slate-400 font-semibold truncate max-w-[100px]">{s.name}</span>
                    </div>
                    <span className="font-mono font-bold text-white">{pct}%</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Return per Position Bar Chart */}
        <div className="lg:col-span-7 p-6 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col justify-between">
          <div>
            <span className="text-xs font-bold text-slate-500 uppercase tracking-widest">Valuation Returns</span>
            <h3 className="text-sm font-bold text-slate-200 mt-0.5">P&amp;L Performance by Position</h3>
          </div>

          <div className="w-full h-40 mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={pnlChartData} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
                <XAxis dataKey="ticker" tick={{ fill: "rgba(255,255,255,0.25)", fontSize: 10 }} axisLine={false} tickLine={false} />
                <YAxis hide />
                <Tooltip
                  formatter={(v) => [formatCurrency(v), "P&L"]}
                  contentStyle={{ background: "#05070c", border: "1px solid rgba(255,255,255,0.08)", borderRadius: "10px", fontSize: 12, color: '#fff' }}
                />
                <Bar dataKey="pnl" radius={[4, 4, 0, 0]}>
                  {pnlChartData.map((entry) => (
                    <Cell key={entry.ticker} fill={entry.pnl >= 0 ? "#2dd4a0" : "#ef4444"} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

      </div>

      {/* ── Holdings List Table ── */}
      <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl overflow-hidden shadow-xl">
        <div className="flex justify-between items-center mb-4 px-1">
          <div>
            <span className="text-xs font-black uppercase tracking-widest text-slate-400">Holdings Inventory</span>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/[0.04] text-[10px] text-slate-500 font-bold uppercase tracking-wider">
                <th className="py-4 px-4">Asset</th>
                <th className="py-4 px-4">Shares</th>
                <th className="py-4 px-4">Avg Price</th>
                <th className="py-4 px-4">Curr Price</th>
                <th className="py-4 px-4">Valuation</th>
                <th className="py-4 px-4">Returns ($)</th>
                <th className="py-4 px-4">Returns (%)</th>
                <th className="py-4 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {metrics.positions.map((pos) => {
                const isPosUp = pos.pnl >= 0;
                return (
                  <tr key={pos.ticker} className="border-b border-white/[0.02] last:border-b-0 hover:bg-white/[0.02] transition-colors">
                    <td className="py-4 px-4">
                      <span className="text-sm font-black text-white tracking-tight block">{pos.ticker}</span>
                      <span className="text-[11px] text-slate-500 font-semibold max-w-[130px] block truncate mt-0.5">{pos.stockName}</span>
                    </td>
                    
                    <td className="py-4 px-4 font-mono font-semibold text-xs text-slate-200">
                      {pos.shares}
                    </td>

                    <td className="py-4 px-4 font-mono text-xs text-slate-400">
                      ${pos.avgPrice.toFixed(2)}
                    </td>

                    <td className="py-4 px-4 font-mono font-bold text-xs text-slate-300">
                      ${pos.currentPrice.toFixed(2)}
                    </td>

                    <td className="py-4 px-4 font-mono font-bold text-xs text-white">
                      {formatCurrency(pos.currentValue)}
                    </td>

                    <td className={`py-4 px-4 font-mono font-bold text-xs ${isPosUp ? 'text-emerald-400' : 'text-red-400'}`}>
                      {isPosUp ? "+" : ""}{formatCurrency(pos.pnl)}
                    </td>

                    <td className="py-4 px-4">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                        isPosUp ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
                      }`}>
                        {isPosUp ? "+" : ""}{pos.pnlPercent.toFixed(2)}%
                      </span>
                    </td>

                    <td className="py-4 px-4 text-right">
                      <button
                        id={`sell-btn-${pos.ticker}`}
                        onClick={() => setSellModal(stocks.find((s) => s.ticker === pos.ticker))}
                        className="px-3 py-1.5 rounded-xl bg-red-500/10 hover:bg-red-500/20 border border-red-500/20 text-red-400 text-xs font-bold transition-all cursor-pointer"
                      >
                        Sell
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* ── Smart Recommendation Advice Panel ── */}
      <div className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl">
        <div className="flex items-center gap-2 mb-3.5 px-1">
          <Lightbulb size={15} className="text-yellow-400" />
          <span className="text-xs font-black uppercase tracking-widest text-slate-400">Portfolio Advisor Insights</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
          {recommendations.map((rec, i) => (
            <div key={i} className="p-4 rounded-2xl bg-white/[0.01] border border-white/[0.03] flex flex-col justify-between">
              <div>
                <span className={`px-2 py-0.5 rounded text-[8px] font-black uppercase tracking-wider ${
                  rec.priority === "high" 
                    ? "bg-red-500/10 text-red-400 border border-red-500/20"
                    : rec.priority === "medium"
                      ? "bg-yellow-500/10 text-yellow-400 border border-yellow-500/20"
                      : "bg-emerald-500/10 text-emerald-400 border border-emerald-500/20"
                }`}>
                  {rec.priority} Priority
                </span>
                <h4 className="text-xs font-bold text-white mt-2 uppercase tracking-wide">{rec.title}</h4>
                <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">{rec.text}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Trade modal for selling positions */}
      {sellModal && (
        <BuyModal 
          stock={sellModal} 
          mode="SELL" 
          onClose={() => setSellModal(null)} 
        />
      )}
    </div>
  );
}
