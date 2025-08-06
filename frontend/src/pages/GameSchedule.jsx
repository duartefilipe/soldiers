import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Calendar, Clock, MapPin, ShoppingCart, Plus, Minus, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export function GameSchedule() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const gamesRes = await api.get('/games');
      setGames(gamesRes.data);
    } catch (error) {
      toast.error('Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  };

  const selectGame = (game) => {
    navigate(`/game/${game.id}/sales`);
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
        <h1 className="text-2xl font-bold text-gray-900">Agenda de Jogos</h1>
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
                      {game.startTime}
                    </div>
                  </div>
                  <div className="flex items-center mt-2">
                    <MapPin className="w-4 h-4 mr-1 text-gray-400" />
                    <span className="text-sm text-gray-500">{game.location}</span>
                  </div>
                </div>
                <span className={`px-2 py-1 text-xs font-medium rounded-full ${getGameStatusColor(game.status)}`}>
                  {getGameStatusText(game.status)}
                </span>
              </div>
            </div>
          ))}
        </div>

        {availableGames.length === 0 && (
          <div className="text-center py-8">
            <Calendar className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhum jogo disponível</h3>
            <p className="mt-1 text-sm text-gray-500">
              Não há jogos agendados ou em andamento.
            </p>
          </div>
        )}
      </div>
    </div>
  );
} 