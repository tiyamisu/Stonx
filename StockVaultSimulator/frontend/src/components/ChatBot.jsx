// ============================================================
// ChatBot.jsx — Floating TradeBot AI assistant
// Overhauled with responsive layout, custom message cards,
// suggestion bubbles, and smooth viewport transitions.
// ============================================================

import { useState, useRef, useEffect } from "react";
import { MessageSquareText, X, Send, Bot, ChevronDown, Sparkles } from "lucide-react";
import { getTradeBotResponse } from "../utils/tradeBot";
import { useApp } from "../context/AppContext";
import { motion, AnimatePresence } from "framer-motion";

const INITIAL_MESSAGES = [
  {
    id: 1,
    from: "bot",
    text: "Hi! I'm **TradeBot**, your AI finance assistant!\n\nTry asking me:\n• *\"analyze AAPL\"*\n• *\"my portfolio\"*\n• *\"what is P/E ratio?\"*\n• *\"market summary\"*",
    time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
  },
];

function parseMarkdown(text) {
  return text
    .replace(/\*\*(.*?)\*\*/g, "<strong class='text-white font-bold'>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em class='text-amber-400 font-semibold'>$1</em>")
    .replace(/\n/g, "<br/>");
}

export default function ChatBot() {
  const { state } = useApp();
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState(INITIAL_MESSAGES);
  const [input, setInput] = useState("");
  const [typing, setTyping] = useState(false);
  const [unread, setUnread] = useState(0);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (open) {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, open]);

  useEffect(() => {
    if (open) setUnread(0);
  }, [open]);

  function sendMessage(text) {
    if (!text.trim()) return;

    const userMsg = {
      id: Date.now(),
      from: "user",
      text: text.trim(),
      time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setTyping(true);

    setTimeout(() => {
      const response = getTradeBotResponse(text, state);
      const botMsg = {
        id: Date.now() + 1,
        from: "bot",
        text: response,
        time: new Date().toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit" }),
      };
      setMessages((prev) => [...prev, botMsg]);
      setTyping(false);
      if (!open) setUnread((n) => n + 1);
    }, 500 + Math.random() * 400);
  }

  const QUICK_ACTIONS = ["analyze AAPL", "market summary", "my portfolio", "top gainers"];

  return (
    <>
      {/* Floating Action Button */}
      <button
        onClick={() => setOpen(!open)}
        className={`fixed bottom-6 right-6 w-13 h-13 rounded-full flex items-center justify-center cursor-pointer transition-all z-50 shadow-xl ${
          open 
            ? "bg-slate-900 border border-white/[0.08] hover:border-red-500/35 text-slate-400 hover:text-white" 
            : "bg-gradient-to-tr from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white shadow-amber-900/25"
        }`}
        aria-label="Open TradeBot"
      >
        {open ? <X size={20} /> : <MessageSquareText size={20} />}
        {!open && unread > 0 && (
          <span className="absolute -top-1.5 -right-1.5 bg-red-500 text-white text-[9px] font-black w-5 h-5 rounded-full flex items-center justify-center border-2 border-[#07090d] animate-pulse">
            {unread}
          </span>
        )}
      </button>

      {/* Floating Chat Window Panel */}
      <AnimatePresence>
        {open && (
          <motion.div 
            initial={{ opacity: 0, scale: 0.9, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.9, y: 20 }}
            transition={{ type: "spring", stiffness: 180 }}
            className="fixed bottom-22 right-6 w-[340px] max-h-[500px] h-[480px] bg-slate-950/80 border border-white/[0.08] backdrop-blur-2xl rounded-3xl flex flex-col justify-between overflow-hidden z-40 shadow-[0_24px_60px_rgba(0,0,0,0.7)]"
          >
            {/* Header */}
            <div className="flex items-center justify-between p-4 bg-slate-900/60 border-b border-white/[0.04] shrink-0">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-amber-500 to-orange-500 flex items-center justify-center text-white shrink-0 shadow-md">
                  <Sparkles size={14} />
                </div>
                <div>
                  <span className="text-xs font-black text-white block">TradeBot Copilot</span>
                  <span className="text-[9px] text-emerald-400 font-bold block uppercase tracking-wider mt-0.5 leading-none">Online Assistant</span>
                </div>
              </div>
              <button 
                onClick={() => setOpen(false)}
                className="p-1 rounded-md text-slate-500 hover:text-white transition-colors cursor-pointer"
              >
                <ChevronDown size={16} />
              </button>
            </div>

            {/* Scrollable messages area */}
            <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-3 min-h-0">
              {messages.map((msg) => {
                const isBot = msg.from === "bot";
                return (
                  <div 
                    key={msg.id}
                    className={`flex gap-2.5 max-w-[85%] ${
                      isBot ? 'self-start' : 'self-end flex-row-reverse'
                    }`}
                  >
                    <div className={`p-3 rounded-2xl text-[11px] leading-relaxed shadow-sm ${
                      isBot 
                        ? 'bg-slate-900/70 border border-white/[0.05] text-slate-300 rounded-tl-sm' 
                        : 'bg-amber-500/10 border border-amber-500/20 text-white rounded-tr-sm'
                    }`}>
                      <div dangerouslySetInnerHTML={{ __html: parseMarkdown(msg.text) }} />
                      <span className="text-[8px] text-slate-500 font-bold uppercase tracking-wider mt-1.5 block text-right pr-0.5">
                        {msg.time}
                      </span>
                    </div>
                  </div>
                );
              })}

              {/* Typing bounce state */}
              {typing && (
                <div className="flex gap-2.5 self-start">
                  <div className="p-3 rounded-2xl bg-slate-900/70 border border-white/[0.05] rounded-tl-sm flex items-center gap-1">
                    <span className="w-1 h-1 rounded-full bg-amber-400 opacity-60 animate-bounce [animation-delay:-0.3s]" />
                    <span className="w-1 h-1 rounded-full bg-amber-400 opacity-60 animate-bounce [animation-delay:-0.15s]" />
                    <span className="w-1 h-1 rounded-full bg-amber-400 opacity-60 animate-bounce" />
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Quick action chips */}
            <div className="p-3 border-t border-white/[0.03] bg-black/15 flex flex-wrap gap-1.5 shrink-0">
              {QUICK_ACTIONS.map((action) => (
                <button
                  key={action}
                  onClick={() => sendMessage(action)}
                  className="px-2.5 py-1 rounded-md bg-white/[0.01] hover:bg-amber-500/5 border border-white/[0.04] hover:border-amber-500/20 text-[9px] font-bold text-slate-400 hover:text-amber-400 cursor-pointer transition-all"
                >
                  {action}
                </button>
              ))}
            </div>

            {/* Input area */}
            <div className="p-3 border-t border-white/[0.04] bg-slate-950/70 flex gap-2 shrink-0">
              <input
                type="text"
                placeholder="Ask TradeBot..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage(input);
                  }
                }}
                className="flex-1 px-3.5 py-2.5 rounded-xl bg-slate-900 border border-white/[0.06] text-white text-[11px] placeholder:text-slate-650 focus:outline-none focus:border-amber-500 focus:bg-slate-900 transition-all font-sans"
              />
              <button
                onClick={() => sendMessage(input)}
                disabled={!input.trim()}
                className="w-10 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white flex items-center justify-center shadow-lg shadow-amber-900/10 cursor-pointer disabled:opacity-40 disabled:pointer-events-none transition-all active:scale-95 shrink-0"
              >
                <Send size={11} />
              </button>
            </div>

          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
