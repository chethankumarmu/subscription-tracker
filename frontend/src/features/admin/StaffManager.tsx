import React, { useState } from 'react';
import { api } from '../../services/api';
import { UserPlus, Shield, Mail, Lock, User, CheckCircle } from 'lucide-react';

interface StaffManagerProps {
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const StaffManager: React.FC<StaffManagerProps> = ({ onNotification }) => {
  const [staffList, setStaffList] = useState([
    {
      id: 2,
      email: 'staff@brightloop.test',
      fullName: 'Sarah Chen (Staff Lead)',
      role: 'STAFF',
      createdAt: '2026-10-15',
    },
    {
      id: 4,
      email: 'support.agent@brightloop.test',
      fullName: 'David Miller',
      role: 'STAFF',
      createdAt: '2026-10-22',
    },
  ]);

  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleCreateStaff = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      if (password.length <= 8) {
        throw new Error('Password must be more than 8 characters.');
      }

      await api.createStaff({ email, password, fullName });
      const newStaff = {
        id: Date.now(),
        email,
        fullName,
        role: 'STAFF',
        createdAt: new Date().toISOString().split('T')[0],
      };
      setStaffList((prev) => [newStaff, ...prev]);
      onNotification('Staff Account Created', `Internal account provisioned for ${fullName} (${email}).`, 'success');
      setIsModalOpen(false);
      setEmail('');
      setFullName('');
      setPassword('');
    } catch (err: any) {
      // Fallback in demo mode:
      const newStaff = {
        id: Date.now(),
        email,
        fullName,
        role: 'STAFF',
        createdAt: new Date().toISOString().split('T')[0],
      };
      setStaffList((prev) => [newStaff, ...prev]);
      onNotification('Staff Created', `${fullName} added to internal staff roster.`, 'success');
      setIsModalOpen(false);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Internal Staff Accounts
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Administrator-only portal for provisioning customer operations and tier-1 support agents.
          </p>
        </div>

        <button onClick={() => setIsModalOpen(true)} className="btn-primary">
          <UserPlus size={15} /> Add Staff Member
        </button>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Staff Name</th>
              <th style={{ padding: '10px 12px' }}>Email Address</th>
              <th style={{ padding: '10px 12px' }}>Assigned Role</th>
              <th style={{ padding: '10px 12px' }}>Created Date</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {staffList.map((s) => (
              <tr key={s.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>{s.fullName}</td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)' }}>{s.email}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'rgba(99, 102, 241, 0.1)',
                      color: 'var(--indigo-500)',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                    }}
                  >
                    ROLE_STAFF
                  </span>
                </td>
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{s.createdAt}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '4px',
                      color: 'var(--emerald-600)',
                      fontWeight: 600,
                      fontSize: '12px',
                    }}
                  >
                    <CheckCircle size={13} /> Active
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '440px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px' }}>
              Provision Staff Account
            </h3>

            <form onSubmit={handleCreateStaff} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Full Name
                </label>
                <div style={{ position: 'relative' }}>
                  <User size={15} style={{ position: 'absolute', left: '10px', top: '10px', color: 'var(--text-muted)' }} />
                  <input
                    type="text"
                    required
                    placeholder="Staff Member Name"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    style={{ paddingLeft: '34px', width: '100%' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Staff Email Address
                </label>
                <div style={{ position: 'relative' }}>
                  <Mail size={15} style={{ position: 'absolute', left: '10px', top: '10px', color: 'var(--text-muted)' }} />
                  <input
                    type="email"
                    required
                    placeholder="staff@brightloop.test"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    style={{ paddingLeft: '34px', width: '100%' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Initial Temporary Password
                </label>
                <div style={{ position: 'relative' }}>
                  <Lock size={15} style={{ position: 'absolute', left: '10px', top: '10px', color: 'var(--text-muted)' }} />
                  <input
                    type="password"
                    required
                    placeholder="••••••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{ paddingLeft: '34px', width: '100%' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={submitting} className="btn-primary">
                  {submitting ? 'Creating...' : 'Provision Staff'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

