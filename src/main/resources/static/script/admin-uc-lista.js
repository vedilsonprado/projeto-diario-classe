const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarUCs();
});

// ==========================================
// 1. LISTAGEM
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

// ==========================================
// 2. EXCLUSÃO DA UC (Mãe)
// ==========================================
async function excluirUC(id) {
    if(!confirm("ATENÇÃO: Ao excluir esta Unidade Curricular, TODOS os critérios associados a ela também serão excluídos permanentemente.\n\nDeseja continuar?")) return;
    
    try {
        const res = await fetch(`${API_URL}/unidadescurriculares/${id}`, { method: 'DELETE' });
        if(res.ok) {
            carregarUCs();
        } else {
            alert("Erro ao excluir. Verifique se existem turmas cursando esta matéria.");
        }
    } catch (e) {
        alert("Erro de conexão.");
    }
}

// ==========================================
// 3. EDIÇÃO (MODAL)
// ==========================================
async function abrirModalEdicao(ucId) {
    document.getElementById('modal-editar-uc').classList.add('active');
    document.getElementById('editIdUC').value = ucId;

    // A. Carregar dados da UC
    const resUC = await fetch(`${API_URL}/unidadescurriculares/${ucId}`);
    const uc = await resUC.json();
    document.getElementById('editNomeUC').value = uc.nome;
    document.getElementById('editCargaUC').value = uc.cargaHoraria;

    // B. Carregar Critérios da UC
    // Usamos o endpoint específico que criamos antes: /api/criterios/unidade/{id}
    carregarCriteriosNoModal(ucId);
}

async function carregarCriteriosNoModal(ucId) {
    const container = document.getElementById('lista-criterios-edicao');
    container.innerHTML = 'Carregando critérios...';

    const res = await fetch(`${API_URL}/criterios/unidade/${ucId}`);
    const criterios = await res.json();

    container.innerHTML = '';
    
    criterios.forEach(criterio => {
        adicionarLinhaCriterioVisual(criterio);
    });
}

/**
 * Cria uma linha no modal. 
 * Se receber um objeto 'criterio', preenche os dados (Edição).
 * Se não receber nada, cria vazia (Novo).
 */
function adicionarLinhaCriterioVisual(criterio = null) {
    const container = document.getElementById('lista-criterios-edicao');
    const div = document.createElement('div');
    div.className = 'student-row'; // Reaproveitando estilo CSS
    
    // Valores iniciais
    const id = criterio ? criterio.id : ''; // ID vazio se for novo
    const desc = criterio ? criterio.descricao : '';
    const tipoAval = criterio ? criterio.tipoAvaliacao : 'CRITICO';
    const tipoCap = criterio ? criterio.tipoCapacidade : 'TECNICA';

    div.innerHTML = `
        <input type="hidden" class="crit-id" value="${id}">
        
        <input type="text" class="crit-desc" value="${desc}" placeholder="Descrição" style="flex: 3;">
        
        <select class="crit-tipo form-select" style="flex: 1;">
            <option value="CRITICO" ${tipoAval === 'CRITICO' ? 'selected' : ''}>Crítico</option>
            <option value="DESEJAVEL" ${tipoAval === 'DESEJAVEL' ? 'selected' : ''}>Desejável</option>
        </select>

        <select class="crit-cap form-select" style="flex: 1;">
            <option value="TECNICA" ${tipoCap === 'TECNICA' ? 'selected' : ''}>Técnica</option>
            <option value="SOCIOEMOCIONAL" ${tipoCap === 'SOCIOEMOCIONAL' ? 'selected' : ''}>Socioemocional</option>
        </select>

        <button class="btn-remove-row" onclick="removerCriterio(this, '${id}')" title="Excluir Critério">
            <i class="fas fa-trash"></i>
        </button>
    `;
    container.appendChild(div);
}

// Ação: Remover Critério (Botão Lixeira da linha)
async function removerCriterio(btn, idCriterio) {
    // Se tem ID, está no banco. Precisamos deletar via API.
    if (idCriterio) {
        if(!confirm("Excluir este critério permanentemente do banco de dados?")) return;
        
        try {
            const res = await fetch(`${API_URL}/criterios/${idCriterio}`, { method: 'DELETE' });
            if (res.ok) {
                btn.parentElement.remove(); // Remove do visual
            } else {
                alert("Erro ao excluir critério. Verifique se há notas lançadas para ele.");
            }
        } catch(e) {
            console.error(e);
            alert("Erro de conexão.");
        }
    } else {
        // Se não tem ID, é apenas uma linha visual não salva. Remove direto.
        btn.parentElement.remove();
    }
}

function fecharModalEdicao() {
    document.getElementById('modal-editar-uc').classList.remove('active');
}

// ==========================================
// 4. SALVAR ALTERAÇÕES
// ==========================================
async function salvarEdicaoUC() {
    const ucId = document.getElementById('editIdUC').value;
    const nome = document.getElementById('editNomeUC').value;
    const carga = document.getElementById('editCargaUC').value;

    const btnSalvar = document.querySelector('.btn-confirm');
    btnSalvar.textContent = "Salvando...";
    btnSalvar.disabled = true;

    try {
        // 1. Atualizar dados básicos da UC (PUT)
        await fetch(`${API_URL}/unidadescurriculares/${ucId}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ nome: nome, cargaHoraria: carga })
        });

        // 2. Processar Critérios (Criar Novos ou Atualizar Existentes)
        const linhas = document.querySelectorAll('#lista-criterios-edicao .student-row');
        
        const promises = Array.from(linhas).map(linha => {
            const id = linha.querySelector('.crit-id').value;
            const descricao = linha.querySelector('.crit-desc').value;
            const tipoAval = linha.querySelector('.crit-tipo').value;
            const tipoCap = linha.querySelector('.crit-cap').value;

            if (!descricao) return null; // Pula vazios

            const payload = {
                descricao: descricao,
                tipoAvaliacao: tipoAval,
                tipoCapacidade: tipoCap,
                unidadeCurricular: { id: ucId }
            };

            if (id) {
                // Tem ID: Atualizar (PUT)
                return fetch(`${API_URL}/criterios/${id}`, {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(payload)
                });
            } else {
                // Não tem ID: Criar (POST)
                return fetch(`${API_URL}/criterios`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(payload)
                });
            }
        });

        await Promise.all(promises);

        alert("Alterações salvas com sucesso!");
        fecharModalEdicao();
        carregarUCs();

    } catch (e) {
        console.error(e);
        alert("Erro ao salvar alterações.");
    } finally {
        btnSalvar.textContent = "Salvar Alterações";
        btnSalvar.disabled = false;
    }
}