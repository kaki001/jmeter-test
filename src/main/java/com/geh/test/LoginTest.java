package com.geh.test;

import com.bfd.harpc.config.ClientConfig;
import com.bfd.harpc.config.RegistryConfig;
import com.caimao.pfs.gateway.api.harpc.entity.PfsApplyTokenReq;
import com.caimao.pfs.gateway.api.harpc.entity.PfsApplyTokenRes;
import com.caimao.pfs.gateway.api.harpc.service.PfsGatewayService;
import com.caimao.pfs.gateway.api.harpc.tenum.PfsEProject;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.thrift.TException;

/**
 * Created by Ge Hui on 2016/6/21.
 */
public class LoginTest extends AbstractJavaSamplerClient {

    private static PfsGatewayService.Iface service = null;

    @Override
    public Arguments getDefaultParameters() {
        Arguments args = new Arguments();
        args.addArgument("zkAddress", "172.32.1.222:2121");
        args.addArgument("zkAuth", "admin:admin123");
        args.addArgument("serviceName", "com.caimao.pfs.gateway.api.harpc$PfsGatewayServiceApplicationTest");
        return args;
    }

    @Override
    public void teardownTest(JavaSamplerContext ctx) {
        super.teardownTest(ctx);
    }

    @Override
    public void setupTest(JavaSamplerContext ctx) {
        super.setupTest(ctx);
    }

    public SampleResult runTest(JavaSamplerContext ctx) {

        String zkAddress = ctx.getParameter("zkAddress").trim();
        String zkAuth = ctx.getParameter("zkAuth").trim();
        String serviceName = ctx.getParameter("serviceName").trim();

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setConnectstr(zkAddress);
        registryConfig.setAuth(zkAuth);

        String iface = PfsGatewayService.Iface.class.getName();
        ClientConfig<PfsGatewayService.Iface> clientConfig = new ClientConfig<PfsGatewayService.Iface>();
        clientConfig.setService(serviceName);
        clientConfig.setIface(iface);

        String source = "1001";
        String ip = "127.0.0.1";

        PfsApplyTokenReq tokenReq = new PfsApplyTokenReq();
        int expire = 7200;
        tokenReq.setExpirein(expire);
        tokenReq.setSource(source);
        tokenReq.setIp(ip);
        tokenReq.setExclusive(false);
        tokenReq.setProject(PfsEProject.CAIMAO);
        PfsApplyTokenRes tokenRes = null;
        SampleResult sampleResult = new SampleResult();
        sampleResult.setSampleLabel("Test");
        try {
            sampleResult.sampleStart();
            if (service == null) {
                service = clientConfig.createProxy(registryConfig);
            }

            tokenRes = service.applyToken(tokenReq);
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(tokenRes.getCode() <= 0);
            return sampleResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        sampleResult.setSuccessful(false);
        return sampleResult;
    }

    public static void main(String args[]) {
        LoginTest loginTest = new LoginTest();
        JavaSamplerContext ctx = new JavaSamplerContext(loginTest.getDefaultParameters());
        loginTest.runTest(ctx);
    }
}
