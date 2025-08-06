# Script PowerShell para executar SQL no PostgreSQL via Docker
# Este script executa o arquivo insert_sample_data.sql no banco de dados

Write-Host "Executando script SQL no banco de dados..." -ForegroundColor Green

# Verificar se o Docker está rodando
try {
    docker ps | Out-Null
} catch {
    Write-Host "Erro: Docker não está rodando ou não está instalado" -ForegroundColor Red
    exit 1
}

# Executar o script SQL usando Docker
Write-Host "Conectando ao PostgreSQL e executando script..." -ForegroundColor Yellow

# Usar o comando docker run para executar psql
Get-Content insert_sample_data.sql | docker run --rm -i --network host postgres:13 psql -h 192.168.100.109 -p 15432 -U postgres -d vendas_futebol

if ($LASTEXITCODE -eq 0) {
    Write-Host "Script SQL executado com sucesso!" -ForegroundColor Green
} else {
    Write-Host "Erro ao executar script SQL" -ForegroundColor Red
    Write-Host "Tentando método alternativo..." -ForegroundColor Yellow
    
    # Método alternativo: copiar o arquivo para um container temporário
    docker run --rm -i --network host -v ${PWD}:/workspace postgres:13 bash -c "
        cd /workspace
        PGPASSWORD=Eunaoseiasenha22 psql -h 192.168.100.109 -p 15432 -U postgres -d vendas_futebol -f insert_sample_data.sql
    "
}

Write-Host "Processo concluído!" -ForegroundColor Green 