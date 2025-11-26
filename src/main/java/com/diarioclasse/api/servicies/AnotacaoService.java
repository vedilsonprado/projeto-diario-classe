package com.diarioclasse.api.servicies;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.diarioclasse.api.entities.Aluno;
import com.diarioclasse.api.entities.Anotacao;
import com.diarioclasse.api.repositories.AnotacaoRepository;

@Service
public class AnotacaoService {

    @Autowired
    private AnotacaoRepository anotacaoRepository;

    @Autowired
    private AlunoService alunoService; // Reutilizamos para validar se o aluno existe

    /**
     * Cria uma nova anotação, processando o arquivo se houver.
     */
    public Anotacao criarAnotacao(Long alunoId, String texto, MultipartFile arquivo) throws IOException {
        // 1. Busca o Aluno (se não existir, o AlunoService já lança erro 404)
        Aluno aluno = alunoService.buscarPorId(alunoId);

        // 2. Prepara a Anotação
        Anotacao nota = new Anotacao();
        nota.setAluno(aluno);
        nota.setTexto(texto);

        // 3. Processa o Arquivo (Blob)
        if (arquivo != null && !arquivo.isEmpty()) {
            nota.setNomeArquivo(arquivo.getOriginalFilename());
            nota.setTipoArquivo(arquivo.getContentType());
            nota.setDadosArquivo(arquivo.getBytes()); // Converte para array de bytes
        }

        return anotacaoRepository.save(nota);
    }

    /**
     * Lista anotações de um aluno específico.
     */
    public List<Anotacao> listarPorAluno(Long alunoId) {
        // Valida existência do aluno
        alunoService.buscarPorId(alunoId);
        
        return anotacaoRepository.findByAlunoIdOrderByDataHoraDesc(alunoId);
    }
    
    // Opcional: Método para deletar anotação
    public void deletar(Long id) {
        anotacaoRepository.deleteById(id);
    }
    
    /**
     * Atualiza uma anotação existente.
     */
    public Anotacao atualizarAnotacao(Long id, String novoTexto, MultipartFile novoArquivo) throws IOException {
        Anotacao nota = anotacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anotação não encontrada"));

        nota.setTexto(novoTexto);

        // Se um novo arquivo for enviado, substitui o antigo.
        if (novoArquivo != null && !novoArquivo.isEmpty()) {
            nota.setNomeArquivo(novoArquivo.getOriginalFilename());
            nota.setTipoArquivo(novoArquivo.getContentType());
            nota.setDadosArquivo(novoArquivo.getBytes());
        }

        return anotacaoRepository.save(nota);
    }
}