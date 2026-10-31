import React, { useState, useEffect } from 'react';
import { SupportTicket } from '../../types';
import { api } from '../../services/api';
import { formatDate } from '../../utils/formatters';
import { StatusChip } from '../../components/ui/StatusChip';
import { Headphones, CheckCircle, Clock, AlertTriangle, MessageSquare } from 'lucide-react';

interface SupportQueueProps {
  onNotification: (title: string, message?: string) => void;
}

export const SupportQueue: React.FC<SupportQueueProps> = ({ onNotification }) => {
  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [selectedTicketId, setSelectedTicketId] = useState<number | null>(null);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      setLoading(true);
      const data = await api.getTickets();
      setTickets(data);
    } catch (err) {
      console.error('Failed to load tickets:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (ticket: SupportTicket, newStatus: string) => {
    try {
      await api.updateTicketStatus(ticket.id, newStatus);
      onNotification(
        'Ticket Updated',
        `Ticket ${ticket.number} status set to ${newStatus} in database.`
      );
      loadTickets();
    } catch (err: any) {
      alert(`Update failed: ${err.message}`);
    }
  };

  const filtered = tickets.filter(
    (t) => filter === 'ALL' || t.status === filter
  );

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Staff Support Queue & Chatbot Escalations
          </h1>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Priority tickets routed from customer chatbot sessions and staff portal
          </p>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          {['ALL', 'OPEN', 'IN_PROGRESS', 'RESOLVED'].map((st) => (
            <button
              key={st}
              onClick={() => setFilter(st)}
              style={{
                border: 'none',
                background: filter === st ? 'var(--indigo-500)' : 'var(--bg-card-secondary)',
                color: filter === st ? '#ffffff' : 'var(--text-secondary)',
                padding: '8px 16px',
                borderRadius: 'var(--radius-pill)',
                fontSize: '12px',
                fontWeight: 600,
                cursor: 'pointer',
                transition: 'all 150ms ease',
              }}
            >
              {st}
            </button>
          ))}
        </div>
      </div>

      <div style={{ display: 'grid', gap: '16px' }}>
        {filtered.length === 0 ? (
          <div className="card" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
            No support tickets found for the selected filter.
          </div>
        ) : (
          filtered.map((ticket) => {
            const isExpanded = selectedTicketId === ticket.id;
            return (
              <div
                key={ticket.id}
                className="card"
                onClick={() => setSelectedTicketId(isExpanded ? null : ticket.id)}
                style={{
                  padding: '20px 24px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '16px',
                  cursor: 'pointer',
                  borderLeft:
                    ticket.priority === 'HIGH'
                      ? '4px solid #ef4444'
                      : ticket.status === 'RESOLVED'
                      ? '4px solid var(--emerald-500)'
                      : '4px solid var(--indigo-500)',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
                    <div
                      style={{
                        width: '40px',
                        height: '40px',
                        borderRadius: '50%',
                        background: 'var(--bg-card-secondary)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'var(--indigo-500)',
                      }}
                    >
                      <Headphones size={20} />
                    </div>

                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '4px' }}>
                        <span style={{ fontWeight: 800, fontSize: '14px', color: 'var(--indigo-500)' }}>
                          {ticket.number}
                        </span>
                        <StatusChip status={ticket.status} />
                        {ticket.priority === 'HIGH' && (
                          <span
                            style={{
                              background: '#fee2e2',
                              color: '#b91c1c',
                              fontSize: '11px',
                              fontWeight: 700,
                              padding: '2px 8px',
                              borderRadius: 'var(--radius-pill)',
                            }}
                          >
                            URGENT ESCALATION
                          </span>
                        )}
                      </div>

                      <h4 style={{ fontSize: '15px', fontWeight: 600, marginBottom: '4px' }}>
                        {ticket.subject}
                      </h4>

                      <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                        Filed: {formatDate(ticket.createdAt)} • Customer ID: #{ticket.customerId}
                        {ticket.sessionId && ` • Escalated from Chat Session #${ticket.sessionId}`}
                      </p>
                    </div>
                  </div>

                  {/* Status change actions */}
                  <div style={{ display: 'flex', gap: '8px' }} onClick={(e) => e.stopPropagation()}>
                    {ticket.status !== 'IN_PROGRESS' && ticket.status !== 'RESOLVED' && (
                      <button
                        onClick={() => handleUpdateStatus(ticket, 'IN_PROGRESS')}
                        className="btn-secondary"
                        style={{ padding: '6px 14px', fontSize: '12px' }}
                      >
                        Mark In Progress
                      </button>
                    )}

                    {ticket.status !== 'RESOLVED' && (
                      <button
                        onClick={() => handleUpdateStatus(ticket, 'RESOLVED')}
                        className="btn-emerald"
                        style={{ padding: '6px 14px', fontSize: '12px' }}
                      >
                        <CheckCircle size={14} /> Resolve Ticket
                      </button>
                    )}

                    {ticket.status === 'RESOLVED' && (
                      <button
                        onClick={() => handleUpdateStatus(ticket, 'OPEN')}
                        className="btn-secondary"
                        style={{ padding: '6px 14px', fontSize: '12px' }}
                      >
                        Re-open
                      </button>
                    )}
                  </div>
                </div>

                {/* Expanded Context Details */}
                {isExpanded && (
                  <div
                    style={{
                      background: 'var(--bg-card-secondary)',
                      borderRadius: 'var(--radius-inner)',
                      padding: '16px',
                      fontSize: '13px',
                      marginTop: '8px',
                      borderTop: '1px solid var(--border-color)',
                      animation: 'fadeIn 150ms ease',
                    }}
                    onClick={(e) => e.stopPropagation()}
                  >
                    <div style={{ fontWeight: 700, marginBottom: '8px', color: 'var(--text-primary)' }}>
                      Ticket Audit Information:
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '12px', marginBottom: '12px' }}>
                      <div>
                        <span style={{ color: 'var(--text-muted)', fontSize: '11px', display: 'block' }}>Ticket ID</span>
                        <strong>#{ticket.id}</strong>
                      </div>
                      <div>
                        <span style={{ color: 'var(--text-muted)', fontSize: '11px', display: 'block' }}>Chat Session Reference</span>
                        <strong>{ticket.sessionId ? `Session #${ticket.sessionId}` : 'Direct Portal Submission'}</strong>
                      </div>
                      <div>
                        <span style={{ color: 'var(--text-muted)', fontSize: '11px', display: 'block' }}>Priority Level</span>
                        <strong style={{ color: ticket.priority === 'HIGH' ? '#ef4444' : 'inherit' }}>{ticket.priority}</strong>
                      </div>
                      <div>
                        <span style={{ color: 'var(--text-muted)', fontSize: '11px', display: 'block' }}>Resolution Timestamp</span>
                        <strong>{ticket.resolvedAt ? formatDate(ticket.resolvedAt) : 'Pending Resolution'}</strong>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

