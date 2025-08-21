import axios from 'axios';

// Configuração da API com fallback para desenvolvimento
const API_URL = import.meta.env.VITE_API_URL || 'https://soldiersservice.share.zrok.io';

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'skip_zrok_interstitial': 'true'
  }
});

export { api };
export default api;

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Garantir que o header skip_zrok_interstitial está sempre presente
    config.headers['skip_zrok_interstitial'] = 'true';
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros de resposta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
); 