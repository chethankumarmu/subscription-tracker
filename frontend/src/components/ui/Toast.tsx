import React, { useEffect } from 'react';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info';
  title: string;
  message?: string;
}

interface ToastProps {
  toasts: ToastMessage[];
  onDismiss: (id: string) => void;
}

export const ToastContainer: React.FC<ToastProps> = ({ toasts, onDismiss }) => {
  return (
    <div
      style={{
        position: 'fixed',
        bottom: '24px',
        right: '24px',
        zIndex: 9999,
        display: 'flex',
        flexDirection: 'column',
        gap: '10px',
        pointerEvents: 'none',
      }}
    >
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} onDismiss={onDismiss} />
      ))}
    </div>
  );
};

const ToastItem: React.FC<{ toast: ToastMessage; onDismiss: (id: string) => void }> = ({
  toast,
  onDismiss,
}) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onDismiss(toast.id);
    }, 4000);
    return () => clearTimeout(timer);
  }, [toast.id, onDismiss]);

  const isSuccess = toast.type === 'success';

  return (
    <div
      className="floating-pill"
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        padding: '12px 18px',
        minWidth: '280px',
        maxWidth: '400px',
        background: 'var(--bg-card)',
        border: `1px solid ${isSuccess ? 'rgba(16, 185, 129, 0.4)' : 'rgba(239, 68, 68, 0.4)'}`,
        boxShadow: isSuccess
          ? '0 10px 25px -5px rgba(16, 185, 129, 0.3)'
          : '0 10px 25px -5px rgba(239, 68, 68, 0.3)',
        pointerEvents: 'auto',
      }}
    >
      {isSuccess ? (
        <CheckCircle2 size={18} color="var(--emerald-500)" />
      ) : (
        <AlertCircle size={18} color="#ef4444" />
      )}
      <div style={{ flex: 1 }}>
        <p style={{ fontWeight: 600, fontSize: '13px' }}>{toast.title}</p>
        {toast.message && (
          <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
            {toast.message}
          </p>
        )}
      </div>
      <button
        onClick={() => onDismiss(toast.id)}
        style={{
          border: 'none',
          background: 'transparent',
          cursor: 'pointer',
          color: 'var(--text-muted)',
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <X size={14} />
      </button>
    </div>
  );
};
