import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { 
  Newspaper,
  DollarSign,
  MapPin,
  Calendar,
  TrendingUp
} from 'lucide-react';

export function Dashboard() {
  const [latestNews, setLatestNews] = useState([]);
  const [teamBudget, setTeamBudget] = useState(null);
  const [upcomingTrips, setUpcomingTrips] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [newsRes, budgetRes, tripsRes] = await Promise.all([
        api.get('/news/latest'),
        api.get('/budgets/balance'),
        api.get('/trips/upcoming')
      ]);

      setLatestNews(newsRes.data || []);
      setTeamBudget(budgetRes.data || 0);
      setUpcomingTrips(tripsRes.data || []);
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

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Bem-vindo ao sistema Soldiers</p>
      </div>

      {/* Cards de informações básicas */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-6">
        {/* Orçamento do Time */}
        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-green-100 rounded-lg flex-shrink-0">
              <DollarSign className="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Orçamento do Time</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                R$ {teamBudget?.toFixed(2) || '0.00'}
              </p>
            </div>
          </div>
        </div>

        {/* Próximas Viagens */}
        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-blue-100 rounded-lg flex-shrink-0">
              <MapPin className="w-5 h-5 sm:w-6 sm:h-6 text-blue-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Próximas Viagens</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                {upcomingTrips.length || 0}
              </p>
            </div>
          </div>
        </div>

        {/* Últimas Notícias */}
        <div className="card">
          <div className="flex items-center">
            <div className="p-2 sm:p-3 bg-purple-100 rounded-lg flex-shrink-0">
              <Newspaper className="w-5 h-5 sm:w-6 sm:h-6 text-purple-600" />
            </div>
            <div className="ml-3 sm:ml-4 min-w-0 flex-1">
              <p className="text-xs sm:text-sm font-medium text-gray-600">Últimas Notícias</p>
              <p className="text-lg sm:text-2xl font-bold text-gray-900 truncate">
                {latestNews.length || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Seção de Últimas Notícias */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
          <Newspaper className="w-5 h-5 mr-2 text-purple-600" />
          Últimas Notícias
        </h3>
        <div className="space-y-4">
          {latestNews.length > 0 ? (
            latestNews.slice(0, 5).map((news) => (
              <div key={news.id} className="border-l-4 border-purple-500 pl-4 py-2">
                <h4 className="font-medium text-gray-900">{news.title}</h4>
                <p className="text-sm text-gray-600 mt-1">{news.content}</p>
                <p className="text-xs text-gray-500 mt-2">
                  {new Date(news.criadoEm).toLocaleDateString('pt-BR')}
                </p>
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center py-4">Nenhuma notícia disponível</p>
          )}
        </div>
      </div>

      {/* Seção de Próximas Viagens */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
          <MapPin className="w-5 h-5 mr-2 text-blue-600" />
          Próximas Viagens
        </h3>
        <div className="space-y-4">
          {upcomingTrips.length > 0 ? (
            upcomingTrips.slice(0, 5).map((trip) => (
              <div key={trip.id} className="border-l-4 border-blue-500 pl-4 py-2">
                <h4 className="font-medium text-gray-900">{trip.destination}</h4>
                <p className="text-sm text-gray-600 mt-1">{trip.description}</p>
                <div className="flex items-center mt-2 text-xs text-gray-500">
                  <Calendar className="w-3 h-3 mr-1" />
                  <span>
                    {new Date(trip.startDate).toLocaleDateString('pt-BR')} - {new Date(trip.endDate).toLocaleDateString('pt-BR')}
                  </span>
                </div>
              </div>
            ))
          ) : (
            <p className="text-gray-500 text-center py-4">Nenhuma viagem programada</p>
          )}
        </div>
      </div>

      {/* Seção de Orçamento Detalhado */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
          <TrendingUp className="w-5 h-5 mr-2 text-green-600" />
          Resumo Financeiro
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="bg-green-50 p-4 rounded-lg">
            <h4 className="font-medium text-green-900">Orçamento Disponível</h4>
            <p className="text-2xl font-bold text-green-600">
              R$ {teamBudget?.toFixed(2) || '0.00'}
            </p>
          </div>
          <div className="bg-blue-50 p-4 rounded-lg">
            <h4 className="font-medium text-blue-900">Status</h4>
            <p className="text-lg font-semibold text-blue-600">
              {teamBudget > 0 ? 'Disponível' : 'Sem orçamento'}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
} 