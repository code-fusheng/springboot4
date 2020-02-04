# springboot4
Spring Boot 整合 JDBC
# Spring Boot 整合 JDBC

#### Spring Boot 整合持久层的具体操作
* JdbcTemplate
* MyBatis
* Spring Data JPA
* Spring Data Redis
* Spring Data MongoDB

***JdbcTemplate 是 Spring 自带的 JDBC 模板组件，底层实现了对 JDBC 的封装，用法 与 MyBatis 类似，需要开发者自定义 SQL 语句。***

***JdbcTemplate 帮助我们完成数据库连接，SQL语句执行，以及结果集的封装。***

***但是它的不足之处是灵活性不如 MyBatis,因为 MyBatis 的 SQL 语句都是定义在 XML 文件中的，更有利于维护和拓展，而 JdbcTemplate 是以硬编码的方式将 SQL 语句直接写在 Java 代码例，不利于维护拓展***

***虽有不足，但整体来说非常方便，且为 Spring 自带组件***

#### 案例演示

* 1、创建 Maven 工程，pom.xml 中添加相关依赖。
```xml
    <!-- 继承父包 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <dependencies>

        <!-- web启动jar -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

* 2、创建数据表
![sutdent 表](https://img-blog.csdnimg.cn/20200204112216754.png)

* 3、创建实体类 Student
```java
package xyz.fusheng.entity;

import java.sql.Date;

public class Student {
    private Long id;
    private String name;
    private  Double score;
    private Date birthday;
}
```
* 4、创建 StudentRepository 
```java
package xyz.fusheng.repository;

import xyz.fusheng.entity.Student;

import java.util.List;

public interface StudentRepository {   
    public List<Student> findAll();
    public Student findById(Long id);
    public int save(Student student);
    public int deleteById(Long id);
}
```
* 5、创建 StudentRepositoryImpl 接口实现类
```java
package xyz.fusheng.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import xyz.fusheng.entity.Student;
import xyz.fusheng.repository.StudentRepository;

import java.util.List;

//没有 @Repository 会导致 StudentHandler 无法自动注入 StudentRepository
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
```
* 6、创建 StudentHandler,并且注入 StudentRepository。
```java
package xyz.fusheng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fusheng.entity.Student;
import xyz.fusheng.repository.StudentRepository;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentHandler {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/findAll")
    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Student findById(@PathVariable("id") Long id){
        return studentRepository.findById(id);
    }

    @PostMapping("/save")
    public int save(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @PutMapping("/update")
    public int update(@RequestBody Student student){
        return studentRepository.update(student);
    }

    @DeleteMapping("/deleteById/{id}")
    public int deleteById(@PathVariable("id") Long id){
        return studentRepository.deleteById(id);
    }
}
```
* 7、创建配置文件 application.yml，添加数据源配置。
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot4?useUnicode=true&charaterEncoding=UTF-8&serverTimezone=Hongkong
    username: root
    password: sa123
    driver-class-name: com.mysql.cj.jdbc.Driver
```
***PS : 此处存在时区问题，在 url 后面追加 serverTimezone=Hongkong 即可，这里我在Idea关联MySQL时在高级设置里用的是Shanghai,也就是serverTimezone=Hongkong，具体问题就不在这里说了。***

* 8、创建启动类 Application
```java
package xyz.fusheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```
+ findAll
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204113151146.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
+ findById
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204113308474.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
+ save
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114024573.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114116370.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
***PS : Postman 的操作，这里需要更改为 POST ，而且提交数据格式为 JSON***
+ update
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114419323.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114528231.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
+ deleteById
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114645659.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200204114713918.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyOTk5ODM1,size_16,color_FFFFFF,t_70)
***Spring Boot 整合 JdbcTemplate，JdbcTemplate 底层实现了对 JDBC 的封装，是 Spring 自带的 JDBC 模板组件。JdbcTemplate 可以完成数据库连接、SQL 语句执行、结果集封装。***
