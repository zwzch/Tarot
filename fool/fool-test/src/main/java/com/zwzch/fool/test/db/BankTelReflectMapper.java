package com.zwzch.fool.test.db;

import org.apache.ibatis.annotations.Param;

public interface BankTelReflectMapper {

    int insert(BankTelReflect bankTelReflect);

    int insertOrUpdate(BankTelReflect bankTelReflect);

    int update(BankTelReflect bankTelReflect);

    int delete(BankTelReflect bankTelReflect);

    BankTelReflect selectByCardNo(@Param("cardNo") String cardNo);

    BankTelReflect selectByCardNoAndTel(@Param("cardNo") String cardNo, @Param("countryCode") String countryCode, @Param("telephone") String telephone);

}
