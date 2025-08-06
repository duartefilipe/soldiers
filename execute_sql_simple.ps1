# Script PowerShell simples para executar SQL no PostgreSQL
Write-Host "Executando script SQL no banco de dados..." -ForegroundColor Green

# Definir a senha como variável de ambiente
$env:PGPASSWORD = "Eunaoseiasenha22"

# Executar o script SQL usando Docker
Write-Host "Conectando ao PostgreSQL e executando script..." -ForegroundColor Yellow

docker run --rm -i --network host -e PGPASSWORD=$env:PGPASSWORD postgres:13 psql -h 192.168.100.109 -p 15432 -U postgres -d vendas_futebol -f /workspace/insert_sample_data.sql -v ON_ERROR_STOP=1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Script SQL executado com sucesso!" -ForegroundColor Green
} else {
    Write-Host "Erro ao executar script SQL" -ForegroundColor Red
}

Write-Host "Processo concluído!" -ForegroundColor Green 