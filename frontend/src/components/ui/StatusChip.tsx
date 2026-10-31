import React from 'react';

interface StatusChipProps {
  status: string;
}

export const StatusChip: React.FC<StatusChipProps> = ({ status }) => {
  const normStatus = (status || 'draft').toLowerCase().replace('-', '_');

  return (
    <span className={`status-chip ${normStatus}`}>
      <span className="status-dot" />
      {status}
    </span>
  );
};

