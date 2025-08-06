Write-Host "🚀 Iniciando Soldiers - Sistema de Vendas para Futebol Americano" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Green

# Verificar se o Docker está instalado
try {
    docker --version | Out-Null
    Write-Host "✅ Docker encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está instalado. Por favor, instale o Docker primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se o Docker Compose está instalado
try {
    docker-compose --version | Out-Null
    Write-Host "✅ Docker Compose encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro." -ForegroundColor Red
    exit 1
}

# Parar containers existentes
Write-Host "🛑 Parando containers existentes..." -ForegroundColor Yellow
docker-compose down

# Remover imagens antigas (opcional)
Write-Host "🧹 Removendo imagens antigas..." -ForegroundColor Yellow
docker-compose down --rmi all

# Construir e iniciar containers
Write-Host "🔨 Construindo e iniciando containers..." -ForegroundColor Yellow
docker-compose up --build -d

Write-Host ""
Write-Host "⏳ Aguardando serviços iniciarem..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar status dos containers
Write-Host "📊 Status dos containers:" -ForegroundColor Cyan
docker-compose ps

Write-Host ""
Write-Host "🌐 URLs de acesso:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:5173" -ForegroundColor White
Write-Host "   Backend API: http://localhost:8080" -ForegroundColor White
Write-Host "   Banco de dados: localhost:5432" -ForegroundColor White
Write-Host ""
Write-Host "🔐 Credenciais de acesso:" -ForegroundColor Cyan
Write-Host "   Email: admin@soldiers.com" -ForegroundColor White
Write-Host "   Senha: admin123" -ForegroundColor White
Write-Host ""
Write-Host "📝 Comandos úteis:" -ForegroundColor Cyan
Write-Host "   Ver logs: docker-compose logs -f" -ForegroundColor White
Write-Host "   Parar: docker-compose down" -ForegroundColor White
Write-Host "   Reiniciar: docker-compose restart" -ForegroundColor White
Write-Host ""
Write-Host "✅ Sistema iniciado com sucesso!" -ForegroundColor Green 