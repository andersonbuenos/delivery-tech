echo "🐳 DEPLOY LOCAL - DELIVERY API"
echo "================================"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[WARN] $1${NC}"
}

info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    error "Docker não está rodando. Inicie o Docker primeiro."
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose não está instalado."
    exit 1
fi

# Parar containers existentes
log "Parando containers existentes..."
docker-compose down

# Limpar imagens antigas (opcional)
read -p "Deseja remover imagens antigas? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log "Removendo imagens antigas..."
    docker-compose down --rmi all --volumes --remove-orphans
fi

# Build das imagens
log "Construindo imagens..."
docker-compose build --no-cache

# Verificar se build foi bem-sucedido
if [ $? -eq 0 ]; then
    log "Build concluído com sucesso!"
else
    error "Falha no build. Verifique os logs acima."
    exit 1
fi

# Subir os containers
log "Iniciando containers..."
docker-compose up -d

# Aguardar aplicação ficar pronta
log "Aguardando aplicação ficar pronta..."
sleep 30

# Verificar se containers estão rodando
if docker-compose ps | grep -q "Up"; then
    log "Containers iniciados com sucesso!"
    
    echo ""
    info "🌐 APLICAÇÃO DISPONÍVEL EM:"
    info "   • API: http://localhost:8080"
    info "   • H2 Console: http://localhost:8080/h2-console"
    info "   • H2 External Console: http://localhost:8082"
    info "   • Adminer: http://localhost:8083"
    info "   • Health Check: http://localhost:8080/actuator/health"
    
    echo ""
    info "📊 MONITORAMENTO:"
    info "   • Logs: docker-compose logs -f delivery-api"
    info "   • Status: docker-compose ps"
    
    echo ""
    info "🔐 CREDENCIAIS:"
    info "   • Usuário: admin"
    info "   • Senha: admin123"
    
else
    error "Falha ao iniciar containers. Verificando logs..."
    docker-compose logs delivery-api
    exit 1
fi

log "Deploy concluído! 🚀"