export function formatCurrency(
  paise: number,
  currency: 'INR' | 'USD' = 'INR',
  showCents: boolean = true
): string {
  if (paise == null || isNaN(paise)) {
    return currency === 'USD' ? '$0.00' : '₹0.00';
  }

  if (currency === 'USD') {
    const dollars = (paise / 100) / 85.0; // 1 USD ≈ 85 INR
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: showCents ? 2 : 0,
      maximumFractionDigits: showCents ? 2 : 0,
    }).format(dollars);
  }

  const rupees = paise / 100;
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: showCents ? 2 : 0,
    maximumFractionDigits: showCents ? 2 : 0,
  }).format(rupees);
}

export function formatDate(dateStr?: string): string {
  if (!dateStr) return '-';
  try {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  } catch {
    return dateStr;
  }
}

