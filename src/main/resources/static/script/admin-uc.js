const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
	adicionarLinhaCriterio(); // Inicia com uma linha
	adicionarLinhaCriterio(); // Visualmente melhor com duas
});

// 1. GERENCIAR LINHAS DE CRITÉRIOS
function adicionarLinhaCriterio() {
	const lista = document.getElementById('lista-criterios');
	const div = document.createElement('div');
	div.className = 'student-row'; // Reaproveitando a classe CSS de "linha"

	// HTML da linha com os Selects necessários para o Backend
	div.innerHTML = `
        <input type="text" class="crit-desc" placeholder="Descrição (Ex: Modelagem ER)" style="flex: 2;">
        
        <select class="crit-tipo form-select" style="flex: 1;">
            <option value="CRITICO">Crítico</option>
            <option value="DESEJAVEL">Desejável</option>
        </select>

        <select class="crit-cap form-select" style="flex: 1;">
            <option value="TECNICA">Técnica</option>
            <option value="SOCIOEMOCIONAL">Socioemocional</option>
        </select>

        <button class="btn-remove-row" onclick="this.parentElement.remove()" title="Remover">
            <i class="fas fa-trash"></i>
        </button>
    `;
	lista.appendChild(div);
}

// 2. SALVAR TUDO (CASCATA)
async function salvarTudo() {
	const nome = document.getElementById('nomeUC').value;
	const carga = document.getElementById('cargaHoraria').value;

	// Validação Básica
	if (!nome) {
		alert("Preencha o Nome da Unidade Curricular.");
		return;
	}

	const btnSalvar = document.querySelector('.btn-save-all');
	btnSalvar.disabled = true;
	btnSalvar.textContent = "Salvando...";

	try {
		// PASSO 1: Criar a Unidade Curricular
		const respUC = await fetch(`${API_URL}/unidadescurriculares`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({
				nome: nome,
				cargaHoraria: carga ? parseInt(carga) : null
			})
		});

		if (!respUC.ok) throw new Error("Erro ao criar Unidade Curricular.");
		const ucCriada = await respUC.json();
		const ucId = ucCriada.id;

		// PASSO 2: Criar os Critérios Vinculados
		const linhas = document.querySelectorAll('#lista-criterios .student-row');
		const promisesCriterios = Array.from(linhas).map(linha => {
			const descricao = linha.querySelector('.crit-desc').value.trim();
			const tipoAvaliacao = linha.querySelector('.crit-tipo').value;
			const tipoCapacidade = linha.querySelector('.crit-cap').value;

			if (descricao) {
				return fetch(`${API_URL}/criterios`, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({
						descricao: descricao,
						tipoAvaliacao: tipoAvaliacao,
						tipoCapacidade: tipoCapacidade,
						unidadeCurricular: { id: ucId } // Vínculo aqui
					})
				});
			}
		});

		// Aguarda todos os critérios serem salvos
		await Promise.all(promisesCriterios);

		alert("Unidade Curricular e Critérios salvos com sucesso!");
		window.location.href = 'admin.html';

	} catch (error) {
		console.error(error);
		alert("Ocorreu um erro: " + error.message);
		btnSalvar.disabled = false;
		btnSalvar.textContent = "Salvar Unidade Curricular";
	}
}