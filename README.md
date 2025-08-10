# 🏈 Soldiers - Sistema de Gestão de Vendas para Futebol Americano

Sistema completo de gestão de vendas para eventos de futebol americano, desenvolvido com Spring Boot e React.

## 🚀 Tecnologias

### Backend
- **Java 11** com Spring Boot 2.7.18
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** como banco de dados
- **Maven** para gerenciamento de dependências
- **Docker** para containerização

### Frontend
- **React 18** com Vite
- **Tailwind CSS** para estilização
- **React Router DOM** para navegação
- **Axios** para requisições HTTP
- **Lucide React** para ícones
- **React Hot Toast** para notificações

## 📋 Funcionalidades

### 👤 Gestão de Usuários
- Login e registro de usuários
- Controle de acesso (Admin/Normal)
- Autenticação simplificada

### 🏈 Gestão de Jogos
- Criação e edição de eventos
- Status dos jogos (Agendado, Em Andamento, Finalizado, Cancelado)
- Agenda de jogos com vendas integradas

### 📦 Gestão de Produtos
- CRUD completo de produtos
- Controle de estoque automático
- Busca e filtros

### 🛒 Sistema de Vendas
- Vendas associadas a jogos específicos
- Carrinho de compras integrado
- Atualização automática de estoque
- Histórico completo de vendas

### 📊 Dashboard
- Estatísticas de vendas
- Receita por jogo
- Produtos mais vendidos
- Vendas por vendedor

## 🛠️ Instalação e Execução

### Pré-requisitos
- Docker e Docker Compose
- Node.js 18+ (para desenvolvimento local)
- Java 11+ (para desenvolvimento local)

### Execução com Docker (Recomendado)

1. **Clone o repositório**
```bash
git clone https://github.com/duartefilipe/soldiers.git
cd soldiers
```

2. **Execute com Docker Compose**
```bash
docker-compose up --build -d
```

3. **Acesse a aplicação**
- Frontend: http://177.203.121.234:8084
- Backend: http://177.203.121.234:8083

### Configuração do Banco de Dados

O sistema está configurado para usar PostgreSQL. As configurações estão em `backend/src/main/resources/application.yml`.

**Credenciais padrão:**
- Email: `anakin@anakin.com`
- Senha: `eumesmo`

## 📁 Estrutura do Projeto

```
soldiers/
├── backend/                 # API Spring Boot
│   ├── src/main/java/
│   │   └── com/soldiers/
│   │       ├── controller/  # Controllers REST
│   │       ├── entity/      # Entidades JPA
│   │       ├── repository/  # Repositórios
│   │       ├── service/     # Lógica de negócio
│   │       └── dto/         # Data Transfer Objects
│   └── src/main/resources/
│       └── application.yml  # Configurações
├── frontend/                # Aplicação React
│   ├── src/
│   │   ├── components/      # Componentes React
│   │   ├── pages/          # Páginas da aplicação
│   │   ├── context/        # Context API
│   │   └── services/       # Serviços de API
│   └── package.json
├── docker-compose.yml       # Configuração Docker
└── README.md
```

## 🔧 Desenvolvimento Local

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📝 API Endpoints

### Autenticação
- `POST /auth/login` - Login de usuário
- `POST /auth/init-data` - Inicializar dados

### Usuários
- `GET /users` - Listar usuários
- `POST /users` - Criar usuário

### Produtos
- `GET /products` - Listar produtos
- `POST /products` - Criar produto
- `PUT /products/{id}` - Atualizar produto
- `DELETE /products/{id}` - Excluir produto

### Jogos
- `GET /games` - Listar jogos
- `POST /games` - Criar jogo
- `PUT /games/{id}` - Atualizar jogo
- `DELETE /games/{id}` - Excluir jogo

### Vendas
- `GET /sales` - Listar vendas
- `POST /sales` - Criar venda
- `GET /sales/{id}` - Buscar venda por ID

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Filipe Duarte**
- GitHub: [@duartefilipe](https://github.com/duartefilipe)

---

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório! 