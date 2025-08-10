import React from 'react';
import { useAuth } from '../context/AuthContext';

const PermissionGate = ({ 
  children, 
  resource, 
  action = 'VIEW', 
  fallback = null,
  requireAdmin = false 
}) => {
  const { isAdmin, hasPermission, canView, canEdit } = useAuth();

  // Se requer admin e não é admin, não mostra
  if (requireAdmin && !isAdmin()) {
    return fallback;
  }

  // Se não tem permissão específica, não mostra
  if (resource && !hasPermission(resource, action)) {
    return fallback;
  }

  // Se é VIEW mas não pode visualizar, não mostra
  if (action === 'VIEW' && !canView(resource)) {
    return fallback;
  }

  // Se é EDIT mas não pode editar, não mostra
  if (action === 'EDIT' && !canEdit(resource)) {
    return fallback;
  }

  return children;
};

export default PermissionGate;
