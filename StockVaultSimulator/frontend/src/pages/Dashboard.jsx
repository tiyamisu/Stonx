// ============================================================
// Dashboard.jsx — Premium Trading Terminal Workspace
// Redesigned with luxury fintech layout, glowing stat grids,
// responsive Recharts, and smooth card entrances.
// ============================================================

import { useApp } from "../context/AppContext";
import { formatCurrency, calcPortfolioMetrics } from "../utils/stockSimulator";
import { MARKET_INDICES, NEWS_HEADLINES, SECTOR_COLORS } from "../data/mockStocks";
import {
  AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell
} from "recharts";
import { 
  TrendingUp, TrendingDown, DollarSign, Briefcase, Activity, 
  Newspaper, PieChart as PieIcon, ArrowUpRight, ArrowDownRight, 
  ArrowRight, ShieldAlert, BadgeCheck
} from "lucide-react";
import { motion } from "framer-motion";

// Custom chart tooltip
function ChartTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-[#111418]/90 border border-white/[0.08] backdrop-blur-md rounded-xl p-3.5 shadow-2xl">
      <div className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">{label}</div>
      <div className="text-sm font-mono font-bold text-amber-400">
        {formatCurrency(payload[0].value)}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const { state } = useApp();
  const { stocks, portfolio, walletBalance, transactions } = state;

  const metrics = calcPortfolioMetrics(portfolio, stocks);
  const totalNetWorth = walletBalance + metrics.totalValue;
  const isPnlPositive = metrics.totalPnL >= 0;

  // Top Movers
  const sorted = [...stocks].sort((a, b) => b.changePercent - a.changePercent);
  const topGainers = sorted.slice(0, 4);
  const topLosers = sorted.slice(-4).reverse();

  // Allocation Pie Data
  const allocationData = metrics.positions.reduce((acc, pos) => {
    const existing = acc.find((a) => a.name === pos.sector);
    if (existing) existing.value += pos.currentValue;
    else acc.push({ name: pos.sector, value: pos.currentValue });
    return acc;
  }, []);

  // 14-day simulated chart data
  const chartData = Array.from({ length: 14 }, (_, i) => {
    const date = new Date();
    date.setDate(date.getDate() - (13 - i));
    const baseValue = totalNetWorth * (0.92 + i * 0.006);
    // Add realistic market noise
    const noise = Math.sin(i * 0.9) * 0.02 * totalNetWorth;
    const val = baseValue + noise;
    return {
      date: date.toLocaleDateString("en-US", { month: "short", day: "numeric" }),
      value: parseFloat(Math.max(100, val).toFixed(2)),
    };
  });
  chartData[chartData.length - 1].value = parseFloat(totalNetWorth.toFixed(2));

  // Quick action container animation
  const containerVariants = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.06
      }
    }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 15 },
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
      <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4 border-b border-white/[0.04] pb-5">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-white">Dashboard Overview</h2>
          <p className="text-xs text-slate-400 mt-1">
            Real-time terminal status and portfolio analytics for trading cycle.
          </p>
        </div>
        
        <div className="flex items-center gap-3">
          {/* Running Clock/Date Widget */}
          <div className="px-3.5 py-1.5 rounded-xl bg-white/[0.02] border border-white/[0.04] text-xs font-semibold text-slate-300">
            {new Date().toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" })}
          </div>
          
          <div className="flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-semibold">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping" />
            Terminal Live
          </div>
        </div>
      </div>

      {/* ── Row 1: Index Quick Tracker ── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {MARKET_INDICES.map((idx) => {
          const isUp = idx.change >= 0;
          return (
            <motion.div 
              key={idx.ticker}
              variants={itemVariants}
              className="p-4 bg-slate-950/40 border border-white/[0.04] rounded-2xl flex flex-col justify-between"
            >
              <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">{idx.name}</span>
              <span className="text-lg font-mono font-bold text-white mt-1.5">{idx.value.toLocaleString()}</span>
              <span className={`text-xs mt-1 font-semibold flex items-center gap-0.5 ${isUp ? 'text-emerald-400' : 'text-red-400'}`}>
                {isUp ? <ArrowUpRight size={12} /> : <ArrowDownRight size={12} />}
                {isUp ? "+" : ""}{idx.change.toFixed(2)}%
              </span>
            </motion.div>
          );
        })}
      </div>

      {/* ── Row 2: Portfolio Wallet Statistics ── */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Net Worth Card */}
        <motion.div 
          variants={itemVariants}
          className="p-5 bg-slate-950/70 border border-white/[0.05] rounded-3xl relative overflow-hidden group shadow-xl"
        >
          <div className="absolute top-0 right-0 w-24 h-24 bg-cyan-500/5 rounded-full blur-2xl group-hover:bg-cyan-500/10 transition-colors pointer-events-none" />
          <div className="flex items-center gap-2 text-slate-400 text-xs font-bold uppercase tracking-widest mb-3">
            <DollarSign size={14} className="text-amber-400" />
            <span>Net Worth</span>
          </div>
          <span className="text-2xl font-mono font-black text-white block">{formatCurrency(totalNetWorth)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2.5 block">Wallet + Portfolio Val</span>
        </motion.div>

        {/* Portfolio Value Card */}
        <motion.div 
          variants={itemVariants}
          className="p-5 bg-slate-950/70 border border-white/[0.05] rounded-3xl relative overflow-hidden group shadow-xl"
        >
          <div className="absolute top-0 right-0 w-24 h-24 bg-violet-500/5 rounded-full blur-2xl group-hover:bg-violet-500/10 transition-colors pointer-events-none" />
          <div className="flex items-center gap-2 text-slate-400 text-xs font-bold uppercase tracking-widest mb-3">
            <Briefcase size={14} className="text-violet-400" />
            <span>Portfolio</span>
          </div>
          <span className="text-2xl font-mono font-black text-violet-400 block">{formatCurrency(metrics.totalValue)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2.5 block">{portfolio.length} Active Positions</span>
        </motion.div>

        {/* Cash Balance Card */}
        <motion.div 
          variants={itemVariants}
          className="p-5 bg-slate-950/70 border border-white/[0.05] rounded-3xl relative overflow-hidden group shadow-xl"
        >
          <div className="absolute top-0 right-0 w-24 h-24 bg-emerald-500/5 rounded-full blur-2xl group-hover:bg-emerald-500/10 transition-colors pointer-events-none" />
          <div className="flex items-center gap-2 text-slate-400 text-xs font-bold uppercase tracking-widest mb-3">
            <Activity size={14} className="text-emerald-400" />
            <span>Cash Available</span>
          </div>
          <span className="text-2xl font-mono font-black text-emerald-400 block">{formatCurrency(walletBalance)}</span>
          <span className="text-[10px] text-slate-500 font-bold tracking-wide uppercase mt-2.5 block">Liquidity for Trading</span>
        </motion.div>

        {/* Total Returns Card */}
        <motion.div 
          variants={itemVariants}
          className={`p-5 bg-slate-950/70 border rounded-3xl relative overflow-hidden group shadow-xl ${
            isPnlPositive ? 'border-emerald-500/10' : 'border-red-500/10'
          }`}
        >
          <div className={`absolute top-0 right-0 w-24 h-24 rounded-full blur-2xl pointer-events-none ${
            isPnlPositive ? 'bg-emerald-500/5 group-hover:bg-emerald-500/10' : 'bg-red-500/5 group-hover:bg-red-500/10'
          }`} />
          <div className="flex items-center gap-2 text-slate-400 text-xs font-bold uppercase tracking-widest mb-3">
            {isPnlPositive ? (
              <TrendingUp size={14} className="text-emerald-400" />
            ) : (
              <TrendingDown size={14} className="text-red-400" />
            )}
            <span>Total Returns</span>
          </div>
          <span className={`text-2xl font-mono font-black block ${
            isPnlPositive ? 'text-emerald-400' : 'text-red-400'
          }`}>
            {isPnlPositive ? "+" : ""}{formatCurrency(metrics.totalPnL)}
          </span>
          <span className={`text-[10px] font-bold tracking-wide uppercase mt-2.5 block ${
            isPnlPositive ? 'text-emerald-500/80' : 'text-red-500/80'
          }`}>
            {isPnlPositive ? "+" : ""}{metrics.totalPnLPercent.toFixed(2)}% ROI
          </span>
        </motion.div>
      </div>

      {/* ── Row 3: Interactive Chart + Sector Allocation ── */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        
        {/* TradingView Portfolio Performance chart */}
        <motion.div 
          variants={itemVariants}
          className="lg:col-span-8 p-6 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col justify-between"
        >
          <div className="flex justify-between items-center mb-6">
            <div>
              <span className="text-xs font-bold text-slate-500 uppercase tracking-widest">Portfolio Performance</span>
              <h3 className="text-sm font-bold text-slate-200 mt-0.5">Asset Net Worth Timeline</h3>
            </div>
            <span className="text-[10px] font-bold text-amber-400 bg-amber-500/10 border border-amber-500/20 px-2.5 py-1 rounded-full uppercase tracking-wider">
              14-Day View
            </span>
          </div>

          <div className="w-full h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="netWorthGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#FDA481" stopOpacity={0.25} />
                    <stop offset="100%" stopColor="#FDA481" stopOpacity={0.01} />
                  </linearGradient>
                </defs>
                <XAxis 
                  dataKey="date" 
                  tick={{ fill: "rgba(255,255,255,0.3)", fontSize: 10, fontWeight: 500 }} 
                  axisLine={false} 
                  tickLine={false} 
                  dy={10}
                />
                <YAxis 
                  domain={["auto", "auto"]} 
                  tick={{ fill: "rgba(255,255,255,0.3)", fontSize: 10, fontWeight: 500 }} 
                  axisLine={false} 
                  tickLine={false}
                  dx={-10}
                />
                <Tooltip content={<ChartTooltip />} crosshair={{ stroke: 'rgba(255,255,255,0.1)', strokeWidth: 1 }} />
                <Area 
                  type="monotone" 
                  dataKey="value" 
                  stroke="#FDA481" 
                  strokeWidth={2} 
                  fill="url(#netWorthGrad)" 
                  dot={{ r: 0 }}
                  activeDot={{ r: 4, stroke: "#FF8C42", strokeWidth: 2, fill: "#07090d" }}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* Sector Allocation Donut Card */}
        <motion.div 
          variants={itemVariants}
          className="lg:col-span-4 p-6 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col justify-between"
        >
          <div>
            <span className="text-xs font-bold text-slate-500 uppercase tracking-widest">Holdings Allocation</span>
            <h3 className="text-sm font-bold text-slate-200 mt-0.5">Asset Diversification</h3>
          </div>

          {allocationData.length > 0 ? (
            <div className="flex flex-col items-center gap-4 my-2">
              <div className="w-full h-40 relative flex items-center justify-center">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie 
                      data={allocationData} 
                      cx="50%" 
                      cy="50%" 
                      innerRadius={45} 
                      outerRadius={65} 
                      paddingAngle={4} 
                      dataKey="value"
                    >
                      {allocationData.map((entry) => (
                        <Cell key={entry.name} fill={SECTOR_COLORS[entry.name] || "#FDA481"} stroke="#07090d" strokeWidth={2} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(v) => formatCurrency(v)}
                      contentStyle={{ background: "#07090d", border: "1px solid rgba(255,255,255,0.08)", borderRadius: "12px", fontSize: 12, color: '#fff' }}
                    />
                  </PieChart>
                </ResponsiveContainer>
                {/* Visual indicator in middle of donut */}
                <div className="absolute flex flex-col items-center justify-center">
                  <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Total</span>
                  <span className="text-xs font-mono font-bold text-white mt-0.5">{portfolio.length} Secs</span>
                </div>
              </div>

              <div className="w-full flex flex-col gap-1.5">
                {allocationData.map((entry) => {
                  const percentage = metrics.totalValue > 0 ? ((entry.value / metrics.totalValue) * 100).toFixed(1) : "0.0";
                  return (
                    <div key={entry.name} className="flex justify-between items-center px-3 py-1.5 rounded-xl bg-white/[0.01] border border-white/[0.03] text-xs">
                      <div className="flex items-center gap-2">
                        <div className="w-2.5 h-2.5 rounded-md" style={{ background: SECTOR_COLORS[entry.name] || "#FDA481" }} />
                        <span className="text-slate-400 font-medium">{entry.name}</span>
                      </div>
                      <span className="font-mono font-bold text-white">{percentage}%</span>
                    </div>
                  );
                })}
              </div>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-3 py-12 text-center text-slate-500 flex-1">
              <div className="p-3.5 rounded-2xl bg-white/[0.02] border border-white/[0.04]">
                <PieIcon size={26} className="text-slate-600" />
              </div>
              <div>
                <span className="text-xs font-bold text-slate-400 block uppercase tracking-wider">No Position Assets</span>
                <span className="text-[11px] text-slate-600 max-w-[180px] block mt-1 leading-relaxed">Purchased holdings will render here.</span>
              </div>
            </div>
          )}
        </motion.div>
      </div>

      {/* ── Row 4: Top Gainers & Losers grids ── */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        
        {/* Top Gainers Card */}
        <motion.div 
          variants={itemVariants}
          className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl"
        >
          <div className="flex items-center gap-2 mb-4 px-1">
            <TrendingUp size={15} className="text-emerald-400" />
            <span className="text-xs font-black uppercase tracking-widest text-slate-400">Top Gainers</span>
          </div>

          <div className="flex flex-col gap-2">
            {topGainers.map((stock) => (
              <div key={stock.ticker} className="flex items-center justify-between p-3.5 rounded-2xl bg-white/[0.01] hover:bg-white/[0.03] border border-white/[0.03] transition-all">
                <div className="flex flex-col">
                  <span className="text-xs font-black text-white tracking-tight">{stock.ticker}</span>
                  <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mt-0.5">{stock.sector}</span>
                </div>
                <div className="text-right flex flex-col">
                  <span className="text-xs font-mono font-bold text-white">${stock.price.toFixed(2)}</span>
                  <span className="text-[11px] text-emerald-400 font-bold inline-flex items-center justify-end gap-0.5 mt-0.5">
                    <ArrowUpRight size={12} /> +{stock.changePercent.toFixed(2)}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Top Losers Card */}
        <motion.div 
          variants={itemVariants}
          className="p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl"
        >
          <div className="flex items-center gap-2 mb-4 px-1">
            <TrendingDown size={15} className="text-red-400" />
            <span className="text-xs font-black uppercase tracking-widest text-slate-400">Top Losers</span>
          </div>

          <div className="flex flex-col gap-2">
            {topLosers.map((stock) => (
              <div key={stock.ticker} className="flex items-center justify-between p-3.5 rounded-2xl bg-white/[0.01] hover:bg-white/[0.03] border border-white/[0.03] transition-all">
                <div className="flex flex-col">
                  <span className="text-xs font-black text-white tracking-tight">{stock.ticker}</span>
                  <span className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mt-0.5">{stock.sector}</span>
                </div>
                <div className="text-right flex flex-col">
                  <span className="text-xs font-mono font-bold text-white">${stock.price.toFixed(2)}</span>
                  <span className="text-[11px] text-red-400 font-bold inline-flex items-center justify-end gap-0.5 mt-0.5">
                    <ArrowDownRight size={12} /> {stock.changePercent.toFixed(2)}%
                  </span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

      </div>

      {/* ── Row 5: Live Market News + Recent Actions ── */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        
        {/* Live News Feed */}
        <motion.div 
          variants={itemVariants}
          className="lg:col-span-8 p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl"
        >
          <div className="flex items-center gap-2 mb-4 px-1">
            <Newspaper size={15} className="text-amber-400" />
            <span className="text-xs font-black uppercase tracking-widest text-slate-400">Market Intelligence Stream</span>
          </div>

          <div className="flex flex-col gap-2">
            {NEWS_HEADLINES.map((news) => (
              <div key={news.id} className="flex items-center gap-3 p-3.5 rounded-2xl bg-white/[0.01] hover:bg-white/[0.03] border border-white/[0.03] transition-all cursor-pointer">
                {news.sentiment === "positive" ? (
                  <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 shrink-0 shadow-lg shadow-emerald-500/20" />
                ) : (
                  <div className="w-2.5 h-2.5 rounded-full bg-red-400 shrink-0 shadow-lg shadow-red-500/20" />
                )}
                
                <span className="flex-1 text-xs font-semibold text-slate-300 truncate leading-relaxed">
                  {news.headline}
                </span>

                <div className="flex items-center gap-3 shrink-0">
                  <span className="px-2 py-0.5 rounded-md text-[9px] font-bold bg-[#FDA481]/5 border border-[#FDA481]/20 text-[#FDA481] uppercase">
                    {news.ticker}
                  </span>
                  <span className="text-[10px] text-slate-500 font-medium">
                    {news.time}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Recent Transactions List */}
        <motion.div 
          variants={itemVariants}
          className="lg:col-span-4 p-5 bg-slate-950/40 border border-white/[0.04] rounded-3xl flex flex-col"
        >
          <div className="flex items-center gap-2 mb-4 px-1 shrink-0">
            <BadgeCheck size={15} className="text-amber-400" />
            <span className="text-xs font-black uppercase tracking-widest text-slate-400">Terminal Log</span>
          </div>

          {transactions.length > 0 ? (
            <div className="flex flex-col gap-2.5 overflow-y-auto flex-1 max-h-[280px] pr-1">
              {transactions.slice(0, 4).map((txn) => {
                const isBuy = txn.type === "BUY";
                return (
                  <div key={txn.id} className="flex items-center justify-between p-3 rounded-2xl bg-white/[0.01] border border-white/[0.03] text-xs">
                    <div className="flex items-center gap-2">
                      <span className={`px-1.5 py-0.5 rounded text-[9px] font-black uppercase tracking-wide ${
                        isBuy ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'
                      }`}>
                        {txn.type}
                      </span>
                      <div>
                        <span className="font-bold text-white tracking-tight block">{txn.ticker}</span>
                        <span className="text-[10px] text-slate-500 font-bold block leading-none mt-0.5">{txn.shares} Shares</span>
                      </div>
                    </div>
                    <span className={`font-mono font-bold ${
                      isBuy ? 'text-red-400' : 'text-emerald-400'
                    }`}>
                      {isBuy ? "−" : "+"}{formatCurrency(txn.total)}
                    </span>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-3 py-12 text-center text-slate-500 flex-1">
              <div className="p-3 rounded-xl bg-white/[0.01]">
                <ShieldAlert size={20} className="text-slate-600" />
              </div>
              <span className="text-[10px] text-slate-600 font-bold uppercase tracking-wider block">No transactions logged</span>
            </div>
          )}
        </motion.div>

      </div>

    </motion.div>
  );
}
