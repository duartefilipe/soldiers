import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';

const AuthContext = createContext({});

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userData = localStorage.getItem('user');
    
    if (userData) {
      setUser(JSON.parse(userData));
    }
    
    setLoading(false);
  }, []);

  const signIn = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { status, user } = response.data;
      
      if (status === 'SUCCESS') {
        localStorage.setItem('user', JSON.stringify(user));
        setUser(user);
        return { success: true };
      } else {
        return { 
          success: false, 
          error: 'Login falhou' 
        };
      }
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Erro ao fazer login' 
      };
    }
  };

  const signOut = () => {
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = () => {
    return user?.role === 'ADMIN';
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      signIn, 
      signOut, 
      isAdmin, 
      loading,
      isAuthenticated: !!user 
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
} 