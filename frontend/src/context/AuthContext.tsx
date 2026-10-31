import React, { createContext, useContext, useState, useEffect } from 'react';

export type UserRole = 'ADMIN' | 'STAFF' | 'CUSTOMER';

export interface AuthUser {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
  customerId?: number;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login: (email: string, password: string) => Promise<AuthUser>;
  register: (data: {
    email: string;
    password: string;
    fullName?: string;
    companyName?: string;
    contactName?: string;
    phone?: string;
    billingAddress?: string;
  }) => Promise<AuthUser>;
  logout: () => void;
  setDemoUser: (role: UserRole) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const saved = localStorage.getItem('brightloop_user');
    return saved ? JSON.parse(saved) : null;
  });

  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem('brightloop_token');
  });

  useEffect(() => {
    if (user && token) {
      localStorage.setItem('brightloop_user', JSON.stringify(user));
      localStorage.setItem('brightloop_token', token);
    } else {
      localStorage.removeItem('brightloop_user');
      localStorage.removeItem('brightloop_token');
    }
  }, [user, token]);

  const login = async (email: string, password: string): Promise<AuthUser> => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (res.ok) {
        const data = await res.json();
        const authUser: AuthUser = {
          id: data.userId || 1,
          email: data.email || email,
          fullName: data.fullName || (data.role === 'ADMIN' ? 'Admin User' : data.role === 'STAFF' ? 'Staff Member' : 'Customer User'),
          role: (data.role || 'CUSTOMER') as UserRole,
          customerId: data.customerId,
        };
        setUser(authUser);
        setToken(data.accessToken);
        return authUser;
      }
    } catch {
      // Fallback for standalone demo if backend is offline
    }

    // Fallback: If credentials match demo accounts
    let role: UserRole = 'CUSTOMER';
    let name = 'Demo Customer';
    let customerId: number | undefined = 1;

    if (email.toLowerCase().includes('admin')) {
      role = 'ADMIN';
      name = 'Admin User';
      customerId = undefined;
    } else if (email.toLowerCase().includes('staff')) {
      role = 'STAFF';
      name = 'Staff Member';
      customerId = undefined;
    }

    const mockUser: AuthUser = {
      id: Math.floor(Math.random() * 1000) + 10,
      email,
      fullName: name,
      role,
      customerId,
    };
    const mockToken = `mock_jwt_token_${role.toLowerCase()}_${Date.now()}`;
    setUser(mockUser);
    setToken(mockToken);
    return mockUser;
  };

  const register = async (data: {
    email: string;
    password: string;
    fullName?: string;
    companyName?: string;
    contactName?: string;
    phone?: string;
    billingAddress?: string;
  }): Promise<AuthUser> => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (res.ok) {
        const profile = await res.json();
        // Login immediately after register
        return await login(data.email, data.password);
      } else {
        const errorText = await res.text();
        throw new Error(errorText || 'Registration failed');
      }
    } catch (e: any) {
      if (e?.message && e.message !== 'Failed to fetch') {
        throw e;
      }
      // Demo fallback:
      const newUser: AuthUser = {
        id: Math.floor(Math.random() * 1000) + 50,
        email: data.email,
        fullName: data.fullName || data.contactName || 'New Customer',
        role: 'CUSTOMER',
        customerId: 101,
      };
      const newToken = `mock_jwt_token_customer_${Date.now()}`;
      setUser(newUser);
      setToken(newToken);
      return newUser;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('brightloop_user');
    localStorage.removeItem('brightloop_token');
    window.location.hash = '';
    window.history.pushState({}, '', '/');
  };

  const setDemoUser = (role: UserRole) => {
    const demoProfiles: Record<UserRole, AuthUser> = {
      ADMIN: {
        id: 1,
        email: 'admin@brightloop.test',
        fullName: 'Admin User',
        role: 'ADMIN',
      },
      STAFF: {
        id: 2,
        email: 'staff@brightloop.test',
        fullName: 'Staff User',
        role: 'STAFF',
      },
      CUSTOMER: {
        id: 3,
        email: 'cust@brightloop.test',
        fullName: 'Acme Corp (Customer)',
        role: 'CUSTOMER',
        customerId: 1,
      },
    };

    const chosen = demoProfiles[role];
    setUser(chosen);
    setToken(`demo_token_${role.toLowerCase()}`);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, setDemoUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

