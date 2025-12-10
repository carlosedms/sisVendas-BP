#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para gerar graficos de metricas de codigo do projeto sisVendas-BP.
Utiliza os dados coletados pelo CK (Chidamber & Kemerer).
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
from pathlib import Path

# Configuracoes
plt.style.use('seaborn-v0_8-whitegrid')
plt.rcParams['figure.figsize'] = (12, 6)
plt.rcParams['font.size'] = 10
plt.rcParams['axes.titlesize'] = 12
plt.rcParams['axes.labelsize'] = 10

# Diretorios
SCRIPT_DIR = Path(__file__).parent
PROJECT_DIR = SCRIPT_DIR.parent
ANALISE_DIR = PROJECT_DIR / 'analise-metricas'
GRAFICOS_DIR = ANALISE_DIR / 'graficos'

# Criar diretorio de graficos se nao existir
GRAFICOS_DIR.mkdir(parents=True, exist_ok=True)


def carregar_dados():
    """Carrega os dados de metricas do CK."""
    classes_file = ANALISE_DIR / 'metricas-ck-classes.csv'
    metodos_file = ANALISE_DIR / 'metricas-ck-metodos.csv'
    
    df_classes = pd.read_csv(classes_file)
    df_metodos = pd.read_csv(metodos_file)
    
    # Extrair apenas o nome da classe (sem pacote completo)
    df_classes['class_name'] = df_classes['class'].apply(lambda x: x.split('.')[-1])
    
    return df_classes, df_metodos


def calcular_estatisticas(df_classes):
    """Calcula estatisticas gerais das metricas."""
    metricas = ['wmc', 'dit', 'noc', 'cbo', 'rfc', 'lcom', 'loc']
    stats = {}
    
    for metrica in metricas:
        if metrica in df_classes.columns:
            stats[metrica] = {
                'total': df_classes[metrica].sum(),
                'media': df_classes[metrica].mean(),
                'mediana': df_classes[metrica].median(),
                'maximo': df_classes[metrica].max(),
                'minimo': df_classes[metrica].min(),
                'desvio_padrao': df_classes[metrica].std()
            }
    
    return stats


def gerar_grafico_wmc(df_classes):
    """Gera grafico de distribuicao de WMC (Weighted Methods per Class)."""
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))
    
    # Histograma
    ax1.hist(df_classes['wmc'], bins=10, edgecolor='black', alpha=0.7, color='steelblue')
    ax1.axvline(df_classes['wmc'].mean(), color='red', linestyle='--', label=f'Media: {df_classes["wmc"].mean():.1f}')
    ax1.set_xlabel('WMC (Weighted Methods per Class)')
    ax1.set_ylabel('Frequencia')
    ax1.set_title('Distribuicao de Complexidade (WMC)')
    ax1.legend()
    
    # Grafico de barras por classe
    df_sorted = df_classes.sort_values('wmc', ascending=True)
    colors = ['red' if x > 20 else 'orange' if x > 10 else 'green' for x in df_sorted['wmc']]
    ax2.barh(df_sorted['class_name'], df_sorted['wmc'], color=colors, edgecolor='black')
    ax2.axvline(10, color='orange', linestyle='--', alpha=0.7, label='Limite baixo (10)')
    ax2.axvline(20, color='red', linestyle='--', alpha=0.7, label='Limite alto (20)')
    ax2.set_xlabel('WMC')
    ax2.set_title('WMC por Classe')
    ax2.legend()
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'wmc_distribuicao.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico WMC gerado: wmc_distribuicao.png")


def gerar_grafico_cbo(df_classes):
    """Gera grafico de acoplamento (CBO - Coupling Between Objects)."""
    fig, ax = plt.subplots(figsize=(12, 6))
    
    df_sorted = df_classes.sort_values('cbo', ascending=True)
    colors = ['red' if x > 10 else 'orange' if x > 5 else 'green' for x in df_sorted['cbo']]
    bars = ax.barh(df_sorted['class_name'], df_sorted['cbo'], color=colors, edgecolor='black')
    
    ax.axvline(5, color='orange', linestyle='--', alpha=0.7, label='Limite baixo (5)')
    ax.axvline(10, color='red', linestyle='--', alpha=0.7, label='Limite alto (10)')
    ax.set_xlabel('CBO (Coupling Between Objects)')
    ax.set_title('Acoplamento por Classe')
    ax.legend()
    
    # Adicionar valores nas barras
    for bar, val in zip(bars, df_sorted['cbo']):
        ax.text(val + 0.2, bar.get_y() + bar.get_height()/2, str(int(val)), va='center')
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'cbo_acoplamento.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico CBO gerado: cbo_acoplamento.png")


def gerar_grafico_lcom(df_classes):
    """Gera grafico de coesao (LCOM - Lack of Cohesion of Methods)."""
    fig, ax = plt.subplots(figsize=(12, 6))
    
    df_sorted = df_classes.sort_values('lcom', ascending=True)
    # LCOM alto = baixa coesao (ruim), LCOM baixo = boa coesao (bom)
    colors = ['red' if x > 10 else 'orange' if x > 1 else 'green' for x in df_sorted['lcom']]
    bars = ax.barh(df_sorted['class_name'], df_sorted['lcom'], color=colors, edgecolor='black')
    
    ax.axvline(1, color='orange', linestyle='--', alpha=0.7, label='Limite bom (1)')
    ax.axvline(10, color='red', linestyle='--', alpha=0.7, label='Limite ruim (10)')
    ax.set_xlabel('LCOM (Lack of Cohesion of Methods)')
    ax.set_title('Falta de Coesao por Classe (menor = melhor)')
    ax.legend()
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'lcom_coesao.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico LCOM gerado: lcom_coesao.png")


def gerar_grafico_rfc(df_classes):
    """Gera grafico de RFC (Response For a Class)."""
    fig, ax = plt.subplots(figsize=(12, 6))
    
    df_sorted = df_classes.sort_values('rfc', ascending=True)
    colors = ['red' if x > 50 else 'orange' if x > 20 else 'green' for x in df_sorted['rfc']]
    bars = ax.barh(df_sorted['class_name'], df_sorted['rfc'], color=colors, edgecolor='black')
    
    ax.axvline(20, color='orange', linestyle='--', alpha=0.7, label='Limite baixo (20)')
    ax.axvline(50, color='red', linestyle='--', alpha=0.7, label='Limite alto (50)')
    ax.set_xlabel('RFC (Response For a Class)')
    ax.set_title('RFC por Classe')
    ax.legend()
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'rfc_resposta.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico RFC gerado: rfc_resposta.png")


def gerar_grafico_loc(df_classes):
    """Gera grafico de tamanho (LOC - Lines of Code)."""
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))
    
    # Histograma
    ax1.hist(df_classes['loc'], bins=10, edgecolor='black', alpha=0.7, color='steelblue')
    ax1.axvline(df_classes['loc'].mean(), color='red', linestyle='--', label=f'Media: {df_classes["loc"].mean():.1f}')
    ax1.set_xlabel('LOC (Lines of Code)')
    ax1.set_ylabel('Frequencia')
    ax1.set_title('Distribuicao de Tamanho (LOC)')
    ax1.legend()
    
    # Grafico de barras
    df_sorted = df_classes.sort_values('loc', ascending=True)
    colors = ['red' if x > 200 else 'orange' if x > 100 else 'green' for x in df_sorted['loc']]
    ax2.barh(df_sorted['class_name'], df_sorted['loc'], color=colors, edgecolor='black')
    ax2.axvline(100, color='orange', linestyle='--', alpha=0.7, label='Limite medio (100)')
    ax2.axvline(200, color='red', linestyle='--', alpha=0.7, label='Limite alto (200)')
    ax2.set_xlabel('LOC')
    ax2.set_title('LOC por Classe')
    ax2.legend()
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'loc_tamanho.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico LOC gerado: loc_tamanho.png")


def gerar_grafico_dit(df_classes):
    """Gera grafico de profundidade de heranca (DIT)."""
    fig, ax = plt.subplots(figsize=(10, 6))
    
    df_sorted = df_classes.sort_values('dit', ascending=True)
    colors = ['red' if x > 5 else 'orange' if x > 3 else 'green' for x in df_sorted['dit']]
    bars = ax.barh(df_sorted['class_name'], df_sorted['dit'], color=colors, edgecolor='black')
    
    ax.set_xlabel('DIT (Depth of Inheritance Tree)')
    ax.set_title('Profundidade de Heranca por Classe')
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'dit_heranca.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Grafico DIT gerado: dit_heranca.png")


def gerar_scatter_wmc_loc(df_classes):
    """Gera scatter plot de WMC vs LOC."""
    fig, ax = plt.subplots(figsize=(10, 8))
    
    scatter = ax.scatter(df_classes['loc'], df_classes['wmc'], 
                         c=df_classes['cbo'], cmap='RdYlGn_r', 
                         s=100, alpha=0.7, edgecolors='black')
    
    # Adicionar nomes das classes
    for _, row in df_classes.iterrows():
        ax.annotate(row['class_name'], (row['loc'], row['wmc']), 
                   fontsize=8, alpha=0.8, ha='left')
    
    ax.set_xlabel('LOC (Lines of Code)')
    ax.set_ylabel('WMC (Weighted Methods per Class)')
    ax.set_title('Relacao entre Tamanho (LOC) e Complexidade (WMC)\n(cor indica CBO - acoplamento)')
    
    cbar = plt.colorbar(scatter, ax=ax)
    cbar.set_label('CBO (Acoplamento)')
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'scatter_wmc_loc.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Scatter plot gerado: scatter_wmc_loc.png")


def gerar_dashboard(df_classes, stats):
    """Gera um dashboard com multiplas metricas."""
    fig, axes = plt.subplots(2, 3, figsize=(16, 10))
    fig.suptitle('Dashboard de Metricas de Codigo - sisVendas-BP', fontsize=14, fontweight='bold')
    
    metricas = [
        ('wmc', 'WMC (Complexidade)', 10, 20),
        ('cbo', 'CBO (Acoplamento)', 5, 10),
        ('rfc', 'RFC (Resposta)', 20, 50),
        ('lcom', 'LCOM (Falta Coesao)', 1, 10),
        ('loc', 'LOC (Tamanho)', 100, 200),
        ('dit', 'DIT (Heranca)', 3, 5)
    ]
    
    for ax, (metrica, titulo, lim1, lim2) in zip(axes.flatten(), metricas):
        if metrica in df_classes.columns:
            valores = df_classes[metrica].dropna()
            ax.hist(valores, bins=8, edgecolor='black', alpha=0.7, color='steelblue')
            ax.axvline(valores.mean(), color='red', linestyle='--', linewidth=2, label=f'Media: {valores.mean():.1f}')
            ax.axvline(lim1, color='orange', linestyle=':', alpha=0.7)
            ax.axvline(lim2, color='red', linestyle=':', alpha=0.7)
            ax.set_title(titulo)
            ax.set_ylabel('Frequencia')
            ax.legend(fontsize=8)
    
    plt.tight_layout()
    plt.savefig(GRAFICOS_DIR / 'dashboard_metricas.png', dpi=150, bbox_inches='tight')
    plt.close()
    print("Dashboard gerado: dashboard_metricas.png")


def gerar_resumo_estatisticas(stats, df_classes):
    """Gera arquivo CSV com estatisticas."""
    rows = []
    for metrica, valores in stats.items():
        rows.append({
            'Metrica': metrica.upper(),
            'Total': f"{valores['total']:.2f}",
            'Media': f"{valores['media']:.2f}",
            'Mediana': f"{valores['mediana']:.2f}",
            'Maximo': f"{valores['maximo']:.2f}",
            'Minimo': f"{valores['minimo']:.2f}",
            'Desvio Padrao': f"{valores['desvio_padrao']:.2f}"
        })
    
    df_stats = pd.DataFrame(rows)
    df_stats.to_csv(ANALISE_DIR / 'estatisticas-metricas.csv', index=False)
    print(f"Estatisticas exportadas: estatisticas-metricas.csv")
    
    # Imprimir resumo no console
    print("\n" + "="*60)
    print("RESUMO ESTATISTICO DAS METRICAS")
    print("="*60)
    print(df_stats.to_string(index=False))
    print("="*60)
    
    # Resumo geral
    print(f"\nTotal de classes analisadas: {len(df_classes)}")
    print(f"Total de linhas de codigo: {df_classes['loc'].sum()}")
    print(f"Media de metodos por classe (WMC): {df_classes['wmc'].mean():.1f}")
    print(f"Media de acoplamento (CBO): {df_classes['cbo'].mean():.1f}")


def main():
    print("="*60)
    print("ANALISE DE METRICAS DE CODIGO - sisVendas-BP")
    print("="*60)
    
    # Carregar dados
    print("\nCarregando dados...")
    df_classes, df_metodos = carregar_dados()
    print(f"Classes carregadas: {len(df_classes)}")
    print(f"Metodos carregados: {len(df_metodos)}")
    
    # Calcular estatisticas
    print("\nCalculando estatisticas...")
    stats = calcular_estatisticas(df_classes)
    
    # Gerar graficos
    print("\nGerando graficos...")
    gerar_grafico_wmc(df_classes)
    gerar_grafico_cbo(df_classes)
    gerar_grafico_lcom(df_classes)
    gerar_grafico_rfc(df_classes)
    gerar_grafico_loc(df_classes)
    gerar_grafico_dit(df_classes)
    gerar_scatter_wmc_loc(df_classes)
    gerar_dashboard(df_classes, stats)
    
    # Gerar resumo
    gerar_resumo_estatisticas(stats, df_classes)
    
    print("\n" + "="*60)
    print("ANALISE CONCLUIDA!")
    print(f"Graficos salvos em: {GRAFICOS_DIR}")
    print("="*60)


if __name__ == '__main__':
    main()

