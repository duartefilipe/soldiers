import React, { useState, useEffect } from 'react';
import { toast } from 'react-hot-toast';
import { Users, Plus, Edit, Trash2, Search } from 'lucide-react';
import { api } from '../services/api';

// Funções utilitárias movidas para fora dos componentes
const getStatusColor = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-green-100 text-green-800';
    case 'INACTIVE':
      return 'bg-gray-100 text-gray-800';
    case 'INJURED':
      return 'bg-red-100 text-red-800';
    case 'SUSPENDED':
      return 'bg-yellow-100 text-yellow-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'Ativo';
    case 'INACTIVE':
      return 'Inativo';
    case 'INJURED':
      return 'Lesionado';
    case 'SUSPENDED':
      return 'Suspenso';
    case 'DISBANDED':
      return 'Dissolvido';
    default:
      return status;
  }
};

export function Team() {
  const [activeTab, setActiveTab] = useState('players');
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [playersRes, teamsRes] = await Promise.all([
        api.get('/players'),
        api.get('/teams')
      ]);
      setPlayers(playersRes.data);
      setTeams(teamsRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados');
      console.error('Erro:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredPlayers = players.filter(player =>
    player.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    player.position.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredTeams = teams.filter(team =>
    team.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Gerenciamento de Time</h1>
        <p className="text-gray-600">Gerencie jogadores e times do Soldiers</p>
      </div>

      {/* Abas */}
      <div className="mb-6">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8">
            <button
              onClick={() => setActiveTab('players')}
              className={`py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'players'
                  ? 'border-primary-500 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <Users className="inline w-4 h-4 mr-2" />
              Jogadores ({players.length})
            </button>
            <button
              onClick={() => setActiveTab('teams')}
              className={`py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'teams'
                  ? 'border-primary-500 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <Users className="inline w-4 h-4 mr-2" />
              Times ({teams.length})
            </button>
          </nav>
        </div>
      </div>

      {/* Barra de pesquisa */}
      <div className="mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <input
            type="text"
            placeholder={`Pesquisar ${activeTab === 'players' ? 'jogadores' : 'times'}...`}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          />
        </div>
      </div>

      {/* Conteúdo das abas */}
      {activeTab === 'players' ? (
        <PlayersTab players={filteredPlayers} onRefresh={loadData} />
      ) : (
        <TeamsTab teams={filteredTeams} players={players} onRefresh={loadData} />
      )}
    </div>
  );
}

// Componente da aba Jogadores
function PlayersTab({ players, onRefresh }) {
  const [showModal, setShowModal] = useState(false);
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [teams, setTeams] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    position: '',
    number: '',
    description: '',
    status: 'ACTIVE',
    teamIds: []
  });

  const positions = [
    'QB', 'RB', 'WR', 'TE', 'OL', 'DL', 'LB', 'CB', 'S', 'K', 'P'
  ];

  // Carregar times quando o componente for montado
  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    try {
      const response = await api.get('/teams');
      setTeams(response.data);
    } catch (error) {
      console.error('Erro ao carregar times:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingPlayer) {
        await api.put(`/players/${editingPlayer.id}`, formData);
        toast.success('Jogador atualizado com sucesso!');
      } else {
        await api.post('/players', formData);
        toast.success('Jogador criado com sucesso!');
      }
      setShowModal(false);
      setEditingPlayer(null);
      resetForm();
      onRefresh();
    } catch (error) {
      toast.error('Erro ao salvar jogador');
      console.error('Erro:', error);
    }
  };

  const handleEdit = (player) => {
    setEditingPlayer(player);
    setFormData({
      name: player.name,
      position: player.position,
      number: player.number,
      description: player.description || '',
      status: player.status,
      teamIds: player.teams ? player.teams.map(team => team.id) : []
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este jogador?')) {
      try {
        await api.delete(`/players/${id}`);
        toast.success('Jogador excluído com sucesso!');
        onRefresh();
      } catch (error) {
        toast.error('Erro ao excluir jogador');
        console.error('Erro:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      position: '',
      number: '',
      description: '',
      status: 'ACTIVE',
      teamIds: []
    });
  };

  const handleTeamToggle = (teamId) => {
    setFormData(prev => ({
      ...prev,
      teamIds: prev.teamIds.includes(teamId)
        ? prev.teamIds.filter(id => id !== teamId)
        : [...prev.teamIds, teamId]
    }));
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-gray-900">Jogadores</h2>
        <button
          onClick={() => {
            setEditingPlayer(null);
            resetForm();
            setShowModal(true);
          }}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-primary-700"
        >
          <Plus className="w-4 h-4" />
          Novo Jogador
        </button>
      </div>

      {/* Lista de jogadores */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Nome
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Posição
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Número
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Times
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {players.map((player) => (
                <tr key={player.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {player.name}
                      </div>
                      {player.description && (
                        <div className="text-sm text-gray-500">{player.description}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {player.position}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {player.number}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex flex-wrap gap-1">
                      {player.teams && player.teams.length > 0 ? (
                        player.teams.map((team) => (
                          <span
                            key={team.id}
                            className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800"
                          >
                            {team.name}
                          </span>
                        ))
                      ) : (
                        <span className="text-sm text-gray-500">Nenhum time</span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(player.status)}`}>
                      {getStatusText(player.status)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleEdit(player)}
                        className="text-primary-600 hover:text-primary-900"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(player.id)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de jogador */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-semibold mb-4">
              {editingPlayer ? 'Editar Jogador' : 'Novo Jogador'}
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nome
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Posição
                </label>
                <select
                  value={formData.position}
                  onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                >
                  <option value="">Selecione uma posição</option>
                  {positions.map(pos => (
                    <option key={pos} value={pos}>{pos}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Número
                </label>
                <input
                  type="text"
                  value={formData.number}
                  onChange={(e) => setFormData({ ...formData, number: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descrição
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  rows="3"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                >
                  <option value="ACTIVE">Ativo</option>
                  <option value="INACTIVE">Inativo</option>
                  <option value="INJURED">Lesionado</option>
                  <option value="SUSPENDED">Suspenso</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Times
                </label>
                <div className="border border-gray-300 rounded-lg p-4 max-h-48 overflow-y-auto">
                  {teams.map((team) => (
                    <label key={team.id} className="flex items-center space-x-2 py-1">
                      <input
                        type="checkbox"
                        checked={formData.teamIds.includes(team.id)}
                        onChange={() => handleTeamToggle(team.id)}
                        className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm">
                        {team.name}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setEditingPlayer(null);
                    resetForm();
                  }}
                  className="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  {editingPlayer ? 'Atualizar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// Componente da aba Times
function TeamsTab({ teams, players, onRefresh }) {
  const [showModal, setShowModal] = useState(false);
  const [editingTeam, setEditingTeam] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    status: 'ACTIVE',
    playerIds: []
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingTeam) {
        await api.put(`/teams/${editingTeam.id}`, formData);
        toast.success('Time atualizado com sucesso!');
      } else {
        await api.post('/teams', formData);
        toast.success('Time criado com sucesso!');
      }
      setShowModal(false);
      setEditingTeam(null);
      resetForm();
      onRefresh();
    } catch (error) {
      toast.error('Erro ao salvar time');
      console.error('Erro:', error);
    }
  };

  const handleEdit = (team) => {
    setEditingTeam(team);
    setFormData({
      name: team.name,
      description: team.description || '',
      status: team.status,
      playerIds: team.players.map(p => p.id)
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este time?')) {
      try {
        await api.delete(`/teams/${id}`);
        toast.success('Time excluído com sucesso!');
        onRefresh();
      } catch (error) {
        toast.error('Erro ao excluir time');
        console.error('Erro:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      status: 'ACTIVE',
      playerIds: []
    });
  };

  const handlePlayerToggle = (playerId) => {
    setFormData(prev => ({
      ...prev,
      playerIds: prev.playerIds.includes(playerId)
        ? prev.playerIds.filter(id => id !== playerId)
        : [...prev.playerIds, playerId]
    }));
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold text-gray-900">Times</h2>
        <button
          onClick={() => {
            setEditingTeam(null);
            resetForm();
            setShowModal(true);
          }}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-primary-700"
        >
          <Plus className="w-4 h-4" />
          Novo Time
        </button>
      </div>

      {/* Lista de times */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Nome
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Jogadores
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {teams.map((team) => (
                <tr key={team.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {team.name}
                      </div>
                      {team.description && (
                        <div className="text-sm text-gray-500">{team.description}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {team.playerCount} jogador{team.playerCount !== 1 ? 'es' : ''}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(team.status)}`}>
                      {getStatusText(team.status)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleEdit(team)}
                        className="text-primary-600 hover:text-primary-900"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(team.id)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de time */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-semibold mb-4">
              {editingTeam ? 'Editar Time' : 'Novo Time'}
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nome
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descrição
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  rows="3"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                >
                  <option value="ACTIVE">Ativo</option>
                  <option value="INACTIVE">Inativo</option>
                  <option value="DISBANDED">Dissolvido</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Jogadores
                </label>
                <div className="border border-gray-300 rounded-lg p-4 max-h-48 overflow-y-auto">
                  {players.filter(p => p.status === 'ACTIVE').map((player) => (
                    <label key={player.id} className="flex items-center space-x-2 py-1">
                      <input
                        type="checkbox"
                        checked={formData.playerIds.includes(player.id)}
                        onChange={() => handlePlayerToggle(player.id)}
                        className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm">
                        {player.name} ({player.position} - {player.number})
                      </span>
                    </label>
                  ))}
                </div>
              </div>
              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setEditingTeam(null);
                    resetForm();
                  }}
                  className="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  {editingTeam ? 'Atualizar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
