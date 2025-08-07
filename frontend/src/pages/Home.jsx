import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import { Calendar, Clock, ArrowRight } from 'lucide-react';
import logoWhite from '../assets/logo-white.svg';
import { NewsModal } from '../components/NewsModal';

export function Home() {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedNews, setSelectedNews] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    loadLatestNews();
  }, []);

  const loadLatestNews = async () => {
    try {
      const response = await api.get('/news/latest');
      setNews(response.data);
    } catch (error) {
      console.error('Erro ao carregar notícias:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleNewsClick = (newsItem) => {
    setSelectedNews(newsItem);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedNews(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-primary-100 flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-2">
            <div className="flex items-center space-x-3">
              <img src={logoWhite} alt="Soldiers Logo" className="w-6 h-6" />
              <div>
                <h1 className="text-base font-bold text-gray-900">Soldiers</h1>
                <p className="text-xs text-gray-600">Futebol Americano</p>
              </div>
            </div>
            <Link
              to="/login"
              className="btn-primary px-3 py-1 text-xs"
            >
              Área Administrativa
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-700 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold">
            Bem-vindo ao Soldiers
          </h2>
        </div>
      </section>

      {/* News Section */}
      <section className="py-12 flex-1">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-8">
            <h3 className="text-2xl font-bold text-gray-900 mb-3">
              Últimas Notícias
            </h3>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Fique por dentro das novidades do time Soldiers
            </p>
          </div>

          {loading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Carregando notícias...</p>
            </div>
          ) : news.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-600">Nenhuma notícia disponível no momento.</p>
            </div>
          ) : (
                         <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
               {news.map((item) => (
                 <article 
                   key={item.id} 
                   className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                   onClick={() => handleNewsClick(item)}
                 >
                  {item.imageUrl && (
                    <div className="aspect-w-16 aspect-h-9">
                      <img
                        src={item.imageUrl}
                        alt={item.title}
                        className="w-full h-48 object-cover"
                      />
                    </div>
                  )}
                  <div className="p-6">
                    <h4 className="text-xl font-semibold text-gray-900 mb-3 line-clamp-2">
                      {item.title}
                    </h4>
                    <p className="text-gray-600 mb-4 line-clamp-3">
                      {item.content}
                    </p>
                    <div className="flex items-center justify-between text-sm text-gray-500">
                      <div className="flex items-center space-x-2">
                        <Calendar className="w-4 h-4" />
                        <span>{formatDate(item.criadoEm)}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Clock className="w-4 h-4" />
                        <span>{formatTime(item.criadoEm)}</span>
                      </div>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </section>

             {/* Footer */}
       <footer className="bg-gray-800 text-white py-6 mt-auto">
         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
           <p>&copy; 2024 Soldiers - Sistema de Vendas. Todos os direitos reservados.</p>
         </div>
       </footer>

       {/* Modal de Notícia */}
       <NewsModal
         news={selectedNews}
         isOpen={isModalOpen}
         onClose={handleCloseModal}
       />
     </div>
   );
 } 