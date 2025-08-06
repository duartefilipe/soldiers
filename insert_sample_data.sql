-- Script para inserir dados de exemplo no banco de dados
-- Execute este script no PostgreSQL para popular o banco com dados de teste

-- Inserir usuários (apenas se não existirem)
INSERT INTO tb_user (name, email, password, role, criado_em) VALUES
('João Silva', 'joao@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'NORMAL', NOW()),
('Maria Santos', 'maria@soldiers.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'NORMAL', NOW())
ON CONFLICT (email) DO NOTHING;

-- Inserir produtos
INSERT INTO tb_product (name, description, price, stock, criado_em) VALUES
('Camisa Oficial', 'Camisa oficial do time com número personalizado', 89.90, 50, NOW()),
('Calção Esportivo', 'Calção esportivo de alta performance', 45.00, 30, NOW()),
('Meias Esportivas', 'Meias esportivas com cano alto', 25.00, 100, NOW()),
('Chuteira Campo', 'Chuteira profissional para campo', 299.90, 20, NOW()),
('Chuteira Society', 'Chuteira para society com travas', 189.90, 25, NOW()),
('Bola Oficial', 'Bola oficial de futebol profissional', 159.90, 15, NOW()),
('Boné Esportivo', 'Boné esportivo com logo do time', 35.00, 40, NOW()),
('Garrafa Térmica', 'Garrafa térmica esportiva 500ml', 29.90, 60, NOW()),
('Mochila Esportiva', 'Mochila esportiva com compartimentos', 79.90, 20, NOW()),
('Protetor Bucal', 'Protetor bucal esportivo', 15.00, 80, NOW());

-- Inserir eventos de jogo
INSERT INTO tb_game_event (name, description, date, start_time, end_time, location, status, criado_em) VALUES
('Jogo de Abertura', 'Primeiro jogo da temporada', '2025-01-15', '19:00:00', '21:00:00', 'Estádio Municipal', 'SCHEDULED', NOW()),
('Clássico Local', 'Jogo contra o rival da cidade', '2025-01-22', '20:00:00', '22:00:00', 'Arena Esportiva', 'SCHEDULED', NOW()),
('Copa Regional', 'Quartas de final da copa', '2025-02-01', '16:00:00', '18:00:00', 'Complexo Esportivo', 'SCHEDULED', NOW()),
('Amistoso Internacional', 'Jogo preparatório para competição', '2025-02-08', '15:30:00', '17:30:00', 'Estádio Nacional', 'SCHEDULED', NOW()),
('Final do Campeonato', 'Grande final da temporada', '2025-02-15', '19:30:00', '21:30:00', 'Arena Principal', 'SCHEDULED', NOW()),
('Jogo de Treino', 'Sessão de treino aberta ao público', '2025-01-10', '14:00:00', '16:00:00', 'Centro de Treinamento', 'FINISHED', NOW()),
('Exibição Especial', 'Jogo beneficente', '2025-01-05', '18:00:00', '20:00:00', 'Estádio Comunitário', 'FINISHED', NOW());

-- Inserir algumas vendas de exemplo
INSERT INTO tb_sale (game_event_id, user_id, total_amount, criado_em) VALUES
(29, 1, 204.80, NOW()),
(30, 2, 344.90, NOW()),
(31, 1, 469.70, NOW()),
(32, 2, 229.90, NOW()),
(33, 1, 109.80, NOW());

-- Inserir itens das vendas
INSERT INTO tb_sale_item (sale_id, product_id, quantity, price) VALUES
(17, 1, 2, 89.90),
(17, 3, 1, 25.00),
(18, 2, 1, 45.00),
(18, 4, 1, 299.90),
(19, 1, 3, 89.90),
(19, 5, 2, 189.90),
(20, 6, 1, 159.90),
(20, 7, 2, 35.00),
(21, 8, 1, 29.90),
(21, 9, 1, 79.90);

-- Atualizar estoque dos produtos após as vendas
UPDATE tb_product SET stock = stock - 2 WHERE id = 1; -- Camisa Oficial
UPDATE tb_product SET stock = stock - 1 WHERE id = 3; -- Meias Esportivas
UPDATE tb_product SET stock = stock - 1 WHERE id = 2; -- Calção Esportivo
UPDATE tb_product SET stock = stock - 1 WHERE id = 4; -- Chuteira Campo
UPDATE tb_product SET stock = stock - 3 WHERE id = 1; -- Camisa Oficial (venda 3)
UPDATE tb_product SET stock = stock - 2 WHERE id = 5; -- Chuteira Society
UPDATE tb_product SET stock = stock - 1 WHERE id = 6; -- Bola Oficial
UPDATE tb_product SET stock = stock - 2 WHERE id = 7; -- Boné Esportivo
UPDATE tb_product SET stock = stock - 1 WHERE id = 8; -- Garrafa Térmica
UPDATE tb_product SET stock = stock - 1 WHERE id = 9; -- Mochila Esportiva 