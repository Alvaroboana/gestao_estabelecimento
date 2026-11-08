package model.bo;

import java.text.Normalizer;
import java.util.List;
import model.dao.CategoriaDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Categoria;
import util.ValidadorObjeto;

public class CategoriaBO {

    private static final int TAMANHO_MAXIMO_NOME = 60;

    private final CategoriaDAO categoriaDAO;

    public CategoriaBO() {
        this(new CategoriaDAO());
    }

    public CategoriaBO(CategoriaDAO categoriaDAO) {
        ValidadorObjeto.validarNaoNulo(categoriaDAO, "DAO de categoria");
        this.categoriaDAO = categoriaDAO;
    }

    public List<Categoria> listarTodos() {
        return categoriaDAO.listarTodos();
    }

    public Categoria buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException("idCategoria deve ser um identificador positivo.");
        }
        Categoria categoria = categoriaDAO.buscarPorId(id);
        if (categoria == null) {
            throw new EntidadeNaoEncontradaException("Categoria nao encontrada.");
        }
        return categoria;
    }

    public void salvar(Categoria categoria) {
        ValidadorObjeto.validarNaoNulo(categoria, "Categoria");
        String nome = limparNome(categoria.getNome());
        if (nome.isEmpty()) {
            throw new DadoInvalidoException("Nome da categoria e obrigatorio.");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new DadoInvalidoException("O nome da categoria nao pode ter mais de " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        categoria.setNome(nome);
        categoriaDAO.salvar(categoria);
    }

    public void atualizar(Categoria categoria) {
        ValidadorObjeto.validarNaoNulo(categoria, "Categoria");
        if (categoria.getIdCategoria() == null || categoria.getIdCategoria() <= 0) {
            throw new DadoInvalidoException("idCategoria deve ser um identificador positivo.");
        }
        String nome = limparNome(categoria.getNome());
        if (nome.isEmpty()) {
            throw new DadoInvalidoException("Nome da categoria e obrigatorio.");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new DadoInvalidoException("O nome da categoria nao pode ter mais de " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        categoria.setNome(nome);
        categoriaDAO.atualizar(categoria);
    }

    public void eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException("idCategoria deve ser um identificador positivo.");
        }
        categoriaDAO.eliminar(id);
    }

    public Categoria criar(String nomeBruto) {
        String nome = limparNome(nomeBruto);
        if (nome.isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório(a).");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new IllegalArgumentException(
                    "O nome da categoria não pode ter mais de " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }

        try {
            if (procurarPorNome(nome) != null) {
                throw new IllegalArgumentException("Já existe uma categoria chamada \"" + nome + "\".");
            }

            Categoria nova = new Categoria();
            nova.setNome(nome);
            categoriaDAO.salvar(nova);

            Categoria gravada = procurarPorNome(nome);
            return gravada != null ? gravada : nova;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.toLowerCase().contains("duplicate")) {
                throw new IllegalArgumentException("Já existe uma categoria chamada \"" + nome + "\".");
            }
            throw new IllegalStateException("Não foi possível guardar a categoria: " + msg, e);
        }
    }

    private Categoria procurarPorNome(String nome) throws Exception {
        List<Categoria> todas = categoriaDAO.listarTodos();
        if (todas == null) {
            return null;
        }
        String chave = chaveComparacao(nome);
        for (Categoria c : todas) {
            if (c != null && c.getNome() != null && chaveComparacao(c.getNome()).equals(chave)) {
                return c;
            }
        }
        return null;
    }

    private static String limparNome(String bruto) {
        if (bruto == null) {
            return "";
        }
        String nome = bruto.trim().replaceAll("\\s+", " ");
        if (nome.isEmpty()) {
            return nome;
        }
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }

    private static String chaveComparacao(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcentos.trim().toLowerCase();
    }
}
