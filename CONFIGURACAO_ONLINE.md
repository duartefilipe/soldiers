# 🌐 Configuração para Ambiente Online

## ✅ Mudanças Realizadas

### 1. **API Service Atualizado** (`frontend/src/services/api.js`)

O frontend agora chama diretamente o backend online via Zrok com header especial:

```javascript
// Configuração da API com fallback para desenvolvimento
const API_URL = import.meta.env.VITE_API_URL || 'https://soldiersservice.share.zrok.io';

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'skip_zrok_interstitial': 'true'
  }
});

// Interceptor para garantir que o header está sempre presente
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
```

### 2. **Vite Config Otimizado** (`frontend/vite.config.js`)

Removido o proxy local e configurado para hosts permitidos:

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 8084,
    host: '0.0.0.0',
    allowedHosts: [
      'localhost',
      '127.0.0.1',
      'soldiers.share.zrok.io',
      'soldiersservice.share.zrok.io',
      '.zrok.io'
    ]
  }
})
```

## 🔄 Como Funciona Agora

### **Fluxo de Comunicação:**
```
Frontend (soldiers.share.zrok.io) 
    ↓ (com header skip_zrok_interstitial: true)
Backend (soldiersservice.share.zrok.io)
    ↓
Banco de Dados PostgreSQL
```

### **Headers Enviados:**
- `Content-Type: application/json`
- `skip_zrok_interstitial: true` (para pular tela do Zrok)
- `Authorization: Bearer {token}` (quando autenticado)

### **URLs Configuradas:**
- **Frontend:** `https://soldiers.share.zrok.io`
- **Backend:** `https://soldiersservice.share.zrok.io`

## 🚀 Como Testar

### **1. Teste do Frontend:**
```
https://soldiers.share.zrok.io
```

### **2. Teste do Login:**
```bash
curl -X POST https://soldiersservice.share.zrok.io/auth/login \
  -H "Content-Type: application/json" \
  -H "skip_zrok_interstitial: true" \
  -d '{
    "email": "admin@soldiers.com",
    "password": "admin123456"
  }'
```

### **3. Teste de Conectividade:**
```bash
# Testar se o backend está respondendo
curl -I https://soldiersservice.share.zrok.io/auth/test \
  -H "skip_zrok_interstitial: true"
```

## 🔧 Configuração de Ambiente

### **Para Desenvolvimento Local:**
Crie um arquivo `.env` na pasta `frontend/`:

```env
VITE_API_URL=http://localhost:8083
```

### **Para Produção Online:**
```env
VITE_API_URL=https://soldiersservice.share.zrok.io
```

## 📋 Endpoints Disponíveis

### **Autenticação:**
- `POST /auth/login` - Login de usuário
- `POST /auth/register` - Registro de usuário
- `GET /auth/users` - Listar usuários

### **Produtos:**
- `GET /products` - Listar produtos
- `POST /products` - Criar produto
- `GET /products/search?name=...` - Buscar produtos

### **Vendas:**
- `GET /sales` - Listar vendas
- `POST /sales` - Criar venda
- `GET /sales/game/{id}` - Vendas por jogo

### **Jogos:**
- `GET /games` - Listar jogos
- `POST /games` - Criar jogo
- `GET /games/upcoming` - Próximos jogos

### **Orçamentos:**
- `GET /budgets` - Listar orçamentos
- `POST /budgets` - Criar orçamento
- `GET /budgets/balance` - Saldo atual

## 🐛 Solução de Problemas

### **Erro de CORS:**
Se houver problemas de CORS, verifique se o backend está configurado para aceitar requisições do domínio do frontend.

### **Erro de Conexão:**
```bash
# Verificar se o zrok está ativo
zrok tunnel list

# Verificar logs do backend
docker-compose logs backend
```

### **Erro de Autenticação:**
1. Verifique se o token está sendo enviado corretamente
2. Confirme se o usuário existe no banco
3. Verifique os logs do backend

### **Problema com Zrok Interstitial:**
Se ainda aparecer a tela do Zrok, verifique se o header `skip_zrok_interstitial` está sendo enviado em todas as requisições.

## 🔄 Revertendo para Desenvolvimento Local

Se precisar voltar para desenvolvimento local:

### **1. Atualizar API Service:**
```javascript
const API_URL = 'http://localhost:8083';
```

### **2. Restaurar Proxy no Vite:**
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://backend:8083',
      changeOrigin: true,
      secure: false,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 🎯 Resultado Esperado

Após essas configurações:
- ✅ Frontend acessível via `https://soldiers.share.zrok.io`
- ✅ Backend respondendo em `https://soldiersservice.share.zrok.io`
- ✅ Login funcionando com usuário admin
- ✅ Todas as funcionalidades operacionais
- ✅ Sem tela de intersticial do Zrok

---

**💡 Dica:** O header `skip_zrok_interstitial: true` é necessário para pular a tela de aviso do Zrok e ir direto para a aplicação.
