const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarUCsNoSelect();
    adicionarLinhaCriterio(); // Começa com uma linha
    adicionarLinhaCriterio(); // Duas linhas para incentivar o cadastro
});

// 1. POPULAR O DROPDOWN DE UCs
async function carregarUCsNoSelect() {
    const select = document.getElementById('ucSelect');
    try {
        const response = await fetch(`${API_URL}/unidadescurriculares`);
        if(response.ok) {
            const ucs = await response.json();
            select.innerHTML = '<option value="">-- Selecione uma Matéria --</option>';

            ucs.forEach(uc => {
                const option = document.createElement('option');
                option.value = uc.id;
                option.textContent = uc.nome;
                select.appendChild(option);
            });
        }
    } catch (e) {
        console.error(e);
        select.innerHTML = '<option value="">Erro ao carregar matérias</option>';
    }
}

// 2. GERENCIAR LINHAS (Reaproveitando classes CSS student-row)
function adicionarLinhaCriterio() {
    const lista = document.getElementById('lista-criterios');
    const div = document.createElement('div');
    div.className = 'student-row'; 
    
    // HTML da linha com Descrição e os dois Selects (Avaliação e Capacidade)
    div.innerHTML = `
        <input type="text" class="crit-desc" placeholder="Ex: Modelagem de Dados" style="flex: 3;">
        
        <select class="crit-tipo form-select" style="flex: 1;">
            <option value="CRITICO">Crítico</option>
            <option value="DESEJAVEL">Desejável</option>
        </select>

        <select class="crit-cap form-select" style="flex: 1;">
            <option value="TECNICA">Técnica</option>
            <option value="SOCIOEMOCIONAL">Socioemocional</option>
        </select>

        <button class="btn-remove-row" onclick="removerLinha(this)" title="Remover">
            <i class="fas fa-trash"></i>
        </button>
    `;
    lista.appendChild(div);
}

function removerLinha(botao) {
    const linhas = document.querySelectorAll('.student-row');
    if (linhas.length > 1) {
        botao.parentElement.remove();
    } else {
        // Se for a última linha, apenas limpa o texto
        botao.parentElement.querySelector('input').value = '';
    }
}

// 3. SALVAR CRITÉRIOS EM LOTE
async function salvarCriterios() {
    const ucId = document.getElementById('ucSelect').value;
    
    // Validação da UC
    if (!ucId) {
        alert("Por favor, selecione uma Unidade Curricular na caixa de seleção.");
        return;
    }

    const btnSalvar = document.querySelector('.btn-save-all');
    const textoOriginal = btnSalvar.textContent;
    btnSalvar.disabled = true;
    btnSalvar.textContent = "Salvando...";

    // Coleta dados das linhas
    const linhas = document.querySelectorAll('.student-row');
    const criteriosParaSalvar = [];

    linhas.forEach(linha => {
        const descricao = linha.querySelector('.crit-desc').value.trim();
        const tipoAvaliacao = linha.querySelector('.crit-tipo').value;
        const tipoCapacidade = linha.querySelector('.crit-cap').value;

        if (descricao) {
            criteriosParaSalvar.push({
                descricao: descricao,
                tipoAvaliacao: tipoAvaliacao,
                tipoCapacidade: tipoCapacidade,
                unidadeCurricular: { id: ucId } // Vincula à UC selecionada
            });
        }
    });

    if (criteriosParaSalvar.length === 0) {
        alert("Preencha a descrição de pelo menos um critério.");
        btnSalvar.disabled = false;
        btnSalvar.textContent = textoOriginal;
        return;
    }

    try {
        // Envia requisições em paralelo
        const promises = criteriosParaSalvar.map(criterio => {
            return fetch(`${API_URL}/criterios`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(criterio)
            }).then(async res => {
                if (!res.ok) {
                    const textoErro = await res.text();
                    throw new Error(`Erro ao salvar '${criterio.descricao}': ${textoErro}`);
                }
                return res.json();
            });
        });

        await Promise.all(promises);

        alert(`${criteriosParaSalvar.length} critérios foram adicionados à Unidade Curricular!`);
        window.location.href = 'admin.html';

    } catch (error) {
        console.error(error);
        alert("Ocorreu um erro durante o salvamento:\n" + error.message);
    } finally {
        btnSalvar.disabled = false;
        btnSalvar.textContent = textoOriginal;
    }
}