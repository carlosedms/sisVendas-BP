#!/bin/bash
# Script para executar PMD e coletar métricas complementares

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
OUTPUT_DIR="$PROJECT_DIR/analise-metricas"

echo "=== Executando PMD para análise de código ==="
echo "Diretório do projeto: $PROJECT_DIR"
echo "Diretório de saída: $OUTPUT_DIR"

cd "$PROJECT_DIR"

# Criar diretório de saída se não existir
mkdir -p "$OUTPUT_DIR"

# Executar PMD via Maven
echo ""
echo "Executando análise PMD..."
mvn pmd:pmd -q

# Copiar relatório PMD para diretório de análise
if [ -f "target/pmd.xml" ]; then
    cp target/pmd.xml "$OUTPUT_DIR/pmd-report.xml"
    echo "Relatório PMD XML copiado para: $OUTPUT_DIR/pmd-report.xml"
fi

# Gerar relatório CPD (Copy-Paste Detector)
echo ""
echo "Executando análise CPD (detecção de código duplicado)..."
mvn pmd:cpd -q

if [ -f "target/cpd.xml" ]; then
    cp target/cpd.xml "$OUTPUT_DIR/cpd-report.xml"
    echo "Relatório CPD XML copiado para: $OUTPUT_DIR/cpd-report.xml"
fi

echo ""
echo "=== Análise PMD concluída ==="
echo "Arquivos gerados em: $OUTPUT_DIR"
ls -la "$OUTPUT_DIR"/*.xml 2>/dev/null || echo "Nenhum arquivo XML encontrado"

