#!/bin/bash

echo "🚀 Iniciando Soldiers - Sistema de Vendas para Futebol Americano"
echo "================================================================"

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

echo "✅ Docker e Docker Compose encontrados"

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down

# Remover imagens antigas (opcional)
echo "🧹 Removendo imagens antigas..."
docker-compose down --rmi all

# Construir e iniciar containers
echo "🔨 Construindo e iniciando containers..."
docker-compose up --build -d

echo ""
echo "⏳ Aguardando serviços iniciarem..."
sleep 10

# Verificar status dos containers
echo "📊 Status dos containers:"
docker-compose ps

echo ""
echo "🌐 URLs de acesso:"
echo "   Frontend: http://localhost:5173"
echo "   Backend API: http://localhost:8080"
echo "   Banco de dados: localhost:5432"
echo ""
echo "🔐 Credenciais de acesso:"
echo "   Email: admin@soldiers.com"
echo "   Senha: admin123"
echo ""
echo "📝 Comandos úteis:"
echo "   Ver logs: docker-compose logs -f"
echo "   Parar: docker-compose down"
echo "   Reiniciar: docker-compose restart"
echo ""
echo "✅ Sistema iniciado com sucesso!" 