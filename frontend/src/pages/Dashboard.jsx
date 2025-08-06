import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { 
  TrendingUp, 
  Package, 
  ShoppingCart, 
  AlertTriangle,
  DollarSign,
  Calendar
} from 'lucide-react';

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

export function Dashboard() {
  const [overview, setOverview] = useState(null);
  const [revenueData, setRevenueData] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [salesBySeller, setSalesBySeller] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [overviewRes, revenueRes, productsRes, sellersRes] = await Promise.all([
        api.get('/dashboard/overview'),
        api.get('/dashboard/revenue-by-game'),
        api.get('/dashboard/top-products'),
        api.get('/dashboard/sales-by-seller')
      ]);

      setOverview(overviewRes.data);
      setRevenueData(revenueRes.data.games || []);
      setTopProducts(productsRes.data || []);
      setSalesBySeller(sellersRes.data.sellers || {});
    } catch (error) {
      console.error('Erro ao carregar dados do dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const sellerData = Object.entries(salesBySeller).map(([name, revenue]) => ({
    name,
    revenue: parseFloat(revenue)
  }));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Visão geral do sistema de vendas</p>
      </div>

      {/* Cards de estatísticas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6">
        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-blue-100 rounded-lg flex-shrink-0">
              <DollarSign className="w-5 h-5 sm:w-6 sm:h-6 text-blue-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Receita Total</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                R$ {overview?.totalRevenue?.toFixed(2) || '0.00'}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-green-100 rounded-lg flex-shrink-0">
              <ShoppingCart className="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Total de Vendas</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                {overview?.totalSales || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-purple-100 rounded-lg flex-shrink-0">
              <Package className="w-5 h-5 sm:w-6 sm:h-6 text-purple-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Produtos</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                {overview?.totalProducts || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-yellow-100 rounded-lg flex-shrink-0">
              <AlertTriangle className="w-5 h-5 sm:w-6 sm:h-6 text-yellow-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Estoque Baixo</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                {overview?.lowStockProducts || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Gráficos */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 lg:gap-6">
        {/* Receita por Jogo */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Receita por Jogo</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={revenueData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="gameName" />
              <YAxis />
              <Tooltip formatter={(value) => `R$ ${value.toFixed(2)}`} />
              <Bar dataKey="revenue" fill="#3B82F6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Vendas por Vendedor */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Vendas por Vendedor</h3>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={sellerData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="revenue"
              >
                {sellerData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => `R$ ${value.toFixed(2)}`} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Produtos Mais Vendidos */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Produtos Mais Vendidos</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Produto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Quantidade
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Receita
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {topProducts.map((product, index) => (
                <tr key={index}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {product.productName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {product.quantity}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    R$ {product.revenue?.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
} 