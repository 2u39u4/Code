package com.sztu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sztu.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
    @Select("select id from chat_history where name like concat('%', #{name}, '%') and user_id=#{userId}")
    List<Long> getIdsByName(@Param("name") String name, @Param("userId") Long userId);
}
