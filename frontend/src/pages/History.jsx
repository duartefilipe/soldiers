import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Search, Calendar, DollarSign, ShoppingCart } from 'lucide-react';

export function History() {
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGame, setSelectedGame] = useState('');
  const [games, setGames] = useState([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [salesRes, gamesRes] = await Promise.all([
        api.get('/sales'),
        api.get('/games')
      ]);
      setSales(salesRes.data);
      setGames(gamesRes.data);
    } catch (error) {
      console.error('Erro ao carregar histórico:', error);
    } finally {
      setLoading(false);
    }
  };

  const getGameName = (gameEventId) => {
    const game = games.find(g => g.id === gameEventId);
    return game ? game.name : 'Jogo não encontrado';
  };

  const getGameDate = (gameEventId) => {
    const game = games.find(g => g.id === gameEventId);
    return game ? new Date(game.date).toLocaleDateString('pt-BR') : '';
  };

  const getTotalSale = (sale) => {
    return sale.items.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const filteredSales = sales.filter(sale => {
    const gameName = getGameName(sale.gameEvent?.id).toLowerCase();
    const searchLower = searchTerm.toLowerCase();
    const gameFilter = !selectedGame || sale.gameEvent?.id === parseInt(selectedGame);
    
    return gameFilter && (
      gameName.includes(searchLower) ||
      sale.id.toString().includes(searchLower)
    );
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Histórico de Vendas</h1>
        <p className="text-gray-600">Visualize todas as vendas realizadas</p>
      </div>

      {/* Filtros */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Buscar vendas..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input-field pl-10"
            />
          </div>
          <div>
            <select
              value={selectedGame}
              onChange={(e) => setSelectedGame(e.target.value)}
              className="input-field"
            >
              <option value="">Todos os jogos</option>
              {games.map(game => (
                <option key={game.id} value={game.id}>
                  {game.name} - {new Date(game.date).toLocaleDateString('pt-BR')}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Estatísticas */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card">
          <div className="flex items-center">
            <div className="p-3 bg-blue-100 rounded-lg">
              <ShoppingCart className="w-6 h-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total de Vendas</p>
              <p className="text-2xl font-bold text-gray-900">{sales.length}</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center">
            <div className="p-3 bg-green-100 rounded-lg">
              <DollarSign className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Receita Total</p>
              <p className="text-2xl font-bold text-gray-900">
                R$ {sales.reduce((total, sale) => total + getTotalSale(sale), 0).toFixed(2)}
              </p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center">
            <div className="p-3 bg-purple-100 rounded-lg">
              <Calendar className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Jogos com Vendas</p>
              <p className="text-2xl font-bold text-gray-900">
                {new Set(sales.map(sale => sale.gameEvent?.id)).size}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Lista de vendas */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Jogo
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Data
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Itens
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Total
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vendedor
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredSales.map((sale) => (
                <tr key={sale.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{sale.id}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {getGameName(sale.gameEvent?.id)}
                      </div>
                      <div className="text-sm text-gray-500">
                        {getGameDate(sale.gameEvent?.id)}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(sale.criadoEm).toLocaleDateString('pt-BR')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {sale.items.length} {sale.items.length === 1 ? 'item' : 'itens'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                    R$ {getTotalSale(sale).toFixed(2)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {sale.seller?.name || 'N/A'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {filteredSales.length === 0 && (
          <div className="text-center py-12">
            <ShoppingCart className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhuma venda encontrada</h3>
            <p className="mt-1 text-sm text-gray-500">
              {searchTerm || selectedGame ? 'Tente ajustar os filtros.' : 'Comece criando sua primeira venda.'}
            </p>
          </div>
        )}
      </div>

      {/* Detalhes da venda (modal ou expandir) */}
      {filteredSales.length > 0 && (
        <div className="space-y-4">
          {filteredSales.map((sale) => (
            <details key={sale.id} className="card">
              <summary className="cursor-pointer font-medium text-gray-900 hover:text-primary-600">
                Venda #{sale.id} - {getGameName(sale.gameEvent?.id)} - R$ {getTotalSale(sale).toFixed(2)}
              </summary>
              <div className="mt-4 pt-4 border-t">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <h4 className="font-medium text-gray-900 mb-2">Informações da Venda</h4>
                    <div className="space-y-1 text-sm text-gray-600">
                      <p><strong>ID:</strong> #{sale.id}</p>
                      <p><strong>Jogo:</strong> {getGameName(sale.gameEvent?.id)}</p>
                      <p><strong>Data:</strong> {new Date(sale.criadoEm).toLocaleString('pt-BR')}</p>
                      <p><strong>Vendedor:</strong> {sale.seller?.name || 'N/A'}</p>
                    </div>
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900 mb-2">Resumo</h4>
                    <div className="space-y-1 text-sm text-gray-600">
                      <p><strong>Total de itens:</strong> {sale.items.length}</p>
                      <p><strong>Quantidade total:</strong> {sale.items.reduce((sum, item) => sum + item.quantity, 0)}</p>
                      <p><strong>Valor total:</strong> R$ {getTotalSale(sale).toFixed(2)}</p>
                    </div>
                  </div>
                </div>
                
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Itens da Venda</h4>
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Produto</th>
                          <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Quantidade</th>
                          <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Preço Unit.</th>
                          <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Subtotal</th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {sale.items?.map((item, index) => (
                          <tr key={index}>
                            <td className="px-4 py-2 text-sm text-gray-900">{item.product?.name || 'Produto não encontrado'}</td>
                            <td className="px-4 py-2 text-sm text-gray-900">{item.quantity}</td>
                            <td className="px-4 py-2 text-sm text-gray-900">R$ {item.price.toFixed(2)}</td>
                            <td className="px-4 py-2 text-sm font-medium text-gray-900">
                              R$ {(item.price * item.quantity).toFixed(2)}
                            </td>
                          </tr>
                        )) || (
                          <tr>
                            <td colSpan="4" className="px-4 py-2 text-sm text-gray-500 text-center">
                              Itens não disponíveis
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </details>
          ))}
        </div>
      )}
    </div>
  );
} 