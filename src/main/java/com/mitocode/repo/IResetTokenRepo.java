package com.mitocode.repo;

import com.mitocode.model.ResetToken;

public interface IResetTokenRepo extends IGenericRepo<ResetToken, Integer>{

	//from resetToken rt where rt.token = :?
	ResetToken findByToken(String token);
}
