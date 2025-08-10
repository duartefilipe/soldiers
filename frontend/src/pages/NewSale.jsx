import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Minus, Trash2, ShoppingCart, Calendar, Clock, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { useParams, useNavigate } from 'react-router-dom';

export function NewSale() {
  const { gameId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [products, setProducts] = useState([]);
  const [games, setGames] = useState([]);
  const [selectedGame, setSelectedGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cartItems, setCartItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (gameId) {
      const game = games.find(g => g.id === parseInt(gameId));
      if (game) {
        setSelectedGame(game);
      }
    }
  }, [gameId, games]);

  const loadData = async () => {
    try {
      const [productsRes, gamesRes] = await Promise.all([
        api.get('/products'),
        api.get('/games')
      ]);
      setProducts(productsRes.data);
      setGames(gamesRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  };

  const selectGame = (game) => {
    setSelectedGame(game);
    navigate(`/sales/${game.id}`);
  };

  const backToGameSelection = () => {
    setSelectedGame(null);
    setCartItems([]);
    navigate('/sales');
  };

  const addToCart = (product) => {
    const existingItem = cartItems.find(item => item.id === product.id);
    
    if (existingItem) {
      if (existingItem.quantity < product.stock) {
        setCartItems(cartItems.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        ));
      } else {
        toast.error('Quantidade máxima em estoque atingida');
      }
    } else {
      if (product.stock > 0) {
        setCartItems([...cartItems, { ...product, quantity: 1 }]);
      } else {
        toast.error('Produto sem estoque');
      }
    }
  };

  const updateQuantity = (productId, newQuantity) => {
    const product = products.find(p => p.id === productId);
    
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }
    
    if (newQuantity > product.stock) {
      toast.error('Quantidade maior que o estoque disponível');
      return;
    }
    
    setCartItems(cartItems.map(item =>
      item.id === productId
        ? { ...item, quantity: newQuantity }
        : item
    ));
  };

  const removeFromCart = (productId) => {
    setCartItems(cartItems.filter(item => item.id !== productId));
  };

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!selectedGame) {
      toast.error('Selecione um jogo');
      return;
    }
    
    if (cartItems.length === 0) {
      toast.error('Adicione produtos ao carrinho');
      return;
    }

    try {
      const saleData = {
        gameEventId: selectedGame.id,
        userId: user.id,
        items: cartItems.map(item => ({
          productId: item.id,
          quantity: item.quantity,
          price: item.price
        }))
      };

      await api.post('/sales', saleData);
      toast.success('Venda realizada com sucesso!');
      setCartItems([]);
    } catch (error) {
      toast.error('Erro ao realizar venda');
      console.error('Erro:', error);
    }
  };

  const getGameStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800';
      case 'FINISHED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getGameStatusText = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'Agendado';
      case 'IN_PROGRESS':
        return 'Em Andamento';
      case 'FINISHED':
        return 'Finalizado';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  };

  const availableGames = games.filter(game => 
    game.status === 'SCHEDULED' || game.status === 'IN_PROGRESS'
  );

  const filteredProducts = products.filter(product =>
    product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  // Se não há jogo selecionado, mostra a lista de jogos
  if (!selectedGame) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Vendas</h1>
          <p className="text-gray-600">Selecione um jogo para fazer vendas</p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Jogos Disponíveis</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {availableGames.map((game) => (
              <div
                key={game.id}
                onClick={() => selectGame(game)}
                className="p-4 border rounded-lg cursor-pointer transition-colors hover:border-primary-300 hover:bg-gray-50"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h4 className="font-medium text-gray-900">{game.name}</h4>
                    <p className="text-sm text-gray-600">{game.description}</p>
                    <div className="flex items-center mt-2 space-x-4 text-sm text-gray-500">
                      <div className="flex items-center">
                        <Calendar className="w-4 h-4 mr-1" />
                        {new Date(game.date).toLocaleDateString('pt-BR')}
                      </div>
                      <div className="flex items-center">
                        <Clock className="w-4 h-4 mr-1" />
                        {new Date(game.date).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
                      </div>
                    </div>
                  </div>
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getGameStatusColor(game.status)}`}>
                    {getGameStatusText(game.status)}
                  </span>
                </div>
              </div>
            ))}
          </div>

          {availableGames.length === 0 && (
            <div className="text-center py-8">
              <p className="text-gray-500">Nenhum jogo disponível para vendas</p>
            </div>
          )}
        </div>
      </div>
    );
  }

  // Se há jogo selecionado, mostra a interface de vendas
  return (
    <div className="space-y-6">
      {/* Header com informações do jogo */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={backToGameSelection}
            className="p-2 text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Vendas - {selectedGame.name}</h1>
            <p className="text-gray-600">{selectedGame.description}</p>
            <div className="flex items-center mt-1 space-x-4 text-sm text-gray-500">
              <div className="flex items-center">
                <Calendar className="w-4 h-4 mr-1" />
                {new Date(selectedGame.date).toLocaleDateString('pt-BR')}
              </div>
              <div className="flex items-center">
                <Clock className="w-4 h-4 mr-1" />
                {new Date(selectedGame.date).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
              </div>
              <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getGameStatusColor(selectedGame.status)}`}>
                {getGameStatusText(selectedGame.status)}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Lista de Produtos */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Produtos</h3>
          
          <div className="mb-4">
            <input
              type="text"
              placeholder="Buscar produtos..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div className="space-y-3 max-h-96 overflow-y-auto">
            {filteredProducts.map((product) => (
              <div key={product.id} className="flex items-center justify-between p-3 border rounded-lg">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900">{product.name}</h4>
                  <p className="text-sm text-gray-600">{product.description}</p>
                  <div className="flex items-center mt-1 space-x-4 text-sm text-gray-500">
                    <span>R$ {product.price.toFixed(2)}</span>
                    <span>Estoque: {product.stock}</span>
                  </div>
                </div>
                <button
                  onClick={() => addToCart(product)}
                  disabled={product.stock === 0}
                  className="ml-4 p-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:bg-gray-300 disabled:cursor-not-allowed"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Carrinho */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Carrinho</h3>
          
          {cartItems.length === 0 ? (
            <div className="text-center py-8">
              <ShoppingCart className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">Carrinho vazio</p>
              <p className="text-sm text-gray-400">Adicione produtos para começar</p>
            </div>
          ) : (
            <>
              <div className="space-y-3 max-h-64 overflow-y-auto">
                {cartItems.map((item) => (
                  <div key={item.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="flex-1">
                      <h4 className="font-medium text-gray-900">{item.name}</h4>
                      <p className="text-sm text-gray-600">R$ {item.price.toFixed(2)}</p>
                    </div>
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="p-1 text-gray-600 hover:text-gray-900"
                      >
                        <Minus className="w-4 h-4" />
                      </button>
                      <span className="w-8 text-center">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="p-1 text-gray-600 hover:text-gray-900"
                      >
                        <Plus className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => removeFromCart(item.id)}
                        className="ml-2 p-1 text-red-600 hover:text-red-900"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              <div className="border-t pt-4 mt-4">
                <div className="flex justify-between items-center mb-4">
                  <span className="text-lg font-semibold">Total:</span>
                  <span className="text-lg font-bold text-primary-600">
                    R$ {getTotalPrice().toFixed(2)}
                  </span>
                </div>
                
                <button
                  onClick={handleSubmit}
                  className="w-full bg-primary-600 text-white py-3 rounded-lg hover:bg-primary-700 transition-colors"
                >
                  Finalizar Venda
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
} 