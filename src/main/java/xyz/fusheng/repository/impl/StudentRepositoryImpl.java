/**
 * Copyright (C), 2020-2020, code_fusheng
 * FileName: StudentRepositoryImpl
 * Author:   25610
 * Date:     2020/2/3 21:41
 * Description:
 * History:
 * <author>        <time>      <version>       <desc>
 * 作者姓名       修改时间       版本号         描述
 */
package xyz.fusheng.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import xyz.fusheng.entity.Student;
import xyz.fusheng.repository.StudentRepository;

import java.util.List;

//没有 Repository 会导致 StudentHandler 无法自动注入 StudentRepository
@Repository
public class StudentRepositoryImpl implements StudentRepository {

    //JdbcTemplate 由 Spring 内置创建 自动注入即可
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //该段代码表示将 SQL 语句查询到的结果封装成一个Student的实例化对象集合，很显然BeanPropertyRowMapper是RowMapper接口的一个实现类
    @Override
    public List<Student> findAll() {
        return jdbcTemplate.query("select * from student",new BeanPropertyRowMapper<Student>(Student.class));
    }

    //条件查询结果，封装对象返回
    @Override
    public Student findById(Long id) {
        return jdbcTemplate.queryForObject("select * from student where id = ?",new Object[]{id},new BeanPropertyRowMapper<Student>(Student.class));
    }

    //保存添加数据
    @Override
    public int save(Student student) {
        return jdbcTemplate.update("insert into student(name,score,birthday) value (?,?,?)", student.getName(),student.getScore(),student.getBirthday());
    }

    //修改更新数据
    @Override
    public int update(Student student) {
        return jdbcTemplate.update("update student set name = ?,score = ?,birthday = ? where id = ?", student.getName(),student.getScore(),student.getBirthday(),student.getId());
    }

    //删除数据对象
    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("delete from student where id = ?",id);
    }
}
