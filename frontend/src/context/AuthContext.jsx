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
    console.log('isAdmin called, user:', user);
    // Se o usuário tem perfil ADMIN, é admin
    if (user?.profile?.name === 'ADMIN') {
      console.log('Admin by profile');
      return true;
    }
    // Se o email é admin@soldiers.com, também é admin (fallback)
    if (user?.email === 'admin@soldiers.com') {
      console.log('Admin by email');
      return true;
    }
    console.log('Not admin');
    return false;
  };

  const hasPermission = (resource, action) => {
    // Admin tem todas as permissões
    if (isAdmin()) {
      return true;
    }
    // Verificar permissões específicas
    if (!user?.permissions) return false;
    return user.permissions.includes(`${resource}:${action}`);
  };

  const canView = (resource) => {
    // Admin pode ver tudo
    if (isAdmin()) {
      return true;
    }
    return hasPermission(resource, 'VIEW') || hasPermission(resource, 'EDIT');
  };

  const canEdit = (resource) => {
    // Admin pode editar tudo
    if (isAdmin()) {
      return true;
    }
    return hasPermission(resource, 'EDIT');
  };

  const getProfileName = () => {
    return user?.profile?.name || 'N/A';
  };

  const getProfileDescription = () => {
    return user?.profile?.description || 'N/A';
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      signIn, 
      signOut, 
      isAdmin, 
      hasPermission,
      canView,
      canEdit,
      getProfileName,
      getProfileDescription,
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