-- =====================================================
-- SCRIPT DE CRIAÇÃO E POPULAÇÃO DO BANCO DE DADOS
-- Sistema Soldiers - Gestão de Vendas para Futebol Americano
-- =====================================================

-- Habilitar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- CRIAÇÃO DAS TABELAS
-- =====================================================

-- Tabela de perfis de usuário
CREATE TABLE IF NOT EXISTS tb_profile (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de permissões de perfil
CREATE TABLE IF NOT EXISTS tb_profile_permission (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL REFERENCES tb_profile(id),
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de relacionamento usuário-perfil
CREATE TABLE IF NOT EXISTS tb_user_profile (
    user_id BIGINT NOT NULL REFERENCES tb_user(id),
    profile_id BIGINT NOT NULL REFERENCES tb_profile(id),
    PRIMARY KEY (user_id, profile_id)
);

-- Tabela de produtos
CREATE TABLE IF NOT EXISTS tb_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de eventos de jogo
CREATE TABLE IF NOT EXISTS tb_game_event (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de times
CREATE TABLE IF NOT EXISTS tb_team (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de jogadores
CREATE TABLE IF NOT EXISTS tb_player (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(100) NOT NULL,
    number VARCHAR(10) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de relacionamento time-jogador
CREATE TABLE IF NOT EXISTS tb_team_player (
    team_id BIGINT NOT NULL REFERENCES tb_team(id),
    player_id BIGINT NOT NULL REFERENCES tb_player(id),
    PRIMARY KEY (team_id, player_id)
);

-- Tabela de vendas
CREATE TABLE IF NOT EXISTS tb_sale (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES tb_user(id),
    game_event_id BIGINT NOT NULL REFERENCES tb_game_event(id),
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deletado_em TIMESTAMP
);

-- Tabela de itens de venda
CREATE TABLE IF NOT EXISTS tb_sale_item (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL REFERENCES tb_sale(id),
    product_id BIGINT NOT NULL REFERENCES tb_product(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Tabela de orçamentos
CREATE TABLE IF NOT EXISTS budgets (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    date TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES tb_user(id),
    notes TEXT
);

-- Tabela de viagens
CREATE TABLE IF NOT EXISTS trips (
    id BIGSERIAL PRIMARY KEY,
    destination VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    departure_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
    total_cost DECIMAL(10,2) DEFAULT 0,
    initial_cost DECIMAL(10,2) DEFAULT 0,
    user_id BIGINT REFERENCES tb_user(id),
    notes TEXT
);

-- Tabela de orçamentos de viagem
CREATE TABLE IF NOT EXISTS trip_budgets (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    date TIMESTAMP NOT NULL,
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    user_id BIGINT REFERENCES tb_user(id),
    notes TEXT
);

-- Tabela de despesas de viagem
CREATE TABLE IF NOT EXISTS trip_expenses (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    date TIMESTAMP NOT NULL,
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    user_id BIGINT REFERENCES tb_user(id),
    notes TEXT
);

-- Tabela de relacionamento viagem-jogador
CREATE TABLE IF NOT EXISTS tb_trip_player (
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    player_id BIGINT NOT NULL REFERENCES tb_player(id),
    PRIMARY KEY (trip_id, player_id)
);

-- Tabela de relacionamento viagem-time
CREATE TABLE IF NOT EXISTS tb_trip_team (
    trip_id BIGINT NOT NULL REFERENCES trips(id),
    team_id BIGINT NOT NULL REFERENCES tb_team(id),
    PRIMARY KEY (trip_id, team_id)
);

-- Tabela de notícias
CREATE TABLE IF NOT EXISTS tb_news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image_url VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    deletado_em TIMESTAMP
);

-- =====================================================
-- POPULAÇÃO COM DADOS DE EXEMPLO
-- =====================================================

-- Inserir perfis
INSERT INTO tb_profile (name, description) VALUES
('ADMIN', 'Administrador do sistema com acesso total'),
('NORMAL', 'Usuário normal com acesso limitado'),
('VENDEDOR', 'Vendedor com acesso a vendas e produtos'),
('GERENTE', 'Gerente com acesso a relatórios e gestão');

-- Inserir permissões para o perfil ADMIN
INSERT INTO tb_profile_permission (profile_id, resource, action) VALUES
(1, 'USERS', 'VIEW'),
(1, 'USERS', 'EDIT'),
(1, 'PRODUCTS', 'VIEW'),
(1, 'PRODUCTS', 'EDIT'),
(1, 'SALES', 'VIEW'),
(1, 'SALES', 'EDIT'),
(1, 'GAMES', 'VIEW'),
(1, 'GAMES', 'EDIT'),
(1, 'BUDGETS', 'VIEW'),
(1, 'BUDGETS', 'EDIT'),
(1, 'TRIPS', 'VIEW'),
(1, 'TRIPS', 'EDIT'),
(1, 'TEAMS', 'VIEW'),
(1, 'TEAMS', 'EDIT'),
(1, 'PLAYERS', 'VIEW'),
(1, 'PLAYERS', 'EDIT'),
(1, 'NEWS', 'VIEW'),
(1, 'NEWS', 'EDIT');

-- Inserir permissões para o perfil NORMAL
INSERT INTO tb_profile_permission (profile_id, resource, action) VALUES
(2, 'PRODUCTS', 'VIEW'),
(2, 'SALES', 'VIEW'),
(2, 'GAMES', 'VIEW'),
(2, 'TEAMS', 'VIEW'),
(2, 'PLAYERS', 'VIEW'),
(2, 'NEWS', 'VIEW');

-- Inserir permissões para o perfil VENDEDOR
INSERT INTO tb_profile_permission (profile_id, resource, action) VALUES
(3, 'PRODUCTS', 'VIEW'),
(3, 'SALES', 'VIEW'),
(3, 'SALES', 'EDIT'),
(3, 'GAMES', 'VIEW'),
(3, 'NEWS', 'VIEW');

-- Inserir permissões para o perfil GERENTE
INSERT INTO tb_profile_permission (profile_id, resource, action) VALUES
(4, 'USERS', 'VIEW'),
(4, 'PRODUCTS', 'VIEW'),
(4, 'PRODUCTS', 'EDIT'),
(4, 'SALES', 'VIEW'),
(4, 'SALES', 'EDIT'),
(4, 'GAMES', 'VIEW'),
(4, 'GAMES', 'EDIT'),
(4, 'BUDGETS', 'VIEW'),
(4, 'BUDGETS', 'EDIT'),
(4, 'TRIPS', 'VIEW'),
(4, 'TRIPS', 'EDIT'),
(4, 'TEAMS', 'VIEW'),
(4, 'TEAMS', 'EDIT'),
(4, 'PLAYERS', 'VIEW'),
(4, 'PLAYERS', 'EDIT'),
(4, 'NEWS', 'VIEW'),
(4, 'NEWS', 'EDIT');

-- Inserir usuário admin (senha: admin123456)
INSERT INTO tb_user (name, email, password) VALUES
('Administrador', 'admin@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa');

-- Associar usuário admin ao perfil ADMIN
INSERT INTO tb_user_profile (user_id, profile_id) VALUES (1, 1);

-- Inserir outros usuários de exemplo
INSERT INTO tb_user (name, email, password) VALUES
('João Silva', 'joao@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'),
('Maria Santos', 'maria@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'),
('Pedro Costa', 'pedro@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa');

-- Associar usuários aos perfis
INSERT INTO tb_user_profile (user_id, profile_id) VALUES
(2, 3), -- João como VENDEDOR
(3, 4), -- Maria como GERENTE
(4, 2); -- Pedro como NORMAL

-- Inserir produtos
INSERT INTO tb_product (name, description, price, stock) VALUES
('Camisa Oficial Soldiers', 'Camisa oficial do time Soldiers', 89.90, 50),
('Boné Soldiers', 'Boné com logo do time', 35.00, 30),
('Caneca Soldiers', 'Caneca personalizada do time', 25.00, 40),
('Chaveiro Soldiers', 'Chaveiro com logo do time', 15.00, 60),
('Agenda Soldiers', 'Agenda personalizada do time', 45.00, 25),
('Mochila Soldiers', 'Mochila esportiva do time', 120.00, 15),
('Garrafa Soldiers', 'Garrafa de água personalizada', 30.00, 35),
('Tênis Soldiers', 'Tênis esportivo do time', 180.00, 20);

-- Inserir eventos de jogo
INSERT INTO tb_game_event (name, description, date, start_time, end_time, location, status) VALUES
('Soldiers vs Warriors', 'Jogo contra o time Warriors', '2024-02-15', '19:00:00', '21:00:00', 'Estádio Municipal', 'SCHEDULED'),
('Soldiers vs Eagles', 'Jogo contra o time Eagles', '2024-02-22', '20:00:00', '22:00:00', 'Arena Sports', 'SCHEDULED'),
('Soldiers vs Lions', 'Jogo contra o time Lions', '2024-01-28', '18:30:00', '20:30:00', 'Estádio Municipal', 'FINISHED'),
('Soldiers vs Tigers', 'Jogo contra o time Tigers', '2024-01-15', '19:00:00', '21:00:00', 'Arena Sports', 'FINISHED');

-- Inserir times
INSERT INTO tb_team (name, description) VALUES
('Soldiers Principal', 'Time principal dos Soldiers'),
('Soldiers Sub-20', 'Time sub-20 dos Soldiers'),
('Soldiers Feminino', 'Time feminino dos Soldiers'),
('Soldiers Veteranos', 'Time de veteranos dos Soldiers');

-- Inserir jogadores
INSERT INTO tb_player (name, position, number, description) VALUES
('Carlos Oliveira', 'QB', '12', 'Quarterback titular'),
('Roberto Silva', 'RB', '23', 'Running back principal'),
('André Santos', 'WR', '84', 'Wide receiver'),
('Lucas Costa', 'TE', '88', 'Tight end'),
('Fernando Lima', 'OL', '72', 'Offensive lineman'),
('Rafael Pereira', 'DL', '95', 'Defensive lineman'),
('Thiago Alves', 'LB', '54', 'Linebacker'),
('Marcos Ferreira', 'DB', '21', 'Defensive back'),
('Diego Rodrigues', 'K', '3', 'Kicker'),
('Paulo Martins', 'P', '8', 'Punter');

-- Associar jogadores aos times
INSERT INTO tb_team_player (team_id, player_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), -- Soldiers Principal
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10); -- Soldiers Sub-20

-- Inserir vendas de exemplo
INSERT INTO tb_sale (user_id, game_event_id, total_amount) VALUES
(2, 3, 450.00),
(2, 4, 320.00),
(3, 3, 280.00);

-- Inserir itens de venda
INSERT INTO tb_sale_item (sale_id, product_id, quantity, price) VALUES
(1, 1, 3, 89.90),
(1, 2, 2, 35.00),
(1, 3, 1, 25.00),
(2, 1, 2, 89.90),
(2, 4, 3, 15.00),
(2, 5, 1, 45.00),
(3, 2, 4, 35.00),
(3, 6, 1, 120.00);

-- Inserir orçamentos
INSERT INTO budgets (description, amount, type, date, user_id, notes) VALUES
('Venda de produtos - Jogo 1', 450.00, 'INCOME', '2024-01-28 21:30:00', 2, 'Vendas do jogo contra Lions'),
('Venda de produtos - Jogo 2', 320.00, 'INCOME', '2024-01-15 21:00:00', 2, 'Vendas do jogo contra Tigers'),
('Compra de uniformes', -1200.00, 'EXPENSE', '2024-01-10 14:00:00', 1, 'Compra de novos uniformes'),
('Doação de patrocinador', 5000.00, 'INCOME', '2024-01-05 10:00:00', 1, 'Doação da empresa ABC'),
('Compra de equipamentos', -800.00, 'EXPENSE', '2024-01-03 16:00:00', 1, 'Compra de equipamentos de treino');

-- Inserir viagens
INSERT INTO trips (destination, description, departure_date, return_date, status, total_cost, initial_cost, user_id, notes) VALUES
('São Paulo', 'Campeonato Estadual', '2024-03-15 08:00:00', '2024-03-17 18:00:00', 'PLANNED', 0, 1500.00, 1, 'Viagem para campeonato estadual'),
('Rio de Janeiro', 'Torneio Interestadual', '2024-04-10 07:00:00', '2024-04-12 19:00:00', 'PLANNED', 0, 2000.00, 1, 'Torneio interestadual'),
('Belo Horizonte', 'Amistoso', '2024-02-05 09:00:00', '2024-02-05 23:00:00', 'COMPLETED', 1200.00, 800.00, 1, 'Jogo amistoso realizado');

-- Inserir orçamentos de viagem
INSERT INTO trip_budgets (description, amount, type, date, trip_id, user_id, notes) VALUES
('Passagens', 800.00, 'EXPENSE', '2024-03-15 08:00:00', 1, 1, 'Passagens de ida e volta'),
('Hospedagem', 600.00, 'EXPENSE', '2024-03-15 08:00:00', 1, 1, 'Hospedagem para 2 noites'),
('Alimentação', 200.00, 'EXPENSE', '2024-03-15 08:00:00', 1, 1, 'Refeições da equipe'),
('Transporte local', 100.00, 'EXPENSE', '2024-03-15 08:00:00', 1, 1, 'Transporte local em SP');

-- Inserir despesas de viagem
INSERT INTO trip_expenses (description, amount, date, trip_id, user_id, notes) VALUES
('Passagens', 800.00, '2024-02-05 09:00:00', 3, 1, 'Passagens de ida e volta'),
('Hospedagem', 300.00, '2024-02-05 09:00:00', 3, 1, 'Hospedagem para 1 dia'),
('Alimentação', 100.00, '2024-02-05 09:00:00', 3, 1, 'Refeições da equipe');

-- Associar jogadores e times às viagens
INSERT INTO tb_trip_player (trip_id, player_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

INSERT INTO tb_trip_team (trip_id, team_id) VALUES
(1, 1),
(2, 2);

-- Inserir notícias
INSERT INTO tb_news (title, content, image_url) VALUES
('Soldiers vence Lions por 28-14', 'Em uma partida emocionante, o time Soldiers conseguiu uma vitória importante contra o Lions, consolidando sua posição na tabela.', 'https://example.com/news1.jpg'),
('Novo uniforme será lançado', 'O time Soldiers apresentará seu novo uniforme na próxima partida. O design foi criado em parceria com uma marca esportiva renomada.', 'https://example.com/news2.jpg'),
('Treino especial para o próximo jogo', 'A equipe realizou um treino especial focado na estratégia para o próximo jogo contra o Warriors.', 'https://example.com/news3.jpg'),
('Soldiers classificado para semifinal', 'Com a vitória de ontem, o time Soldiers garantiu sua classificação para a semifinal do campeonato.', 'https://example.com/news4.jpg');

-- =====================================================
-- CRIAÇÃO DE ÍNDICES PARA MELHOR PERFORMANCE
-- =====================================================

CREATE INDEX idx_user_email ON tb_user(email);
CREATE INDEX idx_user_active ON tb_user(active);
CREATE INDEX idx_product_name ON tb_product(name);
CREATE INDEX idx_product_stock ON tb_product(stock);
CREATE INDEX idx_game_event_date ON tb_game_event(date);
CREATE INDEX idx_game_event_status ON tb_game_event(status);
CREATE INDEX idx_sale_date ON tb_sale(criado_em);
CREATE INDEX idx_sale_user ON tb_sale(user_id);
CREATE INDEX idx_sale_game ON tb_sale(game_event_id);
CREATE INDEX idx_budget_date ON budgets(date);
CREATE INDEX idx_budget_type ON budgets(type);
CREATE INDEX idx_trip_status ON trips(status);
CREATE INDEX idx_trip_dates ON trips(departure_date, return_date);

-- =====================================================
-- MENSAGEM DE CONFIRMAÇÃO
-- =====================================================

SELECT 'Banco de dados criado e populado com sucesso!' as status;
SELECT 'Usuário admin criado: admin@soldiers.com / admin123456' as admin_info;
SELECT COUNT(*) as total_users FROM tb_user;
SELECT COUNT(*) as total_products FROM tb_product;
SELECT COUNT(*) as total_games FROM tb_game_event;
SELECT COUNT(*) as total_sales FROM tb_sale;
