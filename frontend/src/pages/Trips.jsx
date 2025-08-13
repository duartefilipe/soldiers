import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Plus, 
  MapPin, 
  Calendar, 
  DollarSign,
  Edit,
  Trash2,
  Filter,
  Eye,
  Plus as PlusIcon,
  Wallet,
  Users
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { api } from '../services/api';
import PermissionGate from '../components/PermissionGate';

export function Trips() {
  const [trips, setTrips] = useState([]);
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showExpenseModal, setShowExpenseModal] = useState(false);
  const [editingTrip, setEditingTrip] = useState(null);
  const [selectedTrip, setSelectedTrip] = useState(null);
  const [filter, setFilter] = useState('ALL');

  const [formData, setFormData] = useState({
    destination: '',
    description: '',
    departureDate: '',
    returnDate: '',
    status: 'PLANNED',
    initialCost: '',
    notes: '',
    playerIds: [],
    teamIds: []
  });

  const [expenseFormData, setExpenseFormData] = useState({
    description: '',
    amount: '',
    notes: ''
  });



  useEffect(() => {
    loadTrips();
  }, []);

  const loadTrips = async () => {
    try {
      const [tripsRes, playersRes, teamsRes] = await Promise.all([
        api.get('/trips'),
        api.get('/players'),
        api.get('/teams')
      ]);
      setTrips(tripsRes.data);
      setPlayers(playersRes.data);
      setTeams(teamsRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.destination || !formData.description || !formData.departureDate || !formData.returnDate) {
      toast.error('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      const tripData = {
        ...formData,
        departureDate: new Date(formData.departureDate).toISOString(),
        returnDate: new Date(formData.returnDate).toISOString()
      };

      if (editingTrip) {
        await api.put(`/trips/${editingTrip.id}`, tripData);
        toast.success('Viagem atualizada com sucesso!');
      } else {
        await api.post('/trips', tripData);
        toast.success('Viagem criada com sucesso!');
      }
      
      setShowModal(false);
      setEditingTrip(null);
      resetForm();
      loadTrips();
    } catch (error) {
      toast.error('Erro ao salvar viagem');
    }
  };

  const handleExpenseSubmit = async (e) => {
    e.preventDefault();
    
    if (!expenseFormData.description || !expenseFormData.amount) {
      toast.error('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      const expenseData = {
        ...expenseFormData,
        tripId: selectedTrip.id
      };

      await api.post('/trip-expenses', expenseData);
      toast.success('Gasto adicionado com sucesso!');
      
      setShowExpenseModal(false);
      resetExpenseForm();
      loadTrips(); // Recarrega para atualizar os gastos
    } catch (error) {
      toast.error('Erro ao adicionar gasto');
    }
  };

  const handleEdit = (trip) => {
    setEditingTrip(trip);
    setFormData({
      destination: trip.destination,
      description: trip.description,
      departureDate: trip.departureDate ? trip.departureDate.split('T')[0] : '',
      returnDate: trip.returnDate ? trip.returnDate.split('T')[0] : '',
      status: trip.status,
      initialCost: trip.initialCost || '',
      notes: trip.notes || '',
      playerIds: trip.players ? trip.players.map(player => player.id) : [],
      teamIds: trip.teams ? trip.teams.map(team => team.id) : []
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Tem certeza que deseja excluir esta viagem?')) return;
    
    try {
      await api.delete(`/trips/${id}`);
      toast.success('Viagem excluída com sucesso!');
      loadTrips();
    } catch (error) {
      toast.error('Erro ao excluir viagem');
    }
  };

  const handleAddExpense = (trip) => {
    setSelectedTrip(trip);
    setShowExpenseModal(true);
  };



  const resetForm = () => {
    setFormData({
      destination: '',
      description: '',
      departureDate: '',
      returnDate: '',
      status: 'PLANNED',
      initialCost: '',
      notes: '',
      playerIds: [],
      teamIds: []
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

  const handleTeamToggle = (teamId) => {
    setFormData(prev => ({
      ...prev,
      teamIds: prev.teamIds.includes(teamId)
        ? prev.teamIds.filter(id => id !== teamId)
        : [...prev.teamIds, teamId]
    }));
  };

  const resetExpenseForm = () => {
    setExpenseFormData({
      description: '',
      amount: '',
      notes: ''
    });
  };



  const getStatusColor = (status) => {
    switch (status) {
      case 'PLANNED':
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
      case 'PLANNED':
        return 'Planejada';
      case 'IN_PROGRESS':
        return 'Em Andamento';
      case 'COMPLETED':
        return 'Concluída';
      case 'CANCELLED':
        return 'Cancelada';
      default:
        return status;
    }
  };

  const filteredTrips = trips.filter(trip => {
    if (filter === 'ALL') return true;
    return trip.status === filter;
  });

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Viagens</h1>
        <PermissionGate resource="TRIPS" action="EDIT">
          <button
            onClick={() => setShowModal(true)}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-primary-700"
          >
            <Plus className="w-4 h-4" />
            Nova Viagem
          </button>
        </PermissionGate>
      </div>

      {/* Filtros */}
      <div className="bg-white p-4 rounded-lg shadow mb-6">
        <div className="flex items-center gap-4">
          <Filter className="w-4 h-4 text-gray-400" />
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2"
          >
            <option value="ALL">Todas as Viagens</option>
            <option value="PLANNED">Planejadas</option>
            <option value="IN_PROGRESS">Em Andamento</option>
            <option value="COMPLETED">Concluídas</option>
            <option value="CANCELLED">Canceladas</option>
          </select>
        </div>
      </div>

      {/* Lista de Viagens */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredTrips.map((trip) => (
          <div key={trip.id} className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{trip.destination}</h3>
                <p className="text-sm text-gray-600">{trip.description}</p>
              </div>
              <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(trip.status)}`}>
                {getStatusText(trip.status)}
              </span>
            </div>

            <div className="space-y-2 mb-4">
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Calendar className="w-4 h-4" />
                <span>
                  {new Date(trip.departureDate).toLocaleDateString('pt-BR')} - {new Date(trip.returnDate).toLocaleDateString('pt-BR')}
                </span>
              </div>
              
              {trip.totalCost > 0 && (
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <DollarSign className="w-4 h-4" />
                  <span>Gastos: R$ {parseFloat(trip.totalCost).toFixed(2)}</span>
                </div>
              )}

              {/* Participantes */}
              {(trip.players && trip.players.length > 0) || (trip.teams && trip.teams.length > 0) ? (
                <div className="flex items-start gap-2 text-sm text-gray-600">
                  <Users className="w-4 h-4 mt-0.5" />
                  <div className="flex-1">
                    {trip.players && trip.players.length > 0 && (
                      <div className="mb-1">
                        <span className="font-medium">Jogadores:</span>
                        <div className="flex flex-wrap gap-1 mt-1">
                          {trip.players.map((player) => (
                            <span
                              key={player.id}
                              className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800"
                            >
                              {player.name}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}
                    {trip.teams && trip.teams.length > 0 && (
                      <div>
                        <span className="font-medium">Times:</span>
                        <div className="flex flex-wrap gap-1 mt-1">
                          {trip.teams.map((team) => (
                            <span
                              key={team.id}
                              className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800"
                            >
                              {team.name}
                            </span>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-2 text-sm text-gray-500">
                  <Users className="w-4 h-4" />
                  <span>Nenhum participante selecionado</span>
                </div>
              )}
            </div>

            {trip.notes && (
              <div className="mb-4">
                <p className="text-sm text-gray-500">{trip.notes}</p>
              </div>
            )}

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <PermissionGate resource="TRIPS" action="EDIT">
                  <button
                    onClick={() => handleEdit(trip)}
                    className="text-primary-600 hover:text-primary-900"
                    title="Editar viagem"
                  >
                    <Edit className="w-4 h-4" />
                  </button>
                </PermissionGate>
                <Link
                  to={`/trips/${trip.id}/budget`}
                  className="text-blue-600 hover:text-blue-900"
                  title="Ver orçamento detalhado"
                >
                  <Wallet className="w-4 h-4" />
                </Link>
                <PermissionGate resource="TRIPS" action="EDIT">
                  <button
                    onClick={() => handleAddExpense(trip)}
                    className="text-green-600 hover:text-green-900"
                    title="Adicionar gasto"
                  >
                    <PlusIcon className="w-4 h-4" />
                  </button>
                </PermissionGate>
                <PermissionGate resource="TRIPS" action="EDIT">
                  <button
                    onClick={() => handleDelete(trip.id)}
                    className="text-red-600 hover:text-red-900"
                    title="Excluir viagem"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </PermissionGate>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal de Viagem */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">
              {editingTrip ? 'Editar Viagem' : 'Nova Viagem'}
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Destino
                  </label>
                  <input
                    type="text"
                    value={formData.destination}
                    onChange={(e) => setFormData({...formData, destination: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
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
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    required
                  />
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Data de Partida
                    </label>
                    <input
                      type="date"
                      value={formData.departureDate}
                      onChange={(e) => setFormData({...formData, departureDate: e.target.value})}
                      className="w-full border border-gray-300 rounded-lg px-3 py-2"
                      required
                    />
                  </div>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Data de Retorno
                    </label>
                    <input
                      type="date"
                      value={formData.returnDate}
                      onChange={(e) => setFormData({...formData, returnDate: e.target.value})}
                      className="w-full border border-gray-300 rounded-lg px-3 py-2"
                      required
                    />
                  </div>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Status
                  </label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  >
                    <option value="PLANNED">Planejada</option>
                    <option value="IN_PROGRESS">Em Andamento</option>
                    <option value="COMPLETED">Concluída</option>
                    <option value="CANCELLED">Cancelada</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Custo Inicial (opcional)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.initialCost}
                    onChange={(e) => setFormData({...formData, initialCost: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    placeholder="0.00"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observações
                  </label>
                  <textarea
                    value={formData.notes}
                    onChange={(e) => setFormData({...formData, notes: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    rows="3"
                  />
                </div>

                {/* Seleção de Jogadores */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Jogadores
                  </label>
                  <div className="border border-gray-300 rounded-lg p-4 max-h-48 overflow-y-auto">
                    {players.length > 0 ? (
                      players.map((player) => (
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
                      ))
                    ) : (
                      <span className="text-sm text-gray-500">Nenhum jogador cadastrado</span>
                    )}
                  </div>
                </div>

                {/* Seleção de Times */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Times
                  </label>
                  <div className="border border-gray-300 rounded-lg p-4 max-h-48 overflow-y-auto">
                    {teams.length > 0 ? (
                      teams.map((team) => (
                        <label key={team.id} className="flex items-center space-x-2 py-1">
                          <input
                            type="checkbox"
                            checked={formData.teamIds.includes(team.id)}
                            onChange={() => handleTeamToggle(team.id)}
                            className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                          />
                          <span className="text-sm">
                            {team.name} ({team.playerCount} jogadores)
                          </span>
                        </label>
                      ))
                    ) : (
                      <span className="text-sm text-gray-500">Nenhum time cadastrado</span>
                    )}
                  </div>
                </div>
              </div>
              
              <div className="flex justify-end gap-2 mt-6">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setEditingTrip(null);
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
                  {editingTrip ? 'Atualizar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal de Gasto */}
      {showExpenseModal && selectedTrip && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">
              Adicionar Gasto - {selectedTrip.destination}
            </h2>
            <form onSubmit={handleExpenseSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Descrição
                  </label>
                  <input
                    type="text"
                    value={expenseFormData.description}
                    onChange={(e) => setExpenseFormData({...expenseFormData, description: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    required
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Valor
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={expenseFormData.amount}
                    onChange={(e) => setExpenseFormData({...expenseFormData, amount: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    required
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observações
                  </label>
                  <textarea
                    value={expenseFormData.notes}
                    onChange={(e) => setExpenseFormData({...expenseFormData, notes: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    rows="3"
                  />
                </div>
              </div>
              
              <div className="flex justify-end gap-2 mt-6">
                <button
                  type="button"
                  onClick={() => {
                    setShowExpenseModal(false);
                    setSelectedTrip(null);
                    resetExpenseForm();
                  }}
                  className="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  Adicionar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}


    </div>
  );
}
