# 🚀 Instruções para Executar o Projeto Soldiers

## 📋 Pré-requisitos

- **Java 17** ou superior
- **Node.js 16** ou superior
- **PostgreSQL 12** ou superior
- **Maven** (para o backend)
- **npm** ou **yarn** (para o frontend)

## 🗄️ Configuração do Banco de Dados

1. **Instale o PostgreSQL** se ainda não tiver
2. **Crie o banco de dados:**
   ```sql
   CREATE DATABASE vendas_futebol;
   ```

## ⚙️ Configuração do Backend

1. **Navegue para a pasta backend:**
   ```bash
   cd backend
   ```

2. **Configure o banco de dados** no arquivo `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://177.203.121.234:15432/vendas_futebol
       username: postgres
       password: sua_senha_aqui
   ```

3. **Execute o projeto Spring Boot:**
   ```bash
   mvn spring-boot:run
   ```
   
   Ou se preferir usar uma IDE:
   - Abra o projeto no IntelliJ IDEA ou Eclipse
   - Execute a classe `VendasFutebolApplication`

4. **Verifique se a API está rodando:**
   - Acesse: http://177.203.121.234:8083
   - Você deve ver uma página de erro (normal, pois não há endpoint raiz)

## ⚛️ Configuração do Frontend

1. **Navegue para a pasta frontend:**
   ```bash
   cd frontend
   ```

2. **Instale as dependências:**
   ```bash
   npm install
   ```

3. **Execute o projeto React:**
   ```bash
   npm run dev
   ```

4. **Acesse a aplicação:**
   - Frontend: http://177.203.121.234:8084
   - Backend API: http://177.203.121.234:8083

## 🔐 Credenciais de Teste

**Usuário Admin:**
- Email: `admin@soldiers.com`
- Senha: `admin123`

## 📊 Funcionalidades Disponíveis

### ✅ Implementadas:
- ✅ Autenticação com JWT
- ✅ Dashboard com gráficos
- ✅ Controle de usuários (admin)
- ✅ Gerenciamento de produtos
- ✅ Controle de estoque
- ✅ Criação de eventos de jogo
- ✅ Sistema de vendas
- ✅ Relatórios analíticos

### 🚧 Em Desenvolvimento:
- 📝 Páginas de produtos
- 📝 Páginas de jogos
- 📝 Página de nova venda
- 📝 Histórico de vendas
- 📝 Gerenciamento de usuários

## 🛠️ Estrutura do Projeto

```
Soldiers/
├── backend/                    # API Spring Boot
│   ├── src/main/java/
│   │   └── com/soldiers/
│   │       ├── config/         # Configurações
│   │       ├── controller/     # Controllers REST
│   │       ├── dto/           # DTOs
│   │       ├── entity/        # Entidades JPA
│   │       ├── repository/    # Repositories
│   │       ├── service/       # Serviços
│   │       └── security/      # Segurança JWT
│   └── src/main/resources/
│       └── application.yml    # Configurações
├── frontend/                   # React + Vite
│   ├── src/
│   │   ├── components/        # Componentes React
│   │   ├── context/          # Context API
│   │   ├── pages/            # Páginas
│   │   ├── services/         # Serviços API
│   │   └── App.jsx           # App principal
│   └── package.json
└── README.md
```

## 🔧 Comandos Úteis

### Backend:
```bash
# Executar
mvn spring-boot:run

# Compilar
mvn clean compile

# Testes
mvn test

# Limpar e reinstalar
mvn clean install
```

### Frontend:
```bash
# Executar em desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Lint
npm run lint
```

## 🐛 Solução de Problemas

### Backend não inicia:
1. Verifique se o PostgreSQL está rodando
2. Confirme as credenciais no `application.yml`
3. Verifique se a porta 8080 está livre

### Frontend não conecta com o backend:
1. Verifique se o backend está rodando na porta 8080
2. Confirme se o proxy está configurado no `vite.config.js`
3. Verifique o console do navegador para erros CORS

### Erro de autenticação:
1. Verifique se o token JWT está sendo enviado
2. Confirme se o usuário existe no banco
3. Verifique os logs do backend

## 📝 Próximos Passos

1. **Implementar páginas restantes:**
   - Página de produtos
   - Página de jogos
   - Página de nova venda
   - Histórico de vendas

2. **Melhorias:**
   - Notificações de estoque baixo
   - Exportação de relatórios
   - Dark mode
   - Responsividade mobile

3. **Deploy:**
   - Configurar para produção
   - Docker
   - CI/CD

## 🤝 Contribuição

Para contribuir com o projeto:
1. Faça um fork
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📞 Suporte

Se encontrar problemas:
1. Verifique os logs do backend
2. Verifique o console do navegador
3. Confirme se todas as dependências estão instaladas
4. Verifique se o banco de dados está configurado corretamente 