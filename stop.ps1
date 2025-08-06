Write-Host "🛑 Parando Soldiers - Sistema de Vendas para Futebol Americano" -ForegroundColor Yellow
Write-Host "==============================================================" -ForegroundColor Yellow

# Parar todos os containers
Write-Host "🛑 Parando containers..." -ForegroundColor Yellow
docker-compose down

# Remover volumes (opcional - descomente se quiser limpar o banco)
# Write-Host "🧹 Removendo volumes..." -ForegroundColor Yellow
# docker-compose down -v

Write-Host "✅ Sistema parado com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Para iniciar novamente, execute: .\start.ps1" -ForegroundColor Cyan 