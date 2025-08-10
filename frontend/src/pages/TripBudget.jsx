import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Plus, 
  DollarSign, 
  TrendingUp, 
  TrendingDown,
  Edit,
  Trash2,
  Wallet
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { api } from '../services/api';

export function TripBudget() {
  const { tripId } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState(null);
  const [budgets, setBudgets] = useState([]);
  const [balance, setBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);

  const [formData, setFormData] = useState({
    description: '',
    amount: '',
    type: 'EXPENSE',
    notes: ''
  });

  useEffect(() => {
    loadTripData();
  }, [tripId]);

  const loadTripData = async () => {
    try {
      const [tripRes, budgetsRes, balanceRes] = await Promise.all([
        api.get(`/trips/${tripId}`),
        api.get(`/trip-budgets/trip/${tripId}`),
        api.get(`/trip-budgets/trip/${tripId}/balance`)
      ]);
      
      setTrip(tripRes.data);
      setBudgets(budgetsRes.data);
      setBalance(balanceRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados da viagem');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.description || !formData.amount) {
      toast.error('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      const budgetData = {
        ...formData,
        tripId: parseInt(tripId)
      };

      if (editingBudget) {
        await api.put(`/trip-budgets/${editingBudget.id}`, budgetData);
        toast.success('Movimentação atualizada com sucesso!');
      } else {
        await api.post('/trip-budgets', budgetData);
        toast.success('Movimentação adicionada com sucesso!');
      }
      
      setShowModal(false);
      setEditingBudget(null);
      resetForm();
      loadTripData();
    } catch (error) {
      toast.error('Erro ao salvar movimentação');
    }
  };

  const handleEdit = (budget) => {
    setEditingBudget(budget);
    setFormData({
      description: budget.description,
      amount: budget.amount.toString(),
      type: budget.type,
      notes: budget.notes || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Tem certeza que deseja excluir esta movimentação?')) return;
    
    try {
      await api.delete(`/trip-budgets/${id}`);
      toast.success('Movimentação excluída com sucesso!');
      loadTripData();
    } catch (error) {
      toast.error('Erro ao excluir movimentação');
    }
  };

  const resetForm = () => {
    setFormData({
      description: '',
      amount: '',
      type: 'EXPENSE',
      notes: ''
    });
  };

  const getTypeColor = (type) => {
    return type === 'INCOME' ? 'text-green-600' : 'text-red-600';
  };

  const getTypeIcon = (type) => {
    return type === 'INCOME' ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />;
  };

  const getTypeText = (type) => {
    return type === 'INCOME' ? 'Entrada' : 'Saída';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Carregando...</div>
      </div>
    );
  }

  if (!trip) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Viagem não encontrada</div>
      </div>
    );
  }

  const totalIncome = budgets
    .filter(b => b.type === 'INCOME')
    .reduce((sum, b) => sum + parseFloat(b.amount), 0);

  const totalExpenses = budgets
    .filter(b => b.type === 'EXPENSE')
    .reduce((sum, b) => sum + parseFloat(b.amount), 0);

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/trips')}
            className="text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Orçamento da Viagem</h1>
            <p className="text-gray-600">{trip.destination}</p>
          </div>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
        >
          <Plus className="w-4 h-4" />
          Adicionar Movimentação
        </button>
      </div>

      {/* Resumo */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <TrendingUp className="w-6 h-6 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Entradas</p>
              <p className="text-xl font-bold text-green-600">
                R$ {totalIncome.toFixed(2)}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-red-100 rounded-lg">
              <TrendingDown className="w-6 h-6 text-red-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Saídas</p>
              <p className="text-xl font-bold text-red-600">
                R$ {totalExpenses.toFixed(2)}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Wallet className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Saldo</p>
              <p className={`text-xl font-bold ${balance >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                R$ {parseFloat(balance).toFixed(2)}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-gray-100 rounded-lg">
              <DollarSign className="w-6 h-6 text-gray-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Gastos da Viagem</p>
              <p className="text-xl font-bold text-gray-900">
                R$ {parseFloat(trip.totalCost || 0).toFixed(2)}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Lista de Movimentações */}
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">Movimentações</h2>
        </div>
        
        {budgets.length === 0 ? (
          <div className="p-8 text-center text-gray-500">
            Nenhuma movimentação registrada ainda.
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {budgets.map((budget) => (
              <div key={budget.id} className="p-6 flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className={`p-2 rounded-lg ${
                    budget.type === 'INCOME' ? 'bg-green-100' : 'bg-red-100'
                  }`}>
                    {getTypeIcon(budget.type)}
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{budget.description}</p>
                    <p className="text-sm text-gray-600">
                      {new Date(budget.date).toLocaleDateString('pt-BR')} - {budget.userName}
                    </p>
                    {budget.notes && (
                      <p className="text-sm text-gray-500 mt-1">{budget.notes}</p>
                    )}
                  </div>
                </div>
                
                <div className="flex items-center gap-4">
                  <span className={`font-semibold ${getTypeColor(budget.type)}`}>
                    {budget.type === 'INCOME' ? '+' : '-'} R$ {parseFloat(budget.amount).toFixed(2)}
                  </span>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleEdit(budget)}
                      className="text-primary-600 hover:text-primary-900"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(budget.id)}
                      className="text-red-600 hover:text-red-900"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">
              {editingBudget ? 'Editar' : 'Adicionar'} Movimentação
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo
                  </label>
                  <select
                    value={formData.type}
                    onChange={(e) => setFormData({...formData, type: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    required
                  >
                    <option value="INCOME">Entrada de Dinheiro</option>
                    <option value="EXPENSE">Gasto/Despesa</option>
                  </select>
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
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Valor
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.amount}
                    onChange={(e) => setFormData({...formData, amount: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    required
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
              </div>
              
              <div className="flex justify-end gap-2 mt-6">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setEditingBudget(null);
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
                  {editingBudget ? 'Atualizar' : 'Adicionar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
