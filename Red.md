## sunshine的第一个spring boot实现
42 29
### 资料配置过程
[sping web](https://spring.io/guides/gs/serving-web-content/)

[github集成](https://developer.github.com/apps/building-oauth-apps/ )

[bootstrap](https://v3.bootcss.com/components/#navbar)

[h2shujuku](http://www.h2database.com/html/quickstart.html)

[mybatis](http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure)

[mvnre](https://mvnrepository.com/)

[algs4](https://algs4.cs.princeton.edu/13stacks/)

[lintcode](https://www.lintcode.com)

[sunshine](47.106.158.212)

[lombok](https://www.projectlombok.org/)

[buide by self](https://docs.spring.io/spring-boot/docs/2.0.0.RC1/reference/htmlsingle/#using-boot-devtools)

### 工具需求
git 网站 
idea集成mybatis 
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
### 脚本语言
###### 用来将我们mapper里面的那些sql语句简单化


user
```sql
create table USER
(
  ID           INTEGER   primary key,
  NAME         VARCHAR(50),
  ACCOUNT_ID   VARCHAR(100),
  TOKEN        CHAR(36),
  GMT_CREATE   BIGINT,
  GMT_MODIFIED BIGINT
);

```
question
```sql
create table QUESTION
(
  ID            INTEGER primary key,
  TITLE         VARCHAR(50),
  DESCRIPTION   CLOB,
  GMT_CREATE    BIGINT,
  CREATOR       INTEGER,
  COLUMN_6      INTEGER,
  COMMENT_COUNT INTEGER default 0,
  VIEW_COUNT    INTEGER default 0,
  LIKE_COUNT    INTEGER default 0,
  TAG           VARCHAR(256)
);
```

