p2p -web
1. SpringBoot框架web项目
2.dubbo分布式框架中的服务消费者
3.集成SpringMVC，Dubbo, Thymeleaf
添加依赖:dubbo依赖,Thymeleaf
4.当前工程是唯一一个用户可以直接访问的工程(即处理浏览器客户端发送的请求)


项目规定
1.包名:
实体类com. bjpowernode . p2p . mode1. user lloan
数据持久层com. bjpowernode . p2p . mapper . user loan
业务层com . bjpowernode . p2p. service/biz .user lloan
控制层com . bjpowernode . p2p. web/ controller

方法      数据持久层       业务层
新增      insert*         add*
删除      delete*         remove*
修改      update*         modify|edit*
查询      select*         query|find*