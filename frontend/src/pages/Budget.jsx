import { useState, useEffect } from 'react';
import { 
  Plus, 
  DollarSign, 
  TrendingUp, 
  TrendingDown, 
  Wallet,
  Edit,
  Trash2,
  Filter,
  Download
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { api } from '../services/api';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';

export function Budget() {
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);
  const [filter, setFilter] = useState('ALL');
  const [currentBalance, setCurrentBalance] = useState(0);
  const [totalIncome, setTotalIncome] = useState(0);
  const [totalExpenses, setTotalExpenses] = useState(0);

  const [formData, setFormData] = useState({
    description: '',
    amount: '',
    type: 'INCOME',
    notes: ''
  });

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

  useEffect(() => {
    loadBudgets();
    loadBalance();
  }, []);

  const loadBudgets = async () => {
    try {
      const response = await api.get('/budgets');
      setBudgets(response.data);
    } catch (error) {
      toast.error('Erro ao carregar movimentações');
    } finally {
      setLoading(false);
    }
  };

  const loadBalance = async () => {
    try {
      const [balanceRes, incomeRes, expensesRes] = await Promise.all([
        api.get('/budgets/balance'),
        api.get('/budgets/income'),
        api.get('/budgets/expenses')
      ]);
      
      setCurrentBalance(balanceRes.data || 0);
      setTotalIncome(incomeRes.data || 0);
      setTotalExpenses(expensesRes.data || 0);
    } catch (error) {
      console.error('Erro ao carregar saldo:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.description || !formData.amount) {
      toast.error('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      if (editingBudget) {
        await api.put(`/budgets/${editingBudget.id}`, formData);
        toast.success('Movimentação atualizada com sucesso!');
      } else {
        await api.post('/budgets', formData);
        toast.success('Movimentação criada com sucesso!');
      }
      
      setShowModal(false);
      setEditingBudget(null);
      resetForm();
      loadBudgets();
      loadBalance();
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
      await api.delete(`/budgets/${id}`);
      toast.success('Movimentação excluída com sucesso!');
      loadBudgets();
      loadBalance();
    } catch (error) {
      toast.error('Erro ao excluir movimentação');
    }
  };

  const resetForm = () => {
    setFormData({
      description: '',
      amount: '',
      type: 'INCOME',
      notes: ''
    });
  };

  const handleExportExcel = async () => {
    try {
      const response = await api.get('/budgets/export/excel', {
        responseType: 'blob'
      });
      
      // Criar link para download
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'orcamento_soldiers.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      toast.success('Arquivo Excel baixado com sucesso!');
    } catch (error) {
      toast.error('Erro ao baixar arquivo Excel');
      console.error('Erro ao exportar:', error);
    }
  };

  const filteredBudgets = budgets.filter(budget => {
    if (filter === 'ALL') return true;
    return budget.type === filter;
  });

  const chartData = [
    { name: 'Entradas', value: totalIncome, color: '#00C49F' },
    { name: 'Gastos', value: totalExpenses, color: '#FF8042' }
  ];

  const monthlyData = budgets.reduce((acc, budget) => {
    const month = new Date(budget.date).toLocaleDateString('pt-BR', { month: 'short' });
    const existing = acc.find(item => item.month === month);
    
    if (existing) {
      if (budget.type === 'INCOME') {
        existing.income += parseFloat(budget.amount);
      } else {
        existing.expenses += parseFloat(budget.amount);
      }
    } else {
      acc.push({
        month,
        income: budget.type === 'INCOME' ? parseFloat(budget.amount) : 0,
        expenses: budget.type === 'EXPENSE' ? parseFloat(budget.amount) : 0
      });
    }
    
    return acc;
  }, []);

  const getStatusText = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'Agendado';
      case 'IN_PROGRESS':
        return 'Em andamento';
      case 'FINISHED':
        return 'Finalizado';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  };

  const extractSellerName = (budget) => {
    // Se for uma venda (INCOME e descrição contém "Venda")
    if (budget.type === 'INCOME' && 
        budget.description && 
        budget.description.includes('Venda')) {
      
      // Extrair nome do vendedor da descrição
      if (budget.description.includes('Vendedor:')) {
        const parts = budget.description.split('Vendedor:');
        if (parts.length > 1) {
          return parts[1].trim();
        }
      }
      
      // Se não encontrar na descrição, usar o nome do usuário
      return budget.userName || 'Não informado';
    }
    
    // Para outros tipos de movimentação, retornar vazio
    return '';
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Orçamento</h1>
        <button
          onClick={() => setShowModal(true)}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-primary-700"
        >
          <Plus className="w-4 h-4" />
          Nova Movimentação
        </button>
      </div>

      {/* Cards de Resumo */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-lg shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Saldo Atual</p>
              <p className={`text-2xl font-bold ${currentBalance >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                R$ {currentBalance.toFixed(2)}
              </p>
            </div>
            <Wallet className="w-8 h-8 text-primary-600" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Entradas</p>
              <p className="text-2xl font-bold text-green-600">
                R$ {totalIncome.toFixed(2)}
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-green-600" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Gastos</p>
              <p className="text-2xl font-bold text-red-600">
                R$ {totalExpenses.toFixed(2)}
              </p>
            </div>
            <TrendingDown className="w-8 h-8 text-red-600" />
          </div>
        </div>
      </div>

      {/* Gráficos */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold mb-4">Distribuição de Receitas e Despesas</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => `R$ ${value.toFixed(2)}`} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold mb-4">Movimentação Mensal</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip formatter={(value) => `R$ ${value.toFixed(2)}`} />
              <Legend />
              <Bar dataKey="income" fill="#00C49F" name="Entradas" />
              <Bar dataKey="expenses" fill="#FF8042" name="Gastos" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Filtros e Lista */}
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">Movimentações</h3>
            <div className="flex items-center gap-2">
              <button
                onClick={handleExportExcel}
                className="bg-green-600 text-white px-3 py-2 rounded-lg flex items-center gap-2 hover:bg-green-700"
                title="Baixar Excel"
              >
                <Download className="w-4 h-4" />
                Exportar Excel
              </button>
              <Filter className="w-4 h-4 text-gray-400" />
              <select
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2"
              >
                <option value="ALL">Todos</option>
                <option value="INCOME">Entradas</option>
                <option value="EXPENSE">Gastos</option>
              </select>
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Descrição
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Valor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Tipo
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Data
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vendedor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredBudgets.map((budget) => (
                <tr key={budget.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {budget.description}
                      </div>
                      {budget.notes && (
                        <div className="text-sm text-gray-500">{budget.notes}</div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`text-sm font-medium ${
                      budget.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                    }`}>
                      R$ {parseFloat(budget.amount).toFixed(2)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      budget.type === 'INCOME' 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {budget.type === 'INCOME' ? 'Entrada' : 'Gasto'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(budget.date).toLocaleDateString('pt-BR')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {extractSellerName(budget)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
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
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">
              {editingBudget ? 'Editar Movimentação' : 'Nova Movimentação'}
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Descrição da Movimentação
                  </label>
                  <input
                    type="text"
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    placeholder="Ex: Venda de produtos, Compra de material, etc."
                    required
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Valor (R$)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.amount}
                    onChange={(e) => setFormData({...formData, amount: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    placeholder="0,00"
                    required
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo de Movimentação
                  </label>
                  <select
                    value={formData.type}
                    onChange={(e) => setFormData({...formData, type: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  >
                    <option value="INCOME">💰 Entrada de Dinheiro</option>
                    <option value="EXPENSE">💸 Saída de Dinheiro</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Observações (Opcional)
                  </label>
                  <textarea
                    value={formData.notes}
                    onChange={(e) => setFormData({...formData, notes: e.target.value})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    rows="3"
                    placeholder="Detalhes adicionais sobre a movimentação..."
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
                  {editingBudget ? 'Atualizar Movimentação' : 'Criar Movimentação'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
