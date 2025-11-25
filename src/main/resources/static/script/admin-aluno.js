const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarTurmasNoSelect();
    adicionarLinhaAluno(); // Começa com uma linha vazia
    adicionarLinhaAluno(); // Adiciona mais uma pra ficar bonito visualmente
    adicionarLinhaAluno();
});

// 1. POPULAR O DROPDOWN DE TURMAS
async function carregarTurmasNoSelect() {
    const select = document.getElementById('turmaSelect');
    try {
        const response = await fetch(`${API_URL}/turmas`);
        if(response.ok) {
            const turmas = await response.json();
            select.innerHTML = '<option value="">-- Selecione uma Turma --</option>';

            turmas.forEach(turma => {
                const option = document.createElement('option');
                option.value = turma.id;
                option.textContent = `${turma.nomeTurma} (${turma.anoSemestre})`;
                select.appendChild(option);
            });
        }
    } catch (e) {
        console.error(e);
        select.innerHTML = '<option value="">Erro ao carregar turmas</option>';
    }
}

// 2. GERENCIAR LINHAS (Reaproveitado a lógica visual)
function adicionarLinhaAluno() {
    const lista = document.getElementById('lista-alunos');
    const div = document.createElement('div');
    div.className = 'student-row'; // Usa a mesma classe CSS do admin-turma
    div.innerHTML = `
        <div style="flex: 0 0 30px; text-align: center; color: #ccc;">
            <i class="fas fa-user"></i>
        </div>
        <input type="text" class="aluno-nome" placeholder="Nome Completo">
        <input type="email" class="aluno-email" placeholder="E-mail">
        <button class="btn-remove-row" onclick="removerLinha(this)" title="Remover">
            <i class="fas fa-times"></i>
        </button>
    `;
    lista.appendChild(div);
}

function removerLinha(botao) {
    // Impede remover se for a única linha (opcional, mas boa prática)
    const linhas = document.querySelectorAll('.student-row');
    if (linhas.length > 1) {
        botao.parentElement.remove();
    } else {
        // Se for a última, apenas limpa os inputs
        const inputs = botao.parentElement.querySelectorAll('input');
        inputs.forEach(input => input.value = '');
    }
}

// 3. SALVAR ALUNOS
async function salvarAlunos() {
    const turmaId = document.getElementById('turmaSelect').value;
    
    // Validação da Turma
    if (!turmaId) {
        alert("Por favor, selecione uma turma na caixa de seleção.");
        return;
    }

    const btnSalvar = document.querySelector('.btn-save-all');
    const textoOriginal = btnSalvar.textContent;
    btnSalvar.disabled = true;
    btnSalvar.textContent = "Salvando...";

    // Coleta dados das linhas
    const linhas = document.querySelectorAll('.student-row');
    const alunosParaSalvar = [];

    linhas.forEach(linha => {
        const nome = linha.querySelector('.aluno-nome').value.trim();
        const email = linha.querySelector('.aluno-email').value.trim();

        if (nome && email) {
            alunosParaSalvar.push({
                nomeCompleto: nome,
                email: email,
                turma: { id: turmaId } // Vincula à turma selecionada
            });
        }
    });

    if (alunosParaSalvar.length === 0) {
        alert("Preencha os dados de pelo menos um aluno.");
        btnSalvar.disabled = false;
        btnSalvar.textContent = textoOriginal;
        return;
    }

    try {
        // Envia requisições em paralelo (Promise.all)
        // Isso é mais rápido do que um loop com await um por um
        const promises = alunosParaSalvar.map(aluno => {
            return fetch(`${API_URL}/alunos`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(aluno)
            }).then(async res => {
                if (!res.ok) {
                    const textoErro = await res.text();
                    throw new Error(`Erro ao salvar ${aluno.nomeCompleto}: ${textoErro}`);
                }
                return res.json();
            });
        });

        await Promise.all(promises);

        alert(`${alunosParaSalvar.length} alunos foram cadastrados com sucesso!`);
        window.location.href = 'admin.html'; // Volta para o painel

    } catch (error) {
        console.error(error);
        alert("Ocorreu um erro durante o salvamento:\n" + error.message);
    } finally {
        btnSalvar.disabled = false;
        btnSalvar.textContent = textoOriginal;
    }
}