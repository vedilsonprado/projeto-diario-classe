const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarUCs();
});

// ==========================================
// 1. LISTAGEM DE UCs
// ==========================================
async function carregarUCs() {
    const tbody = document.getElementById('tabela-uc-body');
    tbody.innerHTML = '<tr><td colspan="4">Carregando...</td></tr>';

    try {
        const response = await fetch(`${API_URL}/unidadescurriculares`);
        const ucs = await response.json();
        
        tbody.innerHTML = '';
        if (ucs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center">Nenhuma UC cadastrada.</td></tr>';
            return;
        }

        ucs.forEach(uc => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${uc.id}</td>
                <td>${uc.nome}</td>
                <td>${uc.cargaHoraria}h</td>
                <td style="text-align: center;">
                    <button class="btn-action btn-edit" onclick="abrirModalEdicao(${uc.id})" title="Editar">
                        <i class="fas fa-pen"></i>
                    </button>
                    <button class="btn-action btn-delete" onclick="excluirUC(${uc.id})" title="Excluir">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error(error);
        tbody.innerHTML = '<tr><td colspan="4" style="color:red">Erro ao carregar.</td></tr>';
    }
}

async function excluirUC(id) {
    if(!confirm("ATENÇÃO: Excluir esta UC apagará todas as Capacidades e Critérios associados.\nDeseja continuar?")) return;
    
    try {
        await fetch(`${API_URL}/unidadescurriculares/${id}`, { method: 'DELETE' });
        carregarUCs();
    } catch (e) {
        alert("Erro ao excluir.");
    }
}

// ==========================================
// 2. MODAL DE EDIÇÃO (CARREGAMENTO)
// ==========================================
async function abrirModalEdicao(ucId) {
    document.getElementById('modal-editar-uc').classList.add('active');
    document.getElementById('editIdUC').value = ucId;

    // A. Dados Básicos da UC
    const resUC = await fetch(`${API_URL}/unidadescurriculares/${ucId}`);
    const uc = await resUC.json();
    document.getElementById('editNomeUC').value = uc.nome;
    document.getElementById('editCargaUC').value = uc.cargaHoraria;

    // B. Carregar Árvore de Capacidades e Critérios
    carregarCapacidadesNoModal(ucId);
}

async function carregarCapacidadesNoModal(ucId) {
    const container = document.getElementById('container-capacidades-edicao');
    container.innerHTML = 'Carregando estrutura...';

    // Busca as capacidades vinculadas a esta UC
    const res = await fetch(`${API_URL}/capacidades/unidade/${ucId}`);
    const capacidades = await res.json();

    container.innerHTML = '';
    
    // Renderiza cada bloco de capacidade
    capacidades.forEach(cap => {
        adicionarBlocoCapacidadeVisual(cap);
    });
}

/**
 * Cria um bloco de capacidade no modal.
 * Se receber objeto 'cap', preenche (Edição). Se null, cria vazio (Novo).
 */
function adicionarBlocoCapacidadeVisual(cap = null) {
    const container = document.getElementById('container-capacidades-edicao');
    const div = document.createElement('div');
    div.className = 'capacity-block';
    
    const id = cap ? cap.id : '';
    const desc = cap ? cap.descricao : '';
    const tipo = cap ? cap.tipoCapacidade : 'TECNICA';
    // Gera um ID único para o container de critérios deste bloco
    const listaCriteriosId = `lista-crit-${id || Date.now()}`; 

    div.innerHTML = `
        <input type="hidden" class="cap-id" value="${id}">
        
        <button class="btn-remove-cap" onclick="removerCapacidade(this, '${id}')" title="Excluir Capacidade">
            <i class="fas fa-times"></i>
        </button>
        
        <div class="capacity-header">
            <div class="input-group" style="flex: 3; margin-bottom: 0;">
                <label style="font-size: 0.8em;">Capacidade</label>
                <input type="text" class="cap-desc" value="${desc}" placeholder="Descrição">
            </div>
            <div class="input-group" style="flex: 1; margin-bottom: 0;">
                <label style="font-size: 0.8em;">Tipo</label>
                <select class="cap-tipo form-select">
                    <option value="TECNICA" ${tipo === 'TECNICA' ? 'selected' : ''}>Técnica</option>
                    <option value="SOCIOEMOCIONAL" ${tipo === 'SOCIOEMOCIONAL' ? 'selected' : ''}>Socioemocional</option>
                </select>
            </div>
        </div>

        <div class="criteria-list" id="${listaCriteriosId}">
            </div>

        <button class="btn-add-row" style="margin-top:10px; font-size: 0.8em; background-color: #6c757d;" onclick="adicionarLinhaCriterioVisual('${listaCriteriosId}')">
            <i class="fas fa-plus"></i> Critério
        </button>
    `;
    
    container.appendChild(div);

    // Se for edição e tiver critérios, carrega eles
    if (cap && cap.criterios) {
        cap.criterios.forEach(crit => {
            adicionarLinhaCriterioVisual(listaCriteriosId, crit);
        });
    }
}

function adicionarLinhaCriterioVisual(containerId, crit = null) {
    const lista = document.getElementById(containerId);
    const div = document.createElement('div');
    div.className = 'student-row';
    div.style.marginTop = '5px';

    const id = crit ? crit.id : '';
    const desc = crit ? crit.descricao : '';
    const tipo = crit ? crit.tipoAvaliacao : 'CRITICO';

    div.innerHTML = `
        <input type="hidden" class="crit-id" value="${id}">
        
        <input type="text" class="crit-desc" value="${desc}" placeholder="Descrição Critério" style="flex: 3;">
        
        <select class="crit-tipo form-select" style="flex: 1;">
            <option value="CRITICO" ${tipo === 'CRITICO' ? 'selected' : ''}>Crítico</option>
            <option value="DESEJAVEL" ${tipo === 'DESEJAVEL' ? 'selected' : ''}>Desejável</option>
        </select>

        <button class="btn-remove-row" onclick="removerCriterio(this, '${id}')" title="Remover">
            <i class="fas fa-trash"></i>
        </button>
    `;
    lista.appendChild(div);
}

// ==========================================
// 3. REMOÇÃO IMEDIATA (DELETE)
// ==========================================
async function removerCapacidade(btn, id) {
    if (id) {
        if(!confirm("Excluir esta capacidade removerá todos os seus critérios. Continuar?")) return;
        try {
            await fetch(`${API_URL}/capacidades/${id}`, { method: 'DELETE' });
            btn.parentElement.remove();
        } catch(e) { alert("Erro ao excluir capacidade."); }
    } else {
        btn.parentElement.remove();
    }
}

async function removerCriterio(btn, id) {
    if (id) {
        if(!confirm("Excluir critério permanentemente?")) return;
        try {
            await fetch(`${API_URL}/criterios/${id}`, { method: 'DELETE' });
            btn.parentElement.remove();
        } catch(e) { alert("Erro ao excluir critério."); }
    } else {
        btn.parentElement.remove();
    }
}

function fecharModalEdicao() {
    document.getElementById('modal-editar-uc').classList.remove('active');
}

// ==========================================
// 4. SALVAR EM CASCATA (UPDATE/CREATE)
// ==========================================
async function salvarEdicaoUC() {
    const ucId = document.getElementById('editIdUC').value;
    const nome = document.getElementById('editNomeUC').value;
    const carga = document.getElementById('editCargaUC').value;

    const btnSalvar = document.querySelector('.btn-confirm');
    btnSalvar.textContent = "Salvando...";
    btnSalvar.disabled = true;

    try {
        // 1. Atualizar UC
        await fetch(`${API_URL}/unidadescurriculares/${ucId}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ nome: nome, cargaHoraria: carga })
        });

        // 2. Iterar sobre Blocos de Capacidade
        const blocos = document.querySelectorAll('.capacity-block');
        
        for (const bloco of blocos) {
            const capId = bloco.querySelector('.cap-id').value;
            const capDesc = bloco.querySelector('.cap-desc').value;
            const capTipo = bloco.querySelector('.cap-tipo').value;

            if (!capDesc) continue;

            let idCapacidadeSalva = capId;

            // Payload da Capacidade
            const capPayload = {
                descricao: capDesc,
                tipoCapacidade: capTipo,
                unidadeCurricular: { id: ucId }
            };

            // Salvar Capacidade (PUT ou POST)
            let resCap;
            if (capId) {
                resCap = await fetch(`${API_URL}/capacidades/${capId}`, {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(capPayload)
                });
            } else {
                resCap = await fetch(`${API_URL}/capacidades`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(capPayload)
                });
            }

            if (resCap.ok) {
                const capSalva = await resCap.json();
                idCapacidadeSalva = capSalva.id;

                // 3. Salvar Critérios desta Capacidade
                const linhasCrit = bloco.querySelectorAll('.student-row');
                const promisesCrit = Array.from(linhasCrit).map(linha => {
                    const critId = linha.querySelector('.crit-id').value;
                    const critDesc = linha.querySelector('.crit-desc').value;
                    const critTipo = linha.querySelector('.crit-tipo').value;

                    if (!critDesc) return null;

                    const critPayload = {
                        descricao: critDesc,
                        tipoAvaliacao: critTipo,
                        capacidade: { id: idCapacidadeSalva }
                    };

                    if (critId) {
                        return fetch(`${API_URL}/criterios/${critId}`, {
                            method: 'PUT',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(critPayload)
                        });
                    } else {
                        return fetch(`${API_URL}/criterios`, {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(critPayload)
                        });
                    }
                });

                await Promise.all(promisesCrit);
            }
        }

        alert("Alterações salvas com sucesso!");
        fecharModalEdicao();
        carregarUCs();

    } catch (e) {
        console.error(e);
        alert("Erro ao salvar.");
    } finally {
        btnSalvar.textContent = "Salvar Alterações";
        btnSalvar.disabled = false;
    }
}