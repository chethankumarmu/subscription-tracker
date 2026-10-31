import React, { useState } from 'react';
import { Customer } from '../../types';
import { Users, Building, Mail, Phone, MapPin, Search } from 'lucide-react';

export const CustomersView: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');

  const [customers, setCustomers] = useState<Customer[]>([
    {
      id: 1,
      companyName: 'Acme Corp',
      contactName: 'Alice Vance',
      billingAddress: '42 MG Road, Bangalore, KA, India',
      phone: '+91 9876543210',
      status: 'ACTIVE',
      gstin: '29ABCDE1234F1Z5',
    },
    {
      id: 2,
      companyName: 'TechNova Global',
      contactName: 'Robert Smith',
      billingAddress: 'Sector 5, Salt Lake, Kolkata, India',
      phone: '+91 9123456789',
      status: 'ACTIVE',
      gstin: '19XYZAB5678C1Z2',
    },
    {
      id: 3,
      companyName: 'QuantumLeap Inc.',
      contactName: 'Deepa Rao',
      billingAddress: 'Hitec City, Hyderabad, India',
      phone: '+91 9988776655',
      status: 'ACTIVE',
      gstin: '36QWEAS9999P1Z8',
    },
  ]);

  const filtered = customers.filter(
    (c) =>
      c.companyName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.contactName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (c.phone && c.phone.includes(searchTerm))
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Customer Directory
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Client company profiles, contact details, GSTIN, and billing addresses.
          </p>
        </div>

        <div style={{ position: 'relative', width: '280px' }}>
          <Search size={15} style={{ position: 'absolute', left: '12px', top: '11px', color: 'var(--text-muted)' }} />
          <input
            type="text"
            placeholder="Search customer, company..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: '100%', paddingLeft: '34px', fontSize: '12px' }}
          />
        </div>
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>Company</th>
              <th style={{ padding: '10px 12px' }}>Contact Person</th>
              <th style={{ padding: '10px 12px' }}>Phone</th>
              <th style={{ padding: '10px 12px' }}>Billing Address</th>
              <th style={{ padding: '10px 12px' }}>GSTIN</th>
              <th style={{ padding: '10px 12px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((c) => (
              <tr key={c.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontWeight: 700 }}>{c.companyName}</td>
                <td style={{ padding: '12px', color: 'var(--text-primary)' }}>{c.contactName}</td>
                <td style={{ padding: '12px', color: 'var(--text-secondary)' }}>{c.phone || '-'}</td>
                <td style={{ padding: '12px', color: 'var(--text-muted)', fontSize: '12px' }}>{c.billingAddress || '-'}</td>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)', fontSize: '12px' }}>{c.gstin || '-'}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--emerald-50)',
                      color: 'var(--emerald-600)',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                    }}
                  >
                    {c.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

