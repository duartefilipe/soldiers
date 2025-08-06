import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Edit, Trash2, Search, Calendar, MapPin, Clock } from 'lucide-react';
import toast from 'react-hot-toast';

export function Games() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingGame, setEditingGame] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    date: '',
    startTime: '',
    endTime: '',
    location: '',
    status: 'SCHEDULED'
  });

  useEffect(() => {
    loadGames();
  }, []);

  const loadGames = async () => {
    try {
      const response = await api.get('/games');
      setGames(response.data);
    } catch (error) {
      toast.error('Erro ao carregar jogos');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingGame) {
        await api.put(`/games/${editingGame.id}`, formData);
        toast.success('Jogo atualizado com sucesso!');
      } else {
        await api.post('/games', formData);
        toast.success('Jogo criado com sucesso!');
      }
      setShowModal(false);
      setEditingGame(null);
      resetForm();
      loadGames();
    } catch (error) {
      toast.error('Erro ao salvar jogo');
    }
  };

  const handleEdit = (game) => {
    setEditingGame(game);
    setFormData({
      name: game.name,
      description: game.description,
      date: game.date,
      startTime: game.startTime,
      endTime: game.endTime,
      location: game.location,
      status: game.status
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este jogo?')) {
      try {
        await api.delete(`/games/${id}`);
        toast.success('Jogo excluído com sucesso!');
        loadGames();
      } catch (error) {
        toast.error('Erro ao excluir jogo');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      date: '',
      startTime: '',
      endTime: '',
      location: '',
      status: 'SCHEDULED'
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'Agendado';
      case 'IN_PROGRESS':
        return 'Em andamento';
      case 'COMPLETED':
        return 'Concluído';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  };

  const filteredGames = games.filter(game =>
    game.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    game.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
    game.location.toLowerCase().includes(searchTerm.toLowerCase())
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
          <h1 className="text-2xl font-bold text-gray-900">Jogos</h1>
          <p className="text-gray-600">Gerencie os eventos de jogo</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="btn-primary flex items-center mt-4 sm:mt-0"
        >
          <Plus className="w-4 h-4 mr-2" />
          Novo Jogo
        </button>
      </div>

      {/* Busca */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
        <input
          type="text"
          placeholder="Buscar jogos..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="input-field pl-10"
        />
      </div>

      {/* Lista de jogos */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredGames.map((game) => (
          <div key={game.id} className="bg-white rounded-lg shadow-md p-6">
            <div className="flex justify-between items-start mb-4">
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">{game.name}</h3>
                <p className="text-sm text-gray-600 mb-3">{game.description}</p>
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => handleEdit(game)}
                  className="text-primary-600 hover:text-primary-900"
                >
                  <Edit className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDelete(game.id)}
                  className="text-red-600 hover:text-red-900"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center text-sm text-gray-600">
                <Calendar className="w-4 h-4 mr-2" />
                {new Date(game.date).toLocaleDateString('pt-BR')}
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <Clock className="w-4 h-4 mr-2" />
                {game.startTime} - {game.endTime}
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <MapPin className="w-4 h-4 mr-2" />
                {game.location}
              </div>
            </div>

            <div className="mt-4">
              <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(game.status)}`}>
                {getStatusText(game.status)}
              </span>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                {editingGame ? 'Editar Jogo' : 'Novo Jogo'}
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
                  <label className="block text-sm font-medium text-gray-700">Descrição</label>
                  <textarea
                    required
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    className="input-field"
                    rows="3"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Data</label>
                  <input
                    type="date"
                    required
                    value={formData.date}
                    onChange={(e) => setFormData({...formData, date: e.target.value})}
                    className="input-field"
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Início</label>
                    <input
                      type="time"
                      required
                      value={formData.startTime}
                      onChange={(e) => setFormData({...formData, startTime: e.target.value})}
                      className="input-field"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Fim</label>
                    <input
                      type="time"
                      required
                      value={formData.endTime}
                      onChange={(e) => setFormData({...formData, endTime: e.target.value})}
                      className="input-field"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Local</label>
                  <input
                    type="text"
                    required
                    value={formData.location}
                    onChange={(e) => setFormData({...formData, location: e.target.value})}
                    className="input-field"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value})}
                    className="input-field"
                  >
                    <option value="SCHEDULED">Agendado</option>
                    <option value="IN_PROGRESS">Em andamento</option>
                    <option value="COMPLETED">Concluído</option>
                    <option value="CANCELLED">Cancelado</option>
                  </select>
                </div>
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => {
                      setShowModal(false);
                      setEditingGame(null);
                      resetForm();
                    }}
                    className="btn-secondary"
                  >
                    Cancelar
                  </button>
                  <button type="submit" className="btn-primary">
                    {editingGame ? 'Atualizar' : 'Criar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
} 