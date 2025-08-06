-- Inserir usuário admin padrão
INSERT INTO tb_user (name, email, password, role, criado_em) 
VALUES ('Admin', 'admin@soldiers.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', NOW());

-- Inserir usuário Anakin
INSERT INTO tb_user (name, email, password, role, criado_em) 
VALUES ('Anakin', 'anakin@anakin.com', '$2a$10$KC7rwEobWqNQF9fizLb8IOiorKeJOx9Z8SD0ZXouX37cOXyFruRli', 'ADMIN', NOW());

-- Inserir produtos de exemplo
INSERT INTO tb_product (name, description, price, stock, criado_em) VALUES
('Cerveja', 'Cerveja gelada 350ml', 8.50, 100, NOW()),
('Refrigerante', 'Refrigerante 350ml', 5.00, 80, NOW()),
('Água', 'Água mineral 500ml', 3.00, 120, NOW()),
('Cachorro Quente', 'Cachorro quente completo', 12.00, 50, NOW()),
('Hambúrguer', 'Hambúrguer com batata', 18.00, 30, NOW()),
('Camiseta', 'Camiseta do time', 45.00, 25, NOW()),
('Boné', 'Boné oficial', 25.00, 20, NOW());

-- Inserir eventos de jogo de exemplo
INSERT INTO tb_game_event (name, description, date, start_time, end_time, location, status, criado_em) VALUES
('Soldiers vs Warriors', 'Jogo da temporada regular', '2024-01-15', '19:00:00', '22:00:00', 'Estádio Municipal', 'SCHEDULED', NOW()),
('Soldiers vs Titans', 'Semifinal do campeonato', '2024-01-22', '20:00:00', '23:00:00', 'Arena Central', 'SCHEDULED', NOW()),
('Soldiers vs Eagles', 'Final do campeonato', '2024-01-29', '19:30:00', '22:30:00', 'Estádio Nacional', 'SCHEDULED', NOW()); 