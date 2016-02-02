package cn.sinobest.job;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SchemaerFactory;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.core.service.TraverseDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class RYTraverseJob {
    private static final Log logger = LogFactory.getLog(RYTraverseJob.class);

    private String serviceName;

    private String callBackHandlerBeanName;

    private TraverseDataService traverseDataService;

    public void init(){
            Schemaer schemaer = SchemaerFactory.getSchema(serviceName);
            try {
                //为了不多写工厂奇懒无比
                IRowAnalyzerCallBackHandler callBackHandler = (IRowAnalyzerCallBackHandler) SpringContextInit.getContext().getBean(callBackHandlerBeanName, schemaer);

                traverseDataService = (TraverseDataService) SpringContextInit.getContext().getBean("traverseDataService",schemaer,callBackHandler);
            }catch (ClassCastException e){
                logger.error(callBackHandlerBeanName+"不属于IRowAnalyzerCallBackHandler的实现！\n"+e.getMessage(),e);
            }

    }

    public void execute(){
        logger.trace("任务运行开始！");
        traverseDataService.execute();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCallBackHandlerBeanName() {
        return callBackHandlerBeanName;
    }

    public void setCallBackHandlerBeanName(String callBackHandlerBeanName) {
        this.callBackHandlerBeanName = callBackHandlerBeanName;
    }
}
