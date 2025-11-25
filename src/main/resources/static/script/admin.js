// Configuração da API
const API_URL = 'http://localhost:8080/api';

function gerenciar(entidade, acao) {
	console.log(`Ação: ${acao} | Entidade: ${entidade}`);

	// Exemplo de roteamento básico
	if (acao === 'adicionar') {
		switch (entidade) {
			case 'turma':
				window.location.href = 'admin-turma.html';
				break;
			case 'aluno':
				window.location.href = 'admin-aluno.html';
				break;
			case 'uc':
				window.location.href = 'admin-uc.html';
				break;
			case 'criterio':
				window.location.href = 'admin-criterio.html';
				break;
		}
	} else if (acao === 'listar') {
		switch (entidade) {
			case 'turma':
				alert("Exibir lista de todas as Turmas (Tabela)");
				break;
			case 'aluno':
				alert("Exibir lista de todos os Alunos (Tabela)");
				break;
			case 'uc':
				alert("Exibir lista de Unidades Curriculares");
				break;
			case 'criterio':
				alert("Exibir lista de Critérios");
				break;
		}
	}
}