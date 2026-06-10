# ============================================================
# allure-report.ps1 — Gera e abre o relatório Allure
#
# Uso:
#   .\allure-report.ps1            # roda testes + gera + abre
#   .\allure-report.ps1 -SkipTests # só gera (usa allure-results existente)
#   .\allure-report.ps1 -NoOpen    # gera sem abrir o browser
# ============================================================

param(
    [switch]$SkipTests,
    [switch]$NoOpen
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$AllureHome   = Join-Path $ProjectRoot ".allure\allure-2.27.0"
$ResultsDir   = Join-Path $ProjectRoot "target\allure-results"
$ReportDir    = Join-Path $ProjectRoot "target\allure-report"
$IndexHtml    = Join-Path $ReportDir "index.html"

# ---- 1. Rodar testes (salvo se -SkipTests) ----
if (-not $SkipTests) {
    Write-Host "`n==> Executando testes Maven...`n" -ForegroundColor Cyan
    Push-Location $ProjectRoot
    mvn test
    if ($LASTEXITCODE -ne 0) {
        Write-Error "mvn test falhou (exit $LASTEXITCODE)"
    }
    Pop-Location
}

# ---- 2. Verificar allure-results ----
if (-not (Test-Path $ResultsDir)) {
    Write-Error "Diretório de resultados não encontrado: $ResultsDir`nRodou os testes antes?"
}

# ---- 3. Baixar Allure CLI se não existir ----
if (-not (Test-Path $AllureHome)) {
    Write-Host "`n==> Allure CLI não encontrado; baixando via Maven...`n" -ForegroundColor Cyan
    Push-Location $ProjectRoot
    mvn allure:install 2>&1 | Out-Null
    Pop-Location
    if (-not (Test-Path $AllureHome)) {
        Write-Error "Falha ao instalar Allure CLI em: $AllureHome"
    }
}

# ---- 4. Gerar relatório ----
Write-Host "`n==> Gerando relatório Allure...`n" -ForegroundColor Cyan

$libs = (Get-ChildItem "$AllureHome\lib\*.jar" | ForEach-Object { $_.FullName }) -join ";"

java -cp $libs io.qameta.allure.CommandLine generate $ResultsDir --clean -o $ReportDir
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao gerar relatório Allure (exit $LASTEXITCODE)"
}

Write-Host "`n==> Relatório gerado em: $ReportDir" -ForegroundColor Green

# ---- 5. Servir via HTTP e abrir no browser ----
if (-not $NoOpen) {
    # Encontra uma porta livre a partir de 9090
    $Port = 9090
    while ((netstat -ano | Select-String ":$Port " | Select-String "LISTEN") -and $Port -lt 9100) {
        $Port++
    }

    Write-Host "==> Subindo servidor HTTP na porta $Port...`n" -ForegroundColor Cyan
    Start-Process python -ArgumentList "-m http.server $Port --directory `"$ReportDir`"" -WindowStyle Hidden
    Start-Sleep -Seconds 2
    Start-Process "http://localhost:$Port"
    Write-Host "==> Relatório disponível em: http://localhost:$Port" -ForegroundColor Green
    Write-Host "    Para encerrar o servidor: Get-Process python | Stop-Process`n" -ForegroundColor Yellow
}
