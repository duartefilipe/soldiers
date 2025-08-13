import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import PermissionGate from '../components/PermissionGate';

const Profiles = () => {
  const { isAdmin } = useAuth();
  const [profiles, setProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProfile, setEditingProfile] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    active: true,
    permissions: []
  });

  const availableResources = [
    { key: 'DASHBOARD', label: 'Dashboard' },
    { key: 'USERS', label: 'Usuários' },
    { key: 'PRODUCTS', label: 'Produtos' },
    { key: 'SALES', label: 'Vendas' },
    { key: 'TRIPS', label: 'Viagens' },
    { key: 'NEWS', label: 'Notícias' },
    { key: 'BUDGET', label: 'Orçamento' },
    { key: 'GAMES', label: 'Jogos' }
  ];

  const availableActions = [
    { key: 'VIEW', label: 'Visualizar' },
    { key: 'EDIT', label: 'Editar' }
  ];

  useEffect(() => {
    loadProfiles();
  }, []);

  const loadProfiles = async () => {
    try {
      const response = await api.get('/profiles');
      setProfiles(response.data);
    } catch (error) {
      console.error('Erro ao carregar perfis:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingProfile) {
        await api.put(`/profiles/${editingProfile.id}`, formData);
      } else {
        await api.post('/profiles', formData);
      }
      setShowForm(false);
      setEditingProfile(null);
      resetForm();
      loadProfiles();
    } catch (error) {
      console.error('Erro ao salvar perfil:', error);
    }
  };

  const handleEdit = async (profile) => {
    try {
      // Carregar permissões do perfil
      const response = await api.get(`/profiles/${profile.id}`);
      const profileWithPermissions = response.data;
      
      setEditingProfile(profileWithPermissions);
      setFormData({
        name: profileWithPermissions.name,
        description: profileWithPermissions.description,
        active: profileWithPermissions.active,
        permissions: profileWithPermissions.permissions?.map(p => ({
          resource: p.resource,
          action: p.action,
          active: p.active
        })) || []
      });
      setShowForm(true);
    } catch (error) {
      console.error('Erro ao carregar permissões do perfil:', error);
      // Fallback para o perfil sem permissões
      setEditingProfile(profile);
      setFormData({
        name: profile.name,
        description: profile.description,
        active: profile.active,
        permissions: []
      });
      setShowForm(true);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar este perfil?')) {
      try {
        await api.delete(`/profiles/${id}`);
        loadProfiles();
      } catch (error) {
        console.error('Erro ao deletar perfil:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      active: true,
      permissions: []
    });
  };

  const togglePermission = (resource, action) => {
    const permissionKey = `${resource}:${action}`;
    const existingPermission = formData.permissions.find(
      p => p.resource === resource && p.action === action
    );

    if (existingPermission) {
      setFormData(prev => ({
        ...prev,
        permissions: prev.permissions.filter(
          p => !(p.resource === resource && p.action === action)
        )
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        permissions: [...prev.permissions, { resource, action, active: true }]
      }));
    }
  };

  const hasPermission = (profile, resource, action) => {
    return profile.permissions?.some(
      p => p.resource === resource && p.action === action && p.active
    );
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Carregando...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Gerenciar Perfis</h1>
        <PermissionGate resource="USERS" action="EDIT" requireAdmin={true}>
          <button
            onClick={() => setShowForm(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            Novo Perfil
          </button>
        </PermissionGate>
      </div>

      {showForm && (
        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
          <h2 className="text-xl font-semibold mb-4">
            {editingProfile ? 'Editar Perfil' : 'Novo Perfil'}
          </h2>
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nome
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descrição
                </label>
                <input
                  type="text"
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="mb-4">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={formData.active}
                  onChange={(e) => setFormData({...formData, active: e.target.checked})}
                  className="mr-2"
                />
                <span className="text-sm font-medium text-gray-700">Ativo</span>
              </label>
            </div>

            <div className="mb-4">
              <h3 className="text-lg font-medium text-gray-700 mb-2">Permissões</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {availableResources.map(resource => (
                  <div key={resource.key} className="border rounded-lg p-3">
                    <h4 className="font-medium text-gray-700 mb-2">{resource.label}</h4>
                    {availableActions.map(action => (
                      <label key={action.key} className="flex items-center mb-1">
                        <input
                          type="checkbox"
                          checked={hasPermission(formData, resource.key, action.key)}
                          onChange={() => togglePermission(resource.key, action.key)}
                          className="mr-2"
                        />
                        <span className="text-sm text-gray-600">{action.label}</span>
                      </label>
                    ))}
                  </div>
                ))}
              </div>
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
              >
                {editingProfile ? 'Atualizar' : 'Criar'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowForm(false);
                  setEditingProfile(null);
                  resetForm();
                }}
                className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {profiles.map(profile => (
          <div key={profile.id} className="bg-white rounded-lg shadow-md p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-xl font-semibold text-gray-800">{profile.name}</h3>
                <p className="text-gray-600 text-sm">{profile.description}</p>
                <span className={`inline-block px-2 py-1 rounded-full text-xs ${
                  profile.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                }`}>
                  {profile.active ? 'Ativo' : 'Inativo'}
                </span>
              </div>
              <PermissionGate resource="USERS" action="EDIT" requireAdmin={true}>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleEdit(profile)}
                    className="text-blue-600 hover:text-blue-800"
                  >
                    Editar
                  </button>
                  {profile.name !== 'ADMIN' && (
                    <button
                      onClick={() => handleDelete(profile.id)}
                      className="text-red-600 hover:text-red-800"
                    >
                      Deletar
                    </button>
                  )}
                </div>
              </PermissionGate>
            </div>

            <div className="space-y-2">
              <h4 className="font-medium text-gray-700">Permissões:</h4>
              {availableResources.map(resource => (
                <div key={resource.key} className="text-sm">
                  <span className="font-medium">{resource.label}:</span>
                  <div className="ml-2">
                    {availableActions.map(action => (
                      <span
                        key={action.key}
                        className={`inline-block px-2 py-1 rounded text-xs mr-1 ${
                          hasPermission(profile, resource.key, action.key)
                            ? 'bg-blue-100 text-blue-800'
                            : 'bg-gray-100 text-gray-500'
                        }`}
                      >
                        {action.label}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Profiles;
