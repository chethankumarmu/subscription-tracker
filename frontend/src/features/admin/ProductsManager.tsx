import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { formatCurrency } from '../../utils/formatters';
import { Package, Plus, Edit2, Check, X } from 'lucide-react';

interface ProductsManagerProps {
  currency: 'INR' | 'USD';
  onNotification: (title: string, msg?: string, type?: 'success' | 'error' | 'info') => void;
}

export const ProductsManager: React.FC<ProductsManagerProps> = ({ currency, onNotification }) => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const [products, setProducts] = useState<any[]>([
    {
      id: 1,
      sku: 'ODOO-BASE',
      name: 'Odoo Business Suite',
      productType: 'SERVICE',
      salesPricePaise: 149900,
      costPricePaise: 80000,
      marginPaise: 69900,
      notes: 'Standard enterprise business suite base package',
    },
    {
      id: 2,
      sku: 'STORAGE-ADDON',
      name: 'Cloud Storage 500GB',
      productType: 'SERVICE',
      salesPricePaise: 49900,
      costPricePaise: 20000,
      marginPaise: 29900,
      notes: 'High-speed encrypted cloud storage quota',
    },
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<any | null>(null);

  // Form states
  const [sku, setSku] = useState('');
  const [name, setName] = useState('');
  const [productType, setProductType] = useState('SERVICE');
  const [salesPrice, setSalesPrice] = useState(1499);
  const [costPrice, setCostPrice] = useState(800);
  const [notes, setNotes] = useState('');

  const openCreateModal = () => {
    setEditingProduct(null);
    setSku(`PROD-${Math.floor(Math.random() * 900) + 100}`);
    setName('');
    setProductType('SERVICE');
    setSalesPrice(999);
    setCostPrice(500);
    setNotes('');
    setIsModalOpen(true);
  };

  const openEditModal = (p: any) => {
    setEditingProduct(p);
    setSku(p.sku);
    setName(p.name);
    setProductType(p.productType);
    setSalesPrice(p.salesPricePaise / 100);
    setCostPrice((p.costPricePaise || 0) / 100);
    setNotes(p.notes || '');
    setIsModalOpen(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    const salesPricePaise = Math.round(salesPrice * 100);
    const costPricePaise = Math.round(costPrice * 100);
    const marginPaise = salesPricePaise - costPricePaise;

    if (editingProduct) {
      setProducts((prev) =>
        prev.map((p) =>
          p.id === editingProduct.id
            ? { ...p, sku, name, productType, salesPricePaise, costPricePaise, marginPaise, notes }
            : p
        )
      );
      onNotification('Product Updated', `${name} updated successfully.`, 'success');
    } else {
      const newP = {
        id: Date.now(),
        sku,
        name,
        productType,
        salesPricePaise,
        costPricePaise,
        marginPaise,
        notes,
      };
      setProducts((prev) => [...prev, newP]);
      onNotification('Product Created', `${name} added to catalog.`, 'success');
    }

    setIsModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, letterSpacing: '-0.02em' }}>
            Products &amp; Add-ons Catalog
          </h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginTop: '4px' }}>
            Manage sellable goods, recurring licenses, and base service line items.
          </p>
        </div>

        {isAdmin && (
          <button onClick={openCreateModal} className="btn-primary">
            <Plus size={15} /> Add Product
          </button>
        )}
      </div>

      <div className="card" style={{ padding: '24px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)', textAlign: 'left' }}>
              <th style={{ padding: '10px 12px' }}>SKU</th>
              <th style={{ padding: '10px 12px' }}>Product Name</th>
              <th style={{ padding: '10px 12px' }}>Type</th>
              <th style={{ padding: '10px 12px' }}>Sales Price</th>
              {/* Cost Price and Margin: Strictly ADMIN ONLY */}
              {isAdmin && <th style={{ padding: '10px 12px' }}>Cost Price</th>}
              {isAdmin && <th style={{ padding: '10px 12px' }}>Gross Margin</th>}
              <th style={{ padding: '10px 12px' }}>Notes</th>
              {isAdmin && <th style={{ padding: '10px 12px', textAlign: 'right' }}>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                <td style={{ padding: '12px', fontFamily: 'var(--font-mono)', fontWeight: 700 }}>{p.sku}</td>
                <td style={{ padding: '12px', fontWeight: 600 }}>{p.name}</td>
                <td style={{ padding: '12px' }}>
                  <span
                    style={{
                      background: 'var(--bg-card-secondary)',
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-pill)',
                      fontSize: '11px',
                    }}
                  >
                    {p.productType}
                  </span>
                </td>
                <td style={{ padding: '12px', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {formatCurrency(p.salesPricePaise, currency)}
                </td>
                {/* Cost price & Margin visible to ADMIN only */}
                {isAdmin && (
                  <td style={{ padding: '12px', color: 'var(--text-muted)' }}>
                    {p.costPricePaise != null ? formatCurrency(p.costPricePaise, currency) : 'N/A'}
                  </td>
                )}
                {isAdmin && (
                  <td style={{ padding: '12px', color: 'var(--emerald-600)', fontWeight: 700 }}>
                    {p.marginPaise != null ? formatCurrency(p.marginPaise, currency) : 'N/A'}
                  </td>
                )}
                <td style={{ padding: '12px', color: 'var(--text-muted)' }}>{p.notes || '-'}</td>
                {isAdmin && (
                  <td style={{ padding: '12px', textAlign: 'right' }}>
                    <button
                      onClick={() => openEditModal(p)}
                      className="btn-secondary"
                      style={{ padding: '5px 10px', fontSize: '11px' }}
                    >
                      <Edit2 size={12} /> Edit
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Admin Modal for Create/Edit Product */}
      {isModalOpen && isAdmin && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '480px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px' }}>
              {editingProduct ? 'Edit Product' : 'Create New Product'}
            </h3>

            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Product SKU
                </label>
                <input
                  type="text"
                  required
                  value={sku}
                  onChange={(e) => setSku(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Product Name
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Sales Price (₹ / $)
                  </label>
                  <input
                    type="number"
                    required
                    min="0"
                    step="0.01"
                    value={salesPrice}
                    onChange={(e) => setSalesPrice(parseFloat(e.target.value) || 0)}
                    style={{ width: '100%' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                    Cost Price (Admin Only)
                  </label>
                  <input
                    type="number"
                    required
                    min="0"
                    step="0.01"
                    value={costPrice}
                    onChange={(e) => setCostPrice(parseFloat(e.target.value) || 0)}
                    style={{ width: '100%' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: 700, marginBottom: '4px' }}>
                  Internal Notes
                </label>
                <textarea
                  rows={2}
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  style={{ width: '100%' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  {editingProduct ? 'Save Changes' : 'Create Product'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

