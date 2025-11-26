const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    adicionarBlocoCapacidade(); // Inicia com um bloco padrão
});

// ==========================================
// 1. GERENCIAR BLOCOS DE CAPACIDADE (Nível 2)
// ==========================================
function adicionarBlocoCapacidade() {
    const container = document.getElementById('container-capacidades');
    const div = document.createElement('div');
    div.className = 'capacity-block';
    
    // Gera ID único temporário para controlar os filhos
    const blockId = Date.now();

    div.innerHTML = `
        <button class="btn-remove-cap" onclick="this.parentElement.remove()" title="Remover Capacidade">
            <i class="fas fa-times"></i>
        </button>
        
        <div class="capacity-header">
            <div class="input-group" style="flex: 3; margin-bottom: 0;">
                <label style="font-size: 0.8em;">Descrição da Capacidade</label>
                <input type="text" class="cap-desc" placeholder="Ex: Modelagem de Dados">
            </div>
            <div class="input-group" style="flex: 1; margin-bottom: 0;">
                <label style="font-size: 0.8em;">Tipo</label>
                <select class="cap-tipo form-select">
                    <option value="TECNICA">Técnica</option>
                    <option value="SOCIOEMOCIONAL">Socioemocional</option>
                </select>
            </div>
        </div>

        <div class="criteria-list" id="lista-criterios-${blockId}">
            </div>

        <button class="btn-add-row" style="margin-top:10px; font-size: 0.8em; background-color: #6c757d;" onclick="adicionarLinhaCriterio('${blockId}')">
            <i class="fas fa-plus"></i> Adicionar Critério
        </button>
    `;
    
    container.appendChild(div);
    
    // Adiciona um critério padrão dentro desta capacidade nova
    adicionarLinhaCriterio(blockId);
}

// ==========================================
// 2. GERENCIAR LINHAS DE CRITÉRIO (Nível 3)
// ==========================================
function adicionarLinhaCriterio(blockId) {
    const lista = document.getElementById(`lista-criterios-${blockId}`);
    const div = document.createElement('div');
    div.className = 'student-row'; // Reaproveita estilo
    div.style.marginTop = '10px';
    
    div.innerHTML = `
        <i class="fas fa-level-up-alt fa-rotate-90" style="color: #ccc; margin-right: 10px;"></i>
        <input type="text" class="crit-desc" placeholder="Critério (Ex: Cria tabelas corretamente)" style="flex: 3;">
        
        <select class="crit-tipo form-select" style="flex: 1;">
            <option value="CRITICO">Crítico</option>
            <option value="DESEJAVEL">Desejável</option>
        </select>

        <button class="btn-remove-row" onclick="this.parentElement.remove()" title="Remover Critério">
            <i class="fas fa-trash"></i>
        </button>
    `;
    lista.appendChild(div);
}

// ==========================================
// 3. SALVAR TUDO EM CASCATA
// ==========================================
async function salvarTudo() {
    const nomeUC = document.getElementById('nomeUC').value;
    const carga = document.getElementById('cargaHoraria').value;

    if (!nomeUC) {
        alert("Preencha o Nome da Unidade Curricular.");
        return;
    }

    const btnSalvar = document.querySelector('.btn-save-all');
    btnSalvar.disabled = true;
    btnSalvar.textContent = "Processando...";

    try {
        // PASSO 1: Criar a UC
        const respUC = await fetch(`${API_URL}/unidadescurriculares`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ 
                nome: nomeUC, 
                cargaHoraria: carga ? parseInt(carga) : null 
            })
        });

        if (!respUC.ok) throw new Error("Erro ao criar UC.");
        const ucCriada = await respUC.json();
        const ucId = ucCriada.id;

        // PASSO 2: Iterar sobre os Blocos de Capacidade
        const blocosCapacidade = document.querySelectorAll('.capacity-block');
        
        // Usamos um loop for...of para poder usar await dentro (sequencial é mais seguro aqui)
        for (const bloco of blocosCapacidade) {
            const capDesc = bloco.querySelector('.cap-desc').value;
            const capTipo = bloco.querySelector('.cap-tipo').value;

            if (capDesc) {
                // Salvar Capacidade vinculada à UC
                const respCap = await fetch(`${API_URL}/capacidades`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        descricao: capDesc,
                        tipoCapacidade: capTipo,
                        unidadeCurricular: { id: ucId }
                    })
                });

                if(respCap.ok) {
                    const capCriada = await respCap.json();
                    const capId = capCriada.id;

                    // PASSO 3: Salvar Critérios desta Capacidade
                    const linhasCriterio = bloco.querySelectorAll('.student-row');
                    const promisesCriterios = Array.from(linhasCriterio).map(linha => {
                        const critDesc = linha.querySelector('.crit-desc').value;
                        const critTipo = linha.querySelector('.crit-tipo').value;

                        if(critDesc) {
                            return fetch(`${API_URL}/criterios`, {
                                method: 'POST',
                                headers: {'Content-Type': 'application/json'},
                                body: JSON.stringify({
                                    descricao: critDesc,
                                    tipoAvaliacao: critTipo,
                                    capacidade: { id: capId } // Vincula à Capacidade recém criada
                                })
                            });
                        }
                    });
                    
                    await Promise.all(promisesCriterios);
                }
            }
        }

        alert("Unidade Curricular cadastrada com sucesso!");
        window.location.href = 'admin.html';

    } catch (error) {
        console.error(error);
        alert("Erro ao salvar: " + error.message);
        btnSalvar.disabled = false;
        btnSalvar.textContent = "Salvar Unidade Curricular";
    }
}