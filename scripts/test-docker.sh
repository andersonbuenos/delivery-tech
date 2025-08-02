echo "🧪 TESTE AUTOMATIZADO - DELIVERY API"
echo "===================================="

# Aguardar aplicação estar pronta
echo "⏳ Aguardando aplicação inicializar..."
sleep 45

# Função para testar endpoint
test_endpoint() {
    local url=$1
    local expected_status=$2
    local description=$3
    
    echo -n "🔍 Testando $description... "
    
    status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$status" = "$expected_status" ]; then
        echo "✅ OK ($status)"
        return 0
    else
        echo "❌ FALHOU (esperado: $expected_status, obtido: $status)"
        return 1
    fi
}

# Testes dos endpoints
echo ""
echo "📊 EXECUTANDO TESTES:"

# Teste básico de saúde
test_endpoint "http://localhost:8080/actuator/health" "200" "Health Check"

# Teste de autenticação
test_endpoint "http://localhost:8080/api/clientes" "401" "Endpoint protegido (sem auth)"

# Teste H2 Console
test_endpoint "http://localhost:8080/h2-console" "200" "H2 Console"

# Teste Adminer
test_endpoint "http://localhost:8083" "200" "Adminer"

# Teste com autenticação básica
echo -n "🔐 Testando endpoint com autenticação... "
response=$(curl -s -u admin:admin123 -w "%{http_code}" "http://localhost:8080/api/clientes")
status="${response: -3}"

if [ "$status" = "200" ]; then
    echo "✅ OK ($status)"
else
    echo "❌ FALHOU ($status)"
fi

echo ""
echo "📋 RESUMO DOS CONTAINERS:"
docker-compose ps

echo ""
echo "💾 USO DE RECURSOS:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.PIDs}}"

echo ""
echo "✅ Testes concluídos!"