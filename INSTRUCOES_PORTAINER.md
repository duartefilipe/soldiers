# 🐳 Configuração da Stack Soldiers no Portainer

## 📋 Passos para Configurar

### 1. Acesse o Portainer
- Vá para o seu Portainer (geralmente `http://seu-ip:9000`)
- Faça login com suas credenciais

### 2. Criar Nova Stack
1. **Clique em "Stacks"** no menu lateral
2. **Clique em "Add stack"**
3. **Nome da Stack:** `soldiers`
4. **Método:** Selecione "Web editor"

### 3. Cole o Docker Compose
Copie e cole o conteúdo do arquivo `docker-compose-portainer.yml` no editor:

```yaml
version: '3.8'

services:
  soldiers-backend:
    image: maven:3.8.1-openjdk-11
    container_name: soldiers-backend
    ports:
      - "8080:8080"
    restart: unless-stopped
    working_dir: /home/anakin/Documentos/Codigos/soldiers
    volumes:
      - /home/anakin/Documentos/Codigos:/home/anakin/Documentos/Codigos
    command: >
      sh -c "
      if [ ! -d soldiers ]; then
        git clone https://github.com/duartefilipe/soldiers.git;
      fi &&
      cd soldiers/backend &&
      mvn clean package -DskipTests &&
      java -jar target/soldiers-0.0.1-SNAPSHOT.jar
      "
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.100.109:15432/vendas_futebol
      - SPRING_DATASOURCE_PASSWORD=Eunaoseiasenha22
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update

  soldiers-frontend:
    image: node:18-alpine
    container_name: soldiers-frontend
    ports:
      - "5173:5173"
    restart: unless-stopped
    working_dir: /home/anakin/Documentos/Codigos/soldiers
    volumes:
      - /home/anakin/Documentos/Codigos:/home/anakin/Documentos/Codigos
    command: >
      sh -c "
      if [ ! -d soldiers ]; then
        git clone https://github.com/duartefilipe/soldiers.git;
      fi &&
      cd soldiers/frontend &&
      npm install &&
      npm run dev -- --host 0.0.0.0
      "
    depends_on:
      - soldiers-backend
```

### 4. Deploy da Stack
1. **Clique em "Deploy the stack"**
2. **Aguarde** a criação dos containers
3. **Verifique os logs** se necessário

## 🔧 Configurações Importantes

### 📁 Volume Mount
- **Host:** `/home/anakin/Documentos/Codigos`
- **Container:** `/home/anakin/Documentos/Codigos`

### 🌐 Portas
- **Backend:** `8080:8080`
- **Frontend:** `5173:5173`

### 🗄️ Banco de Dados
- **PostgreSQL:** `192.168.100.109:15432`
- **Database:** `vendas_futebol`
- **Password:** `Eunaoseiasenha22`

## 🚀 Acesso à Aplicação

Após o deploy, acesse:
- **Frontend:** `http://seu-ip:5173`
- **Backend:** `http://seu-ip:8080`

### 👤 Credenciais de Login
- **Email:** `anakin@anakin.com`
- **Senha:** `eumesmo`

## 📊 Monitoramento

### Verificar Status
1. **Vá para "Containers"** no Portainer
2. **Procure por:**
   - `soldiers-backend`
   - `soldiers-frontend`

### Ver Logs
1. **Clique no container**
2. **Vá para "Logs"**
3. **Monitore** o progresso da build

## 🔄 Atualizações

Para atualizar o código:
1. **Faça push** para o GitHub
2. **Recreate** a stack no Portainer
3. **Ou reinicie** os containers

## 🛠️ Troubleshooting

### Problema: Container não inicia
- **Verifique** se o PostgreSQL está acessível
- **Confirme** se as portas estão livres
- **Verifique** os logs do container

### Problema: Frontend não carrega
- **Aguarde** a build do Node.js (pode demorar)
- **Verifique** se o backend está rodando
- **Confirme** se a porta 5173 está livre

### Problema: Backend não conecta ao banco
- **Verifique** se o PostgreSQL está rodando
- **Confirme** as credenciais no environment
- **Teste** a conectividade: `telnet 192.168.100.109 15432`

## 📝 Notas

- **Primeira execução** pode demorar devido ao download das dependências
- **Maven** fará o build do projeto Java
- **Node.js** instalará as dependências do React
- **Git clone** será executado automaticamente se o diretório não existir 