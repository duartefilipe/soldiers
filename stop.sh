#!/bin/bash

echo "🛑 Parando Soldiers - Sistema de Vendas para Futebol Americano"
echo "=============================================================="

# Parar todos os containers
echo "🛑 Parando containers..."
docker-compose down

# Remover volumes (opcional - descomente se quiser limpar o banco)
# echo "🧹 Removendo volumes..."
# docker-compose down -v

echo "✅ Sistema parado com sucesso!"
echo ""
echo "📝 Para iniciar novamente, execute: ./start.sh" 