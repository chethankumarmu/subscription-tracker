import React, { useState, useRef, useEffect } from 'react';
import { api } from '../../services/api';
import { ChatMessage } from '../../types';
import {
  MessageSquare,
  X,
  Send,
  Sparkles,
  Bot,
  User,
  ShieldAlert,
  Headphones,
} from 'lucide-react';

interface ChatbotDrawerProps {
  onNotification: (title: string, message?: string) => void;
  isOpenControlled?: boolean;
  onCloseControlled?: () => void;
}

export const ChatbotDrawer: React.FC<ChatbotDrawerProps> = ({
  onNotification,
  isOpenControlled,
  onCloseControlled,
}) => {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const isOpen = isOpenControlled !== undefined ? isOpenControlled : internalIsOpen;
  const setIsOpen = (open: boolean) => {
    setInternalIsOpen(open);
    if (!open && onCloseControlled) onCloseControlled();
  };
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      sender: 'BOT',
      text: "Hello! I'm your Brightloop Virtual Assistant. How can I help you today with your subscriptions, invoices, or plans?",
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
  ]);
  const [inputText, setInputText] = useState('');
  const [sessionId, setSessionId] = useState<number | undefined>();
  const [sending, setSending] = useState(false);
  const [suggestions, setSuggestions] = useState<string[]>([
    'How do I pause my subscription?',
    'What payment methods are supported?',
    'Where can I find tax invoices?',
    'Connect with human agent',
  ]);

  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages, isOpen]);

  const handleSend = async (textToSend?: string) => {
    const text = (textToSend || inputText).trim();
    if (!text || sending) return;

    const userMsg: ChatMessage = {
      sender: 'CUSTOMER',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputText('');
    setSending(true);

    try {
      const res = await api.sendChatMessage({
        sessionId,
        customerId: 1, // Acme Corp default
        message: text,
      });

      setSessionId(res.sessionId);
      if (res.suggestions && res.suggestions.length > 0) {
        setSuggestions(res.suggestions);
      }

      const botMsg: ChatMessage = {
        sender: res.sender,
        text: res.reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        escalated: res.escalated,
        ticketNumber: res.ticketNumber,
      };

      setMessages((prev) => [...prev, botMsg]);

      if (res.escalated) {
        onNotification(
          'Support Ticket Created',
          `Ticket ${res.ticketNumber} created in PostgreSQL database and assigned to staff queue.`
        );
      }
    } catch (err: any) {
      setMessages((prev) => [
        ...prev,
        {
          sender: 'BOT',
          text: `Sorry, I encountered an issue connecting to support service: ${err.message}`,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        },
      ]);
    } finally {
      setSending(false);
    }
  };

  return (
    <>
      {/* Floating Launcher Button */}
      <div
        style={{
          position: 'fixed',
          bottom: '24px',
          right: '24px',
          zIndex: 900,
        }}
      >
        <button
          onClick={() => setIsOpen(!isOpen)}
          style={{
            width: '56px',
            height: '56px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--indigo-500), var(--violet-600))',
            color: '#ffffff',
            border: 'none',
            boxShadow: '0 8px 24px rgba(99, 102, 241, 0.45)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            transition: 'transform 200ms ease',
            position: 'relative',
          }}
          onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.08)')}
          onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
        >
          {isOpen ? <X size={22} /> : <MessageSquare size={22} />}
          <span
            style={{
              position: 'absolute',
              top: '2px',
              right: '2px',
              width: '12px',
              height: '12px',
              background: 'var(--emerald-500)',
              borderRadius: '50%',
              border: '2px solid #ffffff',
            }}
          />
        </button>
      </div>

      {/* Floating Chat Drawer */}
      {isOpen && (
        <div
          className="floating-pill"
          style={{
            position: 'fixed',
            bottom: '90px',
            right: '24px',
            width: '380px',
            height: '520px',
            zIndex: 900,
            background: 'var(--bg-card)',
            borderRadius: '24px',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
            boxShadow: '0 20px 40px rgba(0, 0, 0, 0.25)',
            border: '1px solid var(--border-color)',
            animation: 'slideUp 200ms cubic-bezier(0.16, 1, 0.3, 1)',
          }}
        >
          {/* Header */}
          <div
            style={{
              padding: '16px 20px',
              background: 'linear-gradient(135deg, var(--indigo-500), var(--violet-600))',
              color: '#ffffff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '50%',
                  background: 'rgba(255, 255, 255, 0.2)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <Sparkles size={16} />
              </div>
              <div>
                <h4 style={{ fontSize: '14px', fontWeight: 700 }}>Brightloop Assistant</h4>
                <p style={{ fontSize: '11px', opacity: 0.85 }}>Connected to PostgreSQL FAQs</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              style={{
                border: 'none',
                background: 'transparent',
                color: '#ffffff',
                cursor: 'pointer',
              }}
            >
              <X size={18} />
            </button>
          </div>

          {/* Messages Area */}
          <div
            style={{
              flex: 1,
              padding: '16px',
              overflowY: 'auto',
              display: 'flex',
              flexDirection: 'column',
              gap: '12px',
            }}
          >
            {messages.map((m, idx) => {
              const isCust = m.sender === 'CUSTOMER';
              return (
                <div
                  key={idx}
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: isCust ? 'flex-end' : 'flex-start',
                  }}
                >
                  <div
                    style={{
                      maxWidth: '82%',
                      padding: '10px 14px',
                      borderRadius: isCust ? '16px 16px 2px 16px' : '16px 16px 16px 2px',
                      background: isCust ? 'var(--indigo-500)' : 'var(--bg-card-secondary)',
                      color: isCust ? '#ffffff' : 'var(--text-primary)',
                      fontSize: '13px',
                      lineHeight: '1.4',
                      boxShadow: 'var(--shadow-sm)',
                    }}
                  >
                    {m.text}

                    {m.escalated && m.ticketNumber && (
                      <div
                        style={{
                          marginTop: '8px',
                          paddingTop: '8px',
                          borderTop: '1px solid rgba(255, 255, 255, 0.2)',
                          display: 'flex',
                          alignItems: 'center',
                          gap: '6px',
                          fontSize: '11px',
                          fontWeight: 700,
                        }}
                      >
                        <Headphones size={13} />
                        Priority Ticket: #{m.ticketNumber}
                      </div>
                    )}
                  </div>
                  <span style={{ fontSize: '10px', color: 'var(--text-muted)', marginTop: '4px', marginInline: '4px' }}>
                    {m.timestamp}
                  </span>
                </div>
              );
            })}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Suggestion Chips */}
          <div
            style={{
              padding: '8px 12px',
              background: 'var(--bg-card-secondary)',
              borderTop: '1px solid var(--border-color)',
              display: 'flex',
              gap: '6px',
              overflowX: 'auto',
            }}
          >
            {suggestions.map((chip, idx) => (
              <button
                key={idx}
                onClick={() => handleSend(chip)}
                style={{
                  border: '1px solid var(--border-color)',
                  background: 'var(--bg-card)',
                  color: 'var(--text-secondary)',
                  padding: '4px 10px',
                  borderRadius: 'var(--radius-pill)',
                  fontSize: '11px',
                  fontWeight: 600,
                  whiteSpace: 'nowrap',
                  cursor: 'pointer',
                }}
              >
                {chip}
              </button>
            ))}
          </div>

          {/* Input Area */}
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            style={{
              padding: '12px',
              borderTop: '1px solid var(--border-color)',
              display: 'flex',
              gap: '8px',
            }}
          >
            <input
              type="text"
              placeholder="Ask anything or request agent..."
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              style={{
                flex: 1,
                padding: '8px 14px',
                borderRadius: 'var(--radius-pill)',
                fontSize: '13px',
              }}
            />
            <button
              type="submit"
              disabled={sending}
              style={{
                width: '38px',
                height: '38px',
                borderRadius: '50%',
                border: 'none',
                background: 'var(--indigo-500)',
                color: '#ffffff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
              }}
            >
              <Send size={15} />
            </button>
          </form>
        </div>
      )}
    </>
  );
};

