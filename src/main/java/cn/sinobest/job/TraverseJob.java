package cn.sinobest.job;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SchemaerFactory;
import cn.sinobest.core.handler.IRowCallBackHandler;
import cn.sinobest.core.service.TraverseDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 初始化TraverseDataService，并且根据配置定时运行TraverseDataService
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseJob {
    private static final Log logger = LogFactory.getLog(TraverseJob.class);

    private String serviceName;

    private IRowCallBackHandler callBackHandler;

    private TraverseDataService traverseDataService;

    public void init(){
        Schemaer schemaer = SchemaerFactory.getSchema(serviceName);
//            try {
        //为了不多写工厂奇懒无比
//                IRowAnalyzerCallBackHandler callBackHandler = (IRowAnalyzerCallBackHandler) SpringContextInit.getContext().getBean(callBackHandlerBeanName);
//                callBackHandler.init(schemaer);

        traverseDataService = (TraverseDataService) SpringContextInit.getContext().getBean("traverseDataService");
        traverseDataService.initService(schemaer, callBackHandler);
//            }catch (ClassCastException e){
//                logger.error(callBackHandler+"不属于IRowAnalyzerCallBackHandler的实现！\n"+e.getMessage(),e);
//            }

    }

    public void execute(){
        logger.info("任务运行开始！");
        traverseDataService.execute();
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setCallBackHandler(IRowCallBackHandler callBackHandler) {
        this.callBackHandler = callBackHandler;
    }
}
