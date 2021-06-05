package com.example.web.dao;

import com.example.web.entity.UserAccount;
import com.example.web.entity.UserInfo;
import org.apache.ibatis.annotations.*;

/**
 * 针对用户相关的表操作
 */
@Mapper
public interface UserMapper {

    @Select("select * from DB_ACCOUNTS where email = #{text} or name = #{text}")
    UserAccount getAccountByNameOrEmail(String text);

    @Select("select name, email, phone, note " +
            "from DB_ACCOUNTS left join DB_ACCOUNTS_DETAIL on DB_ACCOUNTS.sid = DB_ACCOUNTS_DETAIL.sid " +
            "where DB_ACCOUNTS.sid = #{id}")
    UserInfo getUserDetailById(int id);

    @Insert("insert into DB_ACCOUNTS (name, email, password) value (#{userName}, #{email}, #{password})")
    int registerBasicAccount(UserAccount account);

    @Update("update DB_ACCOUNTS set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String password, String email);

    @Update("update DB_ACCOUNTS_DETAIL set phone = #{info.phone}, note = #{info.note} where sid = " +
            "(select sid from DB_ACCOUNTS where email = #{email})")
    int resetUserInfoByEmail(UserInfo info, String email);

    @Update("update DB_ACCOUNTS set name = #{name} where email = #{email}")
    int resetNameByEmail(String name, String email);

    @Update("update DB_ACCOUNTS set email = #{newMail} where email = #{email}")
    int resetEmailByEmail(String newMail, String email);
}
