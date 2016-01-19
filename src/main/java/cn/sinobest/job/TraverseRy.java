package cn.sinobest.job;

import cn.sinobest.core.common.util.SpringUtil;
import cn.sinobest.core.config.po.ResultColumn;
import cn.sinobest.core.config.po.TraverseConfigSchema;
import cn.sinobest.core.handler.IRowAnalyzerCallBackHandler;
import cn.sinobest.core.service.TraverseDataService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhouyi1 on 2016/1/19 0019.
 */
public class TraverseRy {

    private Set<String> serviceNames;

    private IRowAnalyzerCallBackHandler callBackHandler;

    public void execute(){
        for (String serviceName:serviceNames) {
            TraverseDataService traverseDataService = (TraverseDataService) SpringUtil.getBeanByAware("traverseDataService",serviceName,callBackHandler);
            traverseDataService.execute();
        }

    }

    public Set<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(Set<String> serviceNames) {
        this.serviceNames = serviceNames;
    }

    public IRowAnalyzerCallBackHandler getCallBackHandler() {
        return callBackHandler;
    }

    public void setCallBackHandler(IRowAnalyzerCallBackHandler callBackHandler) {
        this.callBackHandler = callBackHandler;
    }
}
