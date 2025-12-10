#!/bin/bash
# Script para executar CK (Chidamber & Kemerer) e coletar métricas

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
OUTPUT_DIR="$PROJECT_DIR/analise-metricas"

echo "=== Executando CK para coleta de métricas ==="
echo "Diretório do projeto: $PROJECT_DIR"
echo "Diretório de saída: $OUTPUT_DIR"

# Criar diretório de saída se não existir
mkdir -p "$OUTPUT_DIR"

# Executar CK na pasta de código fonte
# Parâmetros: <source_dir> <use_jars:true/false> <max_files_per_partition> <variables_and_fields:true/false> <output_dir>
java -jar "$SCRIPT_DIR/ck.jar" \
    "$PROJECT_DIR/src/main/java" \
    false \
    0 \
    true \
    "$OUTPUT_DIR/"

# Renomear arquivos gerados pelo CK para nomes mais descritivos
if [ -f "$OUTPUT_DIR/class.csv" ]; then
    mv "$OUTPUT_DIR/class.csv" "$OUTPUT_DIR/metricas-ck-classes.csv"
    echo "Arquivo de métricas de classes gerado: metricas-ck-classes.csv"
fi

if [ -f "$OUTPUT_DIR/method.csv" ]; then
    mv "$OUTPUT_DIR/method.csv" "$OUTPUT_DIR/metricas-ck-metodos.csv"
    echo "Arquivo de métricas de métodos gerado: metricas-ck-metodos.csv"
fi

if [ -f "$OUTPUT_DIR/field.csv" ]; then
    mv "$OUTPUT_DIR/field.csv" "$OUTPUT_DIR/metricas-ck-campos.csv"
    echo "Arquivo de métricas de campos gerado: metricas-ck-campos.csv"
fi

if [ -f "$OUTPUT_DIR/variable.csv" ]; then
    mv "$OUTPUT_DIR/variable.csv" "$OUTPUT_DIR/metricas-ck-variaveis.csv"
    echo "Arquivo de métricas de variáveis gerado: metricas-ck-variaveis.csv"
fi

echo ""
echo "=== Coleta de métricas CK concluída ==="
echo "Arquivos gerados em: $OUTPUT_DIR"
ls -la "$OUTPUT_DIR"/*.csv 2>/dev/null || echo "Nenhum arquivo CSV encontrado"

