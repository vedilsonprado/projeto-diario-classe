const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarAlunos();
});

// ==========================================
// 1. LISTAGEM DE ALUNOS
// ==========================================
async function carregarAlunos() {
    const tbody = document.getElementById('tabela-alunos-body');
    tbody.innerHTML = '<tr><td colspan="5">Carregando...</td></tr>';

    try {
        const response = await fetch(`${API_URL}/alunos`);
        const alunos = await response.json();
        
        tbody.innerHTML = '';
        
        if (alunos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center">Nenhum aluno cadastrado.</td></tr>';
            return;
        }

        alunos.forEach(aluno => {
            // Verifica se o aluno tem turma ou está null
            const nomeTurma = aluno.turma ? `${aluno.turma.nomeTurma} (${aluno.turma.anoSemestre})` : '<span style="color:red">Sem Turma</span>';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${aluno.id}</td>
                <td>${aluno.nomeCompleto}</td>
                <td>${aluno.email || '-'}</td>
                <td>${nomeTurma}</td>
                <td style="text-align: center;">
                    <button class="btn-action btn-edit" onclick="abrirModalEdicao(${aluno.id})" title="Editar">
                        <i class="fas fa-pen"></i>
                    </button>
                    <button class="btn-action btn-delete" onclick="excluirAluno(${aluno.id})" title="Excluir">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error(error);
        tbody.innerHTML = '<tr><td colspan="5" style="color:red">Erro ao carregar lista.</td></tr>';
    }
}

// ==========================================
// 2. EXCLUSÃO
// ==========================================
async function excluirAluno(id) {
    if(!confirm("Tem certeza que deseja excluir este aluno permanentemente?")) return;
    
    try {
        const res = await fetch(`${API_URL}/alunos/${id}`, { method: 'DELETE' });
        if(res.ok) {
            carregarAlunos();
        } else {
            alert("Erro ao excluir. Verifique se o aluno possui notas lançadas.");
        }
    } catch (e) {
        alert("Erro de conexão.");
    }
}

// ==========================================
// 3. EDIÇÃO (MODAL)
// ==========================================

async function abrirModalEdicao(alunoId) {
    // 1. Abre o modal visualmente
    document.getElementById('modal-editar-aluno').classList.add('active');
    document.getElementById('editIdAluno').value = alunoId;

    // 2. Carrega as Turmas para popular o Select
    const selectTurma = document.getElementById('editTurmaSelect');
    selectTurma.innerHTML = '<option value="">Carregando...</option>';

    try {
        // Busca paralela: Aluno + Lista de Turmas
        const [resAluno, resTurmas] = await Promise.all([
            fetch(`${API_URL}/alunos/${alunoId}`),
            fetch(`${API_URL}/turmas`)
        ]);

        const aluno = await resAluno.json();
        const turmas = await resTurmas.json();

        // 3. Preenche os inputs de texto
        document.getElementById('editNomeAluno').value = aluno.nomeCompleto;
        document.getElementById('editEmailAluno').value = aluno.email;

        // 4. Monta o Select de Turmas
        selectTurma.innerHTML = '<option value="">-- Sem Turma --</option>'; // Opção para desvincular
        
        turmas.forEach(t => {
            const option = document.createElement('option');
            option.value = t.id;
            option.textContent = `${t.nomeTurma} (${t.anoSemestre})`;
            
            // Se o aluno já pertence a essa turma, marca como selecionado
            if (aluno.turma && aluno.turma.id === t.id) {
                option.selected = true;
            }
            selectTurma.appendChild(option);
        });

    } catch (error) {
        console.error(error);
        alert("Erro ao carregar dados para edição.");
        fecharModalEdicao();
    }
}

function fecharModalEdicao() {
    document.getElementById('modal-editar-aluno').classList.remove('active');
}

async function salvarEdicaoAluno() {
    const id = document.getElementById('editIdAluno').value;
    const nome = document.getElementById('editNomeAluno').value;
    const email = document.getElementById('editEmailAluno').value;
    const turmaId = document.getElementById('editTurmaSelect').value;

    if (!nome) {
        alert("O nome é obrigatório.");
        return;
    }

    // Monta o objeto para envio
    const alunoAtualizado = {
        nomeCompleto: nome,
        email: email,
        turma: turmaId ? { id: turmaId } : null // Se vazio, manda null (sem turma)
    };

    try {
        const res = await fetch(`${API_URL}/alunos/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(alunoAtualizado)
        });

        if (res.ok) {
            alert("Aluno atualizado com sucesso!");
            fecharModalEdicao();
            carregarAlunos(); // Atualiza a tabela
        } else {
            alert("Erro ao salvar alterações.");
        }
    } catch (e) {
        console.error(e);
        alert("Erro de conexão.");
    }
}