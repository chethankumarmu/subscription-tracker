import React, { useState } from 'react';
import { useAuth, UserRole } from '../../context/AuthContext';
import { ShieldCheck, UserCheck, ArrowRight, Lock, Mail, Building, User, Phone, MapPin } from 'lucide-react';

interface AuthPageProps {
  onSuccess: (role: UserRole) => void;
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const AuthPage: React.FC<AuthPageProps> = ({ onSuccess, onNotification }) => {
  const { login, register, setDemoUser } = useAuth();
  const [mode, setMode] = useState<'LOGIN' | 'REGISTER'>('LOGIN');
  const [roleTab, setRoleTab] = useState<'CUSTOMER' | 'STAFF' | 'ADMIN'>('CUSTOMER');

  // Form states
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [phone, setPhone] = useState('');
  const [billingAddress, setBillingAddress] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      if (mode === 'LOGIN') {
        const user = await login(email, password);
        onNotification('Welcome back!', `Signed in as ${user.fullName} (${user.role})`, 'success');
        onSuccess(user.role);
      } else {
        // Validation check for registration password
        if (password.length <= 8 || !/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/[^A-Za-z0-9]/.test(password)) {
          throw new Error('Password must be more than 8 characters with at least one uppercase, one lowercase, and one special character.');
        }

        const user = await register({
          email,
          password,
          fullName,
          companyName,
          contactName: fullName,
          phone,
          billingAddress,
        });
        onNotification('Registration Successful', `Welcome to Brightloop, ${user.fullName}!`, 'success');
        onSuccess('CUSTOMER');
      }
    } catch (err: any) {
      const msg = err.message || 'Operation failed';
      setError(msg);
      onNotification('Authentication Error', msg, 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleQuickDemo = (role: UserRole) => {
    setDemoUser(role);
    onNotification('Quick Demo Access', `Switched active session to ${role}`, 'info');
    onSuccess(role);
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '32px 20px',
        background: 'radial-gradient(ellipse at top, rgba(99, 102, 241, 0.08), transparent 70%), var(--bg-app)',
      }}
    >
      <div
        className="card"
        style={{
          width: '100%',
          maxWidth: '520px',
          padding: '36px',
          boxShadow: 'var(--shadow-hover)',
          borderRadius: 'var(--radius-card)',
          position: 'relative',
        }}
      >
        {/* Brand Header */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div
            style={{
              width: '44px',
              height: '44px',
              borderRadius: '12px',
              background: 'linear-gradient(135deg, var(--emerald-500), var(--indigo-500))',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffffff',
              fontWeight: 800,
              fontSize: '20px',
              boxShadow: '0 4px 16px rgba(16, 185, 129, 0.35)',
              marginBottom: '12px',
            }}
          >
            ↗
          </div>
          <h1 style={{ fontSize: '26px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Brightloop <span style={{ color: 'var(--indigo-500)' }}>Platform</span>
          </h1>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Subscription Billing, Catalogs, and Lifecycle Management
          </p>
        </div>

        {/* Mode Switcher: Login vs Register */}
        <div
          style={{
            display: 'flex',
            background: 'var(--bg-card-secondary)',
            borderRadius: 'var(--radius-pill)',
            padding: '4px',
            marginBottom: '24px',
          }}
        >
          <button
            type="button"
            onClick={() => {
              setMode('LOGIN');
              setError(null);
            }}
            style={{
              flex: 1,
              border: 'none',
              padding: '9px',
              borderRadius: 'var(--radius-pill)',
              background: mode === 'LOGIN' ? 'var(--bg-card)' : 'transparent',
              color: mode === 'LOGIN' ? 'var(--text-primary)' : 'var(--text-muted)',
              fontWeight: 700,
              fontSize: '13px',
              cursor: 'pointer',
              boxShadow: mode === 'LOGIN' ? 'var(--shadow-sm)' : 'none',
              transition: 'all 150ms ease',
            }}
          >
            Sign In
          </button>
          <button
            type="button"
            onClick={() => {
              setMode('REGISTER');
              setError(null);
            }}
            style={{
              flex: 1,
              border: 'none',
              padding: '9px',
              borderRadius: 'var(--radius-pill)',
              background: mode === 'REGISTER' ? 'var(--bg-card)' : 'transparent',
              color: mode === 'REGISTER' ? 'var(--text-primary)' : 'var(--text-muted)',
              fontWeight: 700,
              fontSize: '13px',
              cursor: 'pointer',
              boxShadow: mode === 'REGISTER' ? 'var(--shadow-sm)' : 'none',
              transition: 'all 150ms ease',
            }}
          >
            Register as Customer
          </button>
        </div>

        {/* Error Alert */}
        {error && (
          <div
            style={{
              padding: '12px 16px',
              borderRadius: 'var(--radius-inner)',
              background: '#fef2f2',
              border: '1px solid #fecaca',
              color: '#b91c1c',
              fontSize: '12px',
              fontWeight: 600,
              marginBottom: '20px',
            }}
          >
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {mode === 'REGISTER' && (
            <>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
                  Full Name / Contact Person
                </label>
                <div style={{ position: 'relative' }}>
                  <User size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
                  <input
                    type="text"
                    required
                    placeholder="Jane Doe"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    style={{ paddingLeft: '38px', width: '100%' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
                  Company Name (Optional)
                </label>
                <div style={{ position: 'relative' }}>
                  <Building size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
                  <input
                    type="text"
                    placeholder="Acme Innovations Ltd."
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                    style={{ paddingLeft: '38px', width: '100%' }}
                  />
                </div>
              </div>
            </>
          )}

          <div>
            <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
              Email Address
            </label>
            <div style={{ position: 'relative' }}>
              <Mail size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
              <input
                type="email"
                required
                placeholder="name@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                style={{ paddingLeft: '38px', width: '100%' }}
              />
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
              <label style={{ fontSize: '12px', fontWeight: 700 }}>
                Password
              </label>
              {mode === 'REGISTER' && (
                <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>
                  &gt;8 chars, 1 upper, 1 lower, 1 special
                </span>
              )}
            </div>
            <div style={{ position: 'relative' }}>
              <Lock size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
              <input
                type="password"
                required
                placeholder="••••••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={{ paddingLeft: '38px', width: '100%' }}
              />
            </div>
          </div>

          {mode === 'REGISTER' && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
                  Phone Number
                </label>
                <div style={{ position: 'relative' }}>
                  <Phone size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
                  <input
                    type="tel"
                    placeholder="+91 9876543210"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    style={{ paddingLeft: '38px', width: '100%' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
                  Billing City / Address
                </label>
                <div style={{ position: 'relative' }}>
                  <MapPin size={16} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
                  <input
                    type="text"
                    placeholder="Bangalore, India"
                    value={billingAddress}
                    onChange={(e) => setBillingAddress(e.target.value)}
                    style={{ paddingLeft: '38px', width: '100%' }}
                  />
                </div>
              </div>
            </div>
          )}

          <button
            type="submit"
            disabled={submitting}
            className="btn-primary"
            style={{
              padding: '12px',
              fontSize: '14px',
              justifyContent: 'center',
              marginTop: '10px',
              boxShadow: '0 4px 14px rgba(99, 102, 241, 0.4)',
            }}
          >
            {submitting ? 'Please wait...' : mode === 'LOGIN' ? 'Sign In' : 'Create Customer Account'}
            <ArrowRight size={16} />
          </button>
        </form>

        {/* Demo Fast-Switch Buttons */}
        <div style={{ marginTop: '28px', paddingTop: '20px', borderTop: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '12px', textAlign: 'center' }}>
            Instant Demo Account Sign-in
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
            <button
              type="button"
              onClick={() => handleQuickDemo('CUSTOMER')}
              className="btn-secondary"
              style={{ padding: '8px 10px', fontSize: '11px', justifyContent: 'center' }}
            >
              <UserCheck size={13} color="var(--emerald-500)" /> Customer
            </button>
            <button
              type="button"
              onClick={() => handleQuickDemo('STAFF')}
              className="btn-secondary"
              style={{ padding: '8px 10px', fontSize: '11px', justifyContent: 'center' }}
            >
              <UserCheck size={13} color="var(--indigo-500)" /> Staff
            </button>
            <button
              type="button"
              onClick={() => handleQuickDemo('ADMIN')}
              className="btn-secondary"
              style={{ padding: '8px 10px', fontSize: '11px', justifyContent: 'center' }}
            >
              <ShieldCheck size={13} color="#f59e0b" /> Admin
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

