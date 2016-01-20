package cn.sinobest.job;

import cn.sinobest.core.common.util.SpringContextInit;
import cn.sinobest.core.config.po.ResultColumn;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.config.po.TraverseConfigSchemaFactory;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.core.service.TraverseDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseJob {
    private static final Log logger = LogFactory.getLog(TraverseJob.class);

    private Set<String> serviceNames;

    private String callBackHandlerBeanName;

    public void execute(){
        logger.trace("任务运行开始！");
        for (String serviceName:serviceNames) {
            TraverseConfigSchema schema = TraverseConfigSchemaFactory.getSchema(serviceName);
            try {
                IRowAnalyzerCallBackHandler callBackHandler = (IRowAnalyzerCallBackHandler) SpringContextInit.getBeanByAware(callBackHandlerBeanName, schema);

                TraverseDataService traverseDataService = (TraverseDataService) SpringContextInit.getBeanByAware("traverseDataService",schema,callBackHandler);
                traverseDataService.execute();
            }catch (ClassCastException e){
                logger.error(callBackHandlerBeanName+"不属于IRowAnalyzerCallBackHandler的实现！\n"+e.getMessage(),e);
            }

        }

    }

    public Set<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(Set<String> serviceNames) {
        this.serviceNames = serviceNames;
    }

    public String getCallBackHandlerBeanName() {
        return callBackHandlerBeanName;
    }

    public void setCallBackHandlerBeanName(String callBackHandlerBeanName) {
        this.callBackHandlerBeanName = callBackHandlerBeanName;
    }
}
