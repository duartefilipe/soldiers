import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Minus, Trash2, ShoppingCart, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { useParams, useNavigate } from 'react-router-dom';

export function GameSales() {
  const { gameId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [game, setGame] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cartItems, setCartItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadData();
  }, [gameId]);

  const loadData = async () => {
    try {
      const [gameRes, productsRes] = await Promise.all([
        api.get(`/games/${gameId}`),
        api.get('/products')
      ]);
      setGame(gameRes.data);
      setProducts(productsRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados');
      navigate('/schedule');
    } finally {
      setLoading(false);
    }
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
    
    if (cartItems.length === 0) {
      toast.error('Adicione produtos ao carrinho');
      return;
    }

    try {
      const saleData = {
        gameEventId: parseInt(gameId),
        userId: user.id,
        items: cartItems.map(item => ({
          productId: item.id,
          quantity: item.quantity
        }))
      };

      await api.post('/sales', saleData);
      toast.success('Venda realizada com sucesso!');
      
      // Limpar carrinho
      setCartItems([]);
      setSearchTerm('');
      
      // Recarregar produtos para atualizar estoque
      await loadData();
    } catch (error) {
      toast.error('Erro ao realizar venda');
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

  const filteredProducts = products
    .filter(product =>
      product.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
      product.stock > 0
    )
    .sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'));

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!game) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900">Jogo não encontrado</h3>
        <button
          onClick={() => navigate('/schedule')}
          className="btn-primary mt-4"
        >
          Voltar para Agenda
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/schedule')}
            className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{game.name}</h1>
            <p className="text-gray-600">{game.description}</p>
          </div>
        </div>
        <span className={`px-3 py-1 text-sm font-medium rounded-full ${getGameStatusColor(game.status)}`}>
          {getGameStatusText(game.status)}
        </span>
      </div>

      {/* Informações do Jogo */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 text-sm">
          <div>
            <span className="font-medium text-gray-700">Data:</span>
            <p className="text-gray-900">{new Date(game.date).toLocaleDateString('pt-BR')}</p>
          </div>
          <div>
            <span className="font-medium text-gray-700">Horário:</span>
            <p className="text-gray-900">{game.startTime} - {game.endTime}</p>
          </div>
          <div>
            <span className="font-medium text-gray-700">Local:</span>
            <p className="text-gray-900">{game.location}</p>
          </div>
          <div>
            <span className="font-medium text-gray-700">Status:</span>
            <p className="text-gray-900">{getGameStatusText(game.status)}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Lista de Produtos */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Produtos Disponíveis</h3>
          
          {/* Busca */}
          <div className="mb-4">
            <input
              type="text"
              placeholder="Buscar produtos..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input-field"
            />
          </div>

          {/* Lista de produtos */}
          <div className="space-y-3 max-h-96 overflow-y-auto">
            {filteredProducts.map((product) => (
              <div key={product.id} className="flex items-center justify-between p-3 border rounded-lg">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900">{product.name}</h4>
                  <p className="text-sm text-gray-600">{product.description}</p>
                  <p className="text-sm text-gray-500">Estoque: {product.stock}</p>
                </div>
                <div className="flex items-center space-x-3">
                  <span className="font-semibold text-gray-900">
                    R$ {product.price.toFixed(2)}
                  </span>
                  <button
                    type="button"
                    onClick={() => addToCart(product)}
                    className="btn-primary flex items-center"
                  >
                    <Plus className="w-4 h-4 mr-1" />
                    Adicionar
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Carrinho */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
            <ShoppingCart className="w-5 h-5 mr-2" />
            Carrinho
          </h3>

          {cartItems.length === 0 ? (
            <p className="text-gray-500 text-center py-8">Nenhum produto no carrinho</p>
          ) : (
            <div className="space-y-4">
              {cartItems.map((item) => (
                <div key={item.id} className="flex items-center justify-between p-3 border rounded-lg">
                  <div className="flex-1">
                    <h4 className="font-medium text-gray-900">{item.name}</h4>
                    <p className="text-sm text-gray-600">R$ {item.price.toFixed(2)} cada</p>
                  </div>
                  <div className="flex items-center space-x-3">
                    <div className="flex items-center space-x-2">
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="p-1 rounded-full bg-gray-200 hover:bg-gray-300"
                      >
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="w-8 text-center">{item.quantity}</span>
                      <button
                        type="button"
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="p-1 rounded-full bg-gray-200 hover:bg-gray-300"
                      >
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                    <span className="font-semibold text-gray-900">
                      R$ {(item.price * item.quantity).toFixed(2)}
                    </span>
                    <button
                      type="button"
                      onClick={() => removeFromCart(item.id)}
                      className="text-red-600 hover:text-red-900"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}

              <div className="border-t pt-4">
                <div className="flex justify-between items-center text-lg font-semibold">
                  <span>Total:</span>
                  <span>R$ {getTotalPrice().toFixed(2)}</span>
                </div>
              </div>

              <button
                onClick={handleSubmit}
                className="btn-primary w-full"
              >
                Finalizar Venda
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 