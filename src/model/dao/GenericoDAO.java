package model.dao;

import java.util.List;

public interface GenericoDAO <T, ID> {
    
   void salvar(T objecto);

   void eliminar(ID id);

   void atualizar(T objecto);

   List<T> listarTodos();
   
   T buscarPorId(ID id);

}
