# code4j
#### `JAVA`模板代码生成工具。
![1648102359(1)](https://user-images.githubusercontent.com/42802329/159853606-d344a28e-fbb1-4696-865b-5959b7e9d203.png)

## 环境资源
* Windows 系统
* JDK8+ 
* Mysql数据库

## 工具说明
* entity 配置：此对象与数据库表结构一一对应

* vo 配置：显示层对象。

* service API配置：业务层接口。

* dao 配置：数据访问层接口。

* xml 配置：mybatis sql 文件。

名称：代码（类名）或文件名称；例如：UserInfo.java  UserMapper.xml

包名：执代码所属的包路径。例如：com.xxx.UserInfo 中只需要填写 com.xxx

路径：项目中代码所属文件夹。（默认 src/main/java 无需改动）

父类：生成代码继承的父类。（必须是全路径 例如：com.xxx.BaseVo）

字段配置：可配置字段类型和选择字段否需要生成规则（注意：如果继承了父类某些字段可以不用生成了）

dao 配置：可选择自定义的接口。（默认不生成）

xml 配置：可选择自定义的接口。（默认不生成，如果dao 配置了父类默认生成所有接口）

## 使用教程

1. 拉取代码到本地打成jar包 （mvn package）双击运行jar包。

2. 配置mysql数据源。

3. 选择左边栏数据库表。

4. 点击生成代码按钮，确定后即可生成代码。
