const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
	carregarTurmas();
});

// ==========================================
// 1. LISTAGEM DE TURMAS
// ==========================================
async function carregarTurmas() {
	const tbody = document.getElementById('tabela-turmas-body');
	tbody.innerHTML = '<tr><td colspan="4">Carregando...</td></tr>';

	try {
		const response = await fetch(`${API_URL}/turmas`);
		const turmas = await response.json();

		tbody.innerHTML = '';
		turmas.forEach(turma => {
			const tr = document.createElement('tr');
			tr.innerHTML = `
                <td>${turma.id}</td>
                <td>${turma.nomeTurma}</td>
                <td>${turma.anoSemestre}</td>
                <td style="text-align: center;">
                    <button class="btn-action btn-edit" onclick="abrirModalEdicao(${turma.id})" title="Editar">
                        <i class="fas fa-pen"></i>
                    </button>
                    <button class="btn-action btn-delete" onclick="excluirTurma(${turma.id})" title="Excluir">
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

async function excluirTurma(id) {
	if (!confirm("Tem certeza que deseja excluir esta turma? Isso pode apagar dados relacionados.")) return;

	try {
		await fetch(`${API_URL}/turmas/${id}`, { method: 'DELETE' });
		carregarTurmas();
	} catch (e) {
		alert("Erro ao excluir.");
	}
}

// ==========================================
// 2. MODAL DE EDIÇÃO (CARREGAMENTO)
// ==========================================
let ucsOriginaisDaTurma = []; // Para controle do que adicionar/remover

async function abrirModalEdicao(turmaId) {
	document.getElementById('modal-editar-turma').classList.add('active');
	document.getElementById('editIdTurma').value = turmaId;

	// A. Carregar Dados da Turma
	const resTurma = await fetch(`${API_URL}/turmas/${turmaId}`);
	const turma = await resTurma.json();
	document.getElementById('editNomeTurma').value = turma.nomeTurma;
	document.getElementById('editSemestre').value = turma.anoSemestre;

	// B. Carregar Todas as UCs e marcar as que a turma já tem
	carregarUCsNoModal(turmaId);

	// C. Carregar Alunos
	carregarAlunosNoModal(turmaId);
}

async function carregarUCsNoModal(turmaId) {
	const container = document.getElementById('lista-ucs-edicao');
	container.innerHTML = 'Carregando...';

	// Pega TODAS as UCs do sistema
	const resTodas = await fetch(`${API_URL}/unidadescurriculares`);
	const todasUCs = await resTodas.json();

	// Pega as UCs JÁ VINCULADAS a esta turma (via tabela associativa)
	const resVinculadas = await fetch(`${API_URL}/associacoes/turmasuc/turma/${turmaId}`);
	const associacoes = await resVinculadas.json();

	// Guarda IDs das associações atuais para facilitar a remoção depois se precisar
	// Mapeamos para saber quais IDs de UC a turma já tem
	const idsUcsVinculadas = associacoes.map(assoc => assoc.unidadeCurricular.id);

	// Guarda a lista original de associações (para saber o ID da associação se precisar deletar)
	ucsOriginaisDaTurma = associacoes;

	container.innerHTML = '';
	todasUCs.forEach(uc => {
		const checked = idsUcsVinculadas.includes(uc.id) ? 'checked' : '';

		const div = document.createElement('div');
		div.className = 'uc-checkbox-item';
		div.innerHTML = `
            <input type="checkbox" id="edit-uc-${uc.id}" value="${uc.id}" ${checked}>
            <label for="edit-uc-${uc.id}">${uc.nome}</label>
        `;
		container.appendChild(div);
	});
}

async function carregarAlunosNoModal(turmaId) {
	const container = document.getElementById('lista-alunos-edicao');
	container.innerHTML = 'Carregando alunos...';

	const res = await fetch(`${API_URL}/alunos/turma/${turmaId}`);
	const alunos = await res.json();

	container.innerHTML = '';
	if (alunos.length === 0) {
		container.innerHTML = '<p style="color:#888">Nenhum aluno nesta turma.</p>';
		return;
	}

	alunos.forEach(aluno => {
		const div = document.createElement('div');
		div.className = 'student-row';
		div.style.padding = '5px';
		div.style.borderBottom = '1px solid #eee';
		div.innerHTML = `
            <span style="flex: 1;">${aluno.nomeCompleto}</span>
            <button class="btn-remove-row" onclick="desvincularAluno(${aluno.id}, '${aluno.nomeCompleto}', ${turmaId})" title="Desvincular da Turma">
                <i class="fas fa-trash"></i>
            </button>
        `;
		container.appendChild(div);
	});
}

function fecharModalEdicao() {
	document.getElementById('modal-editar-turma').classList.remove('active');
}

// ==========================================
// 3. AÇÕES DE EDIÇÃO
// ==========================================

// Ação: Desvincular Aluno (Lixeira)
async function desvincularAluno(alunoId, nomeAluno, turmaId) {
	if (!confirm(`Deseja remover o aluno "${nomeAluno}" desta turma?\nEle não será excluído do sistema, apenas ficará sem turma.`)) return;

	try {
		// 1. Buscamos o aluno completo primeiro (para não perder email e nome)
		const resGet = await fetch(`${API_URL}/alunos/${alunoId}`);
		const alunoObj = await resGet.json();

		// 2. Definimos a turma como null
		alunoObj.turma = null;

		// 3. Atualizamos o aluno (PUT)
		const resPut = await fetch(`${API_URL}/alunos/${alunoId}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(alunoObj)
		});

		if (resPut.ok) {
			// Recarrega a lista de alunos no modal
			carregarAlunosNoModal(turmaId);
		} else {
			alert("Erro ao desvincular. Verifique se o backend permite alunos sem turma.");
		}
	} catch (e) {
		console.error(e);
		alert("Erro de conexão.");
	}
}

// Ação: Salvar Edição da Turma (Dados e UCs)
async function salvarEdicaoTurma() {
	const turmaId = document.getElementById('editIdTurma').value;
	const nome = document.getElementById('editNomeTurma').value;
	const semestre = document.getElementById('editSemestre').value;

	try {
		// 1. Atualizar dados básicos da Turma (PUT)
		await fetch(`${API_URL}/turmas/${turmaId}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ nomeTurma: nome, anoSemestre: semestre })
		});

		// 2. Atualizar UCs (Complexo: N:N)
		// Estratégia simples: Apagar todos os vínculos antigos e criar os novos selecionados
		// (Isso evita lógica complexa de diff)

		// A. Apagar antigos (usando a lista que guardamos ao abrir o modal)
		const promisesDelete = ucsOriginaisDaTurma.map(assoc =>
			fetch(`${API_URL}/associacoes/turmasuc/${assoc.id}`, { method: 'DELETE' })
		);
		await Promise.all(promisesDelete);

		// B. Criar novos (baseado nos checkboxes marcados)
		const checkboxes = document.querySelectorAll('#lista-ucs-edicao input:checked');
		const promisesCreate = Array.from(checkboxes).map(chk =>
			fetch(`${API_URL}/associacoes/turmasuc`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({
					turma: { id: turmaId },
					unidadeCurricular: { id: chk.value }
				})
			})
		);
		await Promise.all(promisesCreate);

		alert("Turma atualizada com sucesso!");
		fecharModalEdicao();
		carregarTurmas(); // Atualiza a tabela principal

	} catch (e) {
		console.error(e);
		alert("Erro ao salvar alterações.");
	}
}