// Configuração da API
const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    carregarTurmas();
});

// ==========================================
// 1. LÓGICA DE TURMAS (APENAS LISTAGEM)
// ==========================================

async function carregarTurmas() {
    try {
        const response = await fetch(`${API_URL}/turmas`);
        if (!response.ok) throw new Error('Erro ao buscar turmas');
        
        const turmas = await response.json();
        renderizarListaTurmas(turmas);
    } catch (error) {
        console.error('Erro:', error);
        alert('Não foi possível carregar as turmas. Verifique se o backend está rodando.');
    }
}

function renderizarListaTurmas(turmas) {
    const listaElement = document.querySelector('.turmas-list');
    listaElement.innerHTML = '';

    turmas.forEach(turma => {
        const li = document.createElement('li');
        li.className = 'turma-item';
        li.textContent = `${turma.nomeTurma} (${turma.anoSemestre})`;
        li.dataset.id = turma.id; 
        
        li.addEventListener('click', () => selecionarTurma(li, turma));
        
        listaElement.appendChild(li);
    });
}

function selecionarTurma(elementoLi, turma) {
    // 1. Atualiza visual do menu lateral
    document.querySelectorAll('.turma-item').forEach(item => item.classList.remove('selected'));
    elementoLi.classList.add('selected');

    // 2. Alterna visual da área central
    document.getElementById('initial-logo').classList.remove('active');
    const areaAlunos = document.getElementById('alunos-list');
    areaAlunos.style.display = 'block';

    // 3. Atualiza título
    const titulo = areaAlunos.querySelector('h2');
    titulo.textContent = `Alunos da Turma: ${turma.nomeTurma}`;

    // 4. Carrega os dados
    carregarAlunos(turma.id);
}

// ==========================================
// 2. LÓGICA DE ALUNOS (APENAS LISTAGEM)
// ==========================================

async function carregarAlunos(turmaId) {
    const areaAlunos = document.getElementById('alunos-list');
    
    let tabelaContainer = document.getElementById('tabela-alunos-container');
    if (!tabelaContainer) {
        tabelaContainer = document.createElement('div');
        tabelaContainer.id = 'tabela-alunos-container';
        areaAlunos.appendChild(tabelaContainer);
    }
    tabelaContainer.innerHTML = '<p>Carregando alunos...</p>';

    try {
        const response = await fetch(`${API_URL}/alunos/turma/${turmaId}`);
        const alunos = await response.json();

        renderizarTabelaAlunos(alunos, tabelaContainer);
    } catch (error) {
        console.error('Erro:', error);
        tabelaContainer.innerHTML = '<p style="color:red">Erro ao carregar alunos.</p>';
    }
}

function renderizarTabelaAlunos(alunos, container) {
    if (alunos.length === 0) {
        container.innerHTML = '<p>Nenhum aluno matriculado nesta turma.</p>';
        return;
    }

    const ucsExemplo = alunos[0].desempenhos || []; 

    if (ucsExemplo.length === 0) {
        container.innerHTML = `<p>Alunos carregados, mas sem UCs vinculadas para exibir notas detalhadas.</p>`;
        return;
    }

    let htmlHeadRow1 = `
        <tr>
            <th rowspan="2" style="width: 5%">ID</th>
            <th rowspan="2" style="width: 25%">Nome Completo</th>
            <th rowspan="2" style="width: 20%">Email</th>
    `;

    let htmlHeadRow2 = `<tr>`;

    ucsExemplo.forEach(uc => {
        htmlHeadRow1 += `<th colspan="2" style="text-align: center; border-bottom: 1px solid rgba(255,255,255,0.3);">${uc.nomeUC}</th>`;
        htmlHeadRow2 += `
            <th style="text-align: center; font-size: 0.8em; filter: brightness(90%);">CC</th>
            <th style="text-align: center; font-size: 0.8em; filter: brightness(90%);">CD</th>
        `;
    });

    htmlHeadRow1 += `<th rowspan="2" style="width: 10%">Ações</th></tr>`;
    htmlHeadRow2 += `</tr>`;

    const htmlBody = alunos.map(aluno => {
        const colunasNotas = aluno.desempenhos.map(desempenho => `
            <td style="text-align: center; background-color: #f9f9f9;"><strong>${desempenho.cc}</strong></td>
            <td style="text-align: center;">${desempenho.cd}</td>
        `).join('');

        return `
            <tr>
                <td>${aluno.id}</td>
                <td>${aluno.nomeCompleto}</td>
                <td>${aluno.email || '-'}</td>
                ${colunasNotas}
                <td style="text-align: center;">
                    <button class="btn-lancar-notas" onclick="abrirDiario(${aluno.id})">
                        <i class="fas fa-edit"></i> Notas
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    const tabelaHTML = `
        <table class="students-table">
            <thead>
                ${htmlHeadRow1}
                ${htmlHeadRow2}
            </thead>
            <tbody>
                ${htmlBody}
            </tbody>
        </table>
    `;

    container.innerHTML = tabelaHTML;
}

function abrirDiario(alunoId) {
    alert(`Funcionalidade futura: Abrir diário para o aluno ID ${alunoId}`);
}