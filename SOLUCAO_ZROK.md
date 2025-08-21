# 🔧 Solução para Erro do Zrok + Vite

## 🚨 Problema Identificado

O erro que você está enfrentando é comum quando se usa **Zrok** com **Vite**. O Vite bloqueia hosts não autorizados por segurança.

## ✅ Solução Aplicada

### 1. **Configuração do `vite.config.js` Atualizada**

O arquivo foi atualizado para incluir os hosts permitidos:

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

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
    ],
    proxy: {
      '/api': {
        target: 'http://backend:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 2. **Reiniciar o Servidor Frontend**

Após a alteração, você precisa reiniciar o servidor de desenvolvimento:

```bash
# Parar o servidor atual (Ctrl+C)
# Depois executar:
cd frontend
npm run dev
```

## 🔄 Passos para Resolver

### **Opção 1: Reiniciar o Frontend**
```bash
# 1. Parar o servidor frontend (Ctrl+C)
# 2. Navegar para a pasta frontend
cd frontend

# 3. Reiniciar o servidor
npm run dev
```

### **Opção 2: Usar Docker (Recomendado)**
```bash
# 1. Parar os containers
docker-compose down

# 2. Reconstruir e iniciar
docker-compose up --build -d
```

### **Opção 3: Configuração Manual**
Se ainda tiver problemas, você pode adicionar manualmente no `vite.config.js`:

```javascript
server: {
  allowedHosts: 'all' // ⚠️ CUIDADO: Remove todas as restrições de host
}
```

## 🌐 Testando a Conexão

### **1. Teste o Frontend:**
```
https://soldiers.share.zrok.io
```

### **2. Teste o Backend:**
```bash
curl -X POST https://soldiersservice.share.zrok.io/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@soldiers.com",
    "password": "admin123456"
  }'
```

## 🔍 Verificações Adicionais

### **1. Verificar se o Zrok está funcionando:**
```bash
# Verificar status do zrok
zrok status

# Listar túneis ativos
zrok tunnel list
```

### **2. Verificar logs do frontend:**
```bash
# Se usando Docker
docker-compose logs frontend

# Se local
# Verificar console do navegador (F12)
```

### **3. Verificar conectividade:**
```bash
# Testar se o zrok está respondendo
curl -I https://soldiers.share.zrok.io
```

## 🚀 Configuração Alternativa (Se necessário)

Se ainda houver problemas, você pode usar uma configuração mais permissiva:

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 8084,
    host: '0.0.0.0',
    allowedHosts: 'all', // Permite todos os hosts
    cors: true, // Habilita CORS
    proxy: {
      '/api': {
        target: 'http://backend:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

## 📞 Próximos Passos

1. **Reinicie o servidor frontend**
2. **Teste o acesso:** `https://soldiers.share.zrok.io`
3. **Teste o login:** Use o comando curl fornecido
4. **Verifique os logs** se houver problemas

## 🎯 Resultado Esperado

Após aplicar essas mudanças, você deve conseguir:
- ✅ Acessar o frontend via `https://soldiers.share.zrok.io`
- ✅ Fazer login com o usuário admin
- ✅ Navegar pelo sistema normalmente

---

**💡 Dica:** Se ainda tiver problemas, verifique se o Zrok está configurado corretamente e se os túneis estão ativos.
