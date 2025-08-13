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
  Download,
  Search,
  Calendar,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { api } from '../services/api';
import PermissionGate from '../components/PermissionGate';
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
  const [error, setError] = useState(null);

  // Estados para paginação
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  // Estados para filtros
  const [searchTerm, setSearchTerm] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const [formData, setFormData] = useState({
    description: '',
    amount: '',
    type: 'INCOME',
    notes: ''
  });

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

  useEffect(() => {
    try {
      loadBudgets();
      loadBalance();
    } catch (error) {
      console.error('Erro ao carregar dados iniciais:', error);
      setError('Erro ao carregar dados. Tente recarregar a página.');
    }
  }, []);

  // Se houver erro, mostrar tela de erro
  if (error) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
          <h2 className="text-lg font-semibold text-red-800 mb-2">Erro ao carregar dados</h2>
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={() => {
              setError(null);
              setLoading(true);
              loadBudgets();
              loadBalance();
            }}
            className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
          >
            Tentar Novamente
          </button>
        </div>
      </div>
    );
  }

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

  // Função para extrair nome do vendedor (movida para antes do uso)
  const extractSellerName = (budget) => {
    try {
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
      
      // Para outras movimentações (doações, etc.), mostrar o nome do usuário
      return budget.userName || 'Não informado';
    } catch (error) {
      console.error('Erro ao extrair nome do vendedor:', error);
      return 'Não informado';
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

  // Função para filtrar dados
  const getFilteredBudgets = () => {
    try {
      let filtered = budgets;

      // Filtro por tipo
      if (filter !== 'ALL') {
        filtered = filtered.filter(budget => budget.type === filter);
      }

      // Filtro por busca
      if (searchTerm) {
        filtered = filtered.filter(budget => {
          try {
            const description = budget.description?.toLowerCase() || '';
            const notes = budget.notes?.toLowerCase() || '';
            const sellerName = extractSellerName(budget).toLowerCase();
            const searchLower = searchTerm.toLowerCase();
            
            return description.includes(searchLower) ||
                   notes.includes(searchLower) ||
                   sellerName.includes(searchLower);
          } catch (error) {
            console.error('Erro ao filtrar por busca:', error);
            return false;
          }
        });
      }

      // Filtro por data
      if (startDate) {
        filtered = filtered.filter(budget => {
          try {
            return new Date(budget.date) >= new Date(startDate);
          } catch (error) {
            console.error('Erro ao filtrar por data inicial:', error);
            return false;
          }
        });
      }

      if (endDate) {
        filtered = filtered.filter(budget => {
          try {
            return new Date(budget.date) <= new Date(endDate + 'T23:59:59');
          } catch (error) {
            console.error('Erro ao filtrar por data final:', error);
            return false;
          }
        });
      }

      return filtered;
    } catch (error) {
      console.error('Erro ao filtrar dados:', error);
      return budgets; // Retorna todos os dados em caso de erro
    }
  };

  // Função para limpar filtros
  const clearFilters = () => {
    setSearchTerm('');
    setStartDate('');
    setEndDate('');
    setFilter('ALL');
    setCurrentPage(1);
  };

  // Dados filtrados
  const filteredBudgets = getFilteredBudgets();

  // Paginação
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredBudgets.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredBudgets.length / itemsPerPage);

  // Funções de paginação
  const goToPage = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const goToPreviousPage = () => {
    setCurrentPage(prev => Math.max(prev - 1, 1));
  };

  const goToNextPage = () => {
    setCurrentPage(prev => Math.min(prev + 1, totalPages));
  };

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

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Orçamento</h1>
        <PermissionGate resource="BUDGET" action="EDIT">
          <button
            onClick={() => setShowModal(true)}
            className="bg-primary-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-primary-700"
          >
            <Plus className="w-4 h-4" />
            Nova Movimentação
          </button>
        </PermissionGate>
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
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
            <h3 className="text-lg font-semibold">Movimentações</h3>
            
            {/* Filtros */}
            <div className="flex flex-col lg:flex-row gap-4">
              {/* Busca */}
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <input
                  type="text"
                  placeholder="Buscar movimentações..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
              </div>

              {/* Filtro de Data */}
              <div className="flex gap-2">
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    placeholder="Data inicial"
                  />
                </div>
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    placeholder="Data final"
                  />
                </div>
              </div>

              {/* Filtro de Tipo */}
              <select
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="ALL">Todos os tipos</option>
                <option value="INCOME">Entradas</option>
                <option value="EXPENSE">Gastos</option>
              </select>

              {/* Botão Limpar Filtros */}
              {(searchTerm || startDate || endDate || filter !== 'ALL') && (
                <button
                  onClick={clearFilters}
                  className="px-3 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Limpar Filtros
                </button>
              )}
            </div>

            {/* Botão Exportar */}
            <div className="flex items-center gap-2">
              <button
                onClick={handleExportExcel}
                className="bg-green-600 text-white px-3 py-2 rounded-lg flex items-center gap-2 hover:bg-green-700"
                title="Baixar Excel"
              >
                <Download className="w-4 h-4" />
                Exportar Excel
              </button>
            </div>
          </div>

          {/* Informações dos filtros */}
          <div className="mt-4 text-sm text-gray-600">
            {(() => {
              try {
                return (
                  <>
                    Mostrando {currentItems.length} de {filteredBudgets.length} movimentações
                    {searchTerm && ` • Busca: "${searchTerm}"`}
                    {startDate && ` • De: ${new Date(startDate).toLocaleDateString('pt-BR')}`}
                    {endDate && ` • Até: ${new Date(endDate).toLocaleDateString('pt-BR')}`}
                    {filter !== 'ALL' && ` • Tipo: ${filter === 'INCOME' ? 'Entradas' : 'Gastos'}`}
                  </>
                );
              } catch (error) {
                console.error('Erro ao renderizar informações dos filtros:', error);
                return 'Erro ao carregar informações dos filtros';
              }
            })()}
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
              {currentItems.map((budget) => {
                try {
                  return (
                    <tr key={budget.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {budget.description || 'Sem descrição'}
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
                          R$ {parseFloat(budget.amount || 0).toFixed(2)}
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
                        {budget.date ? new Date(budget.date).toLocaleDateString('pt-BR') : 'Data não informada'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {extractSellerName(budget)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <div className="flex items-center gap-2">
                          <PermissionGate resource="BUDGET" action="EDIT">
                            <button
                              onClick={() => handleEdit(budget)}
                              className="text-primary-600 hover:text-primary-900"
                            >
                              <Edit className="w-4 h-4" />
                            </button>
                          </PermissionGate>
                          <PermissionGate resource="BUDGET" action="EDIT">
                            <button
                              onClick={() => handleDelete(budget.id)}
                              className="text-red-600 hover:text-red-900"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </PermissionGate>
                        </div>
                      </td>
                    </tr>
                  );
                } catch (error) {
                  console.error('Erro ao renderizar linha da tabela:', error);
                  return (
                    <tr key={budget.id || 'error'}>
                      <td colSpan="6" className="px-6 py-4 text-center text-red-600">
                        Erro ao carregar dados desta movimentação
                      </td>
                    </tr>
                  );
                }
              })}
            </tbody>
          </table>
        </div>

        {/* Paginação */}
        {totalPages > 1 && (
          <div className="px-6 py-4 border-t bg-gray-50">
            <div className="flex items-center justify-between">
              <div className="text-sm text-gray-700">
                Página {currentPage} de {totalPages} • 
                Mostrando {indexOfFirstItem + 1} a {Math.min(indexOfLastItem, filteredBudgets.length)} de {filteredBudgets.length} resultados
              </div>
              
              <div className="flex items-center gap-2">
                <button
                  onClick={goToPreviousPage}
                  disabled={currentPage === 1}
                  className="px-3 py-1 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                
                {/* Números das páginas */}
                <div className="flex items-center gap-1">
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => {
                    // Mostrar apenas algumas páginas para não ficar muito largo
                    if (
                      page === 1 ||
                      page === totalPages ||
                      (page >= currentPage - 1 && page <= currentPage + 1)
                    ) {
                      return (
                        <button
                          key={page}
                          onClick={() => goToPage(page)}
                          className={`px-3 py-1 rounded-lg ${
                            currentPage === page
                              ? 'bg-primary-600 text-white'
                              : 'border border-gray-300 hover:bg-gray-50'
                          }`}
                        >
                          {page}
                        </button>
                      );
                    } else if (
                      page === currentPage - 2 ||
                      page === currentPage + 2
                    ) {
                      return <span key={page} className="px-2">...</span>;
                    }
                    return null;
                  })}
                </div>
                
                <button
                  onClick={goToNextPage}
                  disabled={currentPage === totalPages}
                  className="px-3 py-1 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        )}
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
