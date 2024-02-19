# 校园外卖项目

- **sky-take-out**: maven父工程，统一管理依赖版本，聚合其他子模块
  - **sky-common**: 子模块，存放公共类（如：工具类、常量类、异常类、配置类等）
  - **sky-pojo**: 子模块，存放entity、vo、dto
  - **sky-server**: 后端服务（Controller、Service、Mapper等），存放配置文件、配置类、拦截器、启动类等


- **pojo**: 普通Java对象，只有各个属性及属性的Getter、Setter方法
  - **entity**: 实体，通常和数据库表对应
  - **dto**: 数据传输对象，用于程序各层之间传输数据
  - **vo**: 视图对象，为前端展示数据提供对象