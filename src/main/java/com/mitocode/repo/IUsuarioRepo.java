package com.mitocode.repo;

import com.mitocode.model.Usuario;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer>{
	
	//from usuario where username = ?
	//@Query("FROM Usuario us where us.username = ?")
	//Derived Query
	Usuario findOneByUsername(String username);
}
