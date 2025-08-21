import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { Plus, Edit, Trash2, Calendar, Clock } from 'lucide-react';
import toast from 'react-hot-toast';
import PermissionGate from '../components/PermissionGate';

export function News() {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingNews, setEditingNews] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    imageUrl: ''
  });

  useEffect(() => {
    loadNews();
  }, []);

  const loadNews = async () => {
    try {
      const response = await api.get('/news');
      setNews(response.data);
    } catch (error) {
      toast.error('Erro ao carregar notícias');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      // Limpar URL do Google se necessário
      let cleanImageUrl = formData.imageUrl;
      if (cleanImageUrl && cleanImageUrl.includes('google.com/url')) {
        try {
          const url = new URL(cleanImageUrl);
          const urlParam = url.searchParams.get('url');
          if (urlParam) {
            cleanImageUrl = decodeURIComponent(urlParam);
          }
        } catch (error) {
          console.warn('Erro ao processar URL do Google:', error);
        }
      }
      
      const dataToSend = {
        ...formData,
        imageUrl: cleanImageUrl
      };
      
      if (editingNews) {
        await api.put(`/news/${editingNews.id}`, dataToSend);
        toast.success('Notícia atualizada com sucesso!');
      } else {
        await api.post('/news', dataToSend);
        toast.success('Notícia criada com sucesso!');
      }
      
      setShowForm(false);
      setEditingNews(null);
      setFormData({ title: '', content: '', imageUrl: '' });
      loadNews();
    } catch (error) {
      toast.error('Erro ao salvar notícia');
    }
  };

  const handleEdit = (news) => {
    setEditingNews(news);
    setFormData({
      title: news.title,
      content: news.content,
      imageUrl: news.imageUrl || ''
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir esta notícia?')) {
      try {
        await api.delete(`/news/${id}`);
        toast.success('Notícia excluída com sucesso!');
        loadNews();
      } catch (error) {
        toast.error('Erro ao excluir notícia');
      }
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  };

  const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Gerenciar Notícias</h1>
        <PermissionGate resource="NEWS" action="EDIT">
          <button
            onClick={() => setShowForm(true)}
            className="btn-primary flex items-center space-x-2"
          >
            <Plus className="w-5 h-5" />
            <span>Nova Notícia</span>
          </button>
        </PermissionGate>
      </div>

      {/* Form */}
      {showForm && (
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold mb-4">
            {editingNews ? 'Editar Notícia' : 'Nova Notícia'}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Título *
              </label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                className="input-field"
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Conteúdo *
              </label>
              <textarea
                value={formData.content}
                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                className="input-field min-h-[120px]"
                required
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                URL da Imagem (opcional)
              </label>
              <input
                type="url"
                value={formData.imageUrl}
                onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                className="input-field"
                placeholder="https://exemplo.com/imagem.jpg"
              />
            </div>
            
            <div className="flex space-x-4">
              <button type="submit" className="btn-primary">
                {editingNews ? 'Atualizar' : 'Criar'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowForm(false);
                  setEditingNews(null);
                  setFormData({ title: '', content: '', imageUrl: '' });
                }}
                className="btn-secondary"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* News List */}
      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando notícias...</p>
        </div>
      ) : news.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600">Nenhuma notícia encontrada.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {news.map((item) => (
            <div key={item.id} className="bg-white rounded-lg shadow-md overflow-hidden">
              {item.imageUrl && (
                <img
                  src={item.imageUrl}
                  alt={item.title}
                  className="w-full h-48 object-cover"
                />
              )}
              <div className="p-6">
                <h3 className="text-xl font-semibold text-gray-900 mb-3">
                  {item.title}
                </h3>
                <p className="text-gray-600 mb-4 line-clamp-3">
                  {item.content}
                </p>
                
                <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                  <div className="flex items-center space-x-2">
                    <Calendar className="w-4 h-4" />
                    <span>{formatDate(item.criadoEm)}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4" />
                    <span>{formatTime(item.criadoEm)}</span>
                  </div>
                </div>
                
                <div className="flex space-x-2">
                  <PermissionGate resource="NEWS" action="EDIT">
                    <button
                      onClick={() => handleEdit(item)}
                      className="btn-secondary flex items-center space-x-1"
                    >
                      <Edit className="w-4 h-4" />
                      <span>Editar</span>
                    </button>
                  </PermissionGate>
                  <PermissionGate resource="NEWS" action="EDIT">
                    <button
                      onClick={() => handleDelete(item.id)}
                      className="btn-danger flex items-center space-x-1"
                    >
                      <Trash2 className="w-4 h-4" />
                      <span>Excluir</span>
                    </button>
                  </PermissionGate>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
} 