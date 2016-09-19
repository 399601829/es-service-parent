# es-service-parent
  基于elasticsearch1.1构建的搜索系统，分为索引入库服务、搜索服务、后台词典管理3个模块，线上一直稳定运行，对于初级的搜索系统可以直接使用。


# es-service-index、es-service-index-impl
  索引入库dubbo服务接口，提供定时任务的方式，定时全量、增量同步数据到索引库，同时也提供索引接口，供接入的系统直接调用存入索引库，只需要编写好数据查询接口，设置定时任务频率即可使用。
 
 
# es-service-search、es-service-search-impl
  搜索dubbo服务接口，提供搜索接口，接入的系统传入搜索条件，直接调用即可完成搜索，搜索使用示例见
  <a href="https://github.com/hailin0/es-service-parent/blob/master/es-service-search-impl/src/test/java/com/es/service/search/App.java">搜索示例</a>
 
# es-service-control
  后台服务web工程，提供在线的热更新词典管理（基于ik分词）、搜索查询页面等功能。
 

# 后台演示
![Alt 演示效果](/es-service-doc/doc/demo/man.gif "演示效果") 
