// ============================================================
// TradeBotPage.jsx — Advanced AI Advisor Terminal
// Redesigned with Claude/Perplexity conversational layout,
// message animation entrance sequences, and suggestion cards.
// ============================================================

import { useState, useRef, useEffect } from "react";
import { Bot, Send, Lightbulb, User, Sparkles, MessageSquare, ArrowRight } from "lucide-react";
import { getTradeBotResponse } from "../utils/tradeBot";
import { useApp } from "../context/AppContext";
import { motion, AnimatePresence } from "framer-motion";

function parseMarkdown(text) {
  return text
    .replace(/\*\*(.*?)\*\*/g, "<strong class='text-white font-bold'>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em class='text-amber-400 font-semibold'>$1</em>")
    .replace(/\n/g, "<br/>");
}

const INITIAL_MESSAGES = [
  {
    id: 1, from: "bot",
    text: "Welcome to **StonX Intelligence Console**! I am **TradeBot AI**, your investment research copilot.\n\nI can analyze ticker history, evaluate portfolio concentration risk, review transaction sheets, and clarify financial metrics.\n\nTry entering a prompt below, or select a suggestion chip to begin.",
    time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
  },
];

const QUICK_PROMPTS = [
  { label: "Analyze AAPL",   text: "analyze AAPL"     },
  { label: "Top Gainers",    text: "top gainers"       },
  { label: "My Portfolio",   text: "my portfolio"      },
  { label: "Market Summary", text: "market summary"    },
  { label: "Invest Tips",    text: "investment tips"   },
  { label: "P/E Ratio?",     text: "what is P/E ratio" },
];

export default function TradeBotPage() {
  const { state } = useApp();
  const [messages, setMessages] = useState(INITIAL_MESSAGES);
  const [input, setInput] = useState("");
  const [typing, setTyping] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, typing]);

  function sendMessage(text) {
    if (!text.trim()) return;
    const userMsg = {
      id: Date.now(), from: "user", text: text.trim(),
      time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
    };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setTyping(true);
    
    // Simulate thinking delay
    setTimeout(() => {
      const response = getTradeBotResponse(text, state);
      setMessages((prev) => [...prev, {
        id: Date.now() + 1, from: "bot", text: response,
        time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
      }]);
      setTyping(false);
    }, 600 + Math.random() * 400);
  }

  return (
    <div className="flex flex-col h-full min-h-0 gap-5 animate-fade-in flex-1">
      
      {/* ── Page Header ── */}
      <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-4 border-b border-white/[0.04] pb-5 shrink-0">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-tr from-amber-500 to-orange-500 flex items-center justify-center shadow-lg shadow-amber-500/10 shrink-0">
            <Sparkles size={20} className="text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-black tracking-tight text-white">StonX Intelligence Console</h2>
            <p className="text-xs text-slate-400 mt-1 flex items-center gap-1.5">
              <span className="relative flex h-1.5 w-1.5">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-emerald-500"></span>
              </span>
              <span>TradeBot Copilot · Active Integration</span>
            </p>
          </div>
        </div>
      </div>

      {/* ── Chat Container Cards ── */}
      <div className="flex-1 min-h-0 bg-slate-950/40 border border-white/[0.04] rounded-3xl overflow-hidden flex flex-col justify-between">
        
        {/* Messages scroll content wrapper */}
        <div className="flex-1 overflow-y-auto p-5 flex flex-col gap-4 min-h-0">
          <AnimatePresence initial={false}>
            {messages.map((msg) => {
              const isBot = msg.from === "bot";
              return (
                <motion.div 
                  key={msg.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ type: "spring", stiffness: 150 }}
                  className={`flex gap-3.5 max-w-[80%] ${
                    isBot ? 'self-start' : 'self-end flex-row-reverse'
                  }`}
                >
                  {/* Avatar Icon */}
                  <div className={`w-8.5 h-8.5 rounded-xl flex items-center justify-center shrink-0 border shadow-sm ${
                    isBot 
                      ? 'bg-slate-900 border-white/[0.06] text-amber-400' 
                      : 'bg-amber-500/10 border-amber-500/20 text-white'
                  }`}>
                    {isBot ? <Bot size={15} /> : <User size={15} />}
                  </div>

                  {/* Message bubble card */}
                  <div className="flex flex-col gap-1.5">
                    <div className={`p-4 rounded-2xl text-xs leading-relaxed ${
                      isBot 
                        ? 'bg-slate-900/60 border border-white/[0.05] text-slate-300 rounded-tl-sm shadow-md' 
                        : 'bg-gradient-to-r from-amber-600 to-orange-600 text-white rounded-tr-sm shadow-lg shadow-amber-950/15'
                    }`}>
                      <div dangerouslySetInnerHTML={{ __html: parseMarkdown(msg.text) }} />
                    </div>
                    
                    <span className={`text-[9px] text-slate-500 font-bold uppercase tracking-wider ${
                      isBot ? 'text-left pl-1' : 'text-right pr-1'
                    }`}>
                      {msg.time}
                    </span>
                  </div>
                </motion.div>
              );
            })}

            {/* Typing Indicator */}
            {typing && (
              <motion.div 
                initial={{ opacity: 0, y: 5 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex gap-3.5 self-start"
              >
                <div className="w-8.5 h-8.5 rounded-xl bg-slate-900 border border-white/[0.06] text-cyan-400 flex items-center justify-center shrink-0 shadow-sm">
                  <Bot size={15} />
                </div>
                <div className="p-4 rounded-2xl bg-slate-900/60 border border-white/[0.05] rounded-tl-sm shadow-md flex items-center gap-1.5 h-11 px-5">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400 opacity-60 animate-bounce [animation-delay:-0.3s]" />
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400 opacity-60 animate-bounce [animation-delay:-0.15s]" />
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400 opacity-60 animate-bounce" />
                </div>
              </motion.div>
            )}
          </AnimatePresence>
          <div ref={messagesEndRef} />
        </div>

        {/* Suggestion prompts strip */}
        <div className="px-5 py-3 border-t border-white/[0.03] bg-black/10 flex flex-wrap gap-2 shrink-0">
          {QUICK_PROMPTS.map((p) => (
            <button
              key={p.text}
              onClick={() => sendMessage(p.text)}
              className="px-3 py-1.5 rounded-lg bg-white/[0.01] hover:bg-amber-500/5 border border-white/[0.04] hover:border-amber-500/25 text-[10px] font-bold text-slate-400 hover:text-amber-400 cursor-pointer transition-all inline-flex items-center gap-1"
            >
              <MessageSquare size={10} />
              <span>{p.label}</span>
            </button>
          ))}
        </div>

        {/* Chat input block */}
        <div className="p-4 border-t border-white/[0.04] bg-slate-950/70 shrink-0">
          <div className="flex gap-2">
            <input
              id="tradebot-input"
              type="text"
              placeholder="Ask TradeBot anything... (e.g. 'analyze TSLA', 'market summary', 'my portfolio')"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  sendMessage(input);
                }
              }}
              className="flex-1 px-4.5 py-3 rounded-xl bg-slate-900 border border-white/[0.08] text-white text-xs placeholder:text-slate-650 focus:outline-none focus:border-amber-500 focus:bg-slate-900 transition-all font-sans"
            />
            <button
              onClick={() => sendMessage(input)}
              disabled={!input.trim()}
              className="px-5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white font-bold text-xs tracking-wide shadow-lg shadow-amber-900/10 active:scale-[0.98] transition-all disabled:opacity-40 disabled:pointer-events-none flex items-center justify-center shrink-0 cursor-pointer"
            >
              <Send size={13} />
            </button>
          </div>
          
          <div className="flex items-center gap-1.5 text-slate-500 text-[9px] font-bold tracking-wider mt-3 px-1 uppercase">
            <Lightbulb size={11} className="text-yellow-500" />
            <span>Virtual assistant provides pedagogical feedback. All calculations are simulated.</span>
          </div>
        </div>

      </div>

    </div>
  );
}
