package cn.sinobest.job;

import cn.sinobest.core.common.init.SpringContextInit;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.config.po.TraverseConfigSchemaFactory;
import cn.sinobest.core.config.schema.Schemaer;
import cn.sinobest.core.config.schema.SchemaerFactory;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.core.service.TraverseDataService;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseJob {
    private static final Log logger = LogFactory.getLog(TraverseJob.class);

    private Set<String> serviceNames;

    private String callBackHandlerBeanName;

    private Set<TraverseDataService> services = new HashSet<TraverseDataService>();

    public void init(){
        for (String serviceName:serviceNames) {
            Schemaer schemaer = SchemaerFactory.getSchema(serviceName);
            try {
                //为了不多写工厂奇懒无比
                IRowAnalyzerCallBackHandler callBackHandler = (IRowAnalyzerCallBackHandler) SpringContextInit.getContext().getBean(callBackHandlerBeanName, schemaer);

                //为了不多写工厂奇懒无比
                TraverseDataService traverseDataService = (TraverseDataService) SpringContextInit.getContext().getBean("traverseDataService",schemaer,callBackHandler);
                services.add(traverseDataService);
            }catch (ClassCastException e){
                logger.error(callBackHandlerBeanName+"不属于IRowAnalyzerCallBackHandler的实现！\n"+e.getMessage(),e);
            }

        }
    }

    public void execute(){
        logger.trace("任务运行开始！");
        for (TraverseDataService service:services) {
            service.execute();
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
