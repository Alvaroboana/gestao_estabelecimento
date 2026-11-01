package model.dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericoDAO <T, ID> {
    
   void salvar(T objecto) throws SQLException;

   void eliminar(ID id) throws SQLException;

   void atualizar(T objecto) throws SQLException;

   List<T> listarTodos();
   
   T buscarPorId(ID id);

}
