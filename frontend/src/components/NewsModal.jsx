import { X, Calendar, Clock } from 'lucide-react';

export function NewsModal({ news, isOpen, onClose }) {
  if (!isOpen || !news) return null;

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

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header do Modal */}
        <div className="flex justify-between items-center p-6 border-b border-gray-200">
          <h2 className="text-2xl font-bold text-gray-900">Notícia</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X size={24} />
          </button>
        </div>

        {/* Conteúdo do Modal */}
        <div className="p-6">
          {/* Imagem */}
          {news.imageUrl && (
            <div className="mb-6">
              <img
                src={news.imageUrl}
                alt={news.title}
                className="w-full h-64 object-cover rounded-lg"
              />
            </div>
          )}

          {/* Título */}
          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            {news.title}
          </h1>

          {/* Data e Hora */}
          <div className="flex items-center space-x-4 text-sm text-gray-500 mb-6">
            <div className="flex items-center space-x-2">
              <Calendar className="w-4 h-4" />
              <span>{formatDate(news.criadoEm)}</span>
            </div>
            <div className="flex items-center space-x-2">
              <Clock className="w-4 h-4" />
              <span>{formatTime(news.criadoEm)}</span>
            </div>
          </div>

          {/* Conteúdo */}
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
              {news.content}
            </p>
          </div>
        </div>

        {/* Footer do Modal */}
        <div className="flex justify-end p-6 border-t border-gray-200">
          <button
            onClick={onClose}
            className="btn-primary px-6 py-2"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
} 