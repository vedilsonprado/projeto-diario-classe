const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarUCsExistentes();
    adicionarLinhaAluno(); // Adiciona uma linha vazia ao iniciar
});

// 1. CARREGAR UCs DO BACKEND
async function carregarUCsExistentes() {
    const container = document.getElementById('container-ucs');
    try {
        const response = await fetch(`${API_URL}/unidadescurriculares`);
        if(response.ok) {
            const ucs = await response.json();
            container.innerHTML = ''; // Limpa loading

            if(ucs.length === 0) {
                container.innerHTML = '<p>Nenhuma UC cadastrada.</p>';
                return;
            }

            ucs.forEach(uc => {
                const div = document.createElement('div');
                div.className = 'uc-checkbox-item';
                div.innerHTML = `
                    <input type="checkbox" id="uc-${uc.id}" value="${uc.id}">
                    <label for="uc-${uc.id}">${uc.nome}</label>
                `;
                container.appendChild(div);
            });
        }
    } catch (e) {
        console.error(e);
        container.innerHTML = '<p style="color:red">Erro ao carregar UCs.</p>';
    }
}

// 2. GERENCIAR LINHAS DE ALUNOS
function adicionarLinhaAluno() {
    const lista = document.getElementById('lista-alunos');
    const div = document.createElement('div');
    div.className = 'student-row';
    div.innerHTML = `
        <input type="text" class="aluno-nome" placeholder="Nome do Aluno">
        <input type="email" class="aluno-email" placeholder="E-mail do Aluno">
        <button class="btn-remove-row" onclick="this.parentElement.remove()" title="Remover">
            <i class="fas fa-trash"></i>
        </button>
    `;
    lista.appendChild(div);
}

// 3. SALVAR TUDO (LÓGICA COMPLEXA)
async function salvarTudo() {
    const nomeTurma = document.getElementById('nomeTurma').value;
    const semestre = document.getElementById('anoSemestre').value;

    if (!nomeTurma || !semestre) {
        alert("Preencha o Nome e o Semestre da Turma.");
        return;
    }

    const btnSalvar = document.querySelector('.btn-save-all');
    btnSalvar.disabled = true;
    btnSalvar.textContent = "Salvando...";

    try {
        // PASSO 1: Criar a Turma
        const respTurma = await fetch(`${API_URL}/turmas`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ nomeTurma: nomeTurma, anoSemestre: semestre })
        });

        if (!respTurma.ok) throw new Error("Erro ao criar turma.");
        const turmaCriada = await respTurma.json();
        const turmaId = turmaCriada.id;

        // PASSO 2: Vincular UCs Selecionadas
        const checkboxes = document.querySelectorAll('#container-ucs input[type="checkbox"]:checked');
        const promisesUC = Array.from(checkboxes).map(chk => {
            return fetch(`${API_URL}/associacoes/turmasuc`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    turma: { id: turmaId },
                    unidadeCurricular: { id: chk.value }
                })
            });
        });

        // PASSO 3: Criar Alunos
        const linhasAlunos = document.querySelectorAll('.student-row');
        const promisesAlunos = Array.from(linhasAlunos).map(linha => {
            const nome = linha.querySelector('.aluno-nome').value;
            const email = linha.querySelector('.aluno-email').value;

            if (nome && email) {
                return fetch(`${API_URL}/alunos`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        nomeCompleto: nome,
                        email: email,
                        turma: { id: turmaId }
                    })
                });
            }
        });

        // Aguarda todos os cadastros paralelos terminarem
        await Promise.all([...promisesUC, ...promisesAlunos]);

        alert("Turma, UCs e Alunos salvos com sucesso!");
        window.location.href = 'admin.html'; // Volta para o admin

    } catch (error) {
        console.error(error);
        alert("Ocorreu um erro: " + error.message);
        btnSalvar.disabled = false;
        btnSalvar.textContent = "Salvar Turma Completa";
    }
}