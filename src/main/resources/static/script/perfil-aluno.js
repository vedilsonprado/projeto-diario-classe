const API_URL = 'http://localhost:8080/api';

// Pega o ID da URL (ex: perfil-aluno.html?id=5)
const urlParams = new URLSearchParams(window.location.search);
const alunoId = urlParams.get('id');

let turmaIdGlobal = null;

document.addEventListener('DOMContentLoaded', () => {
    if (!alunoId) {
        alert("ID do aluno não encontrado!");
        window.location.href = 'index.html';
        return;
    }
    carregarDadosAluno();

    // Configura os inputs de arquivo (Criação e Edição) para mostrar o nome do arquivo selecionado
    configurarInputArquivo('arquivoAnotacao', 'nomeArquivoSelecionado');
    configurarInputArquivo('editArquivoAnotacao', 'editNomeArquivoSelecionado');
});

function configurarInputArquivo(inputId, spanId) {
    const input = document.getElementById(inputId);
    if (input) {
        input.addEventListener('change', function(e) {
            const fileName = e.target.files[0] ? e.target.files[0].name : '';
            document.getElementById(spanId).textContent = fileName;
        });
    }
}

// ==========================================
// 1. GESTÃO DE ABAS
// ==========================================
function abrirAba(abaName) {
    // Esconde todas as abas
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));

    // Mostra a selecionada
    document.getElementById(`tab-${abaName}`).classList.add('active');

    // Ativa visualmente o botão
    const btns = document.querySelectorAll('.tab-btn');
    if (abaName === 'notas') btns[0].classList.add('active');
    if (abaName === 'anotacoes') {
        btns[1].classList.add('active');
        carregarAnotacoes(); // Carrega a lista ao clicar na aba
    }
}

// ==========================================
// 2. DADOS DO ALUNO
// ==========================================

async function carregarDadosAluno() {
    try {
        const res = await fetch(`${API_URL}/alunos/${alunoId}`);
        if (!res.ok) throw new Error("Aluno não encontrado");

        const aluno = await res.json();

        document.getElementById('alunoNome').textContent = aluno.nomeCompleto;
        document.getElementById('alunoEmail').textContent = aluno.email;

        if (aluno.turma) {
            document.getElementById('alunoTurma').textContent = `${aluno.turma.nomeTurma} (${aluno.turma.anoSemestre})`;
            turmaIdGlobal = aluno.turma.id;
            carregarUCsDaTurma(turmaIdGlobal);
        } else {
            document.getElementById('alunoTurma').textContent = "Sem Turma Vinculada";
            document.getElementById('container-botoes-uc').innerHTML = "<p>Aluno sem turma, impossível carregar notas.</p>";
        }

    } catch (e) {
        console.error(e);
        alert("Erro ao carregar perfil.");
    }
}

// ==========================================
// 3. NOTAS E AVALIAÇÕES (UCs)
// ==========================================

async function carregarUCsDaTurma(turmaId) {
    const container = document.getElementById('container-botoes-uc');
    container.innerHTML = '<p>Carregando matérias...</p>';

    try {
        const res = await fetch(`${API_URL}/associacoes/turmasuc/turma/${turmaId}`);
        const associacoes = await res.json();

        container.innerHTML = '';
        if (associacoes.length === 0) {
            container.innerHTML = '<p>Nenhuma UC vinculada a esta turma.</p>';
            return;
        }

        associacoes.forEach(assoc => {
            const uc = assoc.unidadeCurricular;
            const btn = document.createElement('div');
            btn.className = 'btn-uc-select';
            btn.innerHTML = `
                <i class="fas fa-book fa-2x"></i>
                <span>${uc.nome}</span>
            `;
            btn.onclick = () => abrirDetalhesUC(uc);
            container.appendChild(btn);
        });

    } catch (e) {
        console.error(e);
        container.innerHTML = '<p style="color:red">Erro ao carregar UCs.</p>';
    }
}

async function abrirDetalhesUC(uc) {
    document.getElementById('container-botoes-uc').style.display = 'none';
    document.getElementById('container-detalhes-uc').style.display = 'block';
    document.getElementById('titulo-uc-selecionada').textContent = uc.nome;

    const containerLista = document.getElementById('lista-avaliacao-conteudo');
    containerLista.innerHTML = '<p>Carregando critérios e notas...</p>';

    try {
        // Busca estrutura completa da UC
        const resCapacidades = await fetch(`${API_URL}/capacidades/unidade/${uc.id}`);
        const capacidades = await resCapacidades.json();

        // Busca notas existentes do aluno
        const resNotas = await fetch(`${API_URL}/lancamentonotas/aluno/${alunoId}`);
        const notasExistentes = await resNotas.json();

        const mapaNotas = {};
        notasExistentes.forEach(nota => {
            mapaNotas[nota.criterio.id] = nota.atingiu;
        });

        containerLista.innerHTML = '';

        if (capacidades.length === 0) {
            containerLista.innerHTML = '<p>Nenhuma capacidade cadastrada para esta UC.</p>';
            return;
        }

        capacidades.forEach(cap => {
            const bloco = document.createElement('div');
            bloco.className = 'eval-capacity-block';

            let htmlCriterios = '';

            if (cap.criterios && cap.criterios.length > 0) {
                cap.criterios.forEach(crit => {
                    const atingiu = mapaNotas[crit.id] === true;
                    const checked = atingiu ? 'checked' : '';
                    const badgeClass = crit.tipoAvaliacao === 'CRITICO' ? 'badge-critico' : 'badge-desejavel';
                    const badgeText = crit.tipoAvaliacao === 'CRITICO' ? 'Crítico' : 'Desejável';

                    htmlCriterios += `
                        <div class="eval-criterion-row">
                            <div class="crit-info">
                                <span class="crit-name">${crit.descricao}</span>
                                <span class="crit-badge ${badgeClass}">${badgeText}</span>
                            </div>
                            <label class="toggle-switch">
                                <input type="checkbox" ${checked} onchange="atualizarNota(this, ${crit.id})">
                                <span class="slider"></span>
                            </label>
                        </div>
                    `;
                });
            } else {
                htmlCriterios = '<div class="eval-criterion-row"><span style="color:#999; font-size:0.9em;">Sem critérios cadastrados.</span></div>';
            }

            bloco.innerHTML = `
                <div class="eval-capacity-title">
                    <span>${cap.descricao}</span>
                    <span style="font-size:0.8em; font-weight:normal; color:#777;">${cap.tipoCapacidade}</span>
                </div>
                ${htmlCriterios}
            `;
            containerLista.appendChild(bloco);
        });

    } catch (e) {
        console.error(e);
        containerLista.innerHTML = '<p style="color:red">Erro ao carregar dados.</p>';
    }
}

async function atualizarNota(checkbox, criterioId) {
    const atingiu = checkbox.checked;
    const payload = {
        aluno: { id: alunoId },
        criterio: { id: criterioId },
        atingiu: atingiu
    };

    try {
        const res = await fetch(`${API_URL}/lancamentonotas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const erro = await res.text();
            alert("Erro ao salvar nota: " + erro);
            checkbox.checked = !atingiu;
        }
    } catch (e) {
        console.error(e);
        alert("Erro de conexão.");
        checkbox.checked = !atingiu;
    }
}

function voltarParaListaUCs() {
    document.getElementById('container-detalhes-uc').style.display = 'none';
    document.getElementById('container-botoes-uc').style.display = 'grid';
}

// ==========================================
// 4. ANOTAÇÕES (CRUD COMPLETO)
// ==========================================

// --- CRIAR ---
async function salvarAnotacao() {
    const texto = document.getElementById('textoAnotacao').value;
    const fileInput = document.getElementById('arquivoAnotacao');

    if (!texto && fileInput.files.length === 0) {
        alert("Escreva algo ou anexe um arquivo.");
        return;
    }

    const formData = new FormData();
    formData.append('alunoId', alunoId);
    formData.append('texto', texto);

    if (fileInput.files[0]) {
        formData.append('arquivo', fileInput.files[0]);
    }

    try {
        const res = await fetch(`${API_URL}/anotacoes`, {
            method: 'POST',
            body: formData
        });

        if (res.ok) {
            document.getElementById('textoAnotacao').value = '';
            fileInput.value = '';
            document.getElementById('nomeArquivoSelecionado').textContent = '';
            carregarAnotacoes();
        } else {
            alert("Erro ao salvar anotação.");
        }
    } catch (e) {
        console.error(e);
        alert("Erro de conexão.");
    }
}

// --- LISTAR ---
async function carregarAnotacoes() {
    const container = document.getElementById('lista-anotacoes');
    container.innerHTML = '<p>Carregando...</p>';

    try {
        const res = await fetch(`${API_URL}/anotacoes/aluno/${alunoId}`);
        const anotacoes = await res.json();

        container.innerHTML = '';
        if (anotacoes.length === 0) {
            container.innerHTML = '<p style="text-align:center; color:#999;">Nenhuma anotação registrada.</p>';
            return;
        }

        anotacoes.forEach(nota => {
            const dataFormatada = new Date(nota.dataHora).toLocaleString('pt-BR');
            let anexoHtml = '';
            
            if (nota.dadosArquivo) {
                if (nota.tipoArquivo && nota.tipoArquivo.startsWith('image/')) {
                    anexoHtml = `
                        <div class="note-attachment">
                            <img src="data:${nota.tipoArquivo};base64,${nota.dadosArquivo}" class="img-preview">
                        </div>
                    `;
                } else {
                    anexoHtml = `
                        <div class="note-attachment">
                            <a href="#" class="file-link">
                                <i class="fas fa-file-alt"></i> ${nota.nomeArquivo}
                            </a>
                        </div>
                    `;
                }
            }

            // Escapa aspas simples para não quebrar o onclick
            const textoSeguro = (nota.texto || '').replace(/'/g, "\\'");

            const card = document.createElement('div');
            card.className = 'note-card';
            card.innerHTML = `
                <div class="note-header">
                    <span><i class="fas fa-clock"></i> ${dataFormatada}</span>
                    <div class="note-actions">
                        <button class="btn-note-action" onclick="abrirModalEdicaoAnotacao(${nota.id}, '${textoSeguro}')" title="Editar">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button class="btn-note-action delete" onclick="excluirAnotacao(${nota.id})" title="Excluir">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="note-body">${nota.texto || ''}</div>
                ${anexoHtml}
            `;
            container.appendChild(card);
        });

    } catch (e) {
        console.error(e);
        container.innerHTML = '<p style="color:red">Erro ao carregar anotações.</p>';
    }
}

// --- EXCLUIR ---
async function excluirAnotacao(id) {
    if(!confirm("Tem certeza que deseja excluir esta anotação permanentemente?")) return;

    try {
        const res = await fetch(`${API_URL}/anotacoes/${id}`, { method: 'DELETE' });
        if(res.ok) {
            carregarAnotacoes();
        } else {
            alert("Erro ao excluir anotação.");
        }
    } catch(e) {
        console.error(e);
        alert("Erro de conexão.");
    }
}

// --- EDITAR (MODAL) ---
function abrirModalEdicaoAnotacao(id, textoAtual) {
    document.getElementById('modal-editar-anotacao').classList.add('active');
    document.getElementById('editIdAnotacao').value = id;
    document.getElementById('editTextoAnotacao').value = textoAtual;
    document.getElementById('editArquivoAnotacao').value = ''; 
    document.getElementById('editNomeArquivoSelecionado').textContent = '';
}

function fecharModalEdicaoAnotacao() {
    document.getElementById('modal-editar-anotacao').classList.remove('active');
}

async function salvarEdicaoAnotacao() {
    const id = document.getElementById('editIdAnotacao').value;
    const texto = document.getElementById('editTextoAnotacao').value;
    const fileInput = document.getElementById('editArquivoAnotacao');

    if (!texto) {
        alert("O texto não pode ficar vazio.");
        return;
    }

    const formData = new FormData();
    formData.append('texto', texto);
    
    if (fileInput.files[0]) {
        formData.append('arquivo', fileInput.files[0]);
    }

    try {
        const res = await fetch(`${API_URL}/anotacoes/${id}`, {
            method: 'PUT',
            body: formData
        });

        if (res.ok) {
            fecharModalEdicaoAnotacao();
            carregarAnotacoes();
        } else {
            alert("Erro ao atualizar anotação.");
        }
    } catch (e) {
        console.error(e);
        alert("Erro de conexão.");
    }
}