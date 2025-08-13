import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, Trash2, User } from 'lucide-react';
import { toast } from 'react-hot-toast';
import api from '../services/api';

export const Users = () => {
  const [users, setUsers] = useState([]);
  const [profiles, setProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'NORMAL',
    profileName: '', // Para compatibilidade
    profileNames: [] // Para múltiplos perfis
  });

  useEffect(() => {
    loadUsers();
    loadProfiles();
  }, []);

  const loadUsers = async () => {
    try {
      const response = await api.get('/auth/users');
      setUsers(response.data);
    } catch (error) {
      toast.error('Erro ao carregar usuários');
    } finally {
      setLoading(false);
    }
  };

  const loadProfiles = async () => {
    try {
      const response = await api.get('/profiles');
      setProfiles(response.data);
    } catch (error) {
      console.error('Erro ao carregar perfis:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.profileNames.length === 0) {
      toast.error('Selecione pelo menos um perfil');
      return;
    }

    try {
      const userData = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        profileNames: formData.profileNames.length > 0 ? formData.profileNames : 
                     (formData.role === 'ADMIN' ? ['ADMIN'] : [formData.profileName])
      };

      if (editingUser) {
        await api.put(`/auth/users/${editingUser.id}`, userData);
        toast.success('Usuário atualizado com sucesso!');
      } else {
        await api.post('/auth/register', userData);
        toast.success('Usuário criado com sucesso!');
      }
      setShowModal(false);
      setEditingUser(null);
      resetForm();
      loadUsers();
    } catch (error) {
      toast.error('Erro ao salvar usuário');
    }
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    setFormData({
      name: user.name,
      email: user.email,
      password: '',
      role: user.profile?.name === 'ADMIN' ? 'ADMIN' : 'NORMAL',
      profileName: user.profile?.name || '',
      profileNames: user.profiles ? user.profiles.map(p => p.name) : []
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este usuário?')) {
      try {
        await api.delete(`/auth/users/${id}`);
        toast.success('Usuário excluído com sucesso!');
        loadUsers();
      } catch (error) {
        toast.error('Erro ao excluir usuário');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      email: '',
      password: '',
      role: 'NORMAL',
      profileName: '',
      profileNames: []
    });
  };

  const getRoleColor = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'bg-red-100 text-red-800';
      case 'NORMAL':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getRoleText = (user) => {
    if (user.profiles && user.profiles.length > 0) {
      const profileNames = user.profiles.map(p => p.name).join(', ');
      return `Usuário (${profileNames})`;
    }
    if (user.profile?.name) {
      return `Usuário (${user.profile.name})`;
    }
    return 'Usuário (Sem perfil)';
  };

  const filteredUsers = users.filter(user =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Usuários</h1>
          <p className="text-gray-600">Gerencie os usuários do sistema</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="btn-primary flex items-center mt-4 sm:mt-0"
        >
          <Plus className="w-4 h-4 mr-2" />
          Novo Usuário
        </button>
      </div>

      {/* Busca */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
        <input
          type="text"
          placeholder="Buscar usuários..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="input-field pl-10"
        />
      </div>

      {/* Lista de usuários */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Usuário
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Email
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Função
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredUsers.map((user) => (
                <tr key={user.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                        <User className="w-4 h-4 text-primary-600" />
                      </div>
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">{user.name}</div>
                        <div className="text-sm text-gray-500">ID: {user.id}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {user.email}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getRoleColor(user.profile?.name === 'ADMIN' ? 'ADMIN' : 'NORMAL')}`}>
                      {getRoleText(user)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      user.deletadoEm ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
                    }`}>
                      {user.deletadoEm ? 'Inativo' : 'Ativo'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      onClick={() => handleEdit(user)}
                      className="text-primary-600 hover:text-primary-900 mr-3"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(user.id)}
                      className="text-red-600 hover:text-red-900"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {filteredUsers.length === 0 && (
          <div className="text-center py-12">
            <User className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhum usuário encontrado</h3>
            <p className="mt-1 text-sm text-gray-500">
              {searchTerm ? 'Tente ajustar a busca.' : 'Comece criando seu primeiro usuário.'}
            </p>
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                {editingUser ? 'Editar Usuário' : 'Novo Usuário'}
              </h3>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Nome</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Email</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    {editingUser ? 'Nova Senha (deixe em branco para manter)' : 'Senha'}
                  </label>
                  <input
                    type="password"
                    required={!editingUser}
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    className="input-field"
                  />
                </div>

                
                <div>
                  <label className="block text-sm font-medium text-gray-700">Perfis</label>
                  <div className="space-y-2 max-h-40 overflow-y-auto border rounded-md p-2">
                    {profiles.map((profile) => (
                      <label key={profile.id} className="flex items-center space-x-2 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={formData.profileNames.includes(profile.name)}
                          onChange={(e) => {
                            if (e.target.checked) {
                              setFormData({
                                ...formData,
                                profileNames: [...formData.profileNames, profile.name]
                              });
                            } else {
                              setFormData({
                                ...formData,
                                profileNames: formData.profileNames.filter(name => name !== profile.name)
                              });
                            }
                          }}
                          className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                        />
                        <span className="text-sm text-gray-700">
                          {profile.name} - {profile.description}
                        </span>
                      </label>
                    ))}
                  </div>
                  {formData.profileNames.length === 0 && (
                    <p className="text-sm text-gray-500 mt-1">Selecione pelo menos um perfil</p>
                  )}
                </div>

                <div className="flex justify-end space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      setShowModal(false);
                      setEditingUser(null);
                      resetForm();
                    }}
                    className="btn-secondary"
                  >
                    Cancelar
                  </button>
                  <button type="submit" className="btn-primary">
                    {editingUser ? 'Atualizar' : 'Criar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Users; 